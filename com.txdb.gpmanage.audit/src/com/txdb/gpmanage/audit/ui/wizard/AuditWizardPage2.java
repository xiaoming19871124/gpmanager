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
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;

public class AuditWizardPage2 extends BaseAuditWizardPage implements UICallBack {

	private Text txt_dbUser;
	private Text txt_dbPwd;
	private Text txt_port;
	private Text txt_database;
	
	private Button btn_check;
	private Label lbl_checkMsg;
	
	private final String TASK_NAME_JDBC_TEST = "JDBC_TEST";
	
	private final String DB_DEFAULT = "postgres";
	private final int CURRENT_JDBC_PORT = 5432;
	private boolean acceptNext = false;
	
	protected AuditWizardPage2() {
		super("Jdbc Configuration", ResourceHandler.getValue(MessageConstants.WIZARD_NEW_PAGE2_TITLE));
	}
	
	@Override
	public void createBody(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		
		// 0.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Group checkGroup = new Group(composite, SWT.NONE);
		checkGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG));
		checkGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkGroup.setLayout(new GridLayout(2, false));
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG_PORT));
		txt_port = new Text(checkGroup, SWT.BORDER);
		txt_port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_port.setText(String.valueOf(CURRENT_JDBC_PORT));
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG_UNAME));
		txt_dbUser = new Text(checkGroup, SWT.BORDER);
		txt_dbUser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_dbUser.setText("gpadmin");
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG_UPASS));
		txt_dbPwd = new Text(checkGroup, SWT.BORDER | SWT.PASSWORD);
		txt_dbPwd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG_DATABASE));
		txt_database = new Text(checkGroup, SWT.BORDER);
		txt_database.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_database.setEnabled(false);
		txt_database.setText(DB_DEFAULT);
		
		btn_check = new Button(checkGroup, SWT.NONE);
		btn_check.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCCFG_TEST));
		btn_check.setEnabled(false);
		
		lbl_checkMsg = new Label(checkGroup, SWT.NONE);
		lbl_checkMsg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_checkMsg.setText("Waiting for Jdbc check...");
		lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String dbPort = txt_port.getText();
				String dbUser = txt_dbUser.getText();
				String dbPwd = txt_dbPwd.getText();
				
				String wizardMsg = "Waiting for Jdbc test.";
				int msgType = INFORMATION;
				
				if (dbPort.length() <= 0) {
					wizardMsg = "Port is empty!";
					msgType = ERROR;
					
				} else if (dbUser.length() <= 0) {
					wizardMsg = "Username is empty!";
					msgType = ERROR;
				
				} else if (dbPwd.length() <= 0) {
					wizardMsg = "Password is empty!";
					msgType = ERROR;
				}
					
				try {
					int portNo = Integer.parseInt(dbPort);
					if (portNo < 0 || portNo > 65536) {
						wizardMsg = "Invalid port number!";
						msgType = ERROR;
					}
				} catch (Exception ex) {
					wizardMsg = "Port must be a number!";
					msgType = ERROR;
				}
				setPageComplete(false);
				btn_check.setEnabled(msgType == INFORMATION);
				setMessage(wizardMsg, msgType);
			}
		};
		txt_port.addModifyListener(modifyListener);
		txt_dbUser.addModifyListener(modifyListener);
		txt_dbPwd.addModifyListener(modifyListener);
		
		btn_check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				setBodyEnable(false);
				setPageComplete(false);
				
				final String dbPort     = txt_port.getText();
				final String dbUsername = txt_dbUser.getText();
				final String dbPassword = txt_dbPwd.getText();
				
				WizardRunnable runnable = new WizardRunnable() {
					@Override
					public void run() {
						boolean connect_ssh = false;
						boolean connect_jdbc = false;
						
						// Test SSH Connection
						IGPConnector gpController = ((AuditWizard) getWizard()).getGpcontroller();
						gpController.setCallback(null);
						connect_ssh = gpController.connect().isSuccessed();
						
						// Test JDBC Connection
						GpManageServiceProxy proxy = gpController.getManageServiceProxy();
						if (connect_ssh)
							connect_jdbc = proxy.connectJdbc(dbUsername, dbPassword, Integer.parseInt(dbPort), DB_DEFAULT);
						gpController.disconnect();
						
						Map<String, Object> sshValueMap = new HashMap<String, Object>();
						sshValueMap.put("SSH_CONN", connect_ssh);
						sshValueMap.put("JDBC_CONN", connect_jdbc);
						setTaskResult(sshValueMap);
					}
				};
				runTask(runnable, TASK_NAME_JDBC_TEST);
			}
		});
		
		setMessage("Config Jdbc", INFORMATION);
		setControl(composite);
	}
	
	@Override
	public void restorePage() {
		lbl_checkMsg.setText("Waiting for check Jdbc...");
		lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
	}
	
	@Override
	public void taskComplete(Map<String, Object> taskMap) {
		super.taskComplete(taskMap);
		
		if (taskMap.containsKey(TASK_NAME_JDBC_TEST)) {
			
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) taskMap.get(TASK_NAME_JDBC_TEST);
			boolean ssh_conn = (boolean) resultMap.get("SSH_CONN");
			boolean jdbc_conn = (boolean) resultMap.get("JDBC_CONN");
			
			IGPConnector gpController = ((AuditWizard) getWizard()).getGpcontroller();
			String hostname = gpController.getDao().getHost();
			
			acceptNext = false;
			if (!ssh_conn || !jdbc_conn) {
				lbl_checkMsg.setText("Host \"" + hostname + "\" test jdbc failed.");
				lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				
			} else {
				lbl_checkMsg.setText("Host \"" + hostname + "\" ready.");
				lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				acceptNext = true;
			}
			setPageComplete(acceptNext);
			setBodyEnable(true);
			
			WizardPage nextPage = (WizardPage) getNextPage();
			((BaseAuditWizardPage) nextPage).fillData();
			
			nextPage.setPageComplete(true);
			getContainer().updateButtons();
		}
	}

	@Override
	public void refreshUI(String msg) {
		// TODO Auto-generated method stub
	}
}
