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

import com.txdb.gpmanage.manage.entity.Compartment;
import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.entity.PolicyDbObject;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;

public class AddLevelOrCompartDialog extends Dialog {
	private Text name_text;
	private Text id_text;
	private Label errorLb;
	private int loc = 0;// 0:level;1:compartment
	private PolicyDbObject ob;

	public AddLevelOrCompartDialog(Shell parentShell, int loc) {
		super(parentShell);
		this.loc = loc;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue("add"));
	}
	protected boolean isResizable() {
		return true;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(3, false));
		Label gp_user_nameLb = new Label(top, SWT.NONE);
		gp_user_nameLb.setText("name");
		gp_user_nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		name_text = new Text(top, SWT.BORDER);
		name_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));

		Label gp_user_pwdLb = new Label(top, SWT.NONE);
		gp_user_pwdLb.setText("id");
		gp_user_pwdLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		id_text = new Text(top, SWT.BORDER );
		id_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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
			String name = name_text.getText();
			String id = id_text.getText();
			if (name == null || name.isEmpty()) {
				errorLb.setText("name can not null");
				return;
			}
			if (id == null || id.isEmpty()) {
				errorLb.setText("id can not null");
				return;
			}
			int idNum = 0;
			try {
				idNum = Integer.valueOf(id);
			} catch (NumberFormatException e) {
				errorLb.setText("id must number");
				return;
			}
			if (loc == 0) {
				ob = new Level(name, idNum);
			} else {
				ob = new Compartment(name, idNum);
			}
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}

		close();
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();

		return new Point(initialSize.x + 200, initialSize.y);// super.getInitialSize()可以得到原来对话框的大小
	}

	public PolicyDbObject getOb() {
		return ob;
	}
}
