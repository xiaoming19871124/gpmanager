package com.txdb.gpmanage.manage.ui.composite;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.common.UICommonUtil;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.ManageProgressMonitorDialog;
import com.txdb.gpmanage.manage.ui.listener.TreeMouseListener;
import com.txdb.gpmanage.manage.ui.wizard.AddGPWizard;
import com.txdb.gpmanage.manage.ui.wizard.AddGPWizardDialog;

public class ManageComposite extends AbstractComposite {
	/**
	 * 导航节点
	 */
	private List<GPTreeNode> nodes;
	private Menu popupMenu;
	public ManageComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void createmain() {
		service = new ManageUiService(Display.getDefault());
		StackLayout layout = new StackLayout();
		right.setLayout(layout);
	}

	/**
	 * 获取导航配置项
	 * 
	 * @return
	 */
	public GPManagerEntity getGp() {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		if (selection.isEmpty())
			return null;
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		while (node.getGPEntity() == null)
			node = node.getParent();
		return (GPManagerEntity) node.getGPEntity();
	}

	@Override
	protected void setInput() {
		nodes = new ArrayList<GPTreeNode>();
		List<IGPEntity> gpEntities = SqliteDao.getInstance().queryGPEntity(new GPManagerEntity(), null);
		for (IGPEntity gpNode : gpEntities) {
			CommonUtil.initNodes(nodes, (GPManagerEntity) gpNode);
		}
		tv.setInput(nodes);
		tv.getTree().addMouseListener(new TreeMouseListener(tv, this));
		createPopupMenu();
	}

