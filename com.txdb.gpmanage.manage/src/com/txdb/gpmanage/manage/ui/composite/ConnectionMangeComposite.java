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
import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.AuthorityDialog;
import com.txdb.gpmanage.manage.ui.provider.ConnectionMangeTableLabelProvider;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class ConnectionMangeComposite extends IupperComposite {
	private TableViewer tv;
	private Label descLb;
	private boolean isReload = false;
	private List<PGHbaInfo> pgHbaInfo = new ArrayList<PGHbaInfo>();

	ConnectionMangeComposite(ManageComposite mianComposite, Composite parent,
			CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("connection_title"),
				ResourceHandler.getValue("connection_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout());
		Composite function = new Composite(composte, SWT.NONE);
		function.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gl_function = new GridLayout(4, false);
		gl_function.horizontalSpacing = 20;
		function.setLayout(gl_function);
		function.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_GRAY));
		final Button addBtn = new Button(function, SWT.PUSH);
		addBtn.setText(ResourceHandler.getValue("add"));
		final Button delBtn = new Button(function, SWT.PUSH);
		delBtn.setText(ResourceHandler.getValue("delete"));
		final Button modifyBtn = new Button(function, SWT.PUSH);
		modifyBtn.setText(ResourceHandler.getValue("modify"));

		final Button reLoadBtn = new Button(function, SWT.PUSH);
		reLoadBtn.setText(ResourceHandler.getValue("connection_reload"));
		reLoadBtn
				.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		tv = new TableViewer(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("connection_type"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("database"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("user_name"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("ip"));
		tLayout.addColumnData(new ColumnWeightData(30));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("connection_method"));
		tv.setLabelProvider(new ConnectionMangeTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(pgHbaInfo);
		createdesc(composte);
		reLoadBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reLoadBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (!isReload) {
							pgHbaInfo = ((ManageUiService) getService())
									.loadPGHba(gp, text,
											ConnectionMangeComposite.this);
							isReload = true;
						} else {
							pgHbaInfo = ((ManageUiService) getService())
									.reloadPGHba(gp, text,
											ConnectionMangeComposite.this);
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (pgHbaInfo == null)
									pgHbaInfo = new ArrayList<PGHbaInfo>();
								tv.setInput(pgHbaInfo);
								tv.refresh();
								updateDesc();
								reLoadBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
		delBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delBtn.setEnabled(false);
				startBar();
				StructuredSelection selection = (StructuredSelection) tv
						.getSelection();
				if (selection != null && !selection.isEmpty()) {
					final PGHbaInfo info = (PGHbaInfo) selection.getFirstElement();
					final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();

					final Display display = Display.getCurrent();
					new Thread(new Runnable() {
						@Override
						public void run() {
							List<PGHbaInfo> infos = new ArrayList<PGHbaInfo>();
							info.setModifyType(PGHbaInfo.MODIFY_RM);
							infos.add(info);
							final boolean isSuccess = ((ManageUiService) getService())
									.updateAuthority(gp, infos, text,
											ConnectionMangeComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									if (isSuccess) {
										List<PGHbaInfo> allInfo = (List<PGHbaInfo>) tv
												.getInput();
										allInfo.remove(info);
										tv.refresh();
										updateDesc();
									}
									delBtn.setEnabled(true);
									stopBar();
								}
							});
						}
					}).start();

				}
			}
		});
		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AuthorityDialog addDlg = new AuthorityDialog(
						ConnectionMangeComposite.this.getShell(), null);
				if (addDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				final PGHbaInfo info = addDlg.getHost();
				addBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<PGHbaInfo> infos = new ArrayList<PGHbaInfo>();
						infos.add(info);
						final boolean isSuccess = ((ManageUiService) getService())
								.updateAuthority(gp, infos, text,
										ConnectionMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (isSuccess) {
									List<PGHbaInfo> allInfo = (List<PGHbaInfo>) tv
											.getInput();
									allInfo.add(info);
									tv.refresh();
									updateDesc();
								}
								addBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		modifyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) tv
						.getSelection();
				if (selection == null || selection.isEmpty())
					return;
				final PGHbaInfo info = (PGHbaInfo) selection.getFirstElement();
				AuthorityDialog addDlg = new AuthorityDialog(
						ConnectionMangeComposite.this.getShell(), info);
				if (addDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				final PGHbaInfo newInfo = addDlg.getHost();
				modifyBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<PGHbaInfo> infos = new ArrayList<PGHbaInfo>();
						infos.add(info);
						infos.add(newInfo);
						final boolean isSuccess = ((ManageUiService) getService())
								.updateAuthority(gp, infos, text,
										ConnectionMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (isSuccess) {
									List<PGHbaInfo> allInfo = (List<PGHbaInfo>) tv
											.getInput();
									int index = allInfo.indexOf(info);
									allInfo.remove(info);
									allInfo.add(index, newInfo);
									tv.refresh();
									updateDesc();
								}
								modifyBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	public void updateAuthority() {
		 GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		pgHbaInfo = ((ManageUiService) getService()).loadPGHba(gp, text,
				ConnectionMangeComposite.this);
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (pgHbaInfo == null)
					pgHbaInfo = new ArrayList<PGHbaInfo>();
				tv.setInput(pgHbaInfo);
				tv.refresh();
				updateDesc();
			}
		});
	}

	public void refreshAuthority() {
		tv.setInput(new ArrayList<PGHbaInfo>());
		tv.refresh();
		isReload = false;
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
		descLb.setText(ResourceHandler.getValue("total",
				new Integer[] { ((List<PGHbaInfo>) tv.getInput()).size() }));
	}

}
