package com.txdb.gpmanage.monitor.ui.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.entity.impl.SystemWarningEntity;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.monitor.controller.MonitorController;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.Activator;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.lisener.WarnToolBarSelectionLisener;
import com.txdb.gpmanage.monitor.service.MonitorUIService;
import com.txdb.gpmanage.monitor.ui.dialog.MonitorPropertyDialog;
import com.txdb.gpmanage.monitor.ui.wizard.MonitorWizard;
import com.txdb.gpmanage.monitor.ui.wizard.MonitorWizardDialog;

public class MonitorComposite extends AbstractComposite {

	private List<GPTreeNode> nodes;

	// The right composite areas map for switch monitor categoroes.
	private Map<String, MonitorContainer> rightAreaMap = new HashMap<String, MonitorContainer>();

	// Tree right click menu
	private Menu popupMenu;

	public MonitorComposite(Composite parent, int style) {
		super(parent, style, true);
	}

	@Override
	public void createmain() {
		long startTime = System.currentTimeMillis();
		System.out.println("开始加载监控页面...");

		service = new MonitorUIService(Display.getDefault());
		StackLayout layout = new StackLayout();
		right.setLayout(layout);
		right.layout();

		long endTime = System.currentTimeMillis();
		System.out.println("监控页面加载完成（耗时：" + (endTime - startTime) + " ms）...");
	}

