package com.txdb.gpmanage.manage.ui.dialog;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 自定义ProgressMonitorDialog
 * 
 * @author ws
 *
 */
public class ManageProgressMonitorDialog extends ProgressMonitorDialog {
	/**
	 * title
	 */
	private String title;

	public ManageProgressMonitorDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}
}
