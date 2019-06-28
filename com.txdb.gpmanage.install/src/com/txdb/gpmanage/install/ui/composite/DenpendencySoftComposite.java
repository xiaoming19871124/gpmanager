package com.txdb.gpmanage.install.ui.composite;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.log.LogUtil;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.service.InstallUiService;
import com.txdb.gpmanage.install.ui.provider.SoftTableLabelProvider;

/**
 * master节点安装面板
 * 
 * @author ws
 *
 */
public class DenpendencySoftComposite extends IupperComposite {
	/**
	 * 机器列表
	 */
	private ComboViewer hostList;
	private boolean isFinish = false;

	public DenpendencySoftComposite(InstallComposite mainComposite, Composite parent, CompositeCode code) {
		super(mainComposite, parent, code, ResourceHandler.getValue("soft.title"), ResourceHandler.getValue("soft.desc"));
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
		Composite functionCom = new Composite(mainComposite, SWT.NONE);
		functionCom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gd_functionCom = new GridLayout(2, false);
		gd_functionCom.horizontalSpacing = 20;
		functionCom.setLayout(gd_functionCom);
		Label masterLb = new Label(functionCom, SWT.NONE);
		masterLb.setText(ResourceHandler.getValue("soft.hostlist"));
		masterLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		hostList = new ComboViewer(functionCom, SWT.READ_ONLY);
		GridData gd_hostList = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_hostList.widthHint = 150;
		hostList.getCombo().setLayoutData(gd_hostList);
		hostList.setContentProvider(new ArrayContentProvider());
		hostList.setLabelProvider(new LabelProvider());
		hostList.setInput(((InstallComposite) mianComposite).getInstallInfo().getAllHost());

		final TableViewer tv = new TableViewer(mainComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.name"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.need.version"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.install.version"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("soft.check.result"));
		tv.setLabelProvider(new SoftTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		hostList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) hostList.getSelection();
				final Host host = (Host) selection.getFirstElement();
				if (host == null) {
					tv.setInput(null);
					tv.refresh();
					return;
				}
				LogUtil.info("checkout " + host.getName() + ":" + host.getIp() + "required soft");
				hostList.getCombo().setEnabled(false);
				startBar();
				tv.setInput(null);
				tv.refresh();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final List<GPRequiredSw> requirSw = ((InstallUiService) getService()).checkSoft(host, text, DenpendencySoftComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								tv.setInput(requirSw);
								hostList.getCombo().setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
	}

	public void updateList() {
		hostList.refresh();
	}
}
