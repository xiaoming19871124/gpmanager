package com.txdb.gpmanage.monitor.ui.composite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.gpmon.Database;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.monitor.controller.MonitorController;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.core.utils.JsonUtils;
import com.txdb.gpmanage.monitor.service.WarnServer;
import com.txdb.gpmanage.monitor.ui.composite.page.BaseCompositePage;
import com.txdb.gpmanage.monitor.ui.composite.page.ClusterQueriesComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.ClusterSystemComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.DashboardHealthDiskComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.DashboardLogComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.DashboardLogHistoryComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HistoryQueriesComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HistoryQueriesListComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HistorySystemComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HostCpuUsageComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HostMemoryUsageComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.HostSkewComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.QueryListDetailComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.SystemSegmentComposite;
import com.txdb.gpmanage.monitor.ui.composite.page.SystemStorageComposite;

public class MonitorContainer extends Composite {
	
	private final long timeInterval = 15000;
	
	private GPMonitorEntity monitorEntity;
	private WarnServer warningService;
	private IGPConnector gpController;
	private MonitorComposite monitorComposite;
	
	// Pages List
	private List<IupperComposite> compositeList;
	
	private DashboardHealthDiskComposite dashboardHealthDiskComposite;
	private DashboardLogComposite dashboardLogComposite;
	private QueryListDetailComposite queryListDetailComposite;
	private HostCpuUsageComposite hostCpuUsageComposite;
	private HostMemoryUsageComposite hostMemoryUsageComposite;
	private ClusterQueriesComposite clusterQueriesComposite;
	private SystemSegmentComposite systemSegmentComposite;
	
	private List<MonitorTaskThread> taskThreadList = new ArrayList<MonitorTaskThread>();
	
	private List<GPSegmentInfo> segmentInfoList;
	private List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList;
	private List<Database> databaseList;
	private List<Diskspace> diskspaceList;
	
	MonitorContainer(GPMonitorEntity monitorEntity, MonitorComposite monitorComposite, Composite parent, int style) {
		super(parent, style);
		this.monitorEntity = monitorEntity;
		this.monitorComposite = monitorComposite;
		warningService = new WarnServer(monitorEntity);
		createControl();
	}
	
	private void createControl() {
		StackLayout layout = new StackLayout();
		setLayout(layout);
		
		compositeList = new ArrayList<IupperComposite>();
		
		// Dashboard
		dashboardHealthDiskComposite = new DashboardHealthDiskComposite(monitorComposite, this, CompositeCode.MONITOR_DASHBOARD_HEALTH_DISK);
		compositeList.add(dashboardHealthDiskComposite);
		dashboardLogComposite = new DashboardLogComposite(monitorComposite, this, CompositeCode.MONITOR_DASHBOARD_LOG);
		compositeList.add(dashboardLogComposite);
		
		// Query Monitor
		queryListDetailComposite = new QueryListDetailComposite(monitorComposite, this, CompositeCode.MONITOR_QUERY_LIST_DETAILS);
		compositeList.add(queryListDetailComposite);
		
		// Host Metrics
		hostCpuUsageComposite = new HostCpuUsageComposite(monitorComposite, this, CompositeCode.MONITOR_HOST_CPU);
		compositeList.add(hostCpuUsageComposite);
		hostMemoryUsageComposite = new HostMemoryUsageComposite(monitorComposite, this, CompositeCode.MONITOR_HOST_MEMORY);
		compositeList.add(hostMemoryUsageComposite);
		compositeList.add(new HostSkewComposite(monitorComposite, this, CompositeCode.MONITOR_HOST_SKEW));
		
		// Cluster Metrics
		clusterQueriesComposite = new ClusterQueriesComposite(monitorComposite, this, CompositeCode.MONITOR_CLUSTER_QUERIES);
		compositeList.add(clusterQueriesComposite);
		compositeList.add(new ClusterSystemComposite(monitorComposite, this, CompositeCode.MONITOR_CLUSTER_SYSTEM));
		
		// History
		compositeList.add(new DashboardLogHistoryComposite(monitorComposite, this, CompositeCode.MONITOR_DASHBOARD_LOG_HISTORY));
		compositeList.add(new HistoryQueriesComposite(monitorComposite, this, CompositeCode.MONITOR_HISTORY_QUERIES));
		compositeList.add(new HistorySystemComposite(monitorComposite, this, CompositeCode.MONITOR_HISTORY_SYSTEM));
		compositeList.add(new HistoryQueriesListComposite(monitorComposite, this, CompositeCode.MONITOR_HISTORY_QUERIES_LIST));
		
		// System
		systemSegmentComposite = new SystemSegmentComposite(monitorComposite, this, CompositeCode.MONITOR_SYSTEM_SEGMENT);
		compositeList.add(systemSegmentComposite);
		compositeList.add(new SystemStorageComposite(monitorComposite, this, CompositeCode.MONITOR_SYSTEM_STORAGE));
		layout.topControl = compositeList.get(0);
		layout();
	}
	
