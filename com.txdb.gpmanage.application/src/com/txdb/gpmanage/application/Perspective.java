package com.txdb.gpmanage.application;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
			String editorArea = layout.getEditorArea();
			layout.setEditorAreaVisible(false);

		    layout.addStandaloneView("com.txdb.gpmanage.application.view.mainView", false, IPageLayout.LEFT, 1.0f, editorArea);       
//			IFolderLayout treeFolder = layout.createFolder(FOLDER_NAVIGATION, IPageLayout.LEFT, 0.20f, editorArea);
//			treeFolder.addView("com.txdb.restore.ui.views.NavigationView");
//			treeFolder.addPlaceholder("org.eclipse.ui.texteditor.TemplatesView");
//			layout.getViewLayout("com.txdb.restore.ui.views.NavigationView").setCloseable(false);
	}
}
