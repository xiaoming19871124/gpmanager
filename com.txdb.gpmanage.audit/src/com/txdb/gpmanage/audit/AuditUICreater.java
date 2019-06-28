package com.txdb.gpmanage.audit;

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
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.audit.ui.composite.AuditComposite;
import com.txdb.gpmanage.widget.DefultLabel;

public class AuditUICreater extends AbstractUICreater {

	private Image img_btn = Activator.getImage("icons/tool_audit.png");
	@Override
	public Button createTitleButton(Composite top) {
		Button auditBtn = new Button(top, SWT.NONE);
		auditBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true));
		auditBtn.setToolTipText(ResourceHandler.getValue("title.audit"));
		auditBtn.setImage(img_btn);
		
		auditBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getItem() == null || getItem().isDisposed()) {
					CTabItem tabItem = new CTabItem(getFolder(), SWT.NONE);
					tabItem.setText(ResourceHandler.getValue("title.audit"));
					AuditComposite monitorComposite = new AuditComposite(getFolder(), SWT.NONE);
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
		return auditBtn;
	}

	@Override
	public Label createWelButton(Composite top) {
		Label label =new DefultLabel(top, SWT.NONE,new GridData(SWT.CENTER, SWT.CENTER, true, true),ResourceHandler.getValue("title.audit"),img_btn).createLabel();
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}
			@Override
			public void mouseDown(MouseEvent e) {
				if (getItem() == null || getItem().isDisposed()) {
					CTabItem tabItem = new CTabItem(getFolder(), SWT.NONE);
					tabItem.setText(ResourceHandler.getValue("title.audit"));
					AuditComposite monitorComposite = new AuditComposite(getFolder(), SWT.NONE);
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
