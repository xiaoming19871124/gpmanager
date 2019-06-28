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

import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class AddUserDialog extends Dialog {
	private DbUser user;
	private Text userNameText;
	private Text pwdText;
	private Label errorLb;

	public DbUser getUser() {
		return user;
	}

	public AddUserDialog(Shell parentShell, DbUser user) {
		super(parentShell);
		this.user = user;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (user == null)
			newShell.setText(ResourceHandler.getValue("add"));
		else
			newShell.setText(ResourceHandler.getValue("modify"));
	}
	protected boolean isResizable() {
		return true;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(2, false));
		// 用户名
		Label userNameLb = new Label(top, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("userName"));
		userNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		userNameText = new Text(top, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		if (user != null) {
			userNameText.setEnabled(false);
			userNameText.setText(user.getUserName());
		}
		// 密码
		Label pwdLb = new Label(top, SWT.NONE);
		pwdLb.setText(ResourceHandler.getValue("password"));
		pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		pwdText = new Text(top, SWT.BORDER|SWT.PASSWORD);
		pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (user != null) {
			pwdText.setText(user.getUserPwd());
		}
		// 错误提示
		errorLb = new Label(top, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		errorLb.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_RED));
		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID,
				ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID,
				ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			String userName = userNameText.getText();
			String pwd = pwdText.getText();
			if (userName == null || userName.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("err_username_null"));
				return;
			}
			if (user == null)
				user = new DbUser();
			user.setUserName(userName);
			user.setUserPwd(pwd);
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
