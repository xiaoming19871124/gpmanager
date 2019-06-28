package com.txdb.gpmanage.manage.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;
import com.txdb.gpmanage.manage.service.ManageUiService;
import com.txdb.gpmanage.manage.ui.provider.AddMirrorTableLabelProvider;

/**
 * 扩容管理面板
 * 
 * @author ws
 *
 */
public class AddMirrorComposite extends IupperComposite {
	private TableViewer tv;

	private Label descLb;
	private List<GPSegmentInfo> info;

	AddMirrorComposite(ManageComposite mianComposite, Composite parent, CompositeCode code) {
		super(mianComposite, parent, code, ResourceHandler.getValue("add_mirror_title"),  ResourceHandler.getValue("add_mirror_desc"));

	}

	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout());
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
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("port"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("role"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("mirror_sync"));
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("mirror_state"));
		tLayout.addColumnData(new ColumnWeightData(40));
		new TableColumn(table, SWT.NONE).setText(ResourceHandler.getValue("mirror_data_dir"));
		tv.setLabelProvider(new AddMirrorTableLabelProvider());
		tv.setContentProvider(new ArrayContentProvider());
		List<Host> hosts = new ArrayList<Host>();
		tv.setInput(hosts);
		createdesc(composte);
		createFunctionComposite(composte);
	}

	private void createFunctionComposite(Composite composte) {
		Composite parent = new Composite(composte, SWT.BORDER);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_parent = new GridLayout(2, true);
		gl_parent.horizontalSpacing = 80;
		parent.setLayout(gl_parent);

		Composite addMirrorComp = new Composite(parent, SWT.NONE);
		addMirrorComp.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_addMirrorComp = new GridLayout(4, false);
		gl_addMirrorComp.horizontalSpacing = 20;
		addMirrorComp.setLayout(gl_addMirrorComp);
		Label addMirrorLb = new Label(addMirrorComp, SWT.NONE);
		addMirrorLb.setText("*");
		addMirrorLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		Label addMirrordescLb = new Label(addMirrorComp, SWT.NONE);
		addMirrordescLb.setText(ResourceHandler.getValue("add_mirror"));
		addMirrordescLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		// new Label(parent, SWT.NONE);
		Button groupBtn = new Button(addMirrorComp, SWT.RADIO);
		groupBtn.setText("grouped");
		groupBtn.setSelection(true);
		final Button spreadBtn = new Button(addMirrorComp, SWT.RADIO);
		spreadBtn.setText("spread");
		final Button addMirrorBtn = new Button(addMirrorComp, SWT.NONE);
		addMirrorBtn.setText(ResourceHandler.getValue("add_mirror"));
		addMirrorBtn.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 4, 1));

		Composite stateComp = new Composite(parent, SWT.NONE);
		stateComp.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		GridLayout gl_stateComp = new GridLayout(2, false);
		gl_stateComp.horizontalSpacing = 20;
		stateComp.setLayout(gl_stateComp);

		Label stateLb = new Label(stateComp, SWT.NONE);
		stateLb.setText("*");
		stateLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		Label statedescLb = new Label(stateComp, SWT.NONE);
		statedescLb.setText(ResourceHandler.getValue("mirror_sync_check"));
		statedescLb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		// new Label(parent, SWT.NONE);
		final Button stateBtn = new Button(stateComp, SWT.NONE);
		stateBtn.setText(ResourceHandler.getValue("mirror_status_check"));
		stateBtn.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 2, 1));
		addMirrorBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addMirrorBtn.setEnabled(false);
				startBar();

				final boolean isSpread = spreadBtn.getSelection();
				final Display display = Display.getCurrent();
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isHavMirror = getService().isHavMirror(gp);
						if (isHavMirror) {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									getService().setErrorMsg(text, ResourceHandler.getValue("mirror_exist_error"));
									addMirrorBtn.setEnabled(true);
									stopBar();
								}
							});
							return;
						}
						boolean isSuccess = ((ManageUiService) getService()).addMirror(gp, isSpread, text, AddMirrorComposite.this);
						if (isSuccess)
							info = ((ManageUiService) getService()).queryMirrorStatus(gp, text, AddMirrorComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								tv.setInput(info);
								tv.refresh();
								addMirrorBtn.setEnabled(true);
								stopBar();
							}
						});
					}
				}).start();
			}
		});
		stateBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
				stateBtn.setEnabled(false);
				startBar();
				new Thread(new Runnable() {
					@Override
					public void run() {
						
						final List<GPSegmentInfo> info = ((ManageUiService) getService()).queryMirrorStatus(gp, text, AddMirrorComposite.this);
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								tv.setInput(info);
								tv.refresh();
								stateBtn.setEnabled(true);
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
	public void loadMirrorState(){
		final GPManagerEntity gp = ((ManageComposite) mianComposite).getGp();
		final List<GPSegmentInfo> info = ((ManageUiService) getService()).queryMirrorStatus(gp, text, AddMirrorComposite.this);
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				tv.setInput(info);
				tv.refresh();
				descLb.setText(ResourceHandler.getValue("total", new Integer[] { info.size() }));
			}
		});
	}
	public void refreshMirror(){
		tv.setInput(new ArrayList<GPSegmentInfo>());
		tv.refresh();
	}
}
