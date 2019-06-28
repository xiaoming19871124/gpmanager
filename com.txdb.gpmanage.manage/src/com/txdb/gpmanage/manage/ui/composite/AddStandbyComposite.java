package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.common.SqliteDao;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.AddExtendHostDialog;

/**
 * add standby
 * 
 * @author ws
 *
 */
public class AddStandbyComposite extends IupperComposite {
	private GPSegmentInfo standbyInfo;
	private Text hostName;
	private Text port;
	private Text mode;
	private Text statu;
	private Text dataDir;
	private Host standbyHost;
	private boolean isContinu = false;
	private boolean isFinish = false;

	AddStandbyComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("add_standby_title"), ResourceHandler.getValue("add_standby_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout());
		Composite infoComp = new Composite(composte, SWT.NONE);
		infoComp.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
		infoComp.setLayout(new GridLayout(2, false));
		Label hostNameLb = new Label(infoComp, SWT.NONE);
		hostNameLb.setText(ResourceHandler.getValue("standby_hostname"));
		hostNameLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		hostName = new Text(infoComp, SWT.READ_ONLY);
		GridData gd_hostName = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_hostName.widthHint = 200;
		hostName.setLayoutData(gd_hostName);
		Label portLb = new Label(infoComp, SWT.NONE);
		portLb.setText(ResourceHandler.getValue("port"));
		portLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		port = new Text(infoComp, SWT.READ_ONLY);
		GridData gd_port = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_port.widthHint = 200;
		port.setLayoutData(gd_port);
		Label modeLb = new Label(infoComp, SWT.NONE);
		modeLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		modeLb.setText(ResourceHandler.getValue("mirror_sync"));
		mode = new Text(infoComp, SWT.READ_ONLY);
		GridData gd_mode = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_mode.widthHint = 200;
		mode.setLayoutData(gd_mode);
		Label statuLb = new Label(infoComp, SWT.NONE);
		statuLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		statuLb.setText(ResourceHandler.getValue("standby_state"));
		statu = new Text(infoComp, SWT.READ_ONLY);
		GridData gd_statu = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_statu.widthHint = 200;
		statu.setLayoutData(gd_statu);
		Label dataDirLb = new Label(infoComp, SWT.NONE);
		dataDirLb.setText(ResourceHandler.getValue("mirror_data_dir"));
		dataDirLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		dataDir = new Text(infoComp, SWT.READ_ONLY);
		GridData gd_dataDir = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_dataDir.widthHint = 200;
		dataDir.setLayoutData(gd_dataDir);
		createFunctionComposite(composte);
	}

