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

import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

public class AddPolicyDialog extends Dialog {
	private Text gp_user_nameText;
	private Text gp_user_pwdText;
	Button isHide;
	private Policy policy = null;
	private Label errorLb;

	public AddPolicyDialog(Shell parentShell) {
		super(parentShell);
	}
	protected boolean isResizable() {
		return true;
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue("add"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(3, false));
		Label gp_user_nameLb = new Label(top, SWT.NONE);
		gp_user_nameLb.setText("policy name");
		gp_user_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		gp_user_nameText = new Text(top, SWT.BORDER);
		gp_user_nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		Label gp_user_pwdLb = new Label(top, SWT.NONE);
		gp_user_pwdLb.setText("column name");
		gp_user_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		gp_user_pwdText = new Text(top, SWT.BORDER );
		gp_user_pwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		isHide = new Button(top, SWT.CHECK);
		isHide.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		isHide.setText("HIDE");
		errorLb = new Label(top, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				3, 1));
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
			errorLb.setText("add.....");
			// String gp_name = gp_Nameeer_host_nameText.getText();
			String gp_user_name = gp_user_nameText.getText();
			String gp_user_pwd = gp_user_pwdText.getText();
			if (gp_user_name == null || gp_user_name.isEmpty()) {
				errorLb.setText("policy can not null");
				return;
			}
			if (gp_user_pwd == null || gp_user_pwd.isEmpty()) {
				errorLb.setText("column can no null");
				return;
			}
			policy = new Policy();
			policy.setName(gp_user_name);
			policy.setColumn(gp_user_pwd);
			policy.setHide(isHide.getSelection());
			policy.setEnable(true);
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}

		close();
	}

	public Policy getPolicy() {
		return policy;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();

		return new Point(initialSize.x + 200, initialSize.y);// super.getInitialSize()可以得到原来对话框的大小
	}
}
