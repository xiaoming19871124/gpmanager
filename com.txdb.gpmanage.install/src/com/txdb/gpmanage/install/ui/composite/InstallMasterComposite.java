package com.txdb.gpmanage.install.ui.composite;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;

/**
 * master节点安装面板
 * 
 * @author ws
 *
 */
public class InstallMasterComposite extends IupperComposite {
	private ListViewer left;
	private ListViewer right;
	private ComboViewer hostList;
	private java.util.List<Host> allConfigHost;
	private boolean isFinish = false;

	public InstallMasterComposite(InstallComposite mainComposite, Composite parent, CompositeCode code) {
		super(mainComposite, parent, code, ResourceHandler.getValue("install.title"), ResourceHandler.getValue("install.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		// 整体布局
		GridLayout gd_mainComposite = new GridLayout();
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 20;
		composte.setLayout(gd_mainComposite);
		createSelectMasterComposite(composte);
		creatSelectComposite(composte);
		CreateOther(composte);
	}

	private void CreateOther(Composite mainComposite) {
		// 服务器参数配置面板
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gd_functionCom = new GridLayout(2, false);
		gd_functionCom.horizontalSpacing = 20;
		functionCom.setLayout(gd_functionCom);
		Label userLb = new Label(functionCom, SWT.NONE);
		userLb.setText(ResourceHandler.getValue("install.create.superuser"));
		userLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		Label label = new Label(functionCom, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		Label userNameLb = new Label(functionCom, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("userName"));
		userNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Text userNameText = new Text(functionCom, SWT.BORDER);
		GridData gd_userNmaeText = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_userNmaeText.widthHint = 180;
		userNameText.setLayoutData(gd_userNmaeText);

		Label pwdLb = new Label(functionCom, SWT.NONE);
		pwdLb.setText(ResourceHandler.getValue("password"));
		pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Text pwdText = new Text(functionCom, SWT.BORDER | SWT.PASSWORD);
		GridData gd_pwdText = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_pwdText.widthHint = 180;
		pwdText.setLayoutData(gd_pwdText);

		Label installPathLb = new Label(functionCom, SWT.NONE);
		installPathLb.setText(ResourceHandler.getValue("install.create.path"));
		installPathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Text installPathText = new Text(functionCom, SWT.BORDER);
		GridData gd_installPathText = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		installPathText.setText("/app/sgrdb/pg/mpp-1.0");
		// gd_installPathText.widthHint = 180;
		installPathText.setLayoutData(gd_installPathText);
		Label runLb = new Label(functionCom, SWT.NONE);
		runLb.setText(ResourceHandler.getValue("install.run"));
		runLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		final Button runBtn = new Button(functionCom, SWT.NONE);
		runBtn.setText(ResourceHandler.getValue("install.run"));
		GridData gd_runBtn = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		runBtn.setLayoutData(gd_runBtn);
		runBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean previous =	((InstallComposite) InstallMasterComposite.this.mianComposite).getConfigure().isFinish();
				if(!previous){
					text.append(ResourceHandler.getValue("installmaster.previous.error"));
					return;
				}
				runBtn.setEnabled(false);
				startBar();
				final InstallInfo info = ((InstallComposite) mianComposite).getInstallInfo();
				String installPath = installPathText.getText().trim();
				String userName = userNameText.getText().trim();
				String pwd = pwdText.getText().trim();
				StructuredSelection selection = (StructuredSelection) hostList.getSelection();
				if (selection == null || selection.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("installMaster.host.error"));
					runBtn.setEnabled(true);
					isFinish = false;
					stopBar();
					return;
				}
				if (info.getInstallHost().size() < 1) {
					getService().setErrorMsg(text, ResourceHandler.getValue("installMaster.segment.error"));
					runBtn.setEnabled(true);
					isFinish = false;
					stopBar();
					return;
				}
				if (userName == null || userName.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("installMaster.username.error"));
					runBtn.setEnabled(true);
					isFinish = false;
					stopBar();
					return;
				}
				if (pwd == null || pwd.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("installMaster.userpwd.error"));
					runBtn.setEnabled(true);
					isFinish = false;
					stopBar();
					return;
				}

				if (installPath == null || installPath.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("installMaster.installPath.error"));
					runBtn.setEnabled(true);
					isFinish = false;
					stopBar();
					return;
				}
				final Host masterHost = (Host) selection.getFirstElement();
				info.setInstallPath(installPath);
				info.setSuperUserName(userName);
				info.setSuperUserPassword(pwd);
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final boolean isSuccess = ((InstallUiService) getService()).insatallMaster(info, masterHost, text, InstallMasterComposite.this);
						isFinish=isSuccess;
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (isSuccess) {
									info.setMasterHost(masterHost);
									((InstallComposite) mianComposite).getInitfile().setMasterName(masterHost.getName());
								}
								runBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
	}

	/**
	 * 设置master节点
	 * 
	 * @param mainComposite
	 */
	private void createSelectMasterComposite(Composite mainComposite) {
		// 服务器参数配置面板
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gd_functionCom = new GridLayout(2, false);
		gd_functionCom.horizontalSpacing = 20;
		functionCom.setLayout(gd_functionCom);
		// "选择机器列表Label"
		Label configureLb = new Label(functionCom, SWT.NONE);
		configureLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		configureLb.setText(ResourceHandler.getValue("install.select.master"));
		hostList = new ComboViewer(functionCom, SWT.READ_ONLY);
		hostList.setLabelProvider(new LabelProvider());
		hostList.setContentProvider(new ArrayContentProvider());
		hostList.setInput(((InstallComposite) mianComposite).getInstallInfo().getConfHost());
		GridData gd_hostList = new GridData(180, SWT.DEFAULT);
		hostList.getCombo().setLayoutData(gd_hostList);
	}

	/**
	 * 选择机器列表面板
	 * 
	 * @param mainComposite
	 */
	private void creatSelectComposite(Composite mainComposite) {
		allConfigHost = new ArrayList<Host>();
		for (Host host : ((InstallComposite) mianComposite).getInstallInfo().getConfHost()) {
			allConfigHost.add(host);
		}
		// 选择机器列表面板
		Composite selectHost = new Composite(mainComposite, SWT.NONE);
		GridData gd_selectHost = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd_selectHost.heightHint = 130;
		selectHost.setLayoutData(gd_selectHost);
		GridLayout gl_selectHost = new GridLayout(2, false);
		gl_selectHost.horizontalSpacing = 20;
		selectHost.setLayout(gl_selectHost);
		// "选择机器列表Label"
		Label selectLb = new Label(selectHost, SWT.NONE);
		selectLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		selectLb.setText(ResourceHandler.getValue("hostConfigure.select.hosts"));
		// 机器列表选择面板
		Composite hostListComp = new Composite(selectHost, SWT.NONE);
		hostListComp.setLayout(new GridLayout(3, false));
		hostListComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		left = new ListViewer(hostListComp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.CENTER);
		// 机器列表list
		GridData gd_left = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_left.widthHint = 80;
		left.getList().setLayoutData(gd_left);
		left.setLabelProvider(new LabelProvider());
		left.setContentProvider(new ArrayContentProvider());
		left.setInput(allConfigHost);
		// 操作面板
		Composite anctionCom = new Composite(hostListComp, SWT.NONE);
		anctionCom.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		anctionCom.setLayout(new GridLayout(1, false));
		Button moveToRight = new Button(anctionCom, SWT.NONE);
		GridData gd_allBtn = new GridData(40, 20);
		moveToRight.setText(">");
		Button moveAllToRight = new Button(anctionCom, SWT.NONE);
		moveAllToRight.setText(">>");
		Button moveAllToLeft = new Button(anctionCom, SWT.NONE);
		moveAllToLeft.setText("<<");
		Button moveToLeft = new Button(anctionCom, SWT.NONE);
		moveToLeft.setText("<");
		moveToRight.setLayoutData(gd_allBtn);
		moveAllToRight.setLayoutData(gd_allBtn);
		moveAllToLeft.setLayoutData(gd_allBtn);
		moveToLeft.setLayoutData(gd_allBtn);
		// 选中的机器列表
		right = new ListViewer(hostListComp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd_right = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gd_right.widthHint = 80;
		right.getList().setLayoutData(gd_right);
		right.setLabelProvider(new LabelProvider());
		right.setContentProvider(new ArrayContentProvider());
		right.setInput(((InstallComposite) mianComposite).getInstallInfo().getInstallHost());
		SelectionAdapter selectionAdapter = creatBtnListener(left, right);
		// 为buttion添加监听
		moveToRight.addSelectionListener(selectionAdapter);
		moveAllToRight.addSelectionListener(selectionAdapter);
		moveAllToLeft.addSelectionListener(selectionAdapter);
		moveToLeft.addSelectionListener(selectionAdapter);
		// 为机器列表添加双击事件
		left.getList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				verifyValue(left.getSelection(), left, right);
			}

		});
		right.getList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				verifyValue(right.getSelection(), right, left);
			}

		});
	}

	/**
	 * 创建buttion选择监听
	 * 
	 * @param left
	 *            源列表
	 * @param right
	 *            目标列表
	 * @return 监听
	 */
	private SelectionAdapter creatBtnListener(final ListViewer left, final ListViewer right) {
		// 创建事件监听类，为内部类
		SelectionAdapter listener = new SelectionAdapter() {
			// 按钮单击事件处理的方法
			public void widgetSelected(SelectionEvent e) {
				// 取得触发事件的空间对象（按钮）
				Button bt = (Button) e.widget;
				if (bt.getText().equals(">")) { // 如果是“>”按钮
					verifyValue(left.getSelection(), left, right);
				} else if (bt.getText().equals(">>")) { // 如果是“>>”按钮
					left.setSelection(new StructuredSelection((java.util.List<Host>) left.getInput()));
					verifyValue(left.getSelection(), left, right);
				} else if (bt.getText().equals("<")) // 如果是“<”按钮
					verifyValue(right.getSelection(), right, left);
				else if (bt.getText().equals("<<")) { // 如果是“<<”按钮
					right.setSelection(new StructuredSelection((java.util.List<Host>) right.getInput()));
					verifyValue(right.getSelection(), right, left);
				}
			}
		};
		return listener;

	}

	/**
	 * 交换数据
	 * 
	 * @param selected
	 *            被选中的列
	 * @param from
	 *            源数据列表
	 * @param to
	 *            目标数据列表
	 */
	private void verifyValue(ISelection selected, ListViewer from, ListViewer to) {
		StructuredSelection selection = (StructuredSelection) selected;
		if (selection == null || selection.isEmpty())
			return;
		java.util.List<Host> selectionHost = selection.toList();
		for (Host host : selectionHost) {
			((java.util.List<Host>) from.getInput()).remove(host);
			((java.util.List<Host>) to.getInput()).add(host);
		}
		from.refresh();
		to.refresh();
		isFinish=false;
		((InstallComposite) mianComposite).getSsh().setFinish(false);
		((InstallComposite) mianComposite).getInstallSegment().setFinish(false);
		((InstallComposite) mianComposite).getInitfile().setFinish(false);
		updataInstallHost();
	}

	private void updataInstallHost() {
		((InstallComposite) mianComposite).getSsh().updateTable();
		((InstallComposite) mianComposite).getInstallSegment().updateTable();
		((InstallComposite) mianComposite).getInstanceInstall().updateTable();
	}

	public void updateHosts(java.util.List<Host> hosts, String operate) {
		if (operate.equals("+")) {
			addAllHost(hosts);
		} else if (operate.equals("-")) {
			for (Host host : hosts) {
				removeHost(host);
				if (((InstallComposite) mianComposite).getInstallInfo().getInstallHost().contains(host)){
					((InstallComposite) mianComposite).getInstallInfo().getInstallHost().remove(host);
					isFinish=false;
					((InstallComposite) mianComposite).getSsh().setFinish(false);
					((InstallComposite) mianComposite).getInstallSegment().setFinish(false);
					((InstallComposite) mianComposite).getInitfile().setFinish(false);
				}
			}
		}
		left.refresh();
		right.refresh();
		hostList.refresh();
		updataInstallHost();

	}

	public void updateHosts(Host host, String operate) {
		if (operate.equals("-")) {
			removeHost(host);
			if (((InstallComposite) mianComposite).getInstallInfo().getInstallHost().contains(host))
				((InstallComposite) mianComposite).getInstallInfo().getInstallHost().remove(host);

		}
		left.refresh();
		right.refresh();
		hostList.refresh();
		updataInstallHost();

	}

	public void addHost(Host host) {
		allConfigHost.add(host);
	}

	public void addAllHost(java.util.List<Host> host) {
		allConfigHost.addAll(host);
	}

	public void removeHost(Host host) {
		if (allConfigHost.contains(host))
			allConfigHost.remove(host);
	}

	public boolean isFinish() {
		return isFinish;
	}

}