	public void switchTopComposite(CompositeCode code) {
		Control[] children = getChildren();
		for (Control child : children) {
			if (((IupperComposite) child).getCode() == code) {
				StackLayout layout = (StackLayout) getLayout();
				if (!layout.topControl.equals((Composite) child)) {
					layout.topControl = (Composite) child;
					layout();
				}
				break;
			}
		}
	}
	
	public String getMonitorName() {
		return monitorEntity.getMonitorName();
	}
	
	public void sendMonitorWarning(List<?> entities) {
		warningService.warn(entities);
	}
	
	@Override
	public void dispose() {
		Control[] controls = getChildren();
		for (Control control : controls)
			control.dispose();
		super.dispose();
	}
	
	// Main GP Controller
	public IGPConnector getGpController() {
		return gpController;
	}

	public void setGpController(IGPConnector gpController) {
		this.gpController = gpController;
	}
	
	// Data Task Thread
	public void taskThreadStart() {
		MonitorTaskThread taskThread = new MonitorTaskThread() {
			@Override
			public void taskBody() {
				GpManageServiceProxy proxy = gpController.getManageServiceProxy();
				
				segmentInfoList = proxy.queryGPSegmentInfo();
				systemList = proxy.querySystem();
				databaseList = proxy.queryDatabase();
				diskspaceList = proxy.queryDiskspace();
				
				// Warning Service
				sendMonitorWarning(segmentInfoList);
				sendMonitorWarning(systemList);
				sendMonitorWarning(diskspaceList);
				sendMonitorWarning(databaseList);
				
				// >>>>>>>>>>>>>>>> Required Data To WebServer
				try {
					String gpSegmentConfListJson = JsonUtils.toJsonArray(segmentInfoList, true);
					String systemListJson = JsonUtils.toJsonArray(systemList, true);
					String databaseListJson = JsonUtils.toJsonArray(databaseList, true);
					String diskspaceListJson = JsonUtils.toJsonArray(diskspaceList, true);
					
					String monitorName = monitorEntity.getMonitorName();
					MonitorController controller = MonitorController.getInstance();
					String result_seg  = controller.updateGpSegmentConf(monitorName, gpSegmentConfListJson);
					String result_sys  = controller.updateSystemNow(monitorName, systemListJson);
					String result_db   = controller.updateDatabaseNow(monitorName, databaseListJson);
					String result_disk = controller.updateDiskspaceNow(monitorName, diskspaceListJson);
					
					System.out.println("请求数据更新线程：\n"
							+ "1." + result_seg + "\n"
							+ "2." + result_sys + "\n"
							+ "3." + result_db + "\n"
							+ "4." + result_disk + "\n"
							+ "");
					
				} catch (IOException e) {
					System.err.println("开启数据线程失败：" + e.getMessage());
				}
				
				// Refresh Data Without Chart
				loadStaticData();
				
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {}
			}
		};
		taskThreadList.add(taskThread);
		taskThread.start();
	}
	
	public void taskThreadStop() {
		for (MonitorTaskThread task : taskThreadList)
			task.stopMe();
		taskThreadList.clear();
	}
	
	private abstract class MonitorTaskThread extends Thread {
		private boolean stopMe;
		
		@Override
		public void run() {
			while (!stopMe)
				taskBody();
		}
		
		public void stopMe() {
			stopMe = true;
		}
		
		public abstract void taskBody();
	}
	
	public List<GPSegmentInfo> getSegmentInfoList() {
		return segmentInfoList;
	}

	public List<com.txdb.gpmanage.core.gp.entry.gpmon.System> getSystemList() {
		return systemList;
	}

	public List<Database> getDatabaseList() {
		return databaseList;
	}

	public List<Diskspace> getDiskspaceList() {
		return diskspaceList;
	}

	public void loadData() {
		for (IupperComposite composite : compositeList)
			((BaseCompositePage) composite).loadData();
	}
	
	public void loadStaticData() {
		try {
			// Dashboard
			dashboardHealthDiskComposite.loadStaticData();
			dashboardLogComposite.loadStaticData();

			// Query Monitor
			queryListDetailComposite.loadStaticData();

			// Host Metrics
			hostCpuUsageComposite.loadStaticData();
			hostMemoryUsageComposite.loadStaticData();

			// Cluster Metrics
			clusterQueriesComposite.loadStaticData();

			// System
			systemSegmentComposite.loadStaticData();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception occured durning staticData updating... " + e.getMessage());
		}
	}
}