	@Override
	public void setInput() {
		nodes = new ArrayList<GPTreeNode>();
		List<IGPEntity> gpEntities = SqliteDao.getInstance().queryGPEntity(new GPMonitorEntity(), null);
		for (IGPEntity gpEntity : gpEntities) {
			GPMonitorEntity gpMonitorEntity = (GPMonitorEntity) gpEntity;
			GPTreeNode node = new GPTreeNode();
			node.setName(gpMonitorEntity.getMonitorName());
			node.setGPEntity(gpMonitorEntity);
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
				popupMenu.getItem(1).setText(isConnected ? 
						ResourceHandler.getValue(MessageConstants.MENU_DISCONNECT) : 
						ResourceHandler.getValue(MessageConstants.MENU_CONNECT));
			}
		});
		createPopupMenu();
	}

	/**
	 * 左侧树导航
	 * @param left
	 */
	public void createItem(Composite left) {
		
		GridLayout layout = new GridLayout(2, false);
		left.setLayout(layout);
		Label label = new Label(left, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		label.setText(ResourceHandler.getValue(MessageConstants.WARNING_TITLE));
		final ToolBar toolBar = new ToolBar(left, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		
		ToolItem add = new ToolItem(toolBar, SWT.PUSH);
		add.setData(WarnToolBarSelectionLisener.OPERATION_ADD);
		add.setToolTipText(ResourceHandler.getValue(MessageConstants.WARNING_BTN_ADD));
		add.setImage(Activator.getImage("icons/add.gif"));

		ToolItem editor = new ToolItem(toolBar, SWT.NONE);
		editor.setData(WarnToolBarSelectionLisener.OPERATION_EDIT);
		editor.setToolTipText(ResourceHandler.getValue(MessageConstants.WARNING_BTN_EDIT));
		editor.setImage(Activator.getImage("icons/editor.png"));

		ToolItem delete = new ToolItem(toolBar, SWT.NONE);
		delete.setData(WarnToolBarSelectionLisener.OPERATION_REMOVE);
		delete.setToolTipText(ResourceHandler.getValue(MessageConstants.WARNING_BTN_REMOVE));
		delete.setImage(Activator.getImage("icons/delete.png"));

		ToolItem mail = new ToolItem(toolBar, SWT.NONE);
		mail.setToolTipText(ResourceHandler.getValue(MessageConstants.WARNING_BTN_MAIL));
		mail.setData(WarnToolBarSelectionLisener.OPERATION_EMAIL);
		mail.setImage(Activator.getImage("icons/mail.png"));
		
		listViewer = new ListViewer(left, SWT.BORDER);
		listViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		listViewer.setLabelProvider(new LabelProvider());
		listViewer.setContentProvider(new ArrayContentProvider());
		setListViewerInput();
		
		WarnToolBarSelectionLisener listener = new WarnToolBarSelectionLisener(tv, listViewer);
		add.addSelectionListener(listener);
		editor.addSelectionListener(listener);
		delete.addSelectionListener(listener);
		mail.addSelectionListener(listener);
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
		MonitorWizard monitorWizard = new MonitorWizard();
		WizardDialog wizardDialog = new MonitorWizardDialog(getShell(), monitorWizard);
		if (wizardDialog.open() != 0)
			return;

		IGPConnector monitorGPC = monitorWizard.getGpcontroller();
		IExecuteDao dao = monitorGPC.getDao();

		GPMonitorEntity entity = new GPMonitorEntity();
		entity.setMonitorName(monitorWizard.getNewMonitorName());
		entity.setHostname(dao.getHost());
		entity.setSshPort(dao.getSshPort());
		entity.setGpUsername(dao.getSshUserName());
		entity.setGpUserpwd(dao.getSshPassword());
		entity.setGpPort(dao.getJdbcPort());
		entity.setDbUsername(dao.getJdbcUsername());
		entity.setDbUserpwd(dao.getJdbcPassword());

		if (!SqliteDao.getInstance().insertGPEntity(entity)) {
			MessageDialog.openError(getShell(), "Error", "Add new monitor \"" + monitorWizard.getNewMonitorName() + "\" failed!");
			return;
		}
		GPTreeNode node = new GPTreeNode();
		node.setName(entity.getMonitorName());
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
			GPMonitorEntity entity = (GPMonitorEntity) treeNode.getGPEntity();
			IGPConnector gpController = new GPConnectorImpl(entity.getHostname(), entity.getGpUsername(), entity.getGpUserpwd(), entity.getSshPort());
			if (!gpController.connect().isSuccessed()) {
				MessageDialog.openError(getShell(), "Error", "\"" + treeNode.getName() + "\"SSH Connect failed!");
				return;
			}
			GpManageServiceProxy proxy = gpController.getManageServiceProxy();
			if (!proxy.connectJdbc(entity.getDbUsername(), entity.getDbUserpwd(), entity.getGpPort(), "gpperfmon")) {
				MessageDialog.openError(getShell(), "Error", "\"" + treeNode.getName() + "\"Jdbc Connect failed!");
				gpController.disconnect();
				return;
			}

			// 1.0 Create MonitorContainer
			MonitorContainer monitorContainer = createRightAreas((GPMonitorEntity) treeNode.getGPEntity());
			monitorContainer.setGpController(gpController);

			// 2.0 Create TreeNode
			List<GPTreeNode> childNodes = CommonUtil.createMonitorNode();
			for (GPTreeNode childNode : childNodes)
				treeNode.addChildren(childNode);
			tv.setInput(nodes);
			tv.expandToLevel(treeNode, 1);
			
			// 2.5 Load Warning
			SqlWhere where = new SqlWhere();
			where.addWhere("monitorName", "=", entity.getMonitorName());
			List<IGPEntity> warns = SqliteDao.getInstance().queryGPEntity(new SystemWarningEntity(), where);
			List<SystemWarningEntity> warnings = new ArrayList<SystemWarningEntity>();
			if (warns != null) {
				for (IGPEntity warnEntity : warns)
					warnings.add((SystemWarningEntity) warnEntity);
			}
			entity.setWarns(warnings);
			listViewer.setInput(warnings);
			listViewer.refresh(true);

			// 3.0 Start Webserver and thread
			startMonitorThread(monitorContainer);

		} else {
			// ############ Disconnect ############
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
			progressDialog.open();

			// 0.0 Stop Webserver and thread
			MonitorContainer monitorContainer = rightAreaMap.get(treeNode.getName());
			stopMonitorThread(monitorContainer);

			// 1.0 Disconnect Server
			monitorContainer.getGpController().disconnect();

			// 2.0 Remove TreeNode
			treeNode.removeAllChildren();
			treeNode.setChild(null);
			tv.setInput(nodes);
			listViewer.setInput(null);
			listViewer.refresh(true);
			
			// 3.0 Destroy MonitorContainer
			destroyRightAreas(treeNode);

			progressDialog.close();
		}
	}

	private boolean startMonitorThread(MonitorContainer monitorContainer) {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();
		
		MonitorController controller = MonitorController.getInstance();
		try {
			controller.serverStartup();
			controller.listServer();
			
			monitorContainer.taskThreadStart();
			progressDialog.close();
			MessageDialog.openInformation(getShell(), "Information", "Chart data has already updated!");
			
			monitorContainer.loadData();
			return true;
			
		} catch (Exception e) {
			return false;
			
		} finally {
			progressDialog.close();
		}
	}

	private boolean stopMonitorThread(MonitorContainer monitorContainer) {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();

		// MonitorController controller = MonitorController.getInstance();
		try {
			monitorContainer.taskThreadStop();
			// controller.serverShutdown();
			return true;

		} catch (Exception e) {
			return false;

		} finally {
			progressDialog.close();
		}
	}

	private MonitorContainer createRightAreas(GPMonitorEntity entity) {
		String monitorName = entity.getMonitorName();
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
		progressDialog.open();

		System.out.println("启动监控组页面（" + monitorName + "）");
		long startTime = System.currentTimeMillis();

		if (!rightAreaMap.containsKey(monitorName))
			rightAreaMap.put(monitorName, new MonitorContainer(entity, this, right, SWT.NO_TRIM));
		MonitorContainer monitorContainer = rightAreaMap.get(monitorName);

		StackLayout layout = (StackLayout) right.getLayout();
		layout.topControl = monitorContainer;
		right.layout();

		long endTime = System.currentTimeMillis();
		System.out.println("监控组页面（" + monitorName + "）加载完成（耗时：" + (endTime - startTime) + " ms）...");

		progressDialog.close();
		return monitorContainer;
	}

	private void destroyRightAreas(GPTreeNode treeNode) {
		String monitorName = treeNode.getName();
		if (!rightAreaMap.containsKey(monitorName))
			return;
		
		MonitorContainer monitorContainer = rightAreaMap.get(monitorName);
		monitorContainer.dispose();
		rightAreaMap.remove(monitorName);
	}

	private void menuEvent_properties() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		final GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
		if (treeNode == null)
			return;
		
		MonitorPropertyDialog propertyDialog = new MonitorPropertyDialog(getShell(), (GPMonitorEntity) treeNode.getGPEntity(), treeNode.getChild() != null);
		if (Dialog.OK == propertyDialog.open()) {
			GPMonitorEntity modifiedEntity = propertyDialog.getFinalEntity();
			treeNode.setName(modifiedEntity.getMonitorName());
			treeNode.setGPEntity(modifiedEntity);
			tv.refresh(treeNode);
		}
	}

	private void menuEvent_delete() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		GPTreeNode treeNode = (GPTreeNode) selection.getFirstElement();
		if (treeNode == null || !MessageDialog.openQuestion(getShell(), "Confirm", "Are you sure to delete monitor \"" + treeNode.getName() + "\" ?"))
			return;

		SqlWhere where = new SqlWhere();
		where.addWhere("monitorName", "=", treeNode.getName());
		if (!SqliteDao.getInstance().deleteGPEntity(new SystemWarningEntity(), where)) {
			MessageDialog.openError(getShell(), "Error", "Delete monitor warn\"" + treeNode.getName() + "\" failed.");
			return;
		}
		if (!SqliteDao.getInstance().deleteGPEntity(new GPMonitorEntity(), where)) {
			MessageDialog.openError(getShell(), "Error", "Delete monitor \"" + treeNode.getName() + "\" failed.");
			return;
		}
		if (treeNode.getChild() != null) {
			treeNode.removeAllChildren();
			treeNode.setChild(null);
			destroyRightAreas(treeNode);
		}
		nodes.remove(treeNode);
		tv.setInput(nodes);
		listViewer.setInput(null);
		listViewer.refresh(true);
	}

	@Override
	protected void changeComposite(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		if (node == null)
			return;
		if (node.getCompositeCode() == null && node.getParent() == null) {
			listViewer.setInput(((GPMonitorEntity) node.getGPEntity()).getWarns());
			listViewer.refresh(true);
			return;
		}
		GPTreeNode parentNode = getTreeRootNode(node);
		MonitorContainer monitorContainer = rightAreaMap.get(parentNode.getName());
		StackLayout layout = (StackLayout) right.getLayout();
		layout.topControl = monitorContainer;
		right.layout();

		monitorContainer.switchTopComposite(node.getCompositeCode());
		listViewer.setInput(((GPMonitorEntity)parentNode.getGPEntity()).getWarns());
		listViewer.refresh(true);
	}

	public GPTreeNode getTreeRootNode(GPTreeNode node) {
		GPTreeNode parentNode = node.getParent();
		IGPEntity entity = parentNode.getGPEntity();
		if (entity == null)
			return getTreeRootNode(parentNode);
		return parentNode;
	}
}
