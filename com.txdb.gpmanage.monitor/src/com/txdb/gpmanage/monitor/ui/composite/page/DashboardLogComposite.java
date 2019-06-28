package com.txdb.gpmanage.monitor.ui.composite.page;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.gpmon.LogAlert;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class DashboardLogComposite extends BaseCompositePage {

	private List<LogAlert> logAlertList;
	
	private Button btn_refresh;
	private Button btn_autoRefresh;
	private Button btn_panic;
	private Button btn_fatal;
	private Button btn_error;
	private Button btn_warning;
	
	private boolean autoRefresh = false;
	private boolean panic = false;
	private boolean fatal = false;
	private boolean error = false;
	private boolean warning = false;
	private boolean tableDataFilled = false;
	
	private Table table;
	
	public DashboardLogComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_ALERTS_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_ALERTS_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new GridLayout(10, false));
		
		btn_refresh = new Button(operComp, SWT.NONE);
		btn_refresh.setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_ALERTS_REFRESH));
		GridData gd = new GridData();
		gd.widthHint = 135;
		btn_refresh.setLayoutData(gd);
		
		btn_autoRefresh = new Button(operComp, SWT.CHECK);
		btn_autoRefresh.setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_ALERTS_AUTO_PREFIX));
		
		Label descLabel = new Label(operComp, SWT.NONE);
		descLabel.setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_ALERTS_AUTO_SUFFIX));
		descLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btn_panic = createCheckButton(operComp, LogAlert.LOG_PANIC, true);
		btn_fatal = createCheckButton(operComp, LogAlert.LOG_FATAL, true);
		btn_error = createCheckButton(operComp, LogAlert.LOG_ERROR, true);
		btn_warning = createCheckButton(operComp, LogAlert.LOG_WARNING, true);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		table = new Table(dataComp, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		final String[] tableHeader = { "Time", "Severity", "Message", "User", "Database", "Host" };
		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(tableHeader[i]);
			column.setWidth(i == 2 ? 300 : 100);
		}
		btn_autoRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoRefresh = btn_autoRefresh.getSelection();
				btn_refresh.setEnabled(!autoRefresh);
			}
		});
		btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_refresh.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
						logAlertList = proxy.queryLogAlert();
						fillTableData();
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								btn_refresh.setEnabled(true);
							}
						});
					}
				}).start();
			}
		});
		SelectionAdapter checkSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillTableData();
			}
		};
		btn_panic.addSelectionListener(checkSelectionAdapter);
		btn_fatal.addSelectionListener(checkSelectionAdapter);
		btn_error.addSelectionListener(checkSelectionAdapter);
		btn_warning.addSelectionListener(checkSelectionAdapter);
	}
	
	private Button createCheckButton(Composite comp, String name, boolean checked) {
		Button checkBtn = new Button(comp, SWT.CHECK);
		checkBtn.setText(name);
		checkBtn.setSelection(checked);
		return checkBtn;
	}
	
	private void fillTableData() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				panic = btn_panic.getSelection();
				fatal = btn_fatal.getSelection();
				error = btn_error.getSelection();
				warning = btn_warning.getSelection();
				table.removeAll();
			}
		});
		if (logAlertList == null || logAlertList.size() <= 0)
			return;
		
		final SimpleDateFormat sdf_short = new SimpleDateFormat("MM-dd HH:mm:ss");
		for (final LogAlert logAlert : logAlertList) {
			final String logSeverity = logAlert.getLogseverity();
			if (!panic && LogAlert.LOG_PANIC.equals(logSeverity))
				continue;
			if (!fatal && LogAlert.LOG_FATAL.equals(logSeverity))
				continue;
			if (!error && LogAlert.LOG_ERROR.equals(logSeverity))
				continue;
			if (!warning && LogAlert.LOG_WARNING.equals(logSeverity))
				continue;
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					if (table.isDisposed())
						return;
					
					new TableItem(table, SWT.NONE).setText(new String[] { 
							sdf_short.format(
									logAlert.getLogtime()), 
									logSeverity, 
									logAlert.getLogmessage(), 
									logAlert.getLoguser(),
									logAlert.getLogdatabase(), 
									logAlert.getLoghost() });
				}
			});
		}
		tableDataFilled = true;
	}
	
	@Override
	public void loadData() {}
	
	@Override
	public void loadStaticData() throws Exception {
		if (!autoRefresh && tableDataFilled)
			return;
		
		System.out.println("DashboardLogComposite loadData");
		GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
		logAlertList = proxy.queryLogAlert();
		fillTableData();
	}
}
