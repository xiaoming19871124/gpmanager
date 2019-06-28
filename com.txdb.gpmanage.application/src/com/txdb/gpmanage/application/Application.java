package com.txdb.gpmanage.application;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.txdb.gpmanage.application.i18n.ResourceHandler;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		
//		Shell shell = new Shell(display, SWT.ON_TOP);
//		Location instanceLoc = Platform.getInstanceLocation();
//		String defaultHomePath = Platform.getInstallLocation().getURL().getPath()+"workspace";
//		
//		File f = new File(defaultHomePath);
//		//创建工作控件目录
//		if (!f.exists()) {
//			try {
//				f.mkdirs();
//			} catch (Exception err) {
//				MessageBox messageBox = new MessageBox(shell, SWT.OK);
//				messageBox.setText(ResourceHandler.getValue("error"));
//				messageBox.setMessage(ResourceHandler.getValue("create_workspace_error"));
//				messageBox.open();
//				return IApplication.EXIT_OK;
//			}
//		}
//		//工作空间是否有读权限
//		if (!f.canRead()) {
//			MessageBox messageBox = new MessageBox(shell, SWT.OK|SWT.ICON_ERROR);
//			messageBox.setText(ResourceHandler.getValue("error"));
//			messageBox.setMessage(ResourceHandler.getValue("read_workspace_error"));
//			return IApplication.EXIT_OK;
//		}
//		if (!instanceLoc.allowsDefault() && !instanceLoc.isSet()) {
//			try {
//				URL defaultHomeURL = new URL("file", //$NON-NLS-1$
//					null, defaultHomePath);
//				boolean keepTrying = true;
//				while (keepTrying) {
//					if (!instanceLoc.set(defaultHomeURL, true)) {
//						// Can't lock specified path
//						MessageBox messageBox =
//							new MessageBox(shell, SWT.ICON_WARNING | SWT.IGNORE | SWT.RETRY | SWT.ABORT);
//						messageBox.setText(ResourceHandler.getValue("error"));
//						messageBox.setMessage(ResourceHandler.getValue("application_Application_start_promptPath")
//							+ defaultHomePath
//							+ ".\n"
//							+ResourceHandler.getValue("application_Application_start_promptContent"));
//						switch (messageBox.open()) {
//							case SWT.ABORT:
//								return IApplication.EXIT_OK;
//							case SWT.IGNORE:
//								instanceLoc.set(defaultHomeURL, false);
//								keepTrying = false;
//								break;
//							case SWT.RETRY:
//								break;
//						}
//					} else {
//						break;
//					}
//				}
//				if (shell.isDisposed()) {
//					shell.dispose();
//				}
//			} catch (Exception e) {
//				// Just skip it
//				// Error may occur if -data parameter was specified at startup
//				System.err.println("Can't switch workspace to '" + defaultHomePath + "' - " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
//			}
//		}
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
