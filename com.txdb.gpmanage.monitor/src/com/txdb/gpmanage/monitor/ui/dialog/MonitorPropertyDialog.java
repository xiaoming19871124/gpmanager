package com.txdb.gpmanage.monitor.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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

import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.utils.VerifyUtil;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;

public class MonitorPropertyDialog extends Dialog {

	private GPMonitorEntity entity;
	private GPMonitorEntity entity_modified;
	private boolean isActive;
	
	private Text txt_monitorName;
	
	private Text txt_ip;
	private Text txt_port;
	
	private Text txt_gpUsername;
	private Text txt_gpPassword;
	
	private Text txt_gpPort;
	private Text txt_jdbcUsername;
	private Text txt_jdbcPassword;
	
	private Label lbl_tips;
	private Button btn_OK;
	
	public MonitorPropertyDialog(Shell parentShell, GPMonitorEntity entity, boolean isActive) {
		super(parentShell);
		this.entity = entity;
		this.isActive = isActive;
		entity_modified = new GPMonitorEntity();
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue(MessageConstants.PROPERTIES_TITLE, new String[] { entity.getMonitorName() }));
		
		Point size = new Point(450, 450);
		newShell.setSize(size);
		newShell.setMinimumSize(size);
		
		Rectangle rect = Display.getCurrent().getBounds();
		newShell.setLocation((rect.width - size.x) / 2, (rect.height - size.y) / 2 - 50);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		
		// 0.0 =====================================================
		Composite nameComp = new Composite(composite, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameComp.setLayoutData(gd);
		nameComp.setLayout(new GridLayout(2, false));
		
		txt_monitorName = createTextControl(nameComp, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_MONITORNAME), "");
		txt_monitorName.setEnabled(!isActive);
		
