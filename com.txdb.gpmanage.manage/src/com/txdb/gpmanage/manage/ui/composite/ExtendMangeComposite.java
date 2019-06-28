package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.dialog.AddExtendHostDialog;
import com.txdb.gpmanage.manage.ui.provider.ExtendMangeTableLabelProvider;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class ExtendMangeComposite extends IupperComposite {
	private CheckboxTableViewer tv;
	private Button spreadBtn;
	private Text numbText;
	private Label descLb;

	ExtendMangeComposite(ManageComposite mianComposite, Composite parent,
			CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler
				.getValue("extendManage_extend_title"), ResourceHandler
				.getValue("extendManage_extend_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout());
		Composite function = new Composite(composte, SWT.NONE);
		function.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gl_function = new GridLayout(9, false);
		gl_function.horizontalSpacing = 20;
		function.setLayout(gl_function);
		function.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_GRAY));
		Button addBtn = new Button(function, SWT.PUSH);
		addBtn.setText(ResourceHandler.getValue("add"));
		SelectionAdapter adapter = creatBtnListener();
		addBtn.addSelectionListener(adapter);
		Button delBtn = new Button(function, SWT.PUSH);
		delBtn.setText(ResourceHandler.getValue("delete"));
		delBtn.addSelectionListener(adapter);
		Button modifyBtn = new Button(function, SWT.PUSH);
		modifyBtn.setText(ResourceHandler.getValue("modify"));
		modifyBtn.addSelectionListener(adapter);
		Button selectAllBtn = new Button(function, SWT.PUSH);
		selectAllBtn.setText(ResourceHandler.getValue("select_all"));
		selectAllBtn.addSelectionListener(adapter);

		Label segNumbLb = new Label(function, SWT.NONE);
		segNumbLb.setText(ResourceHandler.getValue("extendManage_seg_numb"));
		segNumbLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1));
		numbText = new Text(function, SWT.BORDER);
		numbText.setText("0");
		GridData gd_numbText = new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1);
		gd_numbText.widthHint = 20;
		numbText.setLayoutData(gd_numbText);

		Label mirrorModeLb = new Label(function, SWT.NONE);
		mirrorModeLb.setText(ResourceHandler.getValue("extendManage_mirror_type"));
		mirrorModeLb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		Button groupBtn = new Button(function, SWT.RADIO);
		groupBtn.setText("grouped");
		groupBtn.setSelection(true);
		spreadBtn = new Button(function, SWT.RADIO);
		spreadBtn.setText("spread");
		tv = CheckboxTableViewer
				.newCheckList(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
						| SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler
				.getValue("hostMange.table.hostName"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE)
				.setText(ResourceHandler.getValue("ip"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler
				.getValue("role"));
		tv.setLabelProvider(new ExtendMangeTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		List<Host> hosts = new ArrayList<Host>();
		tv.setInput(hosts);
		createdesc(composte);
		createFunctionComposite(composte);
	}

	private void createFunctionComposite(Composite composte) {
		Composite parent = new Composite(composte, SWT.BORDER);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_parent = new GridLayout(3, false);
		gl_parent.horizontalSpacing = 20;
		parent.setLayout(gl_parent);

		Label lb = new Label(parent, SWT.NONE);
		lb.setText("*");
		lb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Label descLb = new Label(parent, SWT.NONE);
		descLb.setText(ResourceHandler
				.getValue("extendManage_extend_anctionDesc"));
		descLb.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				2, 1));
		new Label(parent, SWT.NONE);
		final Button initBtn = new Button(parent, SWT.PUSH);
		initBtn.setText(ResourceHandler.getValue("init"));
		initBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1,
				1));
		final Button backBtn = new Button(parent, SWT.PUSH);
		backBtn.setText(ResourceHandler.getValue("rollback"));
		backBtn.setToolTipText(ResourceHandler.getValue("rollback_tip"));
		backBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1,
				1));
		initBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				Object[] objects = tv.getCheckedElements();
				final List<Host> segHost = new ArrayList<Host>();

				if (objects.length < 1)
					return;
				for (Object o : objects) {
					Host host = (Host) o;
					segHost.add(host);
				}
				initBtn.setEnabled(false);
				backBtn.setEnabled(false);
				startBar();
				final String numbStr = numbText.getText();
				final boolean isSpread = spreadBtn.getSelection();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (segHost.size() > 0) {
							int numb = 0;
							try {
								numb = Integer.valueOf(numbStr);
							} catch (NumberFormatException e) {
								((ManageUiService) getService()).setMsg(text,
										false, ResourceHandler
												.getValue("mirror.numb.error"));
								display.syncExec(new Runnable() {
									@Override
									public void run() {
										initBtn.setEnabled(true);
										stopBar();
									}
								});
								return;
							}
							boolean isNeedRollBack = ((ManageUiService) getService())
									.initSegment(gp, segHost, isSpread, numb,
											text, ExtendMangeComposite.this);
							if (isNeedRollBack) {
								display.syncExec(new Runnable() {
									@Override
									public void run() {
										MessageBox messageBox = new MessageBox(
												ExtendMangeComposite.this
														.getShell(),
												SWT.ICON_QUESTION | SWT.OK
														| SWT.CANCEL);
										messageBox.setMessage(ResourceHandler.getValue("extend_fail_error"));
										int returnValue = messageBox.open();
										if (returnValue == SWT.OK)
											((ManageUiService) getService())
													.rollBack(
															gp,
															text,
															ExtendMangeComposite.this);
									}
								});
							}
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								initBtn.setEnabled(true);
								backBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();

			}
		});
		backBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				backBtn.setEnabled(false);
				startBar();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				final Display display = Display.getCurrent();
				new Thread(new Runnable() {
					@Override
					public void run() {
						((ManageUiService) getService()).rollBack(gp, text,
								ExtendMangeComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								backBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
	}

	private void createdesc(Composite composte) {
		descLb = new Label(composte, SWT.NONE);
		descLb.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { 0 }));
	}

	@SuppressWarnings("unchecked")
	private void updateDesc() {
		descLb.setText(ResourceHandler.getValue("total",
				new Integer[] { ((List<Host>) tv.getInput()).size() }));
	}

	/**
	 * 创建buttion选择监听
	 * 
	 * @param left
	 *            源列表
	 * @param right
	 *            目标列表
	 * @return 监听
	 */
	private SelectionAdapter creatBtnListener() {
		// 创建事件监听类，为内部类
		SelectionAdapter listener = new SelectionAdapter() {
			// 按钮单击事件处理的方法
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				// 取得触发事件的空间对象（按钮）
				Button bt = (Button) e.widget;
				if (bt.getText().equals(ResourceHandler.getValue("add"))) {
					AddExtendHostDialog addDlg = new AddExtendHostDialog(
							ExtendMangeComposite.this, null, false);
					int return_ID = addDlg.open();
					if (return_ID == IDialogConstants.OK_ID) {
						List<Host> hosts = (List<Host>) tv.getInput();
						Host host = addDlg.getHost();
						hosts.add(host);
						tv.refresh();
						updateDesc();
						text.append(ResourceHandler.getValue(
								"hostMange.msg.addHost",
								new String[] { host.getName() + " : "
										+ host.getIp() + " : "
										+ host.getRole().getName() }));

					}

				} else if (bt.getText().equals(
						ResourceHandler.getValue("delete"))) {
					IStructuredSelection selection = (IStructuredSelection) tv
							.getSelection();
					List<Host> selectionHosts = selection.toList();
					if (selectionHosts.size() < 1)
						return;
					List<Host> hosts = (List<Host>) tv.getInput();
					for (Host host : selectionHosts) {
						hosts.remove(host);
						text.append(ResourceHandler.getValue(
								"hostMange.msg.delHost",
								new String[] { host.getName() + " : "
										+ host.getIp() + " : "
										+ host.getRole().getName() }));
					}
					tv.refresh();
					updateDesc();
				} else if (bt.getText().equals(
						ResourceHandler.getValue("modify"))) {
					IStructuredSelection selection = (IStructuredSelection) tv
							.getSelection();
					if (selection.isEmpty())
						return;
					Host host = (Host) selection.getFirstElement();
					AddExtendHostDialog addDlg = new AddExtendHostDialog(
							ExtendMangeComposite.this, host, false);
					int return_ID = addDlg.open();
					if (return_ID == IDialogConstants.OK_ID) {
						text.append(ResourceHandler.getValue(
								"hostMange.msg.modifyHost",
								new String[] { host.getName() + " : "
										+ host.getIp() + " : "
										+ host.getRole().getName() }));
						tv.refresh();
					}
				} else if (bt.getText().equals(ResourceHandler.getValue("select_all"))) {
					tv.setAllChecked(true);
				}
			}
		};
		return listener;

	}

	public void cleanValues() {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				((List) tv.getInput()).clear();
				updateDesc();
				numbText.setText("0");
			}
		});

	}

	public CheckboxTableViewer getTv() {
		return tv;
	}
}
