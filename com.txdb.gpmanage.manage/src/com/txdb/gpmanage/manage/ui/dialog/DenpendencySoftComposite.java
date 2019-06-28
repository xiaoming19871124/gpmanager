package com.txdb.gpmanage.manage.ui.dialog;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * master节点安装面板
 * 
 * @author ws
 *
 */
public class DenpendencySoftComposite extends Composite {

	public DenpendencySoftComposite(Composite parent, int style) {
		super(parent, style);
		CreateOther(parent);
	}

	private void CreateOther(Composite mainComposite) {
		final TableViewer tv = new TableViewer(mainComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
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
	}
}
