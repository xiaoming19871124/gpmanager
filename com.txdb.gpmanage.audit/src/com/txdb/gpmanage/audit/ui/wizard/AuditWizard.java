package com.txdb.gpmanage.audit.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.txdb.gpmanage.core.gp.connector.IGPConnector;

public class AuditWizard extends Wizard {
	
	public IGPConnector gpcontroller;
	public String newAuditName;

	public AuditWizard() {
		setDialogSettings(new DialogSettings("Create Audit"));
	}

	@Override
	public void addPages() {
		addPage(new AuditWizardPage1());
		addPage(new AuditWizardPage2());
		addPage(new AuditWizardPage3());
	}
	
	public IGPConnector getGpcontroller() {
		return gpcontroller;
	}

	public void setGpcontroller(IGPConnector gpcontroller) {
		this.gpcontroller = gpcontroller;
	}
	
	public String getNewAuditName() {
		return newAuditName;
	}

	@Override
	public boolean performFinish() {
		IDialogSettings dialogSettings = getDialogSettings();
		newAuditName = dialogSettings.get(BaseAuditWizardPage.AUDIT_NAME);
		
		new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				MessageDialog.openConfirm(getShell(), "Confirm Information", "auditName = " + newAuditName );
			}
		};
		return true;
	}
}
