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

public class HistoryQueriesComposite extends BaseCompositePage {

	private Browser browser_queries;
	
	private Calendar calendarF;
	private Calendar calendarT;
	private ProgressBar progressBar;
	
	public HistoryQueriesComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIES_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIES_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new GridLayout(10, false));
		
		final Button btn_search = new Button(operComp, SWT.NONE);
		btn_search.setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIES_SEARCH));
		GridData gd = new GridData();
		gd.widthHint = 135;
		btn_search.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIES_DATETIMEFROM));
		final DateTime dateF = new DateTime(operComp, SWT.DATE | SWT.LONG);
		final DateTime timeF = new DateTime(operComp, SWT.TIME | SWT.LONG);
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HISTORY_QUERIES_DATETIMETO));
		final DateTime dateT = new DateTime(operComp, SWT.DATE | SWT.LONG);
		final DateTime timeT = new DateTime(operComp, SWT.TIME | SWT.LONG);
		
		progressBar = new ProgressBar(operComp, SWT.HORIZONTAL | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setVisible(false);
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		browser_queries = new Browser(dataComp, SWT.BORDER);
		browser_queries.setMenu(new Menu(browser_queries));
		browser_queries.setUrl(WAITING_PAGE);
		
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
								browser_queries.setUrl(WAITING_PAGE);

								calendarF = Calendar.getInstance();
								calendarF.set(dateF.getYear(), dateF.getMonth(), dateF.getDay(), timeF.getHours(), timeF.getMinutes(), timeF.getSeconds());
								calendarT = Calendar.getInstance();
								calendarT.set(dateT.getYear(), dateT.getMonth(), dateT.getDay(), timeT.getHours(), timeT.getMinutes(), timeT.getSeconds());
							}
						});
						
//						// 2.0 Get Data & Notify Charts
//						IGPConnector controller = getContainer().getGpController();
//						GpManageServiceProxy proxy = controller.getManageServiceProxy();
//						List<Database> databaseHistoryList = proxy.queryDatabase(calendarF.getTime(), calendarT.getTime(), true);
//						try {
//							String databaseListJson = JsonUtils.toJson(databaseHistoryList, true);
//							String result = MonitorController.getInstance().updateDatabaseHistory(getContainer().getMonitorName(), databaseListJson);
//							System.out.println("Search Queries History: " + result);
//
//						} catch (final IOException ex) {
//							Display.getDefault().syncExec(new Runnable() {
//								@Override
//								public void run() {
//									MessageDialog.openError(getShell(), "Error", "Search Queries History Error: " + ex.getMessage());
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
							String result = MonitorController.getInstance().requireDatabaseHistory(requireConnectionJson);
							System.out.println("Required Queries History: " + result);
							
						} catch (final IOException e) {
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(getShell(), "Error", "Search Queries History Error: " + e.getMessage());
								}
							});
						}
						
						// 3.0 Restore Operation
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								browser_queries.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_queries.jsp?monitorName=" + getContainer().getMonitorName());
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
		// TODO Auto-generated method stub
		System.out.println("HistoryQueriesComposite loadData");
	}
}
