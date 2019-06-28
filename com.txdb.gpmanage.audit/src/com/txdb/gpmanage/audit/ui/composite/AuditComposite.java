package com.txdb.gpmanage.audit.ui.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.audit.i18n.MessageConstants;
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.audit.ui.dialog.AuditPropertyDialog;
import com.txdb.gpmanage.audit.ui.wizard.AuditWizard;
import com.txdb.gpmanage.audit.ui.wizard.AuditWizardDialog;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPAuditEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;

public class AuditComposite extends AbstractComposite {

	private List<GPTreeNode> nodes;
	
	// The right composite areas map for switch audit categoroes.
	private Map<String, AuditContainer> rightAreaMap = new HashMap<String, AuditContainer>();
	
	// Tree right click menu
	private Menu popupMenu;
	
	public AuditComposite(Composite parent, int style) {
		super(parent, style, false);
	}

	@Override
	public void createmain() {
		StackLayout layout = new StackLayout();
		right.setLayout(layout);
		right.layout();
	}

	@Override
	protected void setInput() {
		nodes = new ArrayList<GPTreeNode>();
		List<IGPEntity> gpEntities = SqliteDao.getInstance().queryGPEntity(new GPAuditEntity(), null);
		for (IGPEntity gpEntity : gpEntities) {
			GPAuditEntity gpAuditEntity = (GPAuditEntity) gpEntity;
			GPTreeNode node = new GPTreeNode();
			node.setName(gpAuditEntity.getAuditName());
			node.setGPEntity(gpAuditEntity);
			nodes.add(node);
		}
		tv.setInput(nodes);
		tv.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button != 1)
					return;
				TreeItem selected = tv.getTree().getItem(new Point(e.x, e.y));
				if (selected == null || selected.getParentItem() != null)
					return;
				menuEvent_connect();
			}
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button != 3)
					return;
				TreeItem selected = tv.getTree().getItem(new Point(e.x, e.y));
				if (selected == null || selected.getParentItem() != null) {
					popupMenu.getItem(1).setEnabled(false);
					popupMenu.getItem(2).setEnabled(false);
					popupMenu.getItem(3).setEnabled(false);
					return;
				}
				popupMenu.getItem(1).setEnabled(true);
				popupMenu.getItem(2).setEnabled(true);
				popupMenu.getItem(3).setEnabled(selected.getItemCount() <= 0);
				
				TreeSelection selection = (TreeSelection) tv.getSelection();
				GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
				boolean isConnected = (treeNode.getChild() != null);
				popupMenu.getItem(1).setText(isConnected ? ResourceHandler.getValue(MessageConstants.MENU_DISCONNECT) : ResourceHandler.getValue(MessageConstants.MENU_CONNECT));
			}
		});
		createPopupMenu();
	}
	
	private void createPopupMenu() {
		popupMenu = tv.getTree().getMenu();
		if (popupMenu != null && !popupMenu.isDisposed())
			popupMenu.dispose();
		popupMenu = new Menu(tv.getTree());

		MenuItem createItem = new MenuItem(popupMenu, SWT.PUSH);
		createItem.setText(ResourceHandler.getValue(MessageConstants.MENU_NEW));
		createItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuEvent_new();
			}
		});
		MenuItem connectItem = new MenuItem(popupMenu, SWT.PUSH);
		connectItem.setText(ResourceHandler.getValue(MessageConstants.MENU_CONNECT));
		connectItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuEvent_connect();
			}
		});
		MenuItem propertiesItem = new MenuItem(popupMenu, SWT.PUSH);
		propertiesItem.setText(ResourceHandler.getValue(MessageConstants.MENU_PROPERTIES));
		propertiesItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuEvent_properties();
			}
		});
		MenuItem deleteItem = new MenuItem(popupMenu, SWT.PUSH);
		deleteItem.setText(ResourceHandler.getValue(MessageConstants.MENU_DELETE));
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuEvent_delete();
			}
		});
		tv.getTree().setMenu(popupMenu);
	}
	
	private void menuEvent_new() {
		AuditWizard auditWizard = new AuditWizard();
		WizardDialog wizardDialog = new AuditWizardDialog(getShell(), auditWizard);
		if (wizardDialog.open() != 0)
			return;

		IGPConnector auditGPC = auditWizard.getGpcontroller();
		IExecuteDao dao = auditGPC.getDao();

		GPAuditEntity entity = new GPAuditEntity();
		entity.setAuditName(auditWizard.getNewAuditName());
		entity.setHostname(dao.getHost());
		entity.setSshPort(dao.getSshPort());
		entity.setGpUsername(dao.getSshUserName());
		entity.setGpUserpwd(dao.getSshPassword());
		entity.setGpPort(dao.getJdbcPort());
		entity.setDbUsername(dao.getJdbcUsername());
		entity.setDbUserpwd(dao.getJdbcPassword());

		if (!SqliteDao.getInstance().insertGPEntity(entity)) {
			MessageDialog.openError(getShell(), "Error", "Add new audit \"" + auditWizard.getNewAuditName() + "\" failed!");
			return;
		}
		GPTreeNode node = new GPTreeNode();
		node.setName(entity.getAuditName());
		node.setGPEntity(entity);
		nodes.add(node);
		tv.setInput(nodes);
	}
	
	private void menuEvent_connect() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		final GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
		if (treeNode == null)
			return;

		if (treeNode.getChild() == null) {
			// ############ Connect ############
			// 0.0 Connect Server
			GPAuditEntity entity = (GPAuditEntity) treeNode.getGPEntity();
			IGPConnector gpController = new GPConnectorImpl(entity.getHostname(), entity.getGpUsername(), entity.getGpUserpwd(), entity.getSshPort());
			if (!gpController.connect().isSuccessed()) {
				MessageDialog.openError(getShell(), "Error", "\"" + treeNode.getName() + "\"SSH Connect failed!");
				return;
			}
			GpManageServiceProxy proxy = gpController.getManageServiceProxy();
			if (!proxy.connectJdbc(entity.getDbUsername(), entity.getDbUserpwd(), entity.getGpPort(), null)) {
				MessageDialog.openError(getShell(), "Error", "\"" + treeNode.getName() + "\"Jdbc Connect failed!");
				gpController.disconnect();
				return;
			}
			
			// 1.0 Create AuditContainer
			AuditContainer auditContainer = createRightAreas((GPAuditEntity) treeNode.getGPEntity());
			auditContainer.setGpController(gpController);
			
			// 2.0 Create TreeNode
			List<GPTreeNode> childNodes = CommonUtil.createAuditNode();
			for (GPTreeNode childNode : childNodes)
				treeNode.addChildren(childNode);
			tv.setInput(nodes);
			tv.expandToLevel(treeNode, 2);
			
			// 3.0 Start Webserver and thread
			startAuditThread(auditContainer);

		} else {
			// ############ Disconnect ############
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
			progressDialog.open();

			// 0.0 Stop Webserver and thread
			AuditContainer auditContainer = rightAreaMap.get(treeNode.getName());
			stopAuditThread(auditContainer);

			// 1.0 Disconnect Server
			auditContainer.getGpController().disconnect();

			// 2.0 Remove TreeNode
			treeNode.removeAllChildren();
			treeNode.setChild(null);
			tv.setInput(nodes);
			
			// 3.0 Destroy AuditContainer
			destroyRightAreas(treeNode);
			progressDialog.close();
		}
	}
	
	private AuditContainer createRightAreas(GPAuditEntity entity) {
		String auditName = entity.getAuditName();
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();

		System.out.println("启动审计组页面（" + auditName + "）");
		long startTime = System.currentTimeMillis();

		if (!rightAreaMap.containsKey(auditName))
			rightAreaMap.put(auditName, new AuditContainer(entity, this, right, SWT.NO_TRIM));
		AuditContainer auditContainer = rightAreaMap.get(auditName);

		StackLayout layout = (StackLayout) right.getLayout();
		layout.topControl = auditContainer;
		right.layout();

		long endTime = System.currentTimeMillis();
		System.out.println("审计组页面（" + auditName + "）加载完成（耗时：" + (endTime - startTime) + " ms）...");

		progressDialog.close();
		return auditContainer;
	}
	
	private boolean startAuditThread(AuditContainer auditContainer) {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();
		try {
			auditContainer.taskThreadStart();
			progressDialog.close();
			
			// Load Audit Data
			auditContainer.loadData();
			return true;
			
		} catch (Exception e) {
			return false;
			
		} finally {
			progressDialog.close();
		}
	}

	private boolean stopAuditThread(AuditContainer auditContainer) {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();
		try {
			auditContainer.taskThreadStop();
			return true;
			
		} catch (Exception e) {
			return false;
			
		} finally {
			progressDialog.close();
		}
	}
	
	private void menuEvent_properties() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		final GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
		if (treeNode == null)
			return;
		
		AuditPropertyDialog propertyDialog = new AuditPropertyDialog(getShell(), (GPAuditEntity) treeNode.getGPEntity(), treeNode.getChild() != null);
		if (Dialog.OK == propertyDialog.open()) {
			GPAuditEntity modifiedEntity = propertyDialog.getFinalEntity();
			treeNode.setName(modifiedEntity.getAuditName());
			treeNode.setGPEntity(modifiedEntity);
			tv.refresh(treeNode);
		}
	}
	
	private void menuEvent_delete() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
		if (treeNode == null || !MessageDialog.openQuestion(getShell(), "Confirm", "Are you sure to delete audit \"" + treeNode.getName() + "\" ?"))
			return;
		
		SqlWhere where = new SqlWhere();
		where.addWhere("auditName", "=", treeNode.getName());
		if (!SqliteDao.getInstance().deleteGPEntity(new GPAuditEntity(), where)) {
			MessageDialog.openError(getShell(), "Error", "Delete audit \"" + treeNode.getName() + "\" failed.");
			return;
		}
		if (treeNode.getChild() != null) {
			treeNode.removeAllChildren();
			treeNode.setChild(null);
			destroyRightAreas(treeNode);
		}
		nodes.remove(treeNode);
		tv.setInput(nodes);
	}
	
	private void destroyRightAreas(GPTreeNode treeNode) {
		String auditName = treeNode.getName();
		if (!rightAreaMap.containsKey(auditName))
			return;
		
		AuditContainer auditContainer = rightAreaMap.get(auditName);
		auditContainer.dispose();
		rightAreaMap.remove(auditName);
	}
	
	@Override
	protected void changeComposite(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		if (node == null || node.getCompositeCode() == null)
			return;
//		if (node.getCompositeCode() == null && node.getParent() == null)
//			return;
		
		GPTreeNode parentNode = getTreeRootNode(node);
		AuditContainer auditContainer = rightAreaMap.get(parentNode.getName());
		StackLayout layout = (StackLayout) right.getLayout();
		layout.topControl = auditContainer;
		right.layout();
		
		auditContainer.switchTopComposite(node.getCompositeCode());
	}
	
	public GPTreeNode getTreeRootNode(GPTreeNode node) {
		GPTreeNode parentNode = node.getParent();
		IGPEntity entity = parentNode.getGPEntity();
		if (entity == null)
			return getTreeRootNode(parentNode);
		return parentNode;
	}
}
