package com.txdb.gpmanage.install.ui.composite;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;
import com.txdb.gpmanage.install.ui.provider.SshTableLabelProvider;

/**
 * master节点安装面板
 * 
 * @author ws
 *
 */
public class SshComposite extends IupperComposite {
	private TableViewer checkboxTableViewer;
	private Label descLb;
private boolean isFinish=false;
	public SshComposite(InstallComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("ssh.title"), ResourceHandler.getValue("ssh.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		// 整体布局
		GridLayout gd_mainComposite = new GridLayout(4, false);
		gd_mainComposite.marginHeight = 20;
		gd_mainComposite.marginWidth = 20;
		gd_mainComposite.verticalSpacing = 0;
		composte.setLayout(gd_mainComposite);
		createCenterComposite(composte);
	}

	/**
	 * 设置master节点
	 * 
	 * @param mainComposite
	 */
	private void createCenterComposite(Composite mainComposite) {
//		final Button validateBtn = new Button(mainComposite, SWT.NONE);
//		validateBtn.setText(ResourceHandler.getValue("validate"));
//		validateBtn.setEnabled(false);
//		validateBtn.setToolTipText("暂不开放	");
		final Button exchangeBtn = new Button(mainComposite, SWT.NONE);
		exchangeBtn.setText(ResourceHandler.getValue("exchange"));
		exchangeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 4, 1));
//		Label outTimeLabel = new Label(mainComposite, SWT.NONE);
//		outTimeLabel.setText(ResourceHandler.getValue("ssh.set.timeout"));
//		outTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
//		final Text outTimeText = new Text(mainComposite, SWT.BORDER);
//		outTimeText.setText("5");
//		GridData gd_outTimeText = new GridData(SWT.RIGHT, SWT.TOP, false, false);
//		gd_outTimeText.widthHint = 50;
//		outTimeText.setLayoutData(gd_outTimeText);
		checkboxTableViewer = new TableViewer(mainComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.CENTER);
		Table table = checkboxTableViewer.getTable();
		table.setLinesVisible(true);// 表行
		table.setHeaderVisible(true);// 表格头部显示

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		tLayout.addColumnData(new ColumnWeightData(400));
		TableViewerColumn nameViewerColumn = new TableViewerColumn(checkboxTableViewer, SWT.CENTER);
		TableColumn nameColumn = nameViewerColumn.getColumn();
		nameColumn.setText(ResourceHandler.getValue("ssh.host.name"));
		tLayout.addColumnData(new ColumnWeightData(300));
		TableViewerColumn isMasterViewerColumn = new TableViewerColumn(checkboxTableViewer, SWT.CENTER);
		TableColumn isMasterColumn = isMasterViewerColumn.getColumn();

		isMasterColumn.setText(ResourceHandler.getValue("ip"));
		tLayout.addColumnData(new ColumnWeightData(300));
		TableViewerColumn isSuccessViewerColumn = new TableViewerColumn(checkboxTableViewer, SWT.CENTER);
		TableColumn isSuccessColumn = isSuccessViewerColumn.getColumn();
		isSuccessColumn.setText(ResourceHandler.getValue("ssh.isExchange"));
		checkboxTableViewer.setContentProvider(new ArrayContentProvider()); // 内容器
		checkboxTableViewer.setLabelProvider(new SshTableLabelProvider());// 标签器
		checkboxTableViewer.setInput(((InstallComposite) mianComposite).getInstallInfo().getInstallHost());// 设置表格中的数据
		descLb = new Label(mainComposite, SWT.NONE);
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { ((InstallComposite) mianComposite).getInstallInfo().getInstallHost().size() }));
//		validateBtn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				// TODO 等待接口实现
//				// String timeOut = outTimeText.getText();
//				// if (timeOut != null && timeOut.isEmpty()) {
//				// try {
//				// int i = Integer.valueOf(timeOut);
//				// } catch (Exception ex) {
//				// timeOut = "5";
//				// }
//				//
//				// } else {
//				// timeOut = "5";
//				// }
//				// SShService.getInstance().verificationSSH(((InstallComposite)
//				// mianComposite).getInstallInfo(), timeOut, text, validateBtn,
//				// checkboxTableViewer);
//			}
//		});
		exchangeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean previous =((InstallComposite)mianComposite).getInstllMaster().isFinish();
				if(!previous){
					text.append(ResourceHandler.getValue("ssh.previous.error"));
					return;
				}
                isFinish=false;
				exchangeBtn.setEnabled(false);
				startBar();
				final InstallInfo info = ((InstallComposite) mianComposite).getInstallInfo();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						isFinish=	((InstallUiService) getService()).exchangKey(info, text, SshComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								updateTable();
								exchangeBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	public void updateTable() {
		checkboxTableViewer.refresh();
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { ((InstallComposite) mianComposite).getInstallInfo().getInstallHost().size() }));
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

}
