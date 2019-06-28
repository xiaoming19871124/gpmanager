package com.txdb.gpmanage.monitor.ui.dialog;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.impl.MailEntity;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;

public class MailDialog extends Dialog {

	private MailEntity mailEntity;
	/** 发件人账户地址 */
	private Text senderAddress_Text;
	/** 发件人账户密码 */
	private Text senderPassword_Text;
	/** 发信名称 */
	private Text sendName_Text;
	/** 发件人账户服务器地址 */
	private Text smtpHost_Text;
	/** 设置是否使用ssl安全连接 */
	private Button ssl_btn;
	/** port */
	private Text port_Text;
	/** error message */
	private Label errorLb;
	private static int SSL_DEFUALT_PORT = 465;
	private static int SSL_NO_PORT = 25;

	public MailDialog(Shell parentShell, MailEntity mailEntity) {
		super(parentShell);
		this.mailEntity = mailEntity;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_TITLE));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout_parent = (GridLayout) parent.getLayout();
		layout_parent.marginWidth = 10;
		layout_parent.verticalSpacing = 10;
		createMail(parent);
		createServer(parent);

		errorLb = new Label(parent, SWT.RIGHT);
		errorLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		errorLb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		setValue();
		return parent;
	}

	private void createMail(Composite parent) {
		Group mail = new Group(parent, SWT.NONE);
		mail.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_MAIL));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		mail.setLayout(layout);
		mail.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label senderAddress_Label = new Label(mail, SWT.NONE);
		senderAddress_Label.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_MAIL_ADDRESS));
		senderAddress_Label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		senderAddress_Text = new Text(mail, SWT.BORDER);
		senderAddress_Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label senderPassword_Label = new Label(mail, SWT.NONE);
		senderPassword_Label.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_MAIL_PASSWORD));
		senderPassword_Label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		senderPassword_Text = new Text(mail, SWT.BORDER | SWT.PASSWORD);
		senderPassword_Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label sendName_Label = new Label(mail, SWT.NONE);
		sendName_Label.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_MAIL_NAME));
		sendName_Label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		sendName_Text = new Text(mail, SWT.BORDER);
		sendName_Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private void createServer(Composite parent) {
		Group server = new Group(parent, SWT.NONE);
		server.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_SERVER));
		GridLayout layout = new GridLayout(5, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		server.setLayout(layout);
		server.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label server_host = new Label(server, SWT.NONE);
		server_host.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_SERVER_ADDRESS));
		server_host.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		smtpHost_Text = new Text(server, SWT.BORDER);
		smtpHost_Text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ssl_btn = new Button(server, SWT.CHECK);
		ssl_btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		ssl_btn.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_SERVER_SSL));

		Label port_Label = new Label(server, SWT.NONE);
		port_Label.setText(ResourceHandler.getValue(MessageConstants.WARNING_MAIL_GROUP_SERVER_PORT));
		port_Label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		port_Text = new Text(server, SWT.BORDER);
		GridData gd_port = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd_port.widthHint = 50;
		port_Text.setLayoutData(gd_port);
		ssl_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				port_Text.setText(ssl_btn.getSelection() ? String.valueOf(SSL_DEFUALT_PORT) : String.valueOf(SSL_NO_PORT));
			}
		});
	}

	private void setValue() {
		senderAddress_Text.setText(mailEntity.getSenderAddress() == null ? "" : mailEntity.getSenderAddress());
		senderPassword_Text.setText(mailEntity.getSenderPassword() == null ? "" : mailEntity.getSenderPassword());
		sendName_Text.setText(mailEntity.getSendName() == null ? "" : mailEntity.getSendName());
		smtpHost_Text.setText(mailEntity.getStmpHost() == null ? "" : mailEntity.getStmpHost());
		ssl_btn.setSelection(mailEntity.getSsl() == 1);
		port_Text.setText(String.valueOf(mailEntity.getPort()));
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar(); // 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			String senderAddress = senderAddress_Text.getText();
			if (senderAddress == null || senderAddress.isEmpty()) {
				errorLb.setText("Mail Address cannot be empty!");
				return;
			}
			String senderPassword = senderPassword_Text.getText();
			if (senderPassword == null || senderPassword.isEmpty()) {
				errorLb.setText("Password cannot be empty!");
				return;
			}
			String sendName = sendName_Text.getText();
			String smtpHost = smtpHost_Text.getText();
			if (smtpHost == null || smtpHost.isEmpty()) {
				errorLb.setText("SMTP Server cannot be empty!");
				return;
			}
			int ssl = ssl_btn.getSelection() ? 1 : 0;
			String port = port_Text.getText();
			try {
				Integer.valueOf(port);
			} catch (NumberFormatException e) {
				errorLb.setText("port is error");
				return;
			}
			if(!isConnectMailService(ssl_btn.getSelection(), smtpHost, port == null ? (ssl == 0 ? SSL_NO_PORT : SSL_DEFUALT_PORT) : Integer.valueOf(port), senderAddress, senderPassword)){
				errorLb.setText("mail configure is error");
				return;
			}
			mailEntity.setSenderAddress(senderAddress);
			mailEntity.setSenderPassword(senderPassword);
			mailEntity.setSendName(sendName == null ? "" : sendName);
			mailEntity.setStmpHost(smtpHost);
			mailEntity.setSsl(ssl);
			mailEntity.setPort(port == null ? (ssl == 0 ? SSL_NO_PORT : SSL_DEFUALT_PORT) : Integer.valueOf(port));
			mailEntity.setId(1);
			setReturnCode(IDialogConstants.OK_ID);

		} else
			setReturnCode(IDialogConstants.CANCEL_ID);

		close();
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		return new Point(point.x + 100, point.y);
	}

	public boolean isConnectMailService(boolean isSSl, String service, int port, String userMail, String password) {
		Transport transport = null;
		try {
			Properties props = new Properties();
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.transport.protocol", "smtp");
			props.put("mail.smtp.ssl.enable", isSSl);
			props.put("mail.smtp.port", port);
			props.setProperty("mail.smtp.host", service);
			Session session = Session.getInstance(props);
			session.setDebug(true);
			transport = session.getTransport();
			transport.connect(userMail, password);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (transport != null)
					transport.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
}
