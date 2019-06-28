package com.txdb.gpmanage.monitor.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.txdb.gpmanage.core.gp.connector.IGPConnector;

public class MonitorWizard extends Wizard {
	
	public IGPConnector gpcontroller;
	public String newMonitorName;

	public MonitorWizard() {
		setDialogSettings(new DialogSettings("Create Monitor"));
	}

	@Override
	public void addPages() {
		addPage(new MonitorWizardPage1());
		addPage(new MonitorWizardPage2());
		addPage(new MonitorWizardPage3());
	}
	
	public IGPConnector getGpcontroller() {
		return gpcontroller;
	}

	public void setGpcontroller(IGPConnector gpcontroller) {
		this.gpcontroller = gpcontroller;
	}
	
	public String getNewMonitorName() {
		return newMonitorName;
	}

	@Override
	public boolean performFinish() {
		IDialogSettings dialogSettings = getDialogSettings();
		newMonitorName = dialogSettings.get(BaseMonitorWizardPage.MONITOR_NAME);
		
		new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				MessageDialog.openConfirm(getShell(), "Confirm Information", "monitorName = " + newMonitorName );
			}
		};
//		try {
//			setNeedsProgressMonitor(true);
//			getContainer().run(true, true, new LongRunningOperation(false));
//			
//		} catch (InterruptedException e) {
//			return false;
//			
//		} catch (InvocationTargetException e) {
//			Throwable realException = e.getTargetException();
//			MessageDialog.openError(getShell(), "Error", realException.getMessage());
//			return false;
//		}
		return true;
	}
	
//	class LongRunningOperation implements IRunnableWithProgress {
//		// The total sleep time
//		private static final int TOTAL_TIME = 10000;
//		// The increment sleep time
//		private static final int INCREMENT = 500;
//		private boolean indeterminate;
//		
//		public LongRunningOperation(boolean indeterminate) {
//			this.indeterminate = indeterminate;
//		}
//		
//		@Override
//		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//			monitor.beginTask("Running long running operation", indeterminate ? IProgressMonitor.UNKNOWN : TOTAL_TIME);
//			for (int total = 0; total < TOTAL_TIME && !monitor.isCanceled(); total += INCREMENT) {
//				Thread.sleep(INCREMENT);
//				monitor.worked(INCREMENT);
//				if (total == TOTAL_TIME / 2)
//					monitor.subTask("Doing second half");
//			}
//			monitor.done();
//			if (monitor.isCanceled())
//				throw new InterruptedException("The long running operation was cancelled");
//		}
//	}
}
