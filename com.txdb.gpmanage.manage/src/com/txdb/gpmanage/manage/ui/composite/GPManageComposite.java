package com.txdb.gpmanage.manage.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;

/**
 * 启动、停止、重启等操作
 * 
 * @author ws
 *
 */
public class GPManageComposite extends IupperComposite {
	private Label gpMasterName;
	private Label gpMasterIp;
	private Label gpMasterDataDir;
	private Label gpSegmentDataDir;
	private Label gpMirrorDataDir;
	private Label gpinstallDir;
	private Label gpUser;

	public GPManageComposite(ManageComposite mianComposite, Composite parent,
			CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler
				.getValue("system_gpmanage_title"), ResourceHandler
				.getValue("system_gpmanage_desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		GridLayout gd_mainComposite = new GridLayout(6, false);
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
		Label gpMasterNameLb = new Label(mainComposite, SWT.NONE);
		gpMasterNameLb
				.setText(ResourceHandler.getValue("initfile.name.master"));
		gpMasterNameLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		gpMasterName = new Label(mainComposite, SWT.NONE);

		gpMasterName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));
		Label gpMasterIpLb = new Label(mainComposite, SWT.NONE);
		gpMasterIpLb.setText(ResourceHandler.getValue("master_ip"));
		gpMasterIpLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		gpMasterIp = new Label(mainComposite, SWT.NONE);
		gpMasterIp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));

		Label gpUserLb = new Label(mainComposite, SWT.NONE);
		gpUserLb.setText(ResourceHandler.getValue("gp_current_user"));
		gpUserLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		gpUser = new Label(mainComposite, SWT.NONE);
		gpUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5,
				1));

		Label gpMasterDirLb = new Label(mainComposite, SWT.NONE);
		gpMasterDirLb.setText(ResourceHandler.getValue("initfile.path.master"));
		gpMasterDirLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		gpMasterDataDir = new Label(mainComposite, SWT.NONE);
		gpMasterDataDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));

		Label gpSegmentDirLb = new Label(mainComposite, SWT.NONE);
		gpSegmentDirLb
				.setText(ResourceHandler.getValue("initfile.path.primary"));
		gpSegmentDirLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		gpSegmentDataDir = new Label(mainComposite, SWT.NONE);
		gpSegmentDataDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));

		Label gpMirrorDirLb = new Label(mainComposite, SWT.NONE);
		gpMirrorDirLb.setText(ResourceHandler.getValue("initfile.path.mirror"));
		gpMirrorDirLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		gpMirrorDataDir = new Label(mainComposite, SWT.NONE);
		gpMirrorDataDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));

		Label gpinstallDirLb = new Label(mainComposite, SWT.NONE);
		gpinstallDirLb.setText(ResourceHandler.getValue("install_path"));
		gpinstallDirLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		gpinstallDir = new Label(mainComposite, SWT.NONE);
		gpinstallDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 5, 1));
		// new Label(mainComposite, SWT.NONE);
		final Button startGpBtn = new Button(mainComposite, SWT.NONE);
		startGpBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false,
				true));
		startGpBtn.setText(ResourceHandler.getValue("gp_start"));
		final Button startMasterBtn = new Button(mainComposite, SWT.NONE);
		startMasterBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM,
				false, false));
		startMasterBtn.setText(ResourceHandler.getValue("gp_start_master"));
		final Button restartBtn = new Button(mainComposite, SWT.NONE);
		restartBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false,
				false));
		restartBtn.setText(ResourceHandler.getValue("gp_restart"));
		final Button stopGpBtn = new Button(mainComposite, SWT.NONE);
		stopGpBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false,
				false));
		stopGpBtn.setText(ResourceHandler.getValue("gp_stop"));
		final Button stateBtn = new Button(mainComposite, SWT.NONE);
		stateBtn.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false,
				false));
		stateBtn.setText(ResourceHandler.getValue("gp_status"));
		Label tooltip = new Label(mainComposite, SWT.NONE);
		tooltip.setText(ResourceHandler.getValue("gpmanage_tip"));
		tooltip.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_RED));
		tooltip.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false,
				4, 1));
		Link setLink = new Link(mainComposite, SWT.NONE);
		setLink.setText("<a>" + ResourceHandler.getValue("set") + "</a>");
		refreshMaster();
		startGpBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startGpBtn.setEnabled(false);
				stopGpBtn.setEnabled(false);
				restartBtn.setEnabled(false);
				startMasterBtn.setEnabled(false);
				stateBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite)
						.getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).startGp(gp, text,
								GPManageComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								startGpBtn.setEnabled(true);
								stopGpBtn.setEnabled(true);
								restartBtn.setEnabled(true);
								startMasterBtn.setEnabled(true);
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		stopGpBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startGpBtn.setEnabled(false);
				stopGpBtn.setEnabled(false);
				restartBtn.setEnabled(false);
				startMasterBtn.setEnabled(false);
				stateBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite)
						.getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).stopGp(gp, text,
								GPManageComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								startGpBtn.setEnabled(true);
								stopGpBtn.setEnabled(true);
								restartBtn.setEnabled(true);
								startMasterBtn.setEnabled(true);
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		restartBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startGpBtn.setEnabled(false);
				stopGpBtn.setEnabled(false);
				restartBtn.setEnabled(false);
				startMasterBtn.setEnabled(false);
				stateBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite)
						.getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).restartGp(gp, text,
								GPManageComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								startGpBtn.setEnabled(true);
								stopGpBtn.setEnabled(true);
								restartBtn.setEnabled(true);
								startMasterBtn.setEnabled(true);
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		startMasterBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startGpBtn.setEnabled(false);
				stopGpBtn.setEnabled(false);
				restartBtn.setEnabled(false);
				startMasterBtn.setEnabled(false);
				stateBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite)
						.getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).startMasterGp(gp,
								text, GPManageComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								startGpBtn.setEnabled(true);
								stopGpBtn.setEnabled(true);
								restartBtn.setEnabled(true);
								startMasterBtn.setEnabled(true);
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		stateBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startGpBtn.setEnabled(false);
				stopGpBtn.setEnabled(false);
				restartBtn.setEnabled(false);
				startMasterBtn.setEnabled(false);
				stateBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite)
						.getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).stateGp(gp, text,
								GPManageComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								startGpBtn.setEnabled(true);
								stopGpBtn.setEnabled(true);
								restartBtn.setEnabled(true);
								startMasterBtn.setEnabled(true);
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		setLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ManageComposite) mianComposite)
						.setSelection(CompositeCode.CONNICTMANGE);
			}

		});
	}

	public void refreshMaster() {
		GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		gpMasterName.setText(gp.getMasterHostName());
		gpUser.setText(gp.getGpUserName());
		gpMasterIp.setText(gp.getMasterIp());
		gpMasterDataDir.setText(gp.getMasterDataDir());
		gpSegmentDataDir.setText(gp.getSegmentDataDir());
		if (gp.getHasMirror() != 0)
			gpMirrorDataDir.setText(gp.getMirrorDataDir());
		else
			gpMirrorDataDir.setText("no mirror");
		gpinstallDir.setText(gp.getInstallPath());
	}
}
