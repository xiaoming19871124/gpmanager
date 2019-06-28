package com.txdb.gpmanage.monitor.ui.composite.page;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.PGStatActivity;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class DashboardHealthDiskComposite extends BaseCompositePage {
	
	private Text uptime_value;
	private Text pgVersion_value;
	private Text gpVersion_value;
	private Text connections_value;
	
	private Browser browser_segmentHealth;
	private Browser browser_diskspaceUsage;
	
	private Combo disk_selection;
	private String disk_selection_text = null;
	private Text disk_total;
	private Text disk_used;
	private Text disk_available;

	public DashboardHealthDiskComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(2, true));
		
		// 1.0 About
		Composite aboutComp = new Composite(composte, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		aboutComp.setLayoutData(gd);
		aboutComp.setLayout(new GridLayout(10, false));
		
		new Label(aboutComp, SWT.NONE).setText("--");
		Label label_1 = new Label(aboutComp, SWT.NONE);
		label_1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_1.setText("--");
		
		new Label(aboutComp, SWT.NONE).setText("--");
		Label label_2 = new Label(aboutComp, SWT.NONE);
		label_2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_2.setText("--");
		
		new Label(aboutComp, SWT.NONE).setText("--");
		Label label_3 = new Label(aboutComp, SWT.NONE);
		label_3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_3.setText("--");
		
		new Label(aboutComp, SWT.NONE).setText("--");
		Label label_4 = new Label(aboutComp, SWT.NONE);
		label_4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_4.setText("--");
		
		
		// 2.0 Segment Health
		Group healthGroup = new Group(composte, SWT.NONE);
		healthGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		healthGroup.setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_SEGMENT));
		
		healthGroup.setLayout(new GridLayout(2, false));
		browser_segmentHealth = new Browser(healthGroup, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser_segmentHealth.setLayoutData(gd);
		browser_segmentHealth.setMenu(new Menu(browser_segmentHealth));
		
		new Label(healthGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_SEGMENT_UPTIME));
		uptime_value = new Text(healthGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		uptime_value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(healthGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_SEGMENT_PSQLVERSION));
		pgVersion_value = new Text(healthGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		pgVersion_value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(healthGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_SEGMENT_GPVERSION));
		gpVersion_value = new Text(healthGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		gpVersion_value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(healthGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_SEGMENT_CONNECTONS));
		connections_value = new Text(healthGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		connections_value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		// 3.0 Disk Usage Summary
		Group diskGroup = new Group(composte, SWT.NONE);
		diskGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		diskGroup.setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_DISK));
		
		diskGroup.setLayout(new GridLayout(2, false));
		browser_diskspaceUsage = new Browser(diskGroup, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		browser_diskspaceUsage.setLayoutData(gd);
		browser_diskspaceUsage.setMenu(new Menu(browser_diskspaceUsage));
		
		new Label(diskGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_DISK_LIST));
		disk_selection = new Combo(diskGroup, SWT.READ_ONLY);
		disk_selection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(diskGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_DISK_TOTAL));
		disk_total = new Text(diskGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		disk_total.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(diskGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_DISK_USED));
		disk_used = new Text(diskGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		disk_used.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(diskGroup, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_DASHBOARD_HEALTH_GROUP_DISK_AVAILABLE));
		disk_available = new Text(diskGroup, SWT.NO_TRIM | SWT.READ_ONLY);
		disk_available.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		disk_selection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String hostname = disk_selection.getText();
				if (disk_selection == null)
					return;
				
				List<Diskspace> diskspaceNowList = getCurrentDiskspaceList();
				for (Diskspace diskspace : diskspaceNowList) {
					if (!"/".equals(diskspace.getFilesystem()) || hostname.equals(diskspace.getHostname()))
						continue;
					else {
						disk_total.setText(String.valueOf(diskspace.getTotal_bytes() / 1024 / 1024) + "MB");
						disk_used.setText(String.valueOf(diskspace.getBytes_used() / 1024 / 1024) + "MB");
						disk_available.setText(String.valueOf(diskspace.getBytes_available() / 1024 / 1024) + "MB");
						break;
					}
				}
			}
		});
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("DashboardHealthDiskComposite loadData...");
		
		String monitorName = getContainer().getMonitorName();
		browser_segmentHealth.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/segment_health.jsp?monitorName=" + monitorName);
		browser_diskspaceUsage.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace.jsp?monitorName=" + monitorName);
	}
	
	@Override
	public void loadStaticData() {
		// TODO Auto-generated method stub
		final GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
		final String uptime = proxy.queryUptime();
		final List<PGStatActivity> activityList = proxy.queryPGStatActivities();
		final StringBuffer connectionsTip = new StringBuffer();
		for (PGStatActivity activity : activityList)
			connectionsTip.append(activity.getUsename() + "\n");
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				uptime_value.setText(uptime == null ? "Unknow" : uptime);
				pgVersion_value.setText(proxy.pgVersion());
				gpVersion_value.setText(proxy.gpVersion());
				gpVersion_value.setToolTipText(proxy.gpVersion());
				connections_value.setText(String.valueOf(activityList == null ? "Unknow" : activityList.size()));
				connections_value.setToolTipText(connectionsTip.toString().trim());
			}
		});
		
		// Fill Combo & Details
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				disk_selection_text = disk_selection.getText();
				disk_selection.removeAll();
			}
		});
		List<Diskspace> diskspaceNowList = getCurrentDiskspaceList();
		for (final Diskspace diskspace : diskspaceNowList) {
			if (!"/".equals(diskspace.getFilesystem()))
				continue;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					disk_selection.add(diskspace.getHostname());
				}
			});
		}
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				disk_selection.select(0);
				if (!"".equals(disk_selection_text))
					disk_selection.setText(disk_selection_text);
				disk_selection.notifyListeners(SWT.Selection, null);
			}
		});
	}
}
