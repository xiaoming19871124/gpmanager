package com.txdb.gpmanage.core.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UICommonUtil {
	private static int openID = SWT.CANCEL;

	public static void openErrorMsgDlg(final Display display,final String msg) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
			Shell shell =	display.getActiveShell();
			if(shell==null)
				shell = new Shell(display);
				MessageBox mb = new MessageBox(shell,SWT.OK|SWT.ICON_ERROR);
				mb.setMessage(msg);
				mb.setText("ERROR");
				mb.open();
			}
		});
	}

	public static int openAskDlg(final Display display, final String msg) {
		openID = SWT.CANCEL;
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell =	display.getActiveShell();
				if(shell==null)
					shell = new Shell(display);
				MessageBox mb = new MessageBox(shell, SWT.OK
						| SWT.CANCEL | SWT.ICON_QUESTION);
				mb.setMessage(msg);
				mb.setText("INFO");
				openID = mb.open();
			}
		});
		return openID;
	}
}