	private void createFunctionComposite(Composite composte) {
		Composite parent = new Composite(composte, SWT.BORDER);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_parent = new GridLayout(2, true);
		gl_parent.horizontalSpacing = 80;
		parent.setLayout(gl_parent);

		Composite addMirrorComp = new Composite(parent, SWT.NONE);
		addMirrorComp.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_addMirrorComp = new GridLayout(2, false);
		gl_addMirrorComp.horizontalSpacing = 20;
		addMirrorComp.setLayout(gl_addMirrorComp);
		Label addMirrorLb = new Label(addMirrorComp, SWT.NONE);
		addMirrorLb.setText("*");
		addMirrorLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		Label addMirrordescLb = new Label(addMirrorComp, SWT.NONE);
		addMirrordescLb.setText(ResourceHandler.getValue("add_standby_desc"));
		addMirrordescLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		final Button addStandbyBtn = new Button(addMirrorComp, SWT.NONE);
		addStandbyBtn.setText(ResourceHandler.getValue("add_standby"));
		addStandbyBtn.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 4, 1));

		Composite stateComp = new Composite(parent, SWT.NONE);
		stateComp.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_stateComp = new GridLayout(2, false);
		gl_stateComp.horizontalSpacing = 20;
		stateComp.setLayout(gl_stateComp);

		Label stateLb = new Label(stateComp, SWT.NONE);
		stateLb.setText("*");
		stateLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		Label statedescLb = new Label(stateComp, SWT.NONE);
		statedescLb.setText(ResourceHandler.getValue("standby_check_desc"));
		statedescLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		// new Label(parent, SWT.NONE);
		final Button stateBtn = new Button(stateComp, SWT.NONE);
		stateBtn.setText(ResourceHandler.getValue("standby_check"));
		stateBtn.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 2, 1));
		addStandbyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addStandbyBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();

				new Thread(new Runnable() {
					@Override
					public void run() {
						standbyInfo = getService().queryStandby(gp);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (standbyInfo != null) {
									getService().setErrorMsg(text, ResourceHandler.getValue("standby_exist_error"));
									hostName.setText(standbyInfo.getHostname());
									port.setText(String.valueOf(standbyInfo.getPort()));
									mode.setText(ResourceHandler.getValue(standbyInfo.getMode()));
									statu.setText(ResourceHandler.getValue(standbyInfo.getStatus()));
									dataDir.setText(standbyInfo.getDatadir());
									addStandbyBtn.setEnabled(true);
									stopBar();
									isContinu = false;
									isFinish = true;
									return;
								}
								AddExtendHostDialog addDlg = new AddExtendHostDialog(AddStandbyComposite.this, null, true);
								int return_ID = addDlg.open();
								if (return_ID != IDialogConstants.OK_ID) {
									addStandbyBtn.setEnabled(true);
									stopBar();
									isContinu = false;
									isFinish = true;
									return;
								}
								standbyHost = addDlg.getHost();
								isContinu = true;
								isFinish = true;

							}
						});

						while (!isFinish) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						if (isContinu) {
							final List<Host> standbyHosts = new ArrayList<Host>();
							standbyHosts.add(standbyHost);
							boolean isSuccess = ((ManageUiService) getService()).expandStandby(gp, standbyHosts, text, AddStandbyComposite.this);
							if (isSuccess) {
								standbyInfo = ((ManageUiService) getService()).queryStandbyStatus(gp, text, AddStandbyComposite.this);
								gp.setStandbyHostName(standbyHost.getName());
								gp.setStandbyIp(standbyHost.getIp());
								gp.setStandbyRootName(standbyHost.getUserName());
								gp.setStandbyRootPwd(standbyHost.getPassword());
								gp.setHasStandby(1);

								SqlWhere where = new SqlWhere();
								where.addWhere("gpName", "=", gp.getGpName());
								SqliteDao.getInstance().updateGPEntity(gp, where);
								if (standbyInfo != null) {
									display.syncExec(new Runnable() {

										@Override
										public void run() {
											hostName.setText(standbyInfo.getHostname());
											port.setText(String.valueOf(standbyInfo.getPort()));
											mode.setText(ResourceHandler.getValue(standbyInfo.getMode()));
											statu.setText(ResourceHandler.getValue(standbyInfo.getStatus()));
											dataDir.setText(standbyInfo.getDatadir());
											// addStandbyBtn.setEnabled(true);
											// stopBar();
										}
									});

								}

							}
							display.syncExec(new Runnable() {

								@Override
								public void run() {
									addStandbyBtn.setEnabled(true);
									stopBar();

								}
							});
						}

					}
				}).start();

			}
		});
		stateBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				stateBtn.setEnabled(false);
				startBar();
				hostName.setText("");
				port.setText("");
				mode.setText("");
				statu.setText("");
				dataDir.setText("");
				new Thread(new Runnable() {
					@Override
					public void run() {
						standbyInfo = ((ManageUiService) getService()).queryStandbyStatus(gp, text, AddStandbyComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (standbyInfo != null) {
									hostName.setText(standbyInfo.getHostname());
									port.setText(String.valueOf(standbyInfo.getPort()));
									mode.setText(ResourceHandler.getValue(standbyInfo.getMode()));
									statu.setText(ResourceHandler.getValue(standbyInfo.getStatus()));
									dataDir.setText(standbyInfo.getDatadir());
								} else {
									getService().setErrorMsg(text, ResourceHandler.getValue("standby_no_exist"));
								}
								stateBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	public void loadStandbyState() {
		final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		standbyInfo = ((ManageUiService) getService()).queryStandbyStatus(gp, text, AddStandbyComposite.this);

		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (standbyInfo != null) {
					hostName.setText(standbyInfo.getHostname());
					port.setText(String.valueOf(standbyInfo.getPort()));
					mode.setText(ResourceHandler.getValue(standbyInfo.getMode()));
					statu.setText(ResourceHandler.getValue(standbyInfo.getStatus()));
					dataDir.setText(standbyInfo.getDatadir());
				}
			}
		});
	}

	public void refreshStandby() {
		hostName.setText("");
		port.setText("");
		mode.setText("");
		statu.setText("");
		dataDir.setText("");
	}
}
