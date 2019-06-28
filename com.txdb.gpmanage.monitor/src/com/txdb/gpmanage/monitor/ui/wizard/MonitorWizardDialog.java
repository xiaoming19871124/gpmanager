package com.txdb.gpmanage.monitor.ui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MonitorWizardDialog extends WizardDialog {

	public MonitorWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);

		// TODO Auto-generated constructor stub
		((Wizard) newWizard).setNeedsProgressMonitor(true);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);

		newShell.setText("Create Monitor Wizard");
		
		Point size = new Point(550, 615);
		newShell.setSize(size);
		newShell.setMinimumSize(size);
		
		Rectangle rect = Display.getCurrent().getBounds();
		newShell.setLocation((rect.width - size.x) / 2, (rect.height - size.y) / 2 - 50);
	}
}
