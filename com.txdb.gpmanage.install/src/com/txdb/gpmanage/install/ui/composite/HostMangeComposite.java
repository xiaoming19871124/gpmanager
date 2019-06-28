package com.txdb.gpmanage.install.ui.composite;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.test.GpApplicationTest;
import com.txdb.gpmanage.core.log.LogUtil;
import com.txdb.gpmanage.install.i18n.ResourceHandler;
import com.txdb.gpmanage.install.ui.dialog.AddHostDialog;
import com.txdb.gpmanage.install.ui.provider.HostManageTableLabelProvider;

/**
 * 机器管理面板
 * 
 * @author ws
 *
 */
public class HostMangeComposite extends IupperComposite {

	private TableViewer tv;

	private Label descLb;

	public TableViewer getTv() {
		return tv;
	}

	HostMangeComposite(InstallComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("hostMange.title"), ResourceHandler.getValue("hostMange.desc"));
	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout());
		Composite function = new Composite(composte, SWT.NONE);
		function.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout gl_function = new GridLayout(4, true);
		gl_function.horizontalSpacing = 20;
		function.setLayout(gl_function);
		function.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
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

		Button toolBtn = new Button(function, SWT.PUSH);
		toolBtn.setVisible(false);
		toolBtn.setText("tool");
		toolBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GpApplicationTest gpAppTest = new GpApplicationTest();
				gpAppTest.open();
			}
		});
		tv = new TableViewer(composte, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableLayout tLayout = new TableLayout();// 专用于表格的布局
		table.setLayout(tLayout);
		table.setHeaderVisible(true);// 设置表格头部是否可见
		table.setLinesVisible(true);// 设置线条是否可见
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("hostMange.table.hostName"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("hostMange.table.hostIp"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("hostMange.table.userName"));
		// tLayout.addColumnData(new ColumnWeightData(20));
		// new TableColumn(table,
		// SWT.NONE).setText(ResourceHandler.getValue("password"));
		tv.setLabelProvider(new HostManageTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		tv.setInput(((InstallComposite) mianComposite).getInstallInfo().getAllHost());
		createdesc(composte);
	}

	private void createdesc(Composite composte) {
		descLb = new Label(composte, SWT.NONE);
		descLb.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { ((InstallComposite) mianComposite).getInstallInfo().getAllHost().size() }));
	}

	public void update() {
		tv.refresh();
		descLb.setText(ResourceHandler.getValue("total", new Integer[] { ((InstallComposite) mianComposite).getInstallInfo().getAllHost().size() }));
		((InstallComposite) mianComposite).getDenpendencySoft().updateList();
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
			public void widgetSelected(SelectionEvent e) {
				// 取得触发事件的空间对象（按钮）
				Button bt = (Button) e.widget;
				if (bt.getText().equals(ResourceHandler.getValue("add"))) {
					AddHostDialog addDlg = new AddHostDialog(HostMangeComposite.this, null);
					int return_ID = addDlg.open();
					if (return_ID == IDialogConstants.OK_ID) {
						Host host = addDlg.getHost();
						((InstallComposite) mianComposite).getInstallInfo().getAllHost().add(host);
						LogUtil.info("add host: "+ host.getName() + " : " + host.getIp());
						text.append(ResourceHandler.getValue("hostMange.msg.addHost", new String[] { host.getName() + " : " + host.getIp() }));
						update();
						((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().addHost(host);
						((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().modifyListData(host,"+");
					}
				} else if (bt.getText().equals(ResourceHandler.getValue("delete"))) {
					IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
					@SuppressWarnings("unchecked")
					List<Host> hosts = selection.toList();
					if (hosts.size() < 1)
						return;
					for (Host host : hosts) {
						((InstallComposite) mianComposite).getInstallInfo().getAllHost().remove(host);
						LogUtil.info("delete host: "+ host.getName() + " : " + host.getIp());
						if (((InstallComposite) mianComposite).getInstallInfo().getConfHost().contains(host))
							((InstallComposite) mianComposite).getInstallInfo().getConfHost().remove(host);
						if (((InstallComposite) mianComposite).getInstallInfo().getInstallHost().contains(host))
							((InstallComposite) mianComposite).getInstallInfo().getInstallHost().remove(host);
						((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().removeHost(host);
						((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().modifyListData(host,"-");
						text.append(ResourceHandler.getValue("hostMange.msg.delHost", new String[] { host.getName() + " : " + host.getIp() }));
					}
					update();
//					((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().modifyListData();
				} else if (bt.getText().equals(ResourceHandler.getValue("modify"))) {
					IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
					if (selection.isEmpty())
						return;
					Host host = (Host) selection.getFirstElement();
					String name = host.getName();
					String ip = host.getIp();
					AddHostDialog addDlg = new AddHostDialog(HostMangeComposite.this, host);
					int return_ID = addDlg.open();
					if (return_ID == IDialogConstants.OK_ID) {
						text.append(ResourceHandler.getValue("hostMange.msg.modifyHost", new String[] { host.getName() + " : " + host.getIp() }));
						LogUtil.info("modify host: "+name+":"+ip+" to "+ host.getName() + " : " + host.getIp());
						update();
						((InstallComposite) HostMangeComposite.this.mianComposite).getConfigure().modifyListData(host,"m");
					}
				}
			}
		};
		return listener;

	}

}
