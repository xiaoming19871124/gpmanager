package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
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
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.manage.entity.GPConfigParam;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.ParamSetDialog;
import com.txdb.gpmanage.manage.ui.provider.ParamMangeTableLabelProvider;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class ParamMangeComposite extends IupperComposite {
	private TableViewer tv;
	private Label descLb;
	private List<GPConfigParam> params;
	private List<GPConfig> gpconfigs;

	ParamMangeComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("param_title"), ResourceHandler.getValue("param_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		params = new ArrayList<GPConfigParam>();
		composte.setLayout(new GridLayout());
		Composite function = new Composite(composte, SWT.NONE);
		function.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gl_function = new GridLayout(4, false);
		gl_function.horizontalSpacing = 20;
		function.setLayout(gl_function);
		function.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		final Button addBtn = new Button(function, SWT.PUSH);
		addBtn.setText(ResourceHandler.getValue("add"));
		final Button delBtn = new Button(function, SWT.PUSH);
		delBtn.setText(ResourceHandler.getValue("delete"));
		final Button modifyBtn = new Button(function, SWT.PUSH);
		modifyBtn.setText(ResourceHandler.getValue("modify"));

		final Button reLoadBtn = new Button(function, SWT.PUSH);
		reLoadBtn.setText(ResourceHandler.getValue("set"));
		reLoadBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		tv = new TableViewer(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("param_name"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("master_set_value"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("segment_set_value"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("master_current_value"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("segment_current_value"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("type"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("min_value"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("max_value"));

		tv.setLabelProvider(new ParamMangeTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(params);
		createdesc(composte);
		delBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delBtn.setEnabled(false);
				startBar();
				StructuredSelection selection = (StructuredSelection) tv.getSelection();
				if (selection != null && !selection.isEmpty()) {
					GPConfigParam param = (GPConfigParam) selection.getFirstElement();
					params.remove(param);
					tv.refresh();
				}
				delBtn.setEnabled(true);
				stopBar();
			}
		});
		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				addBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (gpconfigs == null || gpconfigs.size() < 1)
							gpconfigs = ((ManageUiService) getService()).queryAllParam(gp, text, ParamMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								stopBar();
								ParamSetDialog addDlg = new ParamSetDialog(ParamMangeComposite.this.getShell(), gpconfigs, gp, ((ManageUiService) getService()));
								int openID = addDlg.open();
								if (openID == IDialogConstants.CANCEL_ID) {
									addBtn.setEnabled(true);
									return;
								}
								GPConfigParam param = addDlg.getParam();
								params.add(param);
								tv.refresh();
								addBtn.setEnabled(true);
							}
						});

					}
				}).start();

			}
		});
		modifyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) tv.getSelection();
				if (selection == null || selection.isEmpty())
					return;
				final GPConfigParam info = (GPConfigParam) selection.getFirstElement();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				modifyBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (gpconfigs == null || gpconfigs.size() < 1)
							gpconfigs = ((ManageUiService) getService()).queryAllParam(gp, text, ParamMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								stopBar();
								ParamSetDialog addDlg = new ParamSetDialog(ParamMangeComposite.this.getShell(), gpconfigs, gp, ((ManageUiService) getService()), info);
								int openID = addDlg.open();
								if (openID == IDialogConstants.CANCEL_ID) {
									modifyBtn.setEnabled(true);
									return;
								}
								tv.refresh();
								modifyBtn.setEnabled(true);
							}
						});

					}
				}).start();

			}
		});
		reLoadBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (params.size() < 1)
					return;
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				reLoadBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).updateParam(gp, params, text, ParamMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								tv.refresh();
								reLoadBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
	}

	public void refreshAuthority() {
		tv.setInput(new ArrayList<PGHbaInfo>());
		tv.refresh();
		updateDesc();
	}

	private void createdesc(Composite composte) {
		descLb = new Label(composte, SWT.NONE);
		GridData gd_descLb = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		descLb.setLayoutData(gd_descLb);
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { 0 }));
	}

	@SuppressWarnings("unchecked")
	private void updateDesc() {
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { ((List<PGHbaInfo>) tv.getInput()).size() }));
	}

}
