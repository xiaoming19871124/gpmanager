package com.txdb.gpmanage.monitor.ui.composite.page;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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

public class SystemStorageComposite extends BaseCompositePage {

	private Browser browser_diskspace_desc;
	private Browser browser_diskspace_history;
	private Browser browser_diskspace_details;
	
	private String[] periodFragments = new String[]{};
	private ProgressBar progressBar;
	
	public SystemStorageComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_STORAGE_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_STORAGE_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(2, true));
		Display display = Display.getDefault();
		
		// 1.0 Disk Usage Summary
		Composite summaryComp = new Composite(composte, SWT.NONE);
		summaryComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		summaryComp.setLayout(new GridLayout(1, true));
		
		Label summaryTitle = new Label(summaryComp, SWT.NONE);
		summaryTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_STORAGE_SUMMARY));
		summaryTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		summaryTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		summaryTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		browser_diskspace_desc = new Browser(summaryComp, SWT.BORDER);
		browser_diskspace_desc.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser_diskspace_desc.setMenu(new Menu(browser_diskspace_desc));
		
		
		// 2.0 GP Segments Usage History
		Composite historyComp = new Composite(composte, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		historyComp.setLayoutData(gd);
		historyComp.setLayout(new GridLayout(2, true));
		
		Label historyTitle = new Label(historyComp, SWT.NONE);
		historyTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_STORAGE_HISTORY));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		historyTitle.setLayoutData(gd);
		historyTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		historyTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		final Combo combo_period = new Combo(historyComp, SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		combo_period.setLayoutData(gd);
		combo_period.setItems(new String[] { "1 Hour", "6 Hours", "24 Hours", "7 Days", "2 Weeks", "12 Weeks" });
		combo_period.select(0);
		progressBar = new ProgressBar(historyComp, SWT.HORIZONTAL | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setVisible(false);
		
		browser_diskspace_history = new Browser(historyComp, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser_diskspace_history.setLayoutData(gd);
		browser_diskspace_history.setMenu(new Menu(browser_diskspace_history));
		browser_diskspace_history.setUrl(WAITING_PAGE);
		
		combo_period.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Thread taskThread = new Thread() {
					@Override
					public void run() {
						// 1.0 Disable Operation
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								combo_period.setEnabled(false);
								progressBar.setVisible(true);
								browser_diskspace_history.setUrl(WAITING_PAGE);
								periodFragments = combo_period.getText().split(" ");
							}
						});
						
						// GP Segments Usage History
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());

						if (periodFragments[1].contains("Hour"))
							calendar.add(Calendar.HOUR, 0 - Integer.parseInt(periodFragments[0]));
						else if (periodFragments[1].contains("Weeks"))
							calendar.add(Calendar.DATE, 0 - Integer.parseInt(periodFragments[0]) * 7);
						else
							calendar.add(Calendar.DATE, -7);

//						// 2.0 Get Data & Notify Charts
//						MonitorContainer container = getContainer();
//						final String monitorName = container.getMonitorName();
//						GpManageServiceProxy proxy = container.getGpController().getManageServiceProxy();
//						List<Diskspace> diskspaceList = proxy.queryDiskspaceAve(calendar.getTime(), new Date(), true);
//						try {
//							String diskspaceListJson = JsonUtils.toJson(diskspaceList, true);
//							String result = MonitorController.getInstance().updateDiskspaceHistory(monitorName, diskspaceListJson);
//							System.out.println("Search DiskspaceHistory: " + result);
//							
//						} catch (final IOException ex) {
//							Display.getDefault().syncExec(new Runnable() {
//								@Override
//								public void run() {
//									MessageDialog.openError(getShell(), "Error", "Search DiskspaceHistory Error: " + ex.getMessage());
//								}
//							});
//						}
						
						// 2.0 Required Charts
						// TODO 
						RequireConnection rc = getRequireConnection();
						rc.setDateFrom(calendar);
						rc.setDateTo(Calendar.getInstance());
						String requireConnectionJson = JsonUtils.toJson(rc);
						try {
							String result = MonitorController.getInstance().requireDiskspaceHistory(requireConnectionJson);
							System.out.println("Required DiskspaceHistory: " + result);
							
						} catch (final IOException e) {
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(getShell(), "Error", "Search DiskspaceHistory Error: " + e.getMessage());
								}
							});
						}
						
						// 3.0 Restore Operation
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								browser_diskspace_history.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskspace.jsp?monitorName=" + getContainer().getMonitorName());
								combo_period.setEnabled(true);
								progressBar.setVisible(false);
							}
						});
					}
				};
				taskThread.start();
			}
		});
		
		// 3.0 Disk Usage Details
		Composite detailComp = new Composite(composte, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		detailComp.setLayoutData(gd);
		detailComp.setLayout(new GridLayout(1, true));
		
		Label detailsTitle = new Label(detailComp, SWT.NONE);
		detailsTitle.setText(ResourceHandler.getValue(MessageConstants.MONITOR_SYSTEM_STORAGE_DETAILS));
		detailsTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		detailsTitle.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		detailsTitle.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		
		browser_diskspace_details = new Browser(detailComp, SWT.BORDER);
		browser_diskspace_details.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser_diskspace_details.setMenu(new Menu(browser_diskspace_details));
	}

	@Override
	public void loadData() {
		System.out.println("SystemStorageComposite loadData");
		
		String monitorName = getContainer().getMonitorName();
		browser_diskspace_desc.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace.jsp?monitorName=" + monitorName);
		browser_diskspace_details.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace_details.jsp?monitorName=" + monitorName);
	}
}
