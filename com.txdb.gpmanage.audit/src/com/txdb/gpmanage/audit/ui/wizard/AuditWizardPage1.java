package com.txdb.gpmanage.audit.ui.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.audit.i18n.MessageConstants;
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.core.utils.VerifyUtil;

public class AuditWizardPage1 extends BaseAuditWizardPage {

	private Text txt_hostname;
	private Text txt_port;
	private Text txt_username;
	private Text txt_password;
	private Button btn_savePwd;
	
	private Label lbl_testConnectionMsg;
	private Button btn_testConnection;
	
	private final String TASK_NAME_SSH_TEST = "SSH_TEST";
	private boolean acceptNext = false;
	
	protected AuditWizardPage1() {
		super("Audit Configuration", ResourceHandler.getValue(MessageConstants.WIZARD_NEW_PAGE1_TITLE));
	}

	@Override
	public void createBody(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		
		// 1.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Group operateGroup = new Group(composite, SWT.NONE);
		operateGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTCFG));
		operateGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operateGroup.setLayout(new GridLayout(2, false));
		
		new Label(operateGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTCFG_HOSTNAME));
		txt_hostname = new Text(operateGroup, SWT.BORDER);
		txt_hostname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(operateGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTCFG_SSHPORT));
		txt_port = new Text(operateGroup, SWT.BORDER);
		txt_port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		txt_hostname.setText("192.168.0.120");
		txt_port.setText("22");
		
		// 2.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Group sshGroup = new Group(composite, SWT.NONE);
		sshGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHCFG));
		sshGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sshGroup.setLayout(new GridLayout(2, false));
		
		new Label(sshGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHCFG_GPADMINNAME));
		txt_username = new Text(sshGroup, SWT.BORDER);
		txt_username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(sshGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHCFG_GPADMINPASS));
		txt_password = new Text(sshGroup, SWT.BORDER | SWT.PASSWORD);
		txt_password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(sshGroup, SWT.NONE);
		btn_savePwd = new Button(sshGroup, SWT.CHECK);
		btn_savePwd.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHCFG_SAVEPASSWORD));
		
		// 3.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Composite submitComp = new Composite(composite, SWT.NONE);
		submitComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		submitComp.setLayout(new GridLayout(2, false));
		
		lbl_testConnectionMsg = new Label(submitComp, SWT.RIGHT);
		lbl_testConnectionMsg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_testConnectionMsg.setText("Waiting for test connection...");
		lbl_testConnectionMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		btn_testConnection = new Button(submitComp, SWT.NONE);
		btn_testConnection.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_CONNTEST));
		
		btn_testConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				final String hostname = txt_hostname.getText();
				final int port = Integer.parseInt(txt_port.getText());
				final String username = txt_username.getText();
				final String password = txt_password.getText();
				
				setPageComplete(false);
				setBodyEnable(false);
				lbl_testConnectionMsg.setText("Host \"" + hostname + "\" testing...");
				lbl_testConnectionMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
				
				// Test Host Connection
				WizardRunnable runnable = new WizardRunnable() {
					@Override
					public void run() {
						final IGPConnector gpController = new GPConnectorImpl(hostname, username, password, port);
						boolean connected_ssh = gpController.connect().isSuccessed();
						boolean gpdbExist = false;
						acceptNext = false;
						
						if (connected_ssh) {
							GpManageServiceProxy proxy = gpController.getManageServiceProxy();
							gpdbExist = proxy.gpState().isSuccessed();
							acceptNext = (connected_ssh && gpdbExist);

							// 加载版本号
							System.err.println(">>> gpVersion: " + proxy.gpVersion());
							System.err.println(">>> pgVersion: " + proxy.pgVersion());

							gpController.disconnect();
						}
						Map<String, Object> sshValueMap = new HashMap<String, Object>();
						sshValueMap.put(GP_CONTROLLER, gpController);
						sshValueMap.put("ssh", connected_ssh);
						sshValueMap.put("gpdb", gpdbExist);
						setTaskResult(sshValueMap);
					}
				};
				runTask(runnable, TASK_NAME_SSH_TEST);
			}
		});
		
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateWizardMsg();
				setPageComplete(false);
			}
		};
		txt_hostname.addModifyListener(modifyListener);
		txt_port.addModifyListener(modifyListener);
		txt_username.addModifyListener(modifyListener);
		txt_password.addModifyListener(modifyListener);
		
		setMessage("Config a new Audit", INFORMATION);
	}
	
	private void updateWizardMsg() {
		btn_testConnection.setEnabled(false);
		
		String hostname = txt_hostname.getText();
		String port = txt_port.getText();
		String username = txt_username.getText();
		String password = txt_password.getText();
		
		String wizardMsg = "Config a new Audit";
		int msgType = INFORMATION;
		
		// Empty Verify
		if (hostname.length() <= 0) {
			wizardMsg = "Hostname is empty!";
			msgType = ERROR;
			
		} else if (port.length() <= 0) {
			wizardMsg = "Port is empty!";
			msgType = ERROR;
			
		} else if (username.length() <= 0) {
			wizardMsg = "Username is empty!";
			msgType = ERROR;
			
		} else if (password.length() <= 0) {
			wizardMsg = "Password is empty!";
			msgType = ERROR;
		}
		if (msgType != INFORMATION) {
			setMessage(wizardMsg, msgType);
			return;
		}
		
		// Content Verify
		// 1.0 == Hostname
		if (!VerifyUtil.checkIPAddress(hostname)) {
			setMessage("Hostname verify failed!", WARNING);
			return;
		}
		// 2.0 == Port
		if (!VerifyUtil.checkPort(port)) {
			setMessage("Invalid port number!", ERROR);
			return;
		}
		// 3.0 == Username
		if (!VerifyUtil.checkUsername(username)) {
			setMessage("Invalid username!", ERROR);
			return;
		}
		// 4.0 == Password
		if (password.trim().length() <= 0) {
			setMessage("Invalid password!", ERROR);
			return;
		}
		setMessage(wizardMsg, msgType);
		btn_testConnection.setEnabled(true);
	}
	
	@Override
	public void fillData() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void taskComplete(Map<String, Object> taskMap) {
		super.taskComplete(taskMap);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) taskMap.get(TASK_NAME_SSH_TEST);
		IGPConnector gpController = (IGPConnector) resultMap.get(GP_CONTROLLER);
		String hostname = gpController.getDao().getHost();
		
		if (acceptNext) {
			lbl_testConnectionMsg.setText("Host \"" + hostname + "\" test successed.");
			lbl_testConnectionMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
			((AuditWizard) getWizard()).setGpcontroller(gpController);
			
		} else {
			String msg = "Host \"" + hostname + "\" test failed!";
			if ((boolean) resultMap.get("ssh"))
				msg = "No running GPDB on host \"" + hostname + "\" !";
			
			lbl_testConnectionMsg.setText(msg);
			lbl_testConnectionMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		}
		setPageComplete(acceptNext);
		setBodyEnable(true);
		
		WizardPage nextPage = (WizardPage) getNextPage();
		nextPage.setPageComplete(false);
		
		((BaseAuditWizardPage) nextPage).restorePage();
		getContainer().updateButtons();
	}
}
