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

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;
import com.txdb.gpmanage.install.ui.provider.InsatallInstanceTableLabelProvider;

/**
 * 初始化集群
 * 
 * @author ws
 *
 */
public class InstanceInstallComposite extends IupperComposite {
	private TableViewer checkboxTableViewer;
	private Label descLb;

	public InstanceInstallComposite(InstallComposite mainComposite, Composite parent, CompositeCode code) {
		super(mainComposite, parent, code, ResourceHandler.getValue("instance.install.title"), ResourceHandler.getValue("instance.install.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		// 整体布局
		GridLayout gd_mainComposite = new GridLayout(3, false);
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
		final Button installBtn = new Button(mainComposite, SWT.NONE);
		installBtn.setText(ResourceHandler.getValue("title.install"));
		installBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1));
		checkboxTableViewer = new TableViewer(mainComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.CENTER);
		Table table = checkboxTableViewer.getTable();
		table.setLinesVisible(true);// 表行
		table.setHeaderVisible(true);// 表格头部显示

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
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
		checkboxTableViewer.setContentProvider(new ArrayContentProvider()); // 内容器
		checkboxTableViewer.setLabelProvider(new InsatallInstanceTableLabelProvider());// 标签器
		checkboxTableViewer.setInput(((InstallComposite) mianComposite).getInstallInfo().getInstallHost());// 设置表格中的数据
		descLb = new Label(mainComposite, SWT.NONE);
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { 0 }));
		installBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean previous =((InstallComposite)mianComposite).getInitfile().isFinish();
				if(!previous){
					text.append(ResourceHandler.getValue("instance.previous.error"));
					return;
				}
				installBtn.setEnabled(false);
				startBar();
				final InstallInfo info = ((InstallComposite) mianComposite).getInstallInfo();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((InstallUiService)getService()).installInstance( info, text,InstanceInstallComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								installBtn.setEnabled(true);
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
}
