package com.txdb.gpmanage.manage.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.service.AbstractUIService;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

/**
 * 集群设置wizard page
 * 
 * @author ws
 *
 */
public class GPSetPage extends AbsAddGPWizardPage {
	private GPManagerEntity node;

	/**
	 * gp集群名称
	 */
	private Text gp_nameText;
	/**
	 * host
	 */
	private Text master_host_ipText;
	/**
	 * gp用户名输入框
	 */
	private Text gp_user_nameText;
	/**
	 * gp用户密码密码输入框
	 */
	private Text gp_user_pwdText;
	/**
	 * 数据库
	 */
	private Text gp_databaseText;
	/**
	 * gp端口
	 */
	private Text gp_portText;
	/**
	 * master主机root用户名
	 */
	private Text master_root_nameText;
	/**
	 * master主机root用户密码
	 */
	private Text master_root_pwdText;

	/**
	 * 是否连接gp超户成功
	 */
	private boolean isConnection;
	/**
	 * 是否连接root超户成功
	 */
	private boolean isRootConnection = true;

	protected GPSetPage(String pageName, String title, String message) {
		super(pageName, title, message);

	}

	@Override
	protected void createUI(Composite topComp) {
		node = ((AddGPWizard) this.getWizard()).getNode();
		topComp.setLayout(new GridLayout(1, false));
		ScrolledComposite scrolledComposite = new ScrolledComposite(topComp, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Composite mainComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(mainComposite);
		
		mainComposite.setLayout(new GridLayout(2, false));
		Label gp_nameLb = new Label(mainComposite, SWT.NONE);
		gp_nameLb.setText(ResourceHandler.getValue("gp_Name"));
		gp_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_nameText = new Text(mainComposite, SWT.BORDER);
		gp_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createGPSetUI(mainComposite);
		createRootUI(mainComposite);
		// 设置滚动条面板
		scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
//		Label gp_nameLb = new Label(topComp, SWT.NONE);
//		gp_nameLb.setText(ResourceHandler.getValue("gp_Name"));
//		gp_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
//		gp_nameText = new Text(topComp, SWT.BORDER);
//		gp_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		createGPSetUI(topComp);
//		createRootUI(topComp);
		setValue();
		this.setPageComplete(true);
	}

	private void setValue() {
		if (node.getGpName() != null && !node.getGpName().isEmpty()) {
			gp_nameText.setText(node.getGpName());
			master_host_ipText.setText(node.getMasterIp());
			gp_user_nameText.setText(node.getGpUserName());
			gp_user_pwdText.setText(node.getGpUserPwd());
			gp_portText.setText(node.getGpPort());
			gp_databaseText.setText(node.getGpdatabase());
			if (node.getRole().equals("D")) {
				master_root_nameText.setText(node.getMasterRootName());
				master_root_pwdText.setText(node.getMasterRootPwd());
				master_root_nameText.setEditable(true);
				master_root_pwdText.setEditable(true);
			}

		}
	}

	private void createGPSetUI(Composite topComp) {
		Group gpGroup = new Group(topComp, SWT.NONE);
		gpGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		gpGroup.setText(ResourceHandler.getValue("database_configure"));
		gpGroup.setLayout(new GridLayout(2, false));
		Label master_host_ipLb = new Label(gpGroup, SWT.NONE);
		master_host_ipLb.setText(ResourceHandler.getValue("master_host_ip"));
		master_host_ipLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_host_ipText = new Text(gpGroup, SWT.BORDER);
		master_host_ipText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// gp_user_name
		Label gp_user_nameLb = new Label(gpGroup, SWT.NONE);
		gp_user_nameLb.setText(ResourceHandler.getValue("gp_user_name"));
		gp_user_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_user_nameText = new Text(gpGroup, SWT.BORDER);
		gp_user_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label gp_user_pwdLb = new Label(gpGroup, SWT.NONE);
		gp_user_pwdLb.setText(ResourceHandler.getValue("gp_user_pwd"));
		gp_user_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_user_pwdText = new Text(gpGroup, SWT.BORDER | SWT.PASSWORD);
		gp_user_pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label gp_databaseLb = new Label(gpGroup, SWT.NONE);
		gp_databaseLb.setText(ResourceHandler.getValue("database"));
		gp_databaseLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_databaseText = new Text(gpGroup, SWT.BORDER );
		gp_databaseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label gp_portLb = new Label(gpGroup, SWT.NONE);
		gp_portLb.setText(ResourceHandler.getValue("port"));
		gp_portLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_portText = new Text(gpGroup, SWT.BORDER);
		gp_portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		gp_portText.addModifyListener(this);
		Button testBtn = new Button(gpGroup, SWT.NONE);
		testBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		testBtn.setText(ResourceHandler.getValue("test"));
//		testBtn.setVisible(false);
		if (node.getGpName() != null)
			this.setPageComplete(true);
		else
			this.setPageComplete(false);

		testBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isInputFinish = isInputFinish();
				if (!isInputFinish)
					return;
				ManageComposite manageComposite = ((AddGPWizard) GPSetPage.this.getWizard()).getManageComposite();
				final AbstractUIService service = manageComposite.getService();
				final String master_host = master_host_ipText.getText();
				final String name = gp_user_nameText.getText();
				final String port = gp_portText.getText();
				final String database = gp_databaseText.getText();
				final String pwd = gp_user_pwdText.getText();
				try {
					GPSetPage.this.getContainer().run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask(ResourceHandler.getValue("gp_set_test"), 2);
							// gp用户是否正确
							isConnection = service.connectionByJDBC(master_host, name, pwd, Integer.valueOf(port),database);
							if (!isConnection) {
								monitor.done();
								return;
							}

							monitor.worked(1);
							monitor.subTask(ResourceHandler.getValue("gpmanage_user_message"));
							// 获取User 角色
							String role = service.queryRole(master_host, name, pwd, Integer.valueOf(port),database);
							if (role.equals("D")) {
								isConnection = service.verificationHost(master_host, name, pwd);
								if (!isConnection) {
									monitor.done();
									return;
								}
							}
							monitor.worked(1);
							node.setRole(role);
							monitor.worked(1);
							monitor.done();
						}
					});
				} catch (InvocationTargetException | InterruptedException e1) {
					e1.printStackTrace();
				}
				if (!isConnection) {
					setMessage(ResourceHandler.getValue("gpmanage_gpuser_error"), ERROR);
				} else {
					if (node.getRole().equals("D")) {
						master_root_nameText.setEditable(true);
						master_root_pwdText.setEditable(true);
					} else {
						master_root_nameText.setEditable(false);
						master_root_pwdText.setEditable(false);
					}
				}

			}
		});

	}

	private void createRootUI(Composite topComp) {
		Group gpGroup = new Group(topComp, SWT.NONE);
		gpGroup.setText(ResourceHandler.getValue("host_root_configure"));
		gpGroup.setLayout(new GridLayout(2, false));
		gpGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// root_user_name
		Label gp_user_nameLb = new Label(gpGroup, SWT.NONE);
		gp_user_nameLb.setText(ResourceHandler.getValue("master_root_name"));
		gp_user_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_root_nameText = new Text(gpGroup, SWT.BORDER);
		master_root_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label gp_user_pwdLb = new Label(gpGroup, SWT.NONE);
		gp_user_pwdLb.setText(ResourceHandler.getValue("master_root_pwd"));
		gp_user_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_root_pwdText = new Text(gpGroup, SWT.BORDER | SWT.PASSWORD);
		master_root_pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		master_root_nameText.setEditable(false);
		master_root_pwdText.setEditable(false);

	}

	@Override
	public void modifyText(ModifyEvent e) {

		// if (isInputFinish()) {
		// setMessage("push finish", INFORMATION);
		// this.setPageComplete(true);
		// } else {
		// this.setPageComplete(false);
		// }
	}

	private boolean isInputFinish() {
		final String master_host = master_host_ipText.getText();
		final String name = gp_user_nameText.getText();
		final String pwd = gp_user_pwdText.getText();
		final String port = gp_portText.getText();
		final String database = gp_databaseText.getText();
		if (master_host == null || master_host.isEmpty()) {
			setMessage("gp_masterhost_error", ERROR);
			return false;
		}
//		if (master_host == null || master_host.isEmpty()) {
//			setMessage("gp master host is null", ERROR);
//			return false;
//		}
		if (name == null || name.isEmpty()) {
			setMessage(ResourceHandler.getValue("gp_userName_null"), ERROR);
			return false;
		}
		if (pwd == null || pwd.isEmpty()) {
			setMessage(ResourceHandler.getValue("gp_pwd_null"), ERROR);
			return false;
		}
		if (port == null || port.isEmpty()) {
			setMessage(ResourceHandler.getValue("gp_port_null"), ERROR);
			return false;
		}
		if (database == null || database.isEmpty()) {
			setMessage(ResourceHandler.getValue("database_is_null"), ERROR);
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(port);
		if (!isNum.matches()) {
			setMessage(ResourceHandler.getValue("gp_port_error"), ERROR);
			return false;
		}
		setMessage(ResourceHandler.getValue("gp_set_success"), INFORMATION);
		return true;
	}

	@Override
	protected boolean validateValue() {
		boolean isInputFinish = isInputFinish();
		if (!isInputFinish)
			return isInputFinish;
		final String gp_name = gp_nameText.getText();
		if (gp_name == null || gp_name.isEmpty()) {
			setMessage("gp_configureName_null", ERROR);
			return false;
		}
		List<GPTreeNode> gps = ((AddGPWizard) this.getWizard()).getManageComposite().getNodes();
		final GPManagerEntity node = ((AddGPWizard) this.getWizard()).getNode();
		for (GPTreeNode gp : gps) {
			if (gp.getName().equals(gp_name) && !gp.getGPEntity().equals(node)) {
				setMessage("gp_configureName_exist", ERROR);
				return false;
			}
		}
		final String master_host = master_host_ipText.getText();
		final String name = gp_user_nameText.getText();
		final String pwd = gp_user_pwdText.getText();
		final String port = gp_portText.getText();
		final String database = gp_databaseText.getText();
		final String root_user = master_root_nameText.getText();
		final String root_pwd = master_root_pwdText.getText();
		ManageComposite manageComposite = ((AddGPWizard) GPSetPage.this.getWizard()).getManageComposite();
		final AbstractUIService service = manageComposite.getService();
		try {
			GPSetPage.this.getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(ResourceHandler.getValue("gp_set_test"), 2);
					// gp用户是否正确
					isConnection = service.connectionByJDBC(master_host, name, pwd, Integer.valueOf(port),database);
					if (!isConnection) {
						monitor.done();
						return;
					}
					monitor.worked(1);
					monitor.subTask(ResourceHandler.getValue("gpmanage_user_message"));
					// 获取User 角色
					String role = service.queryRole(master_host, name, pwd, Integer.valueOf(port),database);
					if (role.equals("D")) {
						isConnection = service.verificationHost(master_host, name, pwd);
						if (!isConnection) {
							monitor.done();
							return;
						}
						if (root_user == null || root_user.isEmpty() || root_pwd == null || root_pwd.isEmpty()) {
							isRootConnection = false;
							monitor.done();
							return;
						}
						isRootConnection = service.verificationHost(master_host, root_user, root_pwd);
						if (!isRootConnection) {
							monitor.done();
							return;
						}
					}
					monitor.worked(1);
					node.setGpName(gp_name);
					node.setMasterIp(master_host);
					node.setGpUserName(name);
					node.setGpUserPwd(pwd);
					node.setGpPort(port);
					node.setGpdatabase(database);
					node.setMasterRootName(root_user);
					node.setMasterRootPwd(root_pwd);
					node.setRole(role);
					monitor.worked(1);
					monitor.done();
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			e1.printStackTrace();
		}
		if (!isConnection) {
			setMessage(ResourceHandler.getValue("gp_connection_error"), ERROR);
			return false;
		}
		if (!isRootConnection) {
			setMessage(ResourceHandler.getValue("gp_root_error"), ERROR);
			return false;
		}
		return true;
	}
}