package com.txdb.gpmanage.application.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.txdb.gpmanage.application.Activator;
import com.txdb.gpmanage.application.ExtenSionFactory;
import com.txdb.gpmanage.application.composite.AbstractUICreater;
import com.txdb.gpmanage.application.composite.WelComeComposite;
import com.txdb.gpmanage.application.i18n.ResourceHandler;

/**
 * 工具主页面view
 * 
 * @author ws
 */
public class MainView extends ViewPart {

	private Image img_bgi = Activator.getImage("icons/title_bgi.png");
	private Image img_logo = Activator.getImage("icons/logo.png");
	private Font font = new Font(null, "方正姚体", 18, SWT.NONE);

	private CTabFolder folder;
	private Composite main;
	private WelComeComposite welCome;
	private StackLayout layout;
	private List<Button> btns = new ArrayList<Button>();
private Composite top;
	@Override
	public void createPartControl(Composite parent) {
		// 整体布局
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 1;
		parent.setLayout(gridLayout);

		 top = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 70;
		top.setLayoutData(gd);
		main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new StackLayout();
		main.setLayout(layout);
		createTop(top);

		createMain();
		createBtn(top);
		createWelCome();
	}

	/**
	 * 头部布局
	 * 
	 * @param top
	 */
	public void createTop(Composite top) {
		top.setBackgroundImage(img_bgi);
		top.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
//				GC gc = e.gc;
//				// Logo
//				Rectangle logo_bounds = img_logo.getBounds();
//				gc.drawImage(img_logo, 0, 0, logo_bounds.width, logo_bounds.height, 10, 10, logo_bounds.width, logo_bounds.height);
//
//				// Title
//				gc.setFont(font);
//				gc.drawText(ResourceHandler.getValue("title"), logo_bounds.width + 25, 20, true);
	            GC gc = e.gc;
				
				// Background Img
				Rectangle bgi_bounds = img_bgi.getBounds();
				gc.drawImage(img_bgi, 0, 0, bgi_bounds.width, bgi_bounds.height, 0, 0, e.width, bgi_bounds.height / 2);
				
				// Logo
				Rectangle logo_bounds = img_logo.getBounds();
				gc.drawImage(img_logo, 0, 0, logo_bounds.width, logo_bounds.height, 10, 10, logo_bounds.width, logo_bounds.height);
				
				// Title
				gc.setFont(font);
				gc.drawText(ResourceHandler.getValue("title"), logo_bounds.width + 25, 20, true);
			}
		});
	}

	/**
	 * 头部布局
	 * 
	 * @param top
	 */
	private void createBtn(Composite top) {
		top.setLayout(new GridLayout(5, false));
		Label lbl_beginer = new Label(top, SWT.NONE);
		lbl_beginer.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		lbl_beginer.setVisible(false);

		AbstractUICreater[] uiCreaters = ExtenSionFactory.getInstance().getAllCreater();
		for (AbstractUICreater creater : uiCreaters) {
			creater.setFolder(folder);
			Button button = creater.createTitleButton(top);
			button.setVisible(false);
			btns.add(button);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (layout.topControl != folder) {
						layout.topControl = folder;
						main.layout();
					}
				}
			});
		}
	}

	/**
	 * 主体布局
	 * 
	 * @param top
	 */
	private void createMain() {
		folder = new CTabFolder(main, SWT.NONE | SWT.CLOSE);
		// 设置圆角
		folder.setSimple(false);
		folder.setUnselectedCloseVisible(false);
		folder.setMRUVisible(true);
		folder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				if (folder.getItemCount() == 1) {
					if (layout.topControl != welCome) {
						layout.topControl = welCome;
						main.layout();
						for (Button btn : btns) {
							btn.setVisible(false);
							top.redraw();
						}
					}
				}
			}
		});
	}

	private void createWelCome() {
		welCome = new WelComeComposite(main, SWT.NONE, layout, folder, this);
		((StackLayout) main.getLayout()).topControl = welCome;
		main.layout();
	}

	@Override
	public void setFocus() {
	}

	public void showBtns() {
		for (Button btn : btns) {
			top.redraw();
			btn.setVisible(true);
		}
	}
}
