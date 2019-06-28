package com.txdb.gpmanage.manage.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.manage.entity.DatabaseEntity;
import com.txdb.gpmanage.manage.entity.GPData;
import com.txdb.gpmanage.manage.entity.SchameEntity;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.provider.QueryTableLabelProvider;

/**
 * 添加扩展机器
 * 
 * @author ws
 *
 */
public class TableQueryDialog extends Dialog {
	// private Shell shell;
	private java.util.List<DatabaseEntity> condision;
	private java.util.List<DatabaseEntity> newCondision;
	private List<DatabaseEntity> databases;
	private ManageUiService service;
	private GPManagerEntity gp;
	private List<SchameEntity> dbSchames;

	public TableQueryDialog(Shell parentShell, java.util.List<DatabaseEntity> condision, List<DatabaseEntity> databases, ManageUiService service, GPManagerEntity gp) {
		super(parentShell);
		this.condision = condision;
		this.databases = databases;
		this.service = service;
		this.gp = gp;
		newCondision = new ArrayList<DatabaseEntity>();
		for (DatabaseEntity data : condision) {
			newCondision.add(data);
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// this.shell = newShell;
		newShell.setText(ResourceHandler.getValue("modify"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite top = new Composite(parent, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setLayout(new GridLayout(2, false));
		Group g = new Group(top, SWT.NONE);
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		g.setLayout(new GridLayout(2, false));
		Button cleanBtn = new Button(top, SWT.NONE);
		cleanBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true));
		cleanBtn.setText(ResourceHandler.getValue("clean"));
		Button addBtn = new Button(top, SWT.NONE);
		addBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));
		addBtn.setText(ResourceHandler.getValue("add"));
		Label databaseLb = new Label(g, SWT.NONE);
		databaseLb.setText(ResourceHandler.getValue("database"));
		Label schameLb = new Label(g, SWT.NONE);
		schameLb.setText(ResourceHandler.getValue("schame"));
		final ListViewer databaseList = new ListViewer(g, SWT.SINGLE | SWT.BORDER);
		databaseList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		databaseList.setLabelProvider(new LabelProvider());
		databaseList.setContentProvider(new ArrayContentProvider());
		databaseList.setInput(databases);
		final ListViewer schameList = new ListViewer(g, SWT.MULTI | SWT.BORDER);
		schameList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		schameList.setLabelProvider(new LabelProvider());
		schameList.setContentProvider(new ArrayContentProvider());

		Label tableNameLb = new Label(g, SWT.NONE);
		tableNameLb.setText(ResourceHandler.getValue("extendManage_redistribution_table"));
		tableNameLb.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 2, 1));

		final Text tableNameText = new Text(g, SWT.NONE);
		tableNameText.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));

		final TableViewer tv = new TableViewer(top, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		// 库名列
		tLayout.addColumnData(new ColumnWeightData(10));
		TableViewerColumn databaseNameTc = new TableViewerColumn(tv, SWT.NONE);
		databaseNameTc.getColumn().setText(ResourceHandler.getValue("database"));
		// schame名
		tLayout.addColumnData(new ColumnWeightData(30));
		TableViewerColumn schameNameTc = new TableViewerColumn(tv, SWT.NONE);
		schameNameTc.getColumn().setText(ResourceHandler.getValue("schame"));
		// 表名
		tLayout.addColumnData(new ColumnWeightData(10));
		TableViewerColumn tableNameTc = new TableViewerColumn(tv, SWT.NONE);
		tableNameTc.getColumn().setText(ResourceHandler.getValue("extendManage_redistribution_table"));
		tv.setLabelProvider(new QueryTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(newCondision);
		final Table tb = tv.getTable();

		tb.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (e.button == 3) {
					Menu menu = new Menu(tb);
					tb.setMenu(menu);
					MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText(ResourceHandler.getValue("delete"));
					item.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							StructuredSelection dbSelection = (StructuredSelection) tv.getSelection();
							DatabaseEntity dbEntity = (DatabaseEntity) dbSelection.getFirstElement();
							newCondision.remove(dbEntity);
							tv.refresh();
						}
					});
				}
			}
		});
		databaseList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection dbSelection = (StructuredSelection) databaseList.getSelection();
				final DatabaseEntity dbEntity = (DatabaseEntity) dbSelection.getFirstElement();
				final Display display = TableQueryDialog.this.getShell().getDisplay();
				if (dbEntity == null) {
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						dbSchames = dbEntity.getSchame();
						if (dbSchames == null) {
							String dbName = dbEntity.getName();
							dbSchames = service.queryAllSchemaByDbName(gp, dbName);
							dbEntity.setSchame(dbSchames);
						}
						display.syncExec(new Runnable() {

							@Override
							public void run() {
								schameList.setInput(dbSchames);
								schameList.refresh();

							}
						});
					}
				}).start();
			}
		});
		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection dbSelection = (StructuredSelection) databaseList.getSelection();
				DatabaseEntity dbEntity = (DatabaseEntity) dbSelection.getFirstElement();
				StructuredSelection schameSelection = (StructuredSelection) schameList.getSelection();
				String databaseName = GPData.ALl_NAME;
				if (dbEntity != null) {
					databaseName = dbEntity.getName();
				}
				java.util.List<SchameEntity> schames = (java.util.List<SchameEntity>) schameSelection.toList();
				String tableName = tableNameText.getText();
				if (tableName == null || tableName.isEmpty()) {
					tableName = GPData.ALl_NAME;
				}
				if (databaseName.equals(GPData.ALl_NAME) && tableName.equals(GPData.ALl_NAME) && schames.isEmpty()) {
					return;
				}
				DatabaseEntity entity = new DatabaseEntity();
				entity.setName(databaseName);
				entity.setSchame(schames);
				entity.setTableName(tableName);
				newCondision.add(entity);
				databaseList.setSelection(StructuredSelection.EMPTY);
				schameList.setSelection(StructuredSelection.EMPTY);
				schameList.setInput(new ArrayList<SchameEntity>());
				databaseList.refresh();
				schameList.refresh();
				tableNameText.setText("");
				tv.refresh();
			}
		});
		cleanBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				databaseList.setSelection(StructuredSelection.EMPTY);
				schameList.setSelection(StructuredSelection.EMPTY);
				schameList.setInput(new ArrayList<SchameEntity>());
				databaseList.refresh();
				schameList.refresh();
				tableNameText.setText("");
			}
		});
		return top;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {
		Composite comp = (Composite) getButtonBar();// 取得按钮面板
		super.createButton(comp, IDialogConstants.OK_ID, ResourceHandler.getValue("ok"), false);
		super.createButton(comp, IDialogConstants.CANCEL_ID, ResourceHandler.getValue("cancel"), false);
		super.initializeBounds();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			condision = newCondision;
			setReturnCode(IDialogConstants.OK_ID);
		} else {
			setReturnCode(IDialogConstants.CANCEL_ID);
		}
		close();
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		return new Point(point.x + 200, point.y + 200);// super.getInitialSize()可以得到原来对话框的大小
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public java.util.List<DatabaseEntity> getCondision() {
		return condision;
	}

}
