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

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.HostRole;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class ChangeToStandbyDialog extends Dialog {
	private Host host;
	private Text ipText;
	private Text userNameText;
	private Text pwdText;
	// private Combo roleCombo;
	private Label errorLb;
	private IupperComposite extendMangeComposite;

	public Host getHost() {
		return host;
	}

	public ChangeToStandbyDialog(IupperComposite extendMangeComposite) {
		super(extendMangeComposite.getShell());
		this.extendMangeComposite = extendMangeComposite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (host == null)
			newShell.setText(ResourceHandler.getValue("add"));
		else
			newShell.setText(ResourceHandler.getValue("modify"));

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(2, false));
		// ip
		Label ipLb = new Label(top, SWT.NONE);
		ipLb.setText(ResourceHandler.getValue("hostMange.table.hostIp"));
		ipLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		ipText = new Text(top, SWT.BORDER);
		ipText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// 主机用户名
		Label userNameLb = new Label(top, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("hostMange.table.userName"));
		userNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		userNameText = new Text(top, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// 密码
		Label pwdLb = new Label(top, SWT.NONE);
		pwdLb.setText(ResourceHandler.getValue("password"));
		pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		pwdText = new Text(top, SWT.BORDER | SWT.PASSWORD);
		pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// 错误提示
		errorLb = new Label(top, SWT.RIGHT);

		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		if (host != null) {
			ipText.setText(host.getIp());
			userNameText.setText(host.getUserName());
			pwdText.setText(host.getPassword());
		}

		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("add"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			errorLb.setText(ResourceHandler.getValue("validateing"));
			String ip = ipText.getText();
			String userName = userNameText.getText();
			String pwd = pwdText.getText();
			HostRole role = HostRole.STANDBY;
			Host valifyHost = new Host();
			valifyHost.setIp(ip);
			valifyHost.setUserName(userName);
			valifyHost.setPassword(pwd);
			valifyHost.setRole(role);

			// 是否能连接上
			boolean isCurrect = extendMangeComposite.getService().verificationHost(valifyHost);
			if (!isCurrect) {
				errorLb.setText(ResourceHandler.getValue("err_host"));
				return;
			}
			// 是否是standby主机
			GPManagerEntity gp = ((ManageComposite) extendMangeComposite.getMianComposite()).getGp();
			boolean isStandby = extendMangeComposite.getService().isStandby(gp, valifyHost);
			if (!isStandby) {
				errorLb.setText(ResourceHandler.getValue("host.isStandby.error"));
				return;
			}
			host = valifyHost;
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}
		close();
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		return new Point(point.x + 100, point.y);// super.getInitialSize()可以得到原来对话框的大小
	}
}