		// 1.0 =====================================================
		Group detailGroup = new Group(composite, SWT.NONE);
		detailGroup.setText(ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_HOSTINF));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		detailGroup.setLayoutData(gd);
		detailGroup.setLayout(new GridLayout(2, false));
		
		txt_ip = createTextControl(detailGroup, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_HOSTINF_HOSTNAME), "");
		txt_port = createTextControl(detailGroup, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_HOSTINF_PORT), "");
		
		// 2.0 =====================================================
		Group sshGroup = new Group(composite, SWT.NONE);
		sshGroup.setText(ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_SSHINF));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sshGroup.setLayoutData(gd);
		sshGroup.setLayout(new GridLayout(2, false));
		
		txt_gpUsername = createTextControl(sshGroup, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_SSHINF_USERNAME), "");
		txt_gpPassword = createTextControl(sshGroup, SWT.BORDER | SWT.PASSWORD, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_SSHINF_PASSWORD), "");
		
		// 3.0 =====================================================
		Group jdbcGroup = new Group(composite, SWT.NONE);
		jdbcGroup.setText(ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_JDBCINF));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		jdbcGroup.setLayoutData(gd);
		jdbcGroup.setLayout(new GridLayout(2, false));
		
		txt_gpPort = createTextControl(jdbcGroup, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_JDBCINF_PORT), "");
		txt_jdbcUsername = createTextControl(jdbcGroup, SWT.BORDER, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_JDBCINF_USERNAME), "");
		txt_jdbcPassword = createTextControl(jdbcGroup, SWT.BORDER | SWT.PASSWORD, ResourceHandler.getValue(MessageConstants.PROPERTIES_GROUP_JDBCINF_PASSWORD), "");
		
		lbl_tips = new Label(composite, SWT.NONE);
		lbl_tips.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn_reset = new Button(composite, SWT.NONE);
		gd = new GridData();
		gd.widthHint = 105;
		btn_reset.setLayoutData(gd);
		btn_reset.setText(ResourceHandler.getValue(MessageConstants.PROPERTIES_BTN_RESET));
		btn_reset.setVisible(!isActive);
		btn_reset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillInput();
			}
		});
		fillInput();
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				btn_OK.setEnabled(inputVerification());
			}
		};
		txt_monitorName.addModifyListener(modifyListener);
		txt_ip.addModifyListener(modifyListener);
		txt_port.addModifyListener(modifyListener);
		txt_gpUsername.addModifyListener(modifyListener);
		txt_gpPassword.addModifyListener(modifyListener);
		txt_gpPort.addModifyListener(modifyListener);
		txt_jdbcUsername.addModifyListener(modifyListener);
		txt_jdbcPassword.addModifyListener(modifyListener);
		
		updateMessage(isActive ? "" : "Change property to update.", getSystemColor(SWT.COLOR_BLUE));
		return composite;
	}
	
	private Text createTextControl(Composite parentComp, int style, String name, String value) {
		Label label = new Label(parentComp, SWT.NONE);
		label.setText(name + " ");
		
		Text txt = new Text(parentComp, style);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setText(value);
		txt.setEnabled(!isActive);
		txt.setData(label);
		return txt;
	}
	
	private void fillInput() {
		txt_monitorName.setText(entity.getMonitorName());
		
		txt_ip.setText(entity.getHostname());
		txt_port.setText(String.valueOf(entity.getSshPort()));
		
		txt_gpUsername.setText(entity.getGpUsername());
		txt_gpPassword.setText(entity.getGpUserpwd());
		
		txt_gpPort.setText(String.valueOf(entity.getGpPort()));
		txt_jdbcUsername.setText(entity.getDbUsername());
		txt_jdbcPassword.setText(entity.getDbUserpwd());
		
		fillUpdated();
	}
	
	private void fillUpdated() {
		entity_modified.setMonitorName(txt_monitorName.getText().trim().replaceAll(" ", "_"));
		
		entity_modified.setHostname(txt_ip.getText());
		entity_modified.setSshPort(Integer.parseInt(txt_port.getText()));
		
		entity_modified.setGpUsername(txt_gpUsername.getText());
		entity_modified.setGpUserpwd(txt_gpPassword.getText());
		
		entity_modified.setGpPort(Integer.parseInt(txt_gpPort.getText()));
		entity_modified.setDbUsername(txt_jdbcUsername.getText());
		entity_modified.setDbUserpwd(txt_jdbcPassword.getText());
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		btn_OK = createButton(parent, IDialogConstants.OK_ID, ResourceHandler.getValue(MessageConstants.PROPERTIES_BTN_UPDATE), false);
		btn_OK.setEnabled(false);
		
		createButton(parent, IDialogConstants.CANCEL_ID, ResourceHandler.getValue(MessageConstants.PROPERTIES_BTN_CANCEL), false);
	}
	
	private boolean inputVerification() {
		boolean finalResult = true;
		
		// ==================== txt_monitorName ====================
		Label lbl_monitorName = ((Label) txt_monitorName.getData());
		String monitorName = txt_monitorName.getText();
		monitorName = monitorName.trim().replaceAll(" ", "_");
		boolean monitorNameVerify = monitorName.length() > 0;
		lbl_monitorName.setForeground(getSystemColor(monitorNameVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!monitorNameVerify) {
			updateMessage("Monitor Name is empty!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		if (!entity.getMonitorName().equals(monitorName)) {
			SqlWhere where = new SqlWhere();
			where.addWhere("monitorName", "=", monitorName);
			monitorNameVerify = SqliteDao.getInstance().queryGPEntity(new GPMonitorEntity(), where).size() <= 0;
			lbl_monitorName.setForeground(getSystemColor(monitorNameVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
			if (!monitorNameVerify) {
				if (finalResult)
					updateMessage("Monitor Name \"" + monitorName + "\" is exist!", getSystemColor(SWT.COLOR_RED));
				finalResult = false;
			}
		}
		
		// ==================== txt_ip ====================
		boolean hostnameVerify = VerifyUtil.checkIPAddress(txt_ip.getText());
		((Label) txt_ip.getData()).setForeground(getSystemColor(hostnameVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!hostnameVerify) {
			if (finalResult)
				updateMessage("Hostname verify failed!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_port ====================
		boolean sshPortVerify = VerifyUtil.checkPort(txt_port.getText());
		((Label) txt_port.getData()).setForeground(getSystemColor(sshPortVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!sshPortVerify) {
			if (finalResult)
				updateMessage("SSH Port verify failed!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_gpUsername ====================
		boolean gpUsernameVerify = VerifyUtil.checkUsername(txt_gpUsername.getText());
		((Label) txt_gpUsername.getData()).setForeground(getSystemColor(gpUsernameVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!gpUsernameVerify) {
			if (finalResult)
				updateMessage("SSH Username verify failed!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_gpPassword ====================
		boolean gpPasswordVerify = txt_gpPassword.getText().length() > 0;
		((Label) txt_gpPassword.getData()).setForeground(getSystemColor(gpPasswordVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!gpPasswordVerify) {
			if (finalResult)
				updateMessage("SSH Password is empty!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_gpPort ===================
		boolean gpPortVerify = VerifyUtil.checkPort(txt_gpPort.getText());
		((Label) txt_gpPort.getData()).setForeground(getSystemColor(gpPortVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!gpPortVerify) {
			if (finalResult)
				updateMessage("GP Port verify failed!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_jdbcUsername ====================
		boolean jdbcUsernameVerify = VerifyUtil.checkUsername(txt_jdbcUsername.getText());
		((Label) txt_jdbcUsername.getData()).setForeground(getSystemColor(jdbcUsernameVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!jdbcUsernameVerify) {
			if (finalResult)
				updateMessage("JDBC Username verify failed!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// ==================== txt_jdbcPassword ====================
		boolean jdbcPasswordVerify = txt_jdbcPassword.getText().length() > 0;
		((Label) txt_jdbcPassword.getData()).setForeground(getSystemColor(jdbcPasswordVerify ? SWT.COLOR_BLACK : SWT.COLOR_RED));
		if (!jdbcPasswordVerify) {
			if (finalResult)
				updateMessage("JDBC Password is empty!", getSystemColor(SWT.COLOR_RED));
			finalResult = false;
		}
		
		// Final Result
		if (finalResult) {
			fillUpdated();
			updateMessage("Verify Completed!", getSystemColor(SWT.COLOR_GREEN));
		}
		return finalResult;
	}
	
	private Color getSystemColor(int id) {
		return Display.getDefault().getSystemColor(id);
	}
	
	private void updateMessage(String message, Color color) {
		lbl_tips.setForeground(color);
		lbl_tips.setText(" " + message);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			SqlWhere where = new SqlWhere();
			where.addWhere("monitorName", "=", entity.getMonitorName());
			if (!SqliteDao.getInstance().updateGPEntity(entity_modified, where)) {
				MessageDialog.openError(getShell(), "Error", "Update monitor properties failed!");
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public GPMonitorEntity getFinalEntity() {
		return entity_modified;
	}
}
