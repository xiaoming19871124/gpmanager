package com.txdb.gpmanage.monitor.ui.composite.page;

import java.io.IOException;
import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ProgressBar;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.gpmon.RequireConnection;
import com.txdb.gpmanage.core.gp.monitor.controller.MonitorController;
import com.txdb.gpmanage.core.utils.JsonUtils;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class HistorySystemComposite extends BaseCompositePage {

	private Browser browser_system;
//	private Browser browser_cpu;
//	private Browser browser_memory;
//	private Browser browser_diskio;
//	private Browser browser_network;
//	private Browser browser_load;
//	private Browser browser_swap;
	
	private Calendar calendarF;
	private Calendar calendarT;
	private ProgressBar progressBar;
	
	public HistorySystemComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_SYSTEM_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_SYSTEM_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new GridLayout(10, false));
		
		final Button btn_search = new Button(operComp, SWT.NONE);
		btn_search.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_SYSTEM_SEARCH));
		GridData gd = new GridData();
		gd.widthHint = 135;
		btn_search.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_SYSTEM_DATETIMEFROM));
		final DateTime dateF = new DateTime(operComp, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(operComp, SWT.TIME | SWT.LONG);
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_SYSTEM_DATETIMETO));
		final DateTime dateT = new DateTime(operComp, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(operComp, SWT.TIME | SWT.LONG);
		
		progressBar = new ProgressBar(operComp, SWT.HORIZONTAL | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setVisible(false);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		// System
		browser_system = new Browser(dataComp, SWT.BORDER);
		browser_system.setMenu(new Menu(browser_system));
		browser_system.setUrl(WAITING_PAGE);
		
//		ScrolledComposite comp_system_scrolled = new ScrolledComposite(dataComp, SWT.H_SCROLL | SWT.V_SCROLL);
//		comp_system_scrolled.setLayout(new GridLayout(1, true));
//		
//		Composite comp_system = new Composite(comp_system_scrolled, SWT.NONE);
//		comp_system.setSize(-1, 2000);
//		comp_system_scrolled.setContent(comp_system);
//		comp_system_scrolled.setExpandHorizontal(true);
//		comp_system.setLayout(new GridLayout(1, true));
		
//		// CPU
//		Group group_cpu = new Group(comp_system, SWT.NONE);
//		group_cpu.setText("CPU");
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_cpu.setLayoutData(gd);
//		
//		group_cpu.setLayout(new FillLayout());
//		browser_cpu = new Browser(group_cpu, SWT.BORDER);
//		browser_cpu.setMenu(new Menu(browser_cpu));
//		
//		// Memory
//		Group group_memory = new Group(comp_system, SWT.NONE);
//		group_memory.setText("Memory");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_memory.setLayoutData(gd);
//		
//		group_memory.setLayout(new FillLayout());
//		browser_memory = new Browser(group_memory, SWT.BORDER);
//		browser_memory.setMenu(new Menu(browser_memory));
//		
//		// Disk I/O
//		Group group_diskio = new Group(comp_system, SWT.NONE);
//		group_diskio.setText("Disk I/O");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_diskio.setLayoutData(gd);
//		
//		group_diskio.setLayout(new FillLayout());
//		browser_diskio = new Browser(group_diskio, SWT.BORDER);
//		browser_diskio.setMenu(new Menu(browser_diskio));
//		
//		// Network
//		Group group_network = new Group(comp_system, SWT.NONE);
//		group_network.setText("Network");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_network.setLayoutData(gd);
//		
//		group_network.setLayout(new FillLayout());
//		browser_network = new Browser(group_network, SWT.BORDER);
//		browser_network.setMenu(new Menu(browser_network));
//		
//		// Load
//		Group group_load = new Group(comp_system, SWT.NONE);
//		group_load.setText("Load");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_load.setLayoutData(gd);
//		
//		group_load.setLayout(new FillLayout());
//		browser_load = new Browser(group_load, SWT.BORDER);
//		browser_load.setMenu(new Menu(browser_load));
//		
//		// Swap
//		Group group_swap = new Group(comp_system, SWT.NONE);
//		group_swap.setText("Swap");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_swap.setLayoutData(gd);
//		
//		group_swap.setLayout(new FillLayout());
//		browser_swap = new Browser(group_swap, SWT.BORDER);
//		browser_swap.setMenu(new Menu(browser_swap));
		
		btn_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Thread taskThread = new Thread() {
					@Override
					public void run() {
						// 1.0 Disable Operation
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								if (getContainer().getGpController() == null) {
									MessageDialog.openWarning(getShell(), "Tip", "Need connect monitor server first!");
									return;
								}
								btn_search.setEnabled(false);
								dateF.setEnabled(false);
								timeF.setEnabled(false);
								dateT.setEnabled(false);
								timeT.setEnabled(false);

								progressBar.setVisible(true);
								browser_system.setUrl(WAITING_PAGE);

								calendarF = Calendar.getInstance();
								calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
								calendarT = Calendar.getInstance();
								calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
							}
						});

//						// 2.0 Get Data & Notify Charts
//						IGPConnector controller = getContainer().getGpController();
//						final String monitorName = getContainer().getMonitorName();
//						if (controller == null) {
//							MessageDialog.openWarning(getShell(), "Tip", "Need connect monitor server first!");
//							return;
//						}
//						GpManageServiceProxy proxy = controller.getManageServiceProxy();
//						List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemHistoryList = proxy.querySystemAvg(calendarF.getTime(), calendarT.getTime(), true);
//						try {
//							String systemListJson = JsonUtils.toJson(systemHistoryList, true);
//							String result = MonitorController.getInstance().updateSystemHistory(monitorName, systemListJson);
//							System.out.println("Search System History: " + result);
//
//						} catch (final IOException ex) {
//							Display.getDefault().syncExec(new Runnable() {
//								@Override
//								public void run() {
//									MessageDialog.openError(getShell(), "Error", "Search System History Error: " + ex.getMessage());
//								}
//							});
//						}
						
						// 2.0 Required Charts
						// TODO 
						RequireConnection rc = getRequireConnection();
						rc.setDateFrom(calendarF);
						rc.setDateTo(calendarT);
						String requireConnectionJson = JsonUtils.toJson(rc);
						try {
							String result = MonitorController.getInstance().requireSystemHistory(requireConnectionJson);
							System.out.println("Required System History: " + result);
							
						} catch (final IOException e) {
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(getShell(), "Error", "Search System History Error: " + e.getMessage());
								}
							});
						}
						
						// 3.0 Restore Operation
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								browser_system.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_system.jsp?monitorName=" + getContainer().getMonitorName());
//								browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_cpu.jsp?monitorName=" + monitorName);
//								browser_memory.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_memory.jsp?monitorName=" + monitorName);
//								browser_diskio.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskio.jsp?monitorName=" + monitorName);
//								browser_network.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_network.jsp?monitorName=" + monitorName);
//								browser_load.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_load.jsp?monitorName=" + monitorName);
//								browser_swap.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_swap.jsp?monitorName=" + monitorName);
								
								btn_search.setEnabled(true);
								dateF.setEnabled(true);
								timeF.setEnabled(true);
								dateT.setEnabled(true);
								timeT.setEnabled(true);
								progressBar.setVisible(false);
							}
						});
					}
				};
				taskThread.start();
			}
		});
	}
	
	@Override
	public void loadData() {
		System.out.println("HistorySystemComposite loadData");
	}
}
