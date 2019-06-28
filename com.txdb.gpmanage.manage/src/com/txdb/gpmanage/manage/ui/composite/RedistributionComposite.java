package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.manage.entity.DatabaseEntity;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.TableQueryDialog;
import com.txdb.gpmanage.manage.ui.provider.RedistributionLabelProvider;

/**
 * 重分布管理面板
 * 
 * @author ws
 *
 */
public class RedistributionComposite extends IupperComposite {

	private TableViewer tv;

	private Label descLb;
	private java.util.List<DatabaseEntity> condision;
	private List<DatabaseEntity> databases;
	private long total = 0;
	private int perPage = 50;
	private long currentPage = 0;
	private long totalPage = 0;
	private Button upBtn;
	private Button nextBtn;
	private Label totalPageLb;

	RedistributionComposite(ManageComposite mianComposite, Composite parent,
			CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler
				.getValue("extendManage_redistribution_title"), ResourceHandler
				.getValue("extendManage_redistribution_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		condision = new ArrayList<DatabaseEntity>();
		composte.setLayout(new GridLayout(5, false));
		createTableSetComposite(composte);
		tv = new TableViewer(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		// 库名列
		tLayout.addColumnData(new ColumnWeightData(20));
		TableViewerColumn databaseNameTc = new TableViewerColumn(tv, SWT.NONE);
		databaseNameTc.getColumn().setText(ResourceHandler.getValue("database"));
		// schame名
		tLayout.addColumnData(new ColumnWeightData(20));
		TableViewerColumn schameNameTc = new TableViewerColumn(tv, SWT.NONE);
		schameNameTc.getColumn().setText(ResourceHandler.getValue("schema"));
		// 表名
		tLayout.addColumnData(new ColumnWeightData(20));
		TableViewerColumn tableNameTc = new TableViewerColumn(tv, SWT.NONE);
		tableNameTc.getColumn().setText(
				ResourceHandler.getValue("extendManage_redistribution_table"));
		// 分布级别
		tLayout.addColumnData(new ColumnWeightData(20));
		TableViewerColumn rankTc = new TableViewerColumn(tv, SWT.NONE);
		rankTc.getColumn().setText(
				ResourceHandler.getValue("extendManage_redistribution_level"));
		rankTc.setEditingSupport(new EditingSupport(tv) {

			@Override
			protected void setValue(Object element, Object value) {
				GPExpandStatusDetail host = (GPExpandStatusDetail) element;
				int oldValue = host.getRank();
				String rank = (String) value;
				if (!String.valueOf(oldValue).equals(rank))
					host.setModify(true);
				host.setRank(Integer.valueOf(rank));
				tv.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				GPExpandStatusDetail host = (GPExpandStatusDetail) element;
				return String.valueOf(host.getRank());
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(tv.getTable(), SWT.NONE);
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		tv.setLabelProvider(new RedistributionLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(new ArrayList<GPExpandStatusDetail>());
		List<GPExpandStatusDetail> l = new ArrayList<GPExpandStatusDetail>();
		tv.setInput(l);
		createdesc(composte);
		createFunctionComposite(composte);
	}

	private void createTableSetComposite(Composite composte) {
		Composite tableSetCom = new Composite(composte, SWT.NONE);
		tableSetCom.setLayout(new GridLayout(4, false));
		tableSetCom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				5, 1));
		// Label conditionLb = new Label(tableSetCom, SWT.NONE);
		// conditionLb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		// false));
		final Button modifyConditionBtn = new Button(tableSetCom, SWT.NONE);
		modifyConditionBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false));
		modifyConditionBtn.setText(ResourceHandler.getValue("search_condition"));

		final Button queryBtn = new Button(tableSetCom, SWT.NONE);
		queryBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		queryBtn.setText(ResourceHandler.getValue("query"));
		final Button saveModifyBtn = new Button(tableSetCom, SWT.NONE);
		saveModifyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false));
		saveModifyBtn.setText(ResourceHandler.getValue("ok"));
		modifyConditionBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				queryBtn.setEnabled(false);
				modifyConditionBtn.setEnabled(false);
				saveModifyBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (databases == null) {
							databases = ((ManageUiService) getService())
									.queryAllDatabase(gp, text,
											RedistributionComposite.this);
						}
						display.syncExec(new Runnable() {

							@Override
							public void run() {
								TableQueryDialog tqdlg = new TableQueryDialog(
										RedistributionComposite.this.getShell(),
										condision, databases,
										((ManageUiService) getService()), gp);
								int open = tqdlg.open();
								if (IDialogConstants.OK_ID == open) {
									condision = tqdlg.getCondision();
								}
								queryBtn.setEnabled(true);
								modifyConditionBtn.setEnabled(true);
								saveModifyBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
		queryBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				queryBtn.setEnabled(false);
				modifyConditionBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						total = ((ManageUiService) getService())
								.queryTableItem(gp, condision, text,
										RedistributionComposite.this);
						long by = total / perPage;
						long remainder = total % perPage;
						if (remainder == 0) {
							totalPage = by;
						} else {
							totalPage = by + 1;
						}
						currentPage = 1;
						if (total != 0) {
							final List<GPExpandStatusDetail> details = ((ManageUiService) getService())
									.queryTables(gp, condision,
											(currentPage - 1) * perPage,
											perPage, text,
											RedistributionComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(details);
									tv.refresh();
									updateDesc();
									updateBtn();
									queryBtn.setEnabled(true);
									modifyConditionBtn.setEnabled(true);
									stopBar();
								}
							});

						} else {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(new ArrayList<GPExpandStatusDetail>());
									tv.refresh();
									updateDesc();
									updateBtn();
									queryBtn.setEnabled(true);
									modifyConditionBtn.setEnabled(true);
									stopBar();
								}
							});
						}
					}
				}).start();
			}
		});
		saveModifyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				saveModifyBtn.setEnabled(false);
				queryBtn.setEnabled(false);
				modifyConditionBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).queryTableItem(gp,
								condision, text, RedistributionComposite.this);
						List<GPExpandStatusDetail> details = (List<GPExpandStatusDetail>) tv
								.getInput();
						List<GPExpandStatusDetail> modifyDetails = new ArrayList<GPExpandStatusDetail>();
						for (GPExpandStatusDetail detail : details) {
							if (detail.isModify())
								modifyDetails.add(detail);
						}
						if (modifyDetails.size() > 0) {
							((ManageUiService) getService()).updateTableRank(
									gp, modifyDetails, text,
									RedistributionComposite.this);
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								saveModifyBtn.setEnabled(true);
								queryBtn.setEnabled(true);
								modifyConditionBtn.setEnabled(true);
								stopBar();
							}
						});

					}
				}).start();
			}
		});
	}

	private void createFunctionComposite(Composite composte) {
		Composite parent = new Composite(composte, SWT.BORDER);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 5,
				1));
		GridLayout gl_parent = new GridLayout(4, false);
		gl_parent.horizontalSpacing = 20;
		parent.setLayout(gl_parent);

		final Button initBtn = new Button(parent, SWT.NONE);
		initBtn.setText(ResourceHandler
				.getValue("extendManage_redistribution_exe"));
		initBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1,
				1));
		final Button timeBtn = new Button(parent, SWT.CHECK | SWT.RIGHT_TO_LEFT);
		timeBtn.setText(ResourceHandler
				.getValue("extendManage_redistribution_time"));
		timeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1));
		// final cDateTime dt = new DateTime(parent, SWT.DATE | SWT.BORDER);
		final GPDateTime dt = new GPDateTime(parent, SWT.NONE);
		dt.canEnabled(false);
		dt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Button clearShameBtn = new Button(parent, SWT.NONE);
		clearShameBtn.setText(ResourceHandler
				.getValue("extendManage_redistribution_clearShema"));
		clearShameBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				false, 1, 1));
		initBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final boolean isSetTime = timeBtn.getSelection();
				final String time = dt.getTime();
				initBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (isSetTime) {
							((ManageUiService) getService()).exeExpand(gp,
									text, time, RedistributionComposite.this);
						} else {
							((ManageUiService) getService()).exeExpand(gp,
									text, RedistributionComposite.this);
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								initBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		clearShameBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				initBtn.setEnabled(true);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).cleanSchame(gp, text,
								RedistributionComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								initBtn.setEnabled(true);
								updateAll();
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		timeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dt.canEnabled(timeBtn.getSelection());
			}
		});

	}

	private void createdesc(Composite composte) {
		upBtn = new Button(composte, SWT.NONE);
		upBtn.setText(ResourceHandler.getValue("previous_page"));
		upBtn.setEnabled(false);
		upBtn.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		nextBtn = new Button(composte, SWT.NONE);
		nextBtn.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		nextBtn.setEnabled(false);
		nextBtn.setText(ResourceHandler.getValue("next_page"));
		// currentPageText = new Text(composte, SWT.BORDER);
		// currentPageText.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM,
		// false, false));
		totalPageLb = new Label(composte, SWT.NONE);
		totalPageLb.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true,
				false));
		totalPageLb.setText(ResourceHandler.getValue("total_page",
				new Integer[] { 0, 0 }));
		descLb = new Label(composte, SWT.NONE);
		descLb.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { 0 }));
		nextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				nextBtn.setEnabled(false);
				upBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {

						currentPage++;
						if (total != 0) {
							final List<GPExpandStatusDetail> details = ((ManageUiService) getService())
									.queryTables(gp, condision,
											(currentPage - 1) * perPage,
											perPage, text,
											RedistributionComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(details);
									tv.refresh();
									updateDesc();
									updateBtn();
									stopBar();
								}
							});

						} else {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(new ArrayList<GPExpandStatusDetail>());
									tv.refresh();
									updateDesc();
									updateBtn();
									stopBar();
								}
							});
						}
					}
				}).start();
			}
		});
		upBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				nextBtn.setEnabled(false);
				upBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {

						currentPage--;
						if (total != 0) {
							final List<GPExpandStatusDetail> details = ((ManageUiService) getService())
									.queryTables(gp, condision,
											(currentPage - 1) * perPage,
											perPage, text,
											RedistributionComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(details);
									tv.refresh();
									updateDesc();
									updateBtn();
									stopBar();
								}
							});

						} else {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									tv.setInput(new ArrayList<GPExpandStatusDetail>());
									tv.refresh();
									updateDesc();
									updateBtn();
									stopBar();
								}
							});
						}
					}
				}).start();
			}
		});
	}

	private void updateDesc() {
		descLb.setText(ResourceHandler.getValue("total",
				new Long[] { total }));
	}

	private void updateBtn() {
		if (currentPage > 1) {
			upBtn.setEnabled(true);
		} else {
			upBtn.setEnabled(false);
		}
		if (currentPage < totalPage) {
			nextBtn.setEnabled(true);
		} else {
			nextBtn.setEnabled(false);
		}
		totalPageLb.setText(ResourceHandler.getValue("total_page",
				new Long[] { currentPage, totalPage }));
	}

	public void updateAll() {
		total = 0;
		currentPage = 0;
		totalPage = 0;
		updateBtn();
		updateDesc();
		databases = null;
	}

	public void clearAll() {
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				total = 0;
				currentPage = 0;
				totalPage = 0;
				updateBtn();
				updateDesc();
				databases = null;

			}
		});
	}
}
