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
import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.AddUserDialog;
import com.txdb.gpmanage.manage.ui.provider.DbUserMangeTableLabelProvider;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class UserMangeComposite extends IupperComposite {
	private TableViewer tv;
	private Label descLb;
	private List<DbUser> dbUsers = new ArrayList<DbUser>();

	UserMangeComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, "安全管理--用户管理", "集群数据库用户管理");

	}

	@Override
	public void createCenter(Composite composte) {
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

		// final Button reLoadBtn = new Button(function, SWT.PUSH);
		// reLoadBtn.setText("重新加载");
		// reLoadBtn
		// .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		tv = new TableViewer(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("userName"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("password"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("user_create_db"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("user_super"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("user_catupd"));
		// tLayout.addColumnData(new ColumnWeightData(20));
		// new TableColumn(table, SWT.NONE).setText("可否启动复制流");
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("user_valuntil"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("use_config"));
		tv.setLabelProvider(new DbUserMangeTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(dbUsers);
		createdesc(composte);
		delBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delBtn.setEnabled(false);
				startBar();
				StructuredSelection selection = (StructuredSelection) tv.getSelection();
				if (selection != null && !selection.isEmpty()) {
					final List<DbUser> users = selection.toList();
					final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
					new Thread(new Runnable() {
						@Override
						public void run() {
							final boolean isSuccess = ((ManageUiService) getService()).deleteUser(gp, users, text, UserMangeComposite.this);
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									if (isSuccess) {
										((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_del_success"));
										dbUsers.removeAll(users);
										tv.refresh();
										((ContainerComposite) UserMangeComposite.this.getParent()).getAuthorityMangeComposite().refreshUserList();
										updateDesc();
									} else {
										((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_del_success"));
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
				AddUserDialog addDlg = new AddUserDialog(UserMangeComposite.this.getShell(), null);
				if (addDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				final DbUser user = addDlg.getUser();
				addBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final boolean isSuccess = ((ManageUiService) getService()).addUser(gp, user, text, UserMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (isSuccess) {
									((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_add_success"));
									dbUsers.add(user);
									tv.refresh();
									((ContainerComposite) UserMangeComposite.this.getParent()).getAuthorityMangeComposite().refreshUserList();
									updateDesc();
								} else {
									((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_add_error"));
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
				StructuredSelection selection = (StructuredSelection) tv.getSelection();
				if (selection == null || selection.isEmpty())
					return;
				final DbUser user = (DbUser) selection.getFirstElement();
				AddUserDialog addDlg = new AddUserDialog(UserMangeComposite.this.getShell(), user);
				if (addDlg.open() == IDialogConstants.CANCEL_ID)
					return;
				modifyBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final boolean isSuccess = ((ManageUiService) getService()).modifyUser(gp, user, text, UserMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (isSuccess) {
									((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_modify_success"));
									tv.refresh();
									updateDesc();
								} else {
									((ManageUiService) getService()).setMsg(text, isSuccess, ResourceHandler.getValue("user_modify_error"));
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

	public void updateUser(List<DbUser> users) {
		// GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		// dbUsers = ((ManageUiService) getService()).loadUser(gp, text,
		// UserMangeComposite.this);
		this.dbUsers = users;
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				tv.setInput(dbUsers);
				tv.refresh();
				updateDesc();
			}
		});
	}

	private void createdesc(Composite composte) {
		descLb = new Label(composte, SWT.NONE);
		GridData gd_descLb = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		descLb.setLayoutData(gd_descLb);
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { 0 }));
	}

	private void updateDesc() {
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { dbUsers.size() }));
	}

}
