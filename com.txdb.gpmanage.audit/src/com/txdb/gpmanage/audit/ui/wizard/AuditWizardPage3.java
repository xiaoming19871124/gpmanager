package com.txdb.gpmanage.audit.ui.wizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.audit.i18n.MessageConstants;
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPAuditEntity;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;

public class AuditWizardPage3 extends BaseAuditWizardPage {

	private Text txt_auditName;
	
	private Text txt_ip;
	private Text txt_port;
	
	private Text txt_gpUsername;
	private Text txt_gpPassword;
	
	private Text txt_url;
	private Text txt_jdbcUsername;
	private Text txt_jdbcPassword;
	
	protected AuditWizardPage3() {
		super("Database Connect", ResourceHandler.getValue(MessageConstants.WIZARD_NEW_PAGE3_TITLE));
	}

	@Override
	public void createBody(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		// 0.0 =====================================================
		txt_auditName = createTextControl(composite, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_AUDITNAME), "");
		txt_auditName.setEnabled(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		// 1.0 =====================================================
		Group detailGroup = new Group(composite, SWT.NONE);
		detailGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTINF));
		new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		detailGroup.setLayoutData(gd);
		detailGroup.setLayout(new GridLayout(2, false));
		
		txt_ip = createTextControl(detailGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTINF_HOSTNAME), "");
		txt_port = createTextControl(detailGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_HOSTINF_PORT), "");
		
		// 2.0 =====================================================
		Group sshGroup = new Group(composite, SWT.NONE);
		sshGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHINF));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sshGroup.setLayoutData(gd);
		sshGroup.setLayout(new GridLayout(2, false));
		
		txt_gpUsername = createTextControl(sshGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHINF_UNAME), "");
		txt_gpPassword = createTextControl(sshGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_SSHINF_UPASS), "");
		
		// 3.0 =====================================================
		Group jdbcGroup = new Group(composite, SWT.NONE);
		jdbcGroup.setText(ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCINF));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		jdbcGroup.setLayoutData(gd);
		jdbcGroup.setLayout(new GridLayout(2, false));
		
		createTextControl(jdbcGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCINF_DATABASE), "postgres");
		txt_url = createTextControl(jdbcGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCINF_URL), "");
		txt_jdbcUsername = createTextControl(jdbcGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCINF_UNAME), "");
		txt_jdbcPassword = createTextControl(jdbcGroup, ResourceHandler.getValue(MessageConstants.WIZARD_NEW_JDBCINF_UPASS), "");
		
		txt_auditName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String auditName = txt_auditName.getText();
				auditName = auditName.trim().replaceAll(" ", "_");
				
				if (auditName.length() <= 0) {
					setMessage("Audit name is empty!", ERROR);
					return;
						
				} else if (checkNameExist(auditName)) {
					setPageComplete(false);
					setMessage("Audit name \"" + auditName + "\" is exist!", ERROR);
					return;
				}
				IDialogSettings ids = getDialogSettings();
				ids.put(AUDIT_NAME, auditName);
				
				setPageComplete(true);
				setMessage("Ready to create \"" + auditName + "\".", INFORMATION);
			}
		});
		setMessage("Connect to Database");
		setControl(composite);
	}
	
	private Text createTextControl(Composite parentComp, String name, String value) {
		new Label(parentComp, SWT.NONE).setText(name + " ");
		
		int style = SWT.BORDER;
		if (name.contains("Password"))
			style = SWT.BORDER | SWT.PASSWORD;
		Text txt = new Text(parentComp, style);
		txt.setEnabled(false);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setText(value);
		return txt;
	}
	
	@Override
	public void fillData() {
		IGPConnector gpController = ((AuditWizard) getWizard()).getGpcontroller();
		IExecuteDao dao = gpController.getDao();
		
		txt_ip.setText(dao.getHost());
		txt_port.setText(String.valueOf(dao.getSshPort()));
		
		txt_gpUsername.setText(dao.getSshUserName());
		txt_gpPassword.setText(dao.getSshPassword());
		
		txt_url.setText(dao.getJdbcUrl());
		txt_jdbcUsername.setText(dao.getJdbcUsername());
		txt_jdbcPassword.setText(dao.getJdbcPassword());
		
		// Audit Name
		String finalName = generateName(dao.getHost() + "_Audit");
		txt_auditName.setText(finalName);
		IDialogSettings ids = getDialogSettings();
		ids.put(AUDIT_NAME, finalName);
	}
	
	private String generateName(String originalName) {
		int nameSuffix = 0;
		String finalName = originalName;
		while (checkNameExist(finalName))
			finalName = originalName + "_(" + ++ nameSuffix + ")";
		return finalName;
	}
	
	private boolean checkNameExist(String auditName) {
		SqlWhere where = new SqlWhere();
		where.addWhere("auditName", "=", auditName);
		return SqliteDao.getInstance().queryGPEntity(new GPAuditEntity(), where).size() > 0;
	}
}
