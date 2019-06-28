package com.txdb.gpmanage.monitor.ui.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;

public class MonitorWizardPage2 extends BaseMonitorWizardPage implements UICallBack {

	private Text txt_dbUser;
	private Text txt_dbPwd;
	private Text txt_port;
	private Text txt_installLog;
	
	private Button btn_check;
	private Label lbl_checkMsg;
	private Group installServiceGroup;
	
	private final String TASK_NAME_JDBC_TEST = "JDBC_TEST";
	private final String TASK_NAME_SERVICE_INSTALL = "SERVICE_INSTALL";
	
	private final String DB_GPMON = "gpperfmon";
	private final int CURRENT_JDBC_PORT = 5432;
	private boolean acceptNext = false;
	
	protected MonitorWizardPage2() {
		super("Install Service(gpperfmon)", ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_TITLE));
	}
	
	@Override
	public void createBody(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		
		// 0.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Group checkGroup = new Group(composite, SWT.NONE);
		checkGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_CHECK));
		checkGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkGroup.setLayout(new GridLayout(2, false));
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_CHECK_PORT));
		txt_port = new Text(checkGroup, SWT.BORDER);
		txt_port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_port.setText(String.valueOf(CURRENT_JDBC_PORT));
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_CHECK_USERNAME));
		txt_dbUser = new Text(checkGroup, SWT.BORDER);
		txt_dbUser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_dbUser.setText("gpadmin");
		
		new Label(checkGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_CHECK_PASSWORD));
		txt_dbPwd = new Text(checkGroup, SWT.BORDER | SWT.PASSWORD);
		txt_dbPwd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btn_check = new Button(checkGroup, SWT.NONE);
		btn_check.setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_CHECK_CHECKSERVICE));
		btn_check.setEnabled(false);
		
		lbl_checkMsg = new Label(checkGroup, SWT.NONE);
		lbl_checkMsg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_checkMsg.setText("Waiting for check Monitor Service...");
		lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String dbPort = txt_port.getText();
				String dbUser = txt_dbUser.getText();
				String dbPwd = txt_dbPwd.getText();
				
				String wizardMsg = "Read for check monitor service.";
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
						boolean connect_gpmon = false;
						
						// Test SSH Connection
						IGPConnector gpController = ((MonitorWizard) getWizard()).getGpcontroller();
						gpController.setCallback(null);
						connect_ssh = gpController.connect().isSuccessed();
						
						// Test JDBC Connection
						GpManageServiceProxy proxy = gpController.getManageServiceProxy();
						if (connect_ssh)
							connect_jdbc = proxy.connectJdbc(dbUsername, dbPassword, Integer.parseInt(dbPort), null);
						
						// Test gpperfmon Database
						if (connect_jdbc)
							connect_gpmon = proxy.executeQuery("select * from pg_catalog.pg_database t where t.datname = '" + DB_GPMON + "'").size() > 0;
						gpController.disconnect();
						
						Map<String, Object> sshValueMap = new HashMap<String, Object>();
						sshValueMap.put("SSH_CONN", connect_ssh);
						sshValueMap.put("JDBC_CONN", connect_jdbc);
						sshValueMap.put("GPMON_CONN", connect_gpmon);
						setTaskResult(sshValueMap);
					}
				};
				runTask(runnable, TASK_NAME_JDBC_TEST);
			}
		});
		
		// 1.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		installServiceGroup = new Group(composite, SWT.NONE);
		installServiceGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_INSTALL));
		installServiceGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		installServiceGroup.setLayout(new GridLayout(1, false));
		installServiceGroup.setVisible(false);
		
		// 2.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Button btn_createService = new Button(installServiceGroup, SWT.NONE);
		btn_createService.setText(ResourceHandler.getValue(MessageConstants.WIZARD_PAGE2_GROUP_INSTALL_CREATE));
		btn_createService.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		txt_installLog = new Text(installServiceGroup, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		txt_installLog.setLayoutData(gd);
		txt_installLog.setEditable(false);
		txt_installLog.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		btn_createService.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				setBodyEnable(false);
				setPageComplete(false);
				final String dbPort = txt_port.getText();
				
				WizardRunnable wizardRunnable = new WizardRunnable() {
					@Override
					public void run() {
						boolean status_ssh = false;
						boolean status_gpmon = false;
						boolean status_restart = false;
						
						// SSH Connection
						IGPConnector gpController = ((MonitorWizard) getWizard()).getGpcontroller();
						gpController.setCallback(MonitorWizardPage2.this);
						status_ssh = gpController.connect().isSuccessed();
						
						// Install Monitor Service
						if (status_ssh) {
							GPResultSet rs = gpController.getManageServiceProxy().gpperfmonInstall("123456", dbPort);
							status_gpmon = rs.isSuccessed();
						}
						
						// Restart GPDB
						if (status_gpmon) {
							GPResultSet rs = gpController.getManageServiceProxy().gpStop("-r");
							status_restart = rs.isSuccessed();
						}
						gpController.disconnect();
						
						Map<String, Object> sshValueMap = new HashMap<String, Object>();
						sshValueMap.put("STATUS_SSH", status_ssh);
						sshValueMap.put("STATUS_GPMON", status_gpmon);
						sshValueMap.put("STATUS_RESTART", status_restart);
						setTaskResult(sshValueMap);
					}
				};
				runTask(wizardRunnable, TASK_NAME_SERVICE_INSTALL);
			}
		});
		
		setMessage("Check/Install Monitor Service", INFORMATION);
		setControl(composite);
	}
	
	@Override
	public void restorePage() {
		lbl_checkMsg.setText("Waiting for check Monitor Service...");
		lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		installServiceGroup.setVisible(false);
	}
	
	@Override
	public void taskComplete(Map<String, Object> taskMap) {
		super.taskComplete(taskMap);
		
		if (taskMap.containsKey(TASK_NAME_SERVICE_INSTALL)) {

			// 1.0 监控服务安装
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) taskMap.get(TASK_NAME_SERVICE_INSTALL);
			boolean status_ssh = (boolean) resultMap.get("STATUS_SSH");
			boolean status_gpmon = (boolean) resultMap.get("STATUS_GPMON");
			boolean status_restart = (boolean) resultMap.get("STATUS_RESTART");
			setBodyEnable(true);
			
			if (!status_ssh) {
				IGPConnector gpController = ((MonitorWizard) getWizard()).getGpcontroller();
				String hostname = gpController.getDao().getHost();
				MessageDialog.openError(getShell(), "Error", "Cannot connect to host \"" + hostname + "\"");
				return;
			}
			if (!status_gpmon) {
				MessageDialog.openError(getShell(), "Error", "Service gpperfmon install failed!");
				return;
			}
			
			// 2.0 安装结果（安装 + 重启）
			StringBuffer messageBuff = new StringBuffer("Install gpperfmon Service successed");
			if (!status_restart) {
				messageBuff.append(", but restart GPDB failed! \nPlease restart the GPDB manually later for make the monitor service work well.");
				MessageDialog.openWarning(getShell(), "Warning", messageBuff.toString());
				
			} else
				MessageDialog.openInformation(getShell(), "Info", messageBuff.append(".").toString());
			
			// 3.0 重新检查服务
			btn_check.notifyListeners(SWT.Selection, new Event());
			
		} else if (taskMap.containsKey(TASK_NAME_JDBC_TEST)) {
			
			// 终端服务检测
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) taskMap.get(TASK_NAME_JDBC_TEST);
			boolean ssh_conn = (boolean) resultMap.get("SSH_CONN");
			boolean jdbc_conn = (boolean) resultMap.get("JDBC_CONN");
			boolean hasGpmon = (boolean) resultMap.get("GPMON_CONN");
			
			IGPConnector gpController = ((MonitorWizard) getWizard()).getGpcontroller();
			String hostname = gpController.getDao().getHost();
			
			acceptNext = false;
			if (!ssh_conn || !jdbc_conn) {
				lbl_checkMsg.setText("Host \"" + hostname + "\" test jdbc failed.");
				lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				
			} else if (!hasGpmon) {
				lbl_checkMsg.setText("Monitor Service has not yet been installed on \"" + hostname + "\".");
				lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
				installServiceGroup.setVisible(true);
				
			} else {
				lbl_checkMsg.setText("Host \"" + hostname + "\" ready.");
				lbl_checkMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				acceptNext = true;
				installServiceGroup.setVisible(false);
			}
			setPageComplete(acceptNext);
			setBodyEnable(true);
			
			WizardPage nextPage = (WizardPage) getNextPage();
			((BaseMonitorWizardPage) nextPage).fillData();
			
			nextPage.setPageComplete(true);
			getContainer().updateButtons();
		}
	}

	@Override
	public void refreshUI(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				txt_installLog.append(msg);
				txt_installLog.setTopIndex(Integer.MAX_VALUE); 
			}
		});
	}
}
