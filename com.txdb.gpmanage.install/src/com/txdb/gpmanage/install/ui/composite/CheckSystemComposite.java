package com.txdb.gpmanage.install.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;

/**
 * 系统检测
 * 
 * @author ws
 *
 */
public class CheckSystemComposite extends IupperComposite {
	public CheckSystemComposite(InstallComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("check.title"), ResourceHandler.getValue("check.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		GridLayout gd_mainComposite = new GridLayout(2, false);
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 50;
		gd_mainComposite.horizontalSpacing = 100;
		composte.setLayout(gd_mainComposite);
		createSelectMasterComposite(composte);
	}

	/**
	 * 设置master节点
	 * 
	 * @param mainComposite
	 */
	private void createSelectMasterComposite(Composite mainComposite) {
		Label systemCheckLb = new Label(mainComposite, SWT.NONE);
		systemCheckLb.setText(ResourceHandler.getValue("check.system.desc"));
		Label hardwareCheckLb = new Label(mainComposite, SWT.NONE);
		hardwareCheckLb.setText(ResourceHandler.getValue("check.hardware.desc"));
		final Button systemCheckBtn = new Button(mainComposite, SWT.NONE);
		systemCheckBtn.setText(ResourceHandler.getValue("check.system.anction"));
		final Button hardwareCheckBtn = new Button(mainComposite, SWT.NONE);
		hardwareCheckBtn.setText(ResourceHandler.getValue("check.hardware.anction"));
		systemCheckBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				systemCheckBtn.setEnabled(false);
				startBar();
				final InstallInfo info = ((InstallComposite) mianComposite).getInstallInfo();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).systemCheck(info, text, CheckSystemComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								systemCheckBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		hardwareCheckBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hardwareCheckBtn.setEnabled(false);
				startBar();
				final InstallInfo info = ((InstallComposite) mianComposite).getInstallInfo();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService) getService()).hardwareCheck(info, text, CheckSystemComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								hardwareCheckBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

}
