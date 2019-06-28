package com.txdb.gpmanage.install.ui.composite;

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
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;

/**
 * 初始化文件配置
 * 
 * @author ws
 *
 */
public class InitFileComposite extends IupperComposite {
	private Text nameText;
private boolean isFinish=false;
	public InitFileComposite(InstallComposite mainComposite, Composite parent,
			CompositeCode code) {
		super(mainComposite, parent, code, ResourceHandler
				.getValue("initfile.title"), ResourceHandler
				.getValue("initfile.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		// 整体布局
		GridLayout gd_mainComposite = new GridLayout();
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 20;
		composte.setLayout(gd_mainComposite);
		CreateOther(composte);
	}

	private void CreateOther(Composite mainComposite) {
		// 服务器参数配置面板
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gd_functionCom = new GridLayout(3, false);
		gd_functionCom.horizontalSpacing = 20;
		functionCom.setLayout(gd_functionCom);
		Label dbNameLb = new Label(functionCom, SWT.NONE);
		dbNameLb.setText(ResourceHandler.getValue("initfile.db.name"));
		dbNameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false,
				1, 1));
		final Text dbNameText = new Text(functionCom, SWT.BORDER);
		GridData gd_userNmaeText = new GridData(SWT.FILL, SWT.TOP, false,
				false, 2, 1);
		dbNameText.setToolTipText(ResourceHandler
				.getValue("initfile.database.tip"));
		gd_userNmaeText.widthHint = 180;
		dbNameText.setLayoutData(gd_userNmaeText);

		Label installPathLb = new Label(functionCom, SWT.NONE);
		installPathLb
				.setText(ResourceHandler.getValue("initfile.config.title"));
		installPathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		// 是否添加mirror
		final Button isAddMirrorBtn = new Button(functionCom, SWT.CHECK);
		isAddMirrorBtn.setForeground(Display.getCurrent().getSystemColor(
				SWT.COLOR_RED));
		isAddMirrorBtn.setText(ResourceHandler.getValue("initFile.add.mirror"));
		isAddMirrorBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 2, 1));
		// master端口
		Label portLb = new Label(functionCom, SWT.NONE);
		portLb.setText(ResourceHandler.getValue("initfile.port.master"));
		portLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1,
				1));
		final Text portText = new Text(functionCom, SWT.BORDER);
		portText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2,
				1));
		portText.setText("19200");
		// master主机名称
		Label nameLb = new Label(functionCom, SWT.NONE);
		nameLb.setText(ResourceHandler.getValue("initfile.name.master"));
		nameLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1,
				1));
		nameText = new Text(functionCom, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2,
				1));
		nameText.setEditable(false);
		nameText.setText("master");
		// master实例目录
		Label masterPathLb = new Label(functionCom, SWT.NONE);
		masterPathLb.setText(ResourceHandler.getValue("initfile.path.master"));
		masterPathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		final Text masterPathText = new Text(functionCom, SWT.BORDER);
		masterPathText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 2, 1));
		masterPathText.setText("/pgdata/data");
		// segment实例目录
		Label segPathLb = new Label(functionCom, SWT.NONE);
		segPathLb.setText(ResourceHandler.getValue("initfile.path.primary"));
		segPathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false,
				1, 1));
		final Text segPathText = new Text(functionCom, SWT.BORDER);
		segPathText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
				2, 1));
		segPathText.setText("/pgdata/data");
		// mirror实例目录
		Label mirrorPathLb = new Label(functionCom, SWT.NONE);
		mirrorPathLb.setText(ResourceHandler.getValue("initfile.path.mirror"));
		mirrorPathLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		final Text mirrorPathText = new Text(functionCom, SWT.BORDER);
		mirrorPathText.setEnabled(false);
		mirrorPathText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 2, 1));
		mirrorPathText.setText("/pgdata/data");

		Label primaryNumLb = new Label(functionCom, SWT.NONE);
		primaryNumLb.setText(ResourceHandler.getValue("initfile.numb.primary"));
		primaryNumLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		final Text primaryNumText = new Text(functionCom, SWT.BORDER);
		primaryNumText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 2, 1));
		primaryNumText.setText("2");
		// mirror_port_base
		Label mirrorPortLb = new Label(functionCom, SWT.NONE);
		mirrorPortLb.setText("MIRROR_PORT_BASE");
		mirrorPortLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		final Text mirrorPortText = new Text(functionCom, SWT.BORDER);
		mirrorPortText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 2, 1));
		mirrorPortText.setText("21000");
		mirrorPortText.setEnabled(false);
		// REPLICATION_PORT_BASE
		Label replicationPortLb = new Label(functionCom, SWT.NONE);
		replicationPortLb.setText("REPLICATION_PORT_BASE");
		replicationPortLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		final Text replicationPortText = new Text(functionCom, SWT.BORDER);
		replicationPortText.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				false, false, 2, 1));
		replicationPortText.setText("22000");
		replicationPortText.setEnabled(false);
		// "MIRROR_PORT_BASE"
		Label mirrorReplicationPortLb = new Label(functionCom, SWT.NONE);
		mirrorReplicationPortLb.setText("MIRROR_REPLICATION_PORT_BASE");
		mirrorReplicationPortLb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP,
				false, false, 1, 1));
		final Text mirrorReplicationPortText = new Text(functionCom, SWT.BORDER);
		mirrorReplicationPortText.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				false, false, 2, 1));
		mirrorReplicationPortText.setText("23000");
		mirrorReplicationPortText.setEnabled(false);
		Label mirrorModeLb = new Label(functionCom, SWT.NONE);
		mirrorModeLb.setText(ResourceHandler.getValue("initFile.mirror.type"));
		mirrorModeLb.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false,
				false, 1, 1));
		// final Group modeGroup = new Group(functionCom, SWT.NONE);
		// modeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
		// 1, 1));
		// modeGroup.setLayout(new FillLayout());
		final Button groupBtn = new Button(functionCom, SWT.RADIO);
		groupBtn.setText("grouped");
		groupBtn.setSelection(true);
		final Button spreadBtn = new Button(functionCom, SWT.RADIO);
		spreadBtn.setText("spread");
		spreadBtn.setEnabled(false);
		groupBtn.setEnabled(false);
		isAddMirrorBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isAddMirror = isAddMirrorBtn.getSelection();
				if (isAddMirror) {
					mirrorPathText.setEnabled(true);
					mirrorPortText.setEnabled(true);
					replicationPortText.setEnabled(true);
					mirrorReplicationPortText.setEnabled(true);
					spreadBtn.setEnabled(true);
					groupBtn.setEnabled(true);
				} else {
					mirrorPathText.setEnabled(false);
					mirrorPortText.setEnabled(false);
					replicationPortText.setEnabled(false);
					mirrorReplicationPortText.setEnabled(false);
					spreadBtn.setEnabled(false);
					groupBtn.setEnabled(false);
				}

			}

		});

		final Button runBtn = new Button(functionCom, SWT.NONE);
		runBtn.setText(ResourceHandler.getValue("set"));
		GridData gd_runBtn = new GridData(SWT.RIGHT, SWT.TOP, false, false, 3,
				1);
		runBtn.setLayoutData(gd_runBtn);
		runBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean previous =	((InstallComposite) mianComposite).getInstallSegment().isFinish();
				if(!previous){
					text.append(ResourceHandler.getValue("initfile.previous.error"));
					return;
				}
				final InstallInfo info = ((InstallComposite) mianComposite)
						.getInstallInfo();
				isFinish = false;
				
				String dbNmae = dbNameText.getText();
				String port = portText.getText();
				String masterPath = masterPathText.getText();
				String segPath = segPathText.getText();
				String mirrorPath = mirrorPathText.getText();
				String primaryNum = primaryNumText.getText();
				boolean isAddMirror = isAddMirrorBtn.getSelection();
				boolean isSpread = spreadBtn.getSelection();
				String mirrorPort = mirrorPortText.getText();

				String replicationPort = replicationPortText.getText();
				String mirrorReplicationPort = mirrorReplicationPortText
						.getText();
				if (!getService().isInt(port)) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.port.error"));
					return;
				}
				if (masterPath == null || masterPath.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.masterdir.error"));
					return;
				}
				if (segPath == null || segPath.isEmpty()) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.segdir.error"));
					return;
				}
				if (isAddMirror&&(mirrorPath == null || mirrorPath.isEmpty())) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.mirrordir.error"));
					return;
				}
				if (!getService().isInt(primaryNum)) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.segNum.error"));
					return;
				} else if (Integer.valueOf(primaryNum) < 1) {
					getService().setErrorMsg(text, ResourceHandler.getValue("initFile.segNum.error"));
					return;
				}
				if (isAddMirror && !getService().isInt(mirrorPort)) {
					getService().setErrorMsg(text,
							ResourceHandler.getValue("initFile.portBase.error"));
					return;
				}
				if (isAddMirror && !getService().isInt(replicationPort)) {
					getService().setErrorMsg(text,
							ResourceHandler.getValue("initFile.rPortBase.error"));
					return;
				}
				if (isAddMirror && !getService().isInt(mirrorReplicationPort)) {
					getService().setErrorMsg(text,
							ResourceHandler.getValue("initFile.mrPortBase.error"));
					return;
				}
				info.setDatabaseName(dbNmae);
				info.setPort(port);
				info.setMasterDataDir(masterPath);
				info.setSegmentDataDir(segPath);
				info.setMirrorDataDir(mirrorPath);
				info.setPrimaryNumb(Integer.valueOf(primaryNum));
				info.setAddMirror(isAddMirror);
				info.setSpread(isSpread);
				info.setMirrorPort(mirrorPort);
				info.setReplicationPort(replicationPort);
				info.setMirrorReplicationPort(mirrorReplicationPort);
				runBtn.setEnabled(false);
				startBar();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
					isFinish=	((InstallUiService) getService()).createInitFile(info,
								text, InitFileComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								runBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	public void setMasterName(String name) {
		nameText.setText(name);
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
}
