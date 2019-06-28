package com.txdb.gpmanage.manage.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class AuthorityDialog extends Dialog {
	private PGHbaInfo host;
	private Combo typeCombo;
	private Combo databaseCombo;
	private Text userNameText;
	private Text addressText;
	private Combo methodCombo;
	private Label errorLb;
	private String[] types = new String[] { PGHbaInfo.TYPE_LOCAL, PGHbaInfo.TYPE_HOST, PGHbaInfo.TYPE_HOSTNOSSL, PGHbaInfo.TYPE_HOSTSSL };
	private String[] method = new String[] { PGHbaInfo.METHOD_TRUST, PGHbaInfo.METHOD_PASSWORD, PGHbaInfo.METHOD_MD5, PGHbaInfo.METHOD_CERT, PGHbaInfo.METHOD_GSS, PGHbaInfo.METHOD_LDAP, PGHbaInfo.METHOD_PAM, PGHbaInfo.METHOD_PEER,
			PGHbaInfo.METHOD_RADIUS, PGHbaInfo.METHOD_REJECT, PGHbaInfo.METHOD_SSPI };
	private String[] database = new String[] { PGHbaInfo.DATABASE_ALL, PGHbaInfo.DATABASE_REPLICATION, PGHbaInfo.DATABASE_SAMEGROUP, PGHbaInfo.DATABASE_SAMEUSER };

	public PGHbaInfo getHost() {
		return host;
	}

	public AuthorityDialog(Shell parentShell, PGHbaInfo host) {
		super(parentShell);
		this.host = host;
		if (host != null)
			host.setModifyType(PGHbaInfo.MODIFY_RM);
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
		// 连接方式
		Label nameLb = new Label(top, SWT.NONE);
		nameLb.setText(ResourceHandler.getValue("connection_type"));
		typeCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
		typeCombo.setItems(types);
		typeCombo.setText(PGHbaInfo.TYPE_LOCAL);
		nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (host != null) {
			typeCombo.setText(host.getType());
		}
		// 数据库
		Label databaseLb = new Label(top, SWT.NONE);
		databaseLb.setText(ResourceHandler.getValue("database"));
		databaseLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		databaseCombo = new Combo(top, SWT.NONE);
		databaseCombo.setItems(database);
		databaseCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (host != null) {
			databaseCombo.setText(host.getDatabaseArrayString());
		}
		// 用户名
		Label userNameLb = new Label(top, SWT.NONE);
		userNameLb.setText(ResourceHandler.getValue("user_name"));
		userNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		userNameText = new Text(top, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (host != null) {
			userNameText.setText(host.getUserArrayString());
		}
		// 主机地址
		Label addressLb = new Label(top, SWT.NONE);
		addressLb.setText(ResourceHandler.getValue("ip"));
		addressLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		addressText = new Text(top, SWT.BORDER);
		addressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (host != null) {
			addressText.setText(host.getAddress()==null?"":host.getAddress());
		}
		// 认证方式
		Label methodLb = new Label(top, SWT.NONE);
		methodLb.setText(ResourceHandler.getValue("connection_method"));
		methodLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		methodCombo = new Combo(top, SWT.BORDER | SWT.READ_ONLY);
		methodCombo.setItems(method);
		methodCombo.setText(PGHbaInfo.METHOD_TRUST);
		methodCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (host != null) {
			methodCombo.setText(host.getMethod());
		}
		// 错误提示
		errorLb = new Label(top, SWT.RIGHT);

		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

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
			String type = typeCombo.getText();
			String database = databaseCombo.getText();
			String userName = userNameText.getText();
			String address = addressText.getText();
			String method = methodCombo.getText();

			if (database == null || database.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("database_is_null"));
				return;
			}
			if (userName == null || userName.isEmpty()) {
				errorLb.setText(ResourceHandler.getValue("err_username_null"));
				return;
			}

			host = new PGHbaInfo();
			host.setType(type);
			host.setDatabaseArray(database.split(","));
			host.setUserArray(userName.split(","));
			host.setAddress(address);
			host.setMethod(method);
			host.setModifyType(PGHbaInfo.MODIFY_ADD);
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

	public static void main(String[] args) {
		String a = "a,";
		String[] as = a.split(",");
		for (String s : as) {
			System.out.println(s);
		}
	}
}