	public void addContainerComposite(final GPTreeNode node) {
		ManageProgressMonitorDialog pmd = new ManageProgressMonitorDialog(null, ResourceHandler.getValue("gpmanage_menu_open"));
		final Display display = Display.getCurrent();
		final ManageUiService service = (ManageUiService) this.getService();
		IRunnableWithProgress rwp = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask(ResourceHandler.getValue("gpmanage_load"), 12);
				monitor.worked(1);
				monitor.subTask(ResourceHandler.getValue("gpmanage_master_test"));
				final Host host = new Host();
				GPManagerEntity gpNode = (GPManagerEntity) node.getGPEntity();
				String role = gpNode.getRole();

				if (role.equals("D")) {
					host.setIp(gpNode.getMasterIp());
					host.setUserName(gpNode.getMasterRootName());
					host.setPassword(gpNode.getMasterRootPwd());
					boolean isMaster = service.verificationHost(host);
					if (!isMaster) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_master_error"));
						monitor.done();
						return;
					}
					gpNode.setMasterHostName(host.getName());
					// gp用户是否正确
					boolean isConnection = service.verificationHost(gpNode.getMasterIp(), gpNode.getGpUserName(), gpNode.getGpUserPwd());
					if (!isConnection) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_gpuser_error"));
						monitor.done();
						return;
					}
					monitor.worked(1);
					// gp是否存在或者是否启动
					monitor.subTask(ResourceHandler.getValue("gpmanage_gp_check"));
					String gpHome = service.getGpHome(gpNode.getMasterIp(), gpNode.getGpUserName(), gpNode.getGpUserPwd());
					if (gpHome == null || gpHome.isEmpty()) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_gpcheck_error"));
						monitor.done();
						return;
					}
					gpHome = gpHome.substring(0, gpHome.lastIndexOf("/") + 1);
					gpNode.setInstallPath(gpHome);
					monitor.worked(1);

					monitor.subTask(ResourceHandler.getValue("gpmanage_database_connection"));
					boolean isConnectionByJDBC = service.connectionByJDBC(gpNode.getMasterIp(), gpNode.getGpUserName(), gpNode.getGpUserPwd(), Integer.valueOf(gpNode.getGpPort()),
							gpNode.getGpdatabase());
					if (!isConnectionByJDBC) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_connect_error"));
						monitor.done();
						return;
					}
					monitor.worked(1);

					// 获取gp集群信息
					monitor.subTask(ResourceHandler.getValue("gpmanage_get_configure"));
					List<GPSegmentInfo> info = service.queryAllSegmentStatus(gpNode);
					if (info == null || info.size() < 1) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_getconfigure_error"));
						monitor.done();
						return;
					}
					monitor.worked(1);
					// 获取master数据目录
					monitor.subTask(ResourceHandler.getValue("gpmanage_master_configure"));
					GPSegmentInfo master = service.queryMaster(info);
					gpNode.setGpPort(String.valueOf(master.getPort()));
					String masterDataDir = master.getDatadir().substring(0, master.getDatadir().lastIndexOf("/"));
					String dataDir = masterDataDir.substring(0, masterDataDir.lastIndexOf("/") + 1);
					gpNode.setMasterDataDir(masterDataDir);
					gpNode.setDatadir(dataDir);
					monitor.worked(1);
					// 是否有standby
					monitor.subTask(ResourceHandler.getValue("gpmanage_standby_configure"));
					GPSegmentInfo standby = service.queryStandby(info);
					if (standby == null) {
						gpNode.setHasStandby(0);
					} else {
						gpNode.setHasStandby(1);
					}
					monitor.worked(1);
					// 获取segment数据目录
					monitor.subTask(ResourceHandler.getValue("gpmanage_seg_configure"));
					GPSegmentInfo segment = service.querySegment(info);
					String segmentDataDir = segment.getDatadir().substring(0, segment.getDatadir().lastIndexOf("/") + 1);
					gpNode.setSegmentDataDir(segmentDataDir);
					monitor.worked(1);
					// 获取mirror数据目录
					monitor.subTask(ResourceHandler.getValue("gpmanage_mirror_configure"));
					GPSegmentInfo mirror = service.queryMirror(info);
					if (mirror == null) {
						gpNode.setHasMirror(0);
					} else {
						gpNode.setHasMirror(1);
						String mirrorDataDir = mirror.getDatadir().substring(0, mirror.getDatadir().lastIndexOf("/") + 1);
						gpNode.setMirrorDataDir(mirrorDataDir);
					}
					monitor.worked(1);
					monitor.subTask(ResourceHandler.getValue("gpmanage_save_configure"));
					SqlWhere where = new SqlWhere();
					where.addWhere("gpName", "=", gpNode.getGpName());
					boolean isSave = SqliteDao.getInstance().updateGPEntity(gpNode, where);
					if (!isSave) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_save_erro"));
						monitor.done();
						return;
					}
				} else {
					boolean isConnectionByJDBC = service.connectionByJDBC(gpNode.getMasterIp(), gpNode.getGpUserName(), gpNode.getGpUserPwd(), Integer.valueOf(gpNode.getGpPort()),
							gpNode.getGpdatabase());
					if (!isConnectionByJDBC) {
						UICommonUtil.openErrorMsgDlg(display, ResourceHandler.getValue("gpmanage_connect_error"));
						monitor.done();
						return;
					}
					monitor.worked(1);
					monitor.subTask(ResourceHandler.getValue("gpmanage_user_message"));
				}

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						ContainerComposite composite = new ContainerComposite(ManageComposite.this, right, node, SWT.NONE);
						((StackLayout) right.getLayout()).topControl = composite;
						composite.updateAllValue();
						CommonUtil.createChildren(node);
						right.layout();
						tv.refresh();
					}
				});
				monitor.done();
			}
		};
		try {
			pmd.run(true, false, rwp);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 销毁右侧功能区
	 */
	private void disposeComposite(GPTreeNode node) {
		Control[] child = right.getChildren();
		if (child == null)
			return;
		for (Control children : child) {
			if (((ContainerComposite) children).getNode().equals(node)) {
				children.dispose();
				StackLayout layout = (StackLayout) right.getLayout();
				if (layout.topControl == children) {
					layout.topControl = null;
					right.layout();
				}
				return;
			}
		}
	}

	@Override
	protected void changeComposite(SelectionChangedEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		// 未选中节点
		if (node == null)
			return;
		// 选中顶级节点
		if (node.getCompositeCode() == null && node.getParent() == null) {
			// 如果没有打开过
			if (node.getChild() == null || node.getChild().size() < 1) {
				return;
			}
			StackLayout layout = (StackLayout) right.getLayout();
			Control[] child = right.getChildren();
			for (Control children : child) {
				if (((ContainerComposite) children).getNode().equals(node)) {
					((ContainerComposite) children).setTopComposite(node);
					if (layout.topControl != children) {
						layout.topControl = children;
						right.layout();
						break;
					}
				}
			}
			return;
		}
		// 选中二级节点
		if (node.getCompositeCode() == null && node.getParent() != null) {
			tv.expandToLevel(node, 1);
			tv.setSelection(new StructuredSelection(new Object[] { node.getChild().get(0) }));
			return;
		}
		// 选中三级节点
		Control[] child = right.getChildren();
		StackLayout topLayout = (StackLayout) right.getLayout();
		GPTreeNode topNode = node.getParent().getParent();
		ContainerComposite conComposite = null;
		for (Control children : child) {
			if (((ContainerComposite) children).getNode().equals(topNode)) {
				conComposite = (ContainerComposite) children;
				if (topLayout.topControl != children) {
					topLayout.topControl = children;
					right.layout();
					break;
				}
			}
		}
		conComposite.setTopComposite(node);
	}

	public void setSelection(CompositeCode code) {
		tv.setSelection(new StructuredSelection(new Object[] { queryNodeByCompositeCode(code) }));
	}

	public void createPopupMenu() {
		popupMenu = tv.getTree().getMenu();
		if (popupMenu != null && !popupMenu.isDisposed()) {
			popupMenu.dispose();
		}
		popupMenu = new Menu(tv.getTree());
		//新建
		MenuItem addGpItem = new MenuItem(popupMenu, SWT.PUSH);
		addGpItem.setText(ResourceHandler.getValue("gpmanage_menu_add"));
		// 打开
		MenuItem openGpItem = new MenuItem(popupMenu, SWT.PUSH);
		openGpItem.setText(ResourceHandler.getValue("gpmanage_menu_open"));
		// 关闭
		MenuItem closedGpItem = new MenuItem(popupMenu, SWT.PUSH);
		closedGpItem.setText(ResourceHandler.getValue("gpmanage_menu_closed"));
		//修改
		MenuItem modifyGpItem = new MenuItem(popupMenu, SWT.PUSH);
		modifyGpItem.setText(ResourceHandler.getValue("modify"));
		// 删除
		MenuItem delGpItem = new MenuItem(popupMenu, SWT.PUSH);
		delGpItem.setText(ResourceHandler.getValue("gpmanage_menu_del"));
		openGpItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeSelection selection = (TreeSelection) tv.getSelection();
				GPTreeNode node = (GPTreeNode) selection.getFirstElement();
				node.setConnection(true);
				tv.refresh();
				addContainerComposite(node);
			}
		});
		closedGpItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeSelection selection = (TreeSelection) tv.getSelection();
				GPTreeNode node = (GPTreeNode) selection.getFirstElement();
				node.removeAllChildren();
				tv.refresh();
				disposeComposite(node);
			}
		});
		addGpItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GPManagerEntity gpNode = new GPManagerEntity();
				AddGPWizard addGPWizard = new AddGPWizard(ManageComposite.this, gpNode);
				AddGPWizardDialog addgpDialog = new AddGPWizardDialog(getShell(), addGPWizard);
				int openId = addgpDialog.open();
				if (openId == Dialog.OK) {
					SqliteDao.getInstance().insertGPEntity(gpNode);
					CommonUtil.initNodes(nodes, gpNode);
					tv.refresh();
				}
			}
		});
		delGpItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeSelection selection = (TreeSelection) tv.getSelection();
				GPTreeNode node = (GPTreeNode) selection.getFirstElement();
				while (node.getParent() != null) {
					node = node.getParent();
				}
				SqlWhere where = new SqlWhere();
				where.addWhere("gpName", "=", node.getName());
				SqliteDao.getInstance().deleteGPEntity(new GPManagerEntity(), where);
				nodes.remove(node);
				tv.refresh();
				disposeComposite(node);
			}
		});
		modifyGpItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeSelection selection = (TreeSelection) tv.getSelection();
				GPTreeNode node = (GPTreeNode) selection.getFirstElement();
				while (node.getParent() != null) {
					node = node.getParent();
				}
				GPManagerEntity gpNode = (GPManagerEntity) node.getGPEntity();
				String oldName = gpNode.getGpName();
				AddGPWizard addGPWizard = new AddGPWizard(ManageComposite.this, gpNode);
				AddGPWizardDialog addgpDialog = new AddGPWizardDialog(getShell(), addGPWizard);
				int openId = addgpDialog.open();
				if (openId == Dialog.OK) {
					SqlWhere where = new SqlWhere();
					where.addWhere("gpName", "=", oldName);
					SqliteDao.getInstance().updateGPEntity(gpNode, where);
					node.setName(gpNode.getGpName());
					tv.refresh();
					Control[] children = right.getChildren();
					for (Control child : children) {
						if (((ContainerComposite) child).getNode() == node) {
							((ContainerComposite) child).updateAllValue();
						}
					}
				}
			}
		});
		tv.getTree().setMenu(popupMenu);
	}

	public void createTopClosedNodeMenu() {
		popupMenu.getItem(0).setEnabled(true);
		popupMenu.getItem(1).setEnabled(false);
		popupMenu.getItem(2).setEnabled(true);
		popupMenu.getItem(3).setEnabled(false);
		popupMenu.getItem(4).setEnabled(true);
	}

	public void createNoNodeMenu() {
		popupMenu.getItem(0).setEnabled(true);
		popupMenu.getItem(1).setEnabled(false);
		popupMenu.getItem(2).setEnabled(false);
		popupMenu.getItem(3).setEnabled(false);
		popupMenu.getItem(4).setEnabled(false);
	}

	private GPTreeNode queryNodeByCompositeCode(CompositeCode code) {
		TreeSelection selection = (TreeSelection) tv.getSelection();
		GPTreeNode node = (GPTreeNode) selection.getFirstElement();
		while (node.getParent() != null) {
			node = node.getParent();
		}
		List<GPTreeNode> childNodes = node.getChild();
		for (GPTreeNode childNode : childNodes) {
			List<GPTreeNode> nodes = childNode.getChild();
			for (GPTreeNode result : nodes) {
				if (result.getCompositeCode() == code)
					return result;
			}
		}
		return null;
	}

	public List<GPTreeNode> getNodes() {
		return nodes;
	}

	public void createTopOpenNodeMenu() {
		popupMenu.getItem(0).setEnabled(true);
		popupMenu.getItem(1).setEnabled(true);
		popupMenu.getItem(2).setEnabled(false);
		popupMenu.getItem(3).setEnabled(true);
		popupMenu.getItem(4).setEnabled(true);
	}

	public void disableAllItem() {
		popupMenu.getItem(1).setEnabled(false);
		popupMenu.getItem(2).setEnabled(false);
		popupMenu.getItem(3).setEnabled(false);
		popupMenu.getItem(4).setEnabled(false);
		popupMenu.getItem(0).setEnabled(false);
	}
}
