package com.txdb.gpmanage.manage.ui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 添加集群wizard dialog
 * 
 * @author ws
 *
 */
public class AddGPWizardDialog extends WizardDialog {
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceHandler.getValue("add"));
		newShell.setSize(400, 550);
		newShell.setMinimumSize(200, 550);
	}

	public AddGPWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		((Wizard) newWizard).setNeedsProgressMonitor(true);
	}

	@Override
	protected void nextPressed() {
		IWizardPage page = getCurrentPage();
		boolean isCanNext = ((AbsAddGPWizardPage) page).validateValue();
		if (!isCanNext)
			return;
		super.nextPressed();
	}

	@Override
	protected void finishPressed() {
		IWizardPage page = getCurrentPage();
		boolean isCanNext = ((AbsAddGPWizardPage) page).validateValue();
		if (!isCanNext)
			return;
//		if (page instanceof HostSetPage) {
//			IWizardPage nextPage = getWizard().getNextPage(page);
//			boolean isNextFinish = ((AbsAddGPWizardPage) nextPage).validateValue();
//			if (!isNextFinish) {
//				super.nextPressed();
//				return;
//			}
//		}
		super.finishPressed();
	}
}