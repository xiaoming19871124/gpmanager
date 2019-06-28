package com.txdb.gpmanage.application.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.Activator;
import com.txdb.gpmanage.application.ExtenSionFactory;
import com.txdb.gpmanage.application.i18n.ResourceHandler;
import com.txdb.gpmanage.application.view.MainView;

public class WelComeComposite extends Composite {

	private Image img_main = Activator.getImage("icons/main_bgi.png");
	private CTabFolder folder;
	private StackLayout parentLayout;
	private MainView view;
	public WelComeComposite(Composite parent, int style, StackLayout layout, CTabFolder folder,MainView view) {
		super(parent, style);
		this.parentLayout = layout;
		this.folder = folder;
		this.view = view;
		this.setBackgroundImage(img_main);
		init();
	}

	private void init() {
		createUI();
		this.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				gc.setFont(new Font(Display.getCurrent(), "微软雅黑", 30, SWT.NORMAL));
				gc.drawText(ResourceHandler.getValue("welcome_title"), 10, 10, true);
			}
		});

	}

	private void createUI() {
		AbstractUICreater[] uiCreaters = ExtenSionFactory.getInstance().getAllCreater();
		GridLayout layout = new GridLayout(uiCreaters.length, true);
		for (AbstractUICreater creater : uiCreaters) {
			final Label link = creater.createWelButton(this);
			link.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(MouseEvent e) {
				}

				@Override
				public void mouseDown(MouseEvent e) {
					if (parentLayout.topControl != folder) {
						parentLayout.topControl = folder;
						WelComeComposite.this.getParent().layout();
						view.showBtns();
					}
				}

				@Override
				public void mouseDoubleClick(MouseEvent e) {
				}
			});
		}
		layout.marginTop = 50;
		this.setLayout(layout);
	}
}
