package com.txdb.gpmanage.manage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.composite.AbstractUICreater;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.ui.composite.ManageComposite;
import com.txdb.gpmanage.widget.DefultLabel;

public class ManageUICreater extends AbstractUICreater {
	
	private Image img_btn = Activator.getImage("icons/tool_manage.png");
	@Override
	public Button createTitleButton(Composite top) {
		Button manageBtn = new Button(top, SWT.NONE);
		manageBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true));
		manageBtn.setToolTipText(ResourceHandler.getValue("title.manage"));
		manageBtn.setImage(img_btn);
		
		manageBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getItem() == null || getItem().isDisposed()) {
					CTabItem tabItem = new CTabItem(getFolder(), SWT.NONE);
					tabItem.setText(ResourceHandler.getValue("title.manage"));
					ManageComposite monitorComposite = new ManageComposite(getFolder(), SWT.NONE);
					tabItem.setControl(monitorComposite);
					tabItem.setImage(img_btn);
					setItem(tabItem);
					getFolder().setSelection(tabItem);
				} else
					getFolder().setSelection(getItem());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		return manageBtn;
	}

	@Override
	public Label createWelButton(Composite top) {
		Label label =new DefultLabel(top, SWT.NONE,new GridData(SWT.CENTER, SWT.CENTER, true, true),ResourceHandler.getValue("title.manage"),img_btn).createLabel();
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}
			@Override
			public void mouseDown(MouseEvent e) {
				if (getItem() == null || getItem().isDisposed()) {
					CTabItem tabItem = new CTabItem(getFolder(), SWT.NONE);
					tabItem.setText(ResourceHandler.getValue("title.manage"));
					ManageComposite monitorComposite = new ManageComposite(getFolder(), SWT.NONE);
					tabItem.setControl(monitorComposite);
					tabItem.setImage(img_btn);
					setItem(tabItem);
					getFolder().setSelection(tabItem);
				} else
					getFolder().setSelection(getItem());
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
			return label;
		}
}
