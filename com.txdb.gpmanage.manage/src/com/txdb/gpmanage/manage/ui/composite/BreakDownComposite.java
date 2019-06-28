package com.txdb.gpmanage.manage.ui.composite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.ChangeToStandbyDialog;

/**
 * 启动、停止、重启等操作
 * 
 * @author ws
 *
 */
public class BreakDownComposite extends IupperComposite {
	public BreakDownComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("breakdown_title"), ResourceHandler.getValue("breakdown_desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		GridLayout gd_mainComposite = new GridLayout(2, true);
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 50;
		gd_mainComposite.horizontalSpacing = 50;
		gd_mainComposite.verticalSpacing = 20;
		composte.setLayout(gd_mainComposite);
		createSelectMasterComposite(composte);
	}

	/**
	 * 设置master节点
	 * 
	 * @param mainComposite
	 */
	private void createSelectMasterComposite(Composite mainComposite) {
		// GPNode gp = ((ManageComposite) mianComposite).getGp();

		Group statuCheckGroup = new Group(mainComposite, SWT.NONE);
		statuCheckGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		statuCheckGroup.setLayout(new GridLayout(1, false));
		Label statuCheckLb = new Label(statuCheckGroup, SWT.NONE);
		statuCheckLb.setText(ResourceHandler.getValue("statu_check"));
		statuCheckLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		final Button checkBtn = new Button(statuCheckGroup, SWT.NONE);
		checkBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true));
		checkBtn.setText(ResourceHandler.getValue("check"));

		Group repairGroup = new Group(mainComposite, SWT.NONE);
		repairGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		repairGroup.setLayout(new GridLayout(1, false));
		Label repairLb = new Label(repairGroup, SWT.NONE);
		repairLb.setText(ResourceHandler.getValue("breakdown_reconstruct"));
		repairLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		final Button repairBtn = new Button(repairGroup, SWT.NONE);
		repairBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true));
		repairBtn.setText(ResourceHandler.getValue("reconstruct"));

		Group recoverGroup = new Group(mainComposite, SWT.NONE);
		recoverGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		recoverGroup.setLayout(new GridLayout(1, false));
		Label recoverLb = new Label(recoverGroup, SWT.NONE);
		recoverLb.setText(ResourceHandler.getValue("breakdown_recovery"));
		recoverLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		final Button reciverBtn = new Button(recoverGroup, SWT.NONE);
		reciverBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		reciverBtn.setText(ResourceHandler.getValue("recovery"));

		Group mTsGroup = new Group(mainComposite, SWT.NONE);
		mTsGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		mTsGroup.setLayout(new GridLayout(1, false));
		Label mTsLb = new Label(mTsGroup, SWT.NONE);
		mTsLb.setText(ResourceHandler.getValue("m_s_interchange"));
		mTsLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		final Button mTsBtn = new Button(mTsGroup, SWT.NONE);
		mTsBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		mTsBtn.setText(ResourceHandler.getValue("interchange"));
		checkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkBtn.setEnabled(false);
				mTsBtn.setEnabled(false);
				reciverBtn.setEnabled(false);
				repairBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).checkStatus(gp, text, BreakDownComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								checkBtn.setEnabled(true);
								mTsBtn.setEnabled(true);
								reciverBtn.setEnabled(true);
								repairBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		repairBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkBtn.setEnabled(false);
				mTsBtn.setEnabled(false);
				reciverBtn.setEnabled(false);
				repairBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).recoverSeg(gp, text, "-F","repair.segment", BreakDownComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								checkBtn.setEnabled(true);
								mTsBtn.setEnabled(true);
								reciverBtn.setEnabled(true);
								repairBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		reciverBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkBtn.setEnabled(false);
				mTsBtn.setEnabled(false);
				reciverBtn.setEnabled(false);
				repairBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).recoverSeg(gp, text, "-r","recover.segment", BreakDownComposite.this);

						display.syncExec(new Runnable() {
							@Override
							public void run() {
								checkBtn.setEnabled(true);
								mTsBtn.setEnabled(true);
								reciverBtn.setEnabled(true);
								repairBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		mTsBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkBtn.setEnabled(false);
				mTsBtn.setEnabled(false);
				reciverBtn.setEnabled(false);
				repairBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				if (gp.getStandbyHostName() == null || gp.getStandbyHostName().isEmpty()) {
					ChangeToStandbyDialog addDlg = new ChangeToStandbyDialog(BreakDownComposite.this);
					int return_ID = addDlg.open();
					if (return_ID != IDialogConstants.OK_ID) {
						checkBtn.setEnabled(true);
						mTsBtn.setEnabled(true);
						reciverBtn.setEnabled(true);
						repairBtn.setEnabled(true);
						stopBar();
						return;
					}
					final Host host = addDlg.getHost();
					gp.setStandbyHostName(host.getName());
					gp.setStandbyIp(host.getIp());
					gp.setStandbyRootName(host.getUserName());
					gp.setStandbyRootPwd(host.getPassword());
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isSuccess = ((ManageUiService) getService()).activateStandby(gp, text, BreakDownComposite.this);
						if (isSuccess) {
							gp.setMasterHostName(gp.getStandbyHostName());
							gp.setMasterIp(gp.getStandbyIp());
							gp.setMasterRootName(gp.getStandbyRootName());
							gp.setMasterRootPwd(gp.getStandbyRootPwd());
							gp.setStandbyHostName("");
							gp.setStandbyIp("");
							gp.setStandbyRootName("");
							gp.setStandbyRootPwd("");
							gp.setHasStandby(0);
							
							SqlWhere where = new SqlWhere();
							where.addWhere("gpName", "=", gp.getGpName());
							SqliteDao.getInstance().updateGPEntity(gp, where);
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								ContainerComposite parent = (ContainerComposite)BreakDownComposite.this.getParent();
								parent.updateAllValue();
								checkBtn.setEnabled(true);
								mTsBtn.setEnabled(true);
								reciverBtn.setEnabled(true);
								repairBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}
}
