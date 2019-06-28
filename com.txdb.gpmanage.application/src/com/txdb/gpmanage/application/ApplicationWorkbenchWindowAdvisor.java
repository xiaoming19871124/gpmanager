package com.txdb.gpmanage.application;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.layout.ITrimManager;
import org.eclipse.ui.internal.progress.ProgressRegion;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IWorkbenchWindowConfigurer configurer;
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        this.configurer = configurer;
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
    public void postWindowOpen() {
    	WorkbenchWindow window = (WorkbenchWindow) configurer.getWindow();
    	
    	// 1. 把progressIndicator移动到在上面
        ITrimManager trimManager = window.getTrimManager();
        ProgressRegion progressRegion = window.getProgressRegion();
        if (trimManager != null && progressRegion != null)
        {
            trimManager.removeTrim(progressRegion);
            trimManager.addTrim(ITrimManager.TOP, progressRegion);
            window.getShell().layout();
        }
        
//         2. 隐藏标题栏
//    	long handle = configurer.getWindow().getShell().handle;
//    	int oldStyle = OS.GetWindowLong(handle, OS.GWL_STYLE);
//    	OS.SetWindowLong(handle, OS.GWL_STYLE, oldStyle & OS.WS_SYSMENU /** ~OS.WS_CAPTION */);
    }
    
    @Override
	public void preWindowOpen() {
    	configurer.setInitialSize(new Point(1200, 700));
//    	configurer.setShowStatusLine(false);
		// window
//		configurer.setShowFastViewBars(false);

		// 除了配置的标题信息外，提供客户端程序的版本信息
//		configurer.getWindow().getShell().setImage(ImageDescriptor.createFromFile(getClass(), "client.gif").createImage());
	}
}
