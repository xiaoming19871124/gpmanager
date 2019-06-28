package com.txdb.gpmanage.manage.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

public class AddGpDialog extends Dialog {
	private GPManagerEntity gp;
	ManageComposite manageComposite;
	private Text master_host_ipText;
	private Text master_root_pwdText;
	private Text master_root_nameText;
	private Text gp_user_nameText;
	private Text gp_user_pwdText;
	private Text gp_install_pathtText;
	private Text gp_dataDirText;
	private Label errorLb;

	public AddGpDialog(ManageComposite manageComposite) {
		super(manageComposite.getShell());
		this.manageComposite = manageComposite;
	}

	public AddGpDialog(ManageComposite manageComposite, GPManagerEntity gp) {
		super(manageComposite.getShell());
		this.manageComposite = manageComposite;
		this.gp = gp;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (gp == null) {
			newShell.setText(ResourceHandler.getValue("add"));
		} else {
			newShell.setText(ResourceHandler.getValue("modify"));
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(2, false));
		Label master_host_ipLb = new Label(top, SWT.NONE);
		master_host_ipLb.setText(ResourceHandler.getValue("master_host_ip"));
		master_host_ipLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_host_ipText = new Text(top, SWT.BORDER);
		master_host_ipText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// master_root_name
		Label master_root_nameLb = new Label(top, SWT.NONE);
		master_root_nameLb.setText(ResourceHandler.getValue("master_root_name"));
		master_root_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_root_nameText = new Text(top, SWT.BORDER);
		master_root_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// master_root_pwd
		Label master_root_pwdLb = new Label(top, SWT.NONE);
		master_root_pwdLb.setText(ResourceHandler.getValue("master_root_pwd"));
		master_root_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		master_root_pwdText = new Text(top, SWT.BORDER | SWT.PASSWORD);
		master_root_pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// gp_user_name

		Label gp_user_nameLb = new Label(top, SWT.NONE);
		gp_user_nameLb.setText(ResourceHandler.getValue("gp_user_name"));
		gp_user_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_user_nameText = new Text(top, SWT.BORDER);
		gp_user_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// gp_user_pwd

		Label gp_user_pwdLb = new Label(top, SWT.NONE);
		gp_user_pwdLb.setText(ResourceHandler.getValue("gp_user_pwd"));
		gp_user_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_user_pwdText = new Text(top, SWT.BORDER | SWT.PASSWORD);
		gp_user_pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// gp_port

		Label gp_portLb = new Label(top, SWT.NONE);
		gp_portLb.setText(ResourceHandler.getValue("initfile.path.data"));
		gp_portLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_dataDirText = new Text(top, SWT.BORDER);
		gp_dataDirText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// gp_install_path

		Label gp_install_pathLb = new Label(top, SWT.NONE);
		gp_install_pathLb.setText(ResourceHandler.getValue("gp_install_path"));
		gp_install_pathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		gp_install_pathtText = new Text(top, SWT.BORDER);
		gp_install_pathtText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		errorLb = new Label(top, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		initValus();
		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorLb.setText(ResourceHandler.getValue("validateing"));
			// String gp_name = gp_Nameeer_host_nameText.getText();
			String master_host_ip = master_host_ipText.getText();
			String master_root_pwd = master_root_pwdText.getText();
			String master_root_name = master_root_nameText.getText();
			String gp_user_name = gp_user_nameText.getText();
			String gp_user_pwd = gp_user_pwdText.getText();
			String gp_install_path = gp_install_pathtText.getText();
			String gp_dataDir = gp_dataDirText.getText();
			if (gp_install_path == null || gp_install_path.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("path.install.error"));
				return;
			}
			if (gp_dataDir == null || gp_dataDir.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("path.datadir.error"));
				return;
			}
			Host host = new Host();
			host.setIp(master_host_ip);
			host.setUserName(master_root_name);
			host.setPassword(master_root_pwd);

			boolean isRootRight = manageComposite.getService().verificationHost(host);
			if (!isRootRight) {
				errorLb.setText(ResourceHandler.getValue("gp.root.error"));
				return;
			}
			host.setUserName(gp_user_name);
			host.setPassword(gp_user_pwd);
			boolean isGPUserRight = manageComposite.getService().verificationHost(host);
			if (!isGPUserRight) {
				errorLb.setText(ResourceHandler.getValue("gp.gpuser.error"));
				return;
			}
			if (gp == null) {
				gp = new GPManagerEntity();
				gp.setGpName(host.getName());
			}
			gp.setMasterHostName(host.getName());
			gp.setMasterIp(master_host_ip);
			gp.setMasterRootName(master_root_name);
			gp.setMasterRootPwd(master_root_pwd);
			gp.setDatadir(gp_dataDir);
			gp.setInstallPath(gp_install_path);
			gp.setGpUserName(gp_user_name);
			gp.setGpUserPwd(gp_user_pwd);
//			errorLb.setText("获取集群信息");
//			boolean isHavMirror = manageComposite.getService().isHavMirror(gp);
//			gp.setHasMirror(isHavMirror ? 1 : 0);
//		boolean ishavStandby =	manageComposite.getService().getStandby(gp);
//		if(!ishavStandby){
			gp.setStandbyHostName("");
			gp.setStandbyIp("");
			gp.setStandbyRootName("");
			gp.setStandbyRootPwd("");
//		}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}

		close();
	}

	private void initValus() {
		if (this.gp == null)
			return;
		master_host_ipText.setText(gp.getMasterIp());
		master_root_pwdText.setText(gp.getMasterRootPwd());
		master_root_nameText.setText(gp.getMasterRootName());
		gp_user_nameText.setText(gp.getGpUserName());
		gp_user_pwdText.setText(gp.getGpUserPwd());
		gp_install_pathtText.setText(gp.getInstallPath());
		gp_dataDirText.setText(gp.getDatadir());
	}

	public GPManagerEntity getGp() {
		return gp;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();

		return new Point(initialSize.x + 200, initialSize.y);// super.getInitialSize()可以得到原来对话框的大小
	}
}
