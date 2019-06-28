package com.txdb.gpmanage.charts.dataproviders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.charts.ChartUtil;
import com.txdb.gpmanage.charts.entries.Category;
import com.txdb.gpmanage.charts.entries.Data;
import com.txdb.gpmanage.charts.entries.Dataset;
import com.txdb.gpmanage.charts.entries.FusionCharts;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.core.utils.JsonUtils;
import com.txdb.gpmanage.gpmon.entries.RequireConnection;
import com.txdb.gpmanage.gpmon.entries.System;

public class SystemDataProvider {

	private static Map<String, SystemDataProvider> providerMap = new HashMap<String, SystemDataProvider>();
	
	private String monitorName;
	
	private List<System> systemNowList;
	private List<System> systemHistoryList;
	
	// Host Chart Json Body
	private String chartBody_hostUsageCpu;
	private String chartBody_hostUsageMemory;
	private String chartBody_hostUsageSkew;
	
	// Cluster Chart Json Body
	private String chartBody_clusterUsageCpu;
	private String chartBody_clusterUsageDiskIO;
	private String chartBody_clusterUsageLoad;
	private String chartBody_clusterUsageMemory;
	private String chartBody_clusterUsageNetwork;
	private String chartBody_clusterUsageSwap;
	
	// History Chart Json Body
	private String chartBody_historyUsageCpu;
	private String chartBody_historyUsageDiskIO;
	private String chartBody_historyUsageLoad;
	private String chartBody_historyUsageMemory;
	private String chartBody_historyUsageNetwork;
	private String chartBody_historyUsageSwap;
	
	// Cluster Data
	private double ave_cpu_user = 0, ave_cpu_sys = 0/*, ave_cpu_idle = 0*/;
	private double mem_actual_used_percent = 0;
	private long /*ave_disk_ro = 0, */ave_disk_rb = 0, /*ave_disk_wo = 0, */ave_disk_wb = 0;
	private long /*ave_net_rp = 0, */ave_net_rb = 0, /*ave_net_wp = 0, */ave_net_wb = 0;
	private double ave_load0 = 0, ave_load1 = 0, ave_load2 = 0;
	private double swap_used_percent = 0;
	
	public static SystemDataProvider getInstance(String monitorName) {
		if (!providerMap.containsKey(monitorName))
			providerMap.put(monitorName, new SystemDataProvider(monitorName));
		return providerMap.get(monitorName);
	}
	
	public SystemDataProvider(String monitorName) {
		this.monitorName = monitorName;
	}
	
	public String getMonitorName() {
		return monitorName;
	}
	
	public void updateSystemNowJson(String systemListJson) {
		systemNowList = JsonUtils.toCollection(systemListJson, System.class);
		int hostCount = systemNowList.size();
		if (hostCount <= 0) {
			java.lang.System.err.println("updateSystemNowJson: Host count is 0, stop monitor.");
			return;
		}
		
		// Build Host Chart Body
		String[] dataArray = new String[hostCount];
		String[] dataArray_skew = new String[hostCount * 4];
		for (int i = 0; i < dataArray.length; i++) {
			String currHostname = systemNowList.get(i).getHostname();
			dataArray[i] = currHostname;
			
			dataArray_skew[i * 4] = currHostname + " Disk R";
			dataArray_skew[i * 4 + 1] = currHostname + " Disk W";
			dataArray_skew[i * 4 + 2] = currHostname + " Net R";
			dataArray_skew[i * 4 + 3] = currHostname + " Net W";
		}

		chartBody_hostUsageCpu = ChartUtil.buildRealtimeLine2Json("CPU Usage(%)", dataArray, "dp/host_usage_cpu_dp.jsp?monitorName=" + getMonitorName());
		chartBody_hostUsageMemory = ChartUtil.buildRealtimeLine2Json("Memory in Use(%)", dataArray, "dp/host_usage_memory_dp.jsp?monitorName=" + getMonitorName());
		
		FusionCharts fusionCharts = ChartUtil.buildRealtimeLine("Skew(MB/s)", dataArray_skew, "dp/host_usage_skew_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.getDataSource().getChart().setNumberSuffix("MB/s");
		chartBody_hostUsageSkew = ChartUtil.toJsonChart(fusionCharts);
		
		// Build Cluster Chart Body
//		chartBody_clusterUsageCpu = ChartUtil.buildRealtimeLine2Json("CPU Usage(%)", new String[] { "System", "User" }, "dp/cluster_usage_cpu_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts = ChartUtil.buildRealtimeLine("CPU Usage(%)", new String[] { "System", "User" }, "dp/cluster_usage_cpu_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.setRenderAt("chart-container1");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageCpu = ChartUtil.toJsonChart(fusionCharts);
		
		fusionCharts = ChartUtil.buildRealtimeLine("Disk I/O(MB/s)", new String[] { "Read", "Write" }, "dp/cluster_usage_diskio_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.getDataSource().getChart().setNumberSuffix("MB/s");
		fusionCharts.setRenderAt("chart-container3");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageDiskIO = ChartUtil.toJsonChart(fusionCharts);
		
		fusionCharts = ChartUtil.buildRealtimeLine("Load(MB)", new String[] { "1 min", "5 min", "15 min" }, "dp/cluster_usage_load_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.getDataSource().getChart().setNumberSuffix("MB");
		fusionCharts.setRenderAt("chart-container5");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageLoad = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_clusterUsageMemory = ChartUtil.buildRealtimeLine2Json("Memory In Use(%)", new String[] { "In Use" }, "dp/cluster_usage_memory_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts = ChartUtil.buildRealtimeLine("Memory In Use(%)", new String[] { "In Use" }, "dp/cluster_usage_memory_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.setRenderAt("chart-container2");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageMemory = ChartUtil.toJsonChart(fusionCharts);
		
		fusionCharts = ChartUtil.buildRealtimeLine("Network(MB/s)", new String[] { "Read", "Write" }, "dp/cluster_usage_network_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.getDataSource().getChart().setNumberSuffix("MB/s");
		fusionCharts.setRenderAt("chart-container4");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageNetwork = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_clusterUsageSwap = ChartUtil.buildRealtimeLine2Json("Swap In Use(%)", new String[] { "In Use" }, "dp/cluster_usage_swap_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts = ChartUtil.buildRealtimeLine("Swap In Use(%)", new String[] { "In Use" }, "dp/cluster_usage_swap_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.setRenderAt("chart-container6");
		fusionCharts.setHeight("300");
		chartBody_clusterUsageSwap = ChartUtil.toJsonChart(fusionCharts);
		
		// Prepare Cluster Data
		double sum_cpu_user = 0, sum_cpu_sys = 0/*, sum_cpu_idle = 0*/;
		long sum_mem_actual_used = 0, sum_mem_total = 0;
		long /*sum_disk_ro = 0, */sum_disk_rb = 0, /*sum_disk_wo = 0, */sum_disk_wb = 0;
		long /*sum_net_rp = 0, */sum_net_rb = 0, /*sum_net_wp = 0, */sum_net_wb = 0;
		double sum_load0 = 0, sum_load1 = 0, sum_load2 = 0;
		long sum_swap_used = 0, sum_swap_total = 0;
		
		for (System system : systemNowList) {
			sum_cpu_user += system.getCpu_user();
			sum_cpu_sys += system.getCpu_sys();
//			sum_cpu_idle += system.getCpu_idle();
			
			sum_mem_actual_used += system.getMem_actual_used();
			sum_mem_total += system.getMem_total();
			
//			sum_disk_ro += system.getDisk_ro_rate();
			sum_disk_rb += system.getDisk_rb_rate();
//			sum_disk_wo += system.getDisk_wo_rate();
			sum_disk_wb += system.getDisk_wb_rate();
			
//			sum_net_rp += system.getNet_rp_rate();
			sum_net_rb += system.getNet_rb_rate();
//			sum_net_wp += system.getNet_wp_rate();
			sum_net_wb += system.getNet_wb_rate();
			
			sum_load0 += system.getLoad0();
			sum_load1 += system.getLoad1();
			sum_load2 += system.getLoad2();
			
			sum_swap_used += system.getSwap_used();
			sum_swap_total += system.getSwap_total();
		}
		// Figure Average
		ave_cpu_user = formatPoint2(sum_cpu_user / hostCount);
		ave_cpu_sys = formatPoint2(sum_cpu_sys / hostCount);
//		ave_cpu_idle = formatPoint2(sum_cpu_idle / hostCount);
		
		mem_actual_used_percent = formatPoint2(sum_mem_actual_used / (double) sum_mem_total * 100);
		
//		ave_disk_ro = sum_disk_ro / hostCount;
		ave_disk_rb = sum_disk_rb / hostCount;
//		ave_disk_wo = sum_disk_wo / hostCount;
		ave_disk_wb = sum_disk_wb / hostCount;
		
//		ave_net_rp = sum_net_rp / hostCount;
		ave_net_rb = sum_net_rb / hostCount;
//		ave_net_wp = sum_net_wp / hostCount;
		ave_net_wb = sum_net_wb / hostCount;
		
		ave_load0 = formatPoint2(sum_load0 / hostCount);
		ave_load1 = formatPoint2(sum_load1 / hostCount);
		ave_load2 = formatPoint2(sum_load2 / hostCount);
		
		swap_used_percent = formatPoint2(sum_swap_used / (double)sum_swap_total * 100);
	}
	
	public void requireSystemHistoryJson(RequireConnection requireConnection) {
		// TODO
		IGPConnector connector = new GPConnectorImpl(
				requireConnection.getHost(), 
				requireConnection.getSshUsername(), 
				requireConnection.getSshPassword(), 
				requireConnection.getSshPort());
		
		if (!connector.connect().isSuccessed()) {
			java.lang.System.err.println("(requireSystemHistoryJson) Jetty SSH Connect failed!");
			return;
		}
		try {
			connector.getDao().createPSQL(
					requireConnection.getJdbcUsername(), 
					requireConnection.getJdbcPassword(), 
					requireConnection.getJdbcPort(), 
					requireConnection.getDatabase());
			
		} catch (Exception e) {
			java.lang.System.err.println("(requireSystemHistoryJson) Jetty JDBC Connect failed!");
			connector.disconnect();
			return;
		}
		GpManageServiceProxy proxy = connector.getManageServiceProxy();
		String systemListJson = "";
		try {
			List<?> systemHistoryList = proxy.querySystemAvg(requireConnection.getDateFrom(), requireConnection.getDateTo(), true);
			systemListJson = JsonUtils.toJsonArray(systemHistoryList, true);
			updateSystemHistoryJson(systemListJson);
			
		} catch (ParseException e) {
			java.lang.System.err.println("(requireSystemHistoryJson) Jetty query system failed.");
			return;
			
		} finally {
			connector.disconnect();
		}
	}
	
	public void updateSystemHistoryJson(String systemListJson) {
		systemHistoryList = JsonUtils.toCollection(systemListJson, System.class);
		
		// Build History Chart Body
		int itemsCount = systemHistoryList.size();
		Category[] categoryArray = new Category[itemsCount];
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		// Prepare Dataset
		// CPU
		Data[] dataArray_cpu_system = new Data[itemsCount];
		Data[] dataArray_cpu_user  = new Data[itemsCount];
		
		Dataset[] datasetArray_cpu = new Dataset[2];
		datasetArray_cpu[0] = new Dataset("System", "FF801F", 50, dataArray_cpu_system);
		datasetArray_cpu[1] = new Dataset("User", "8844FC", 50, dataArray_cpu_user);
		
		// Memory
		Data[] dataArray_memory_inuse = new Data[itemsCount];
		
		Dataset[] datasetArray_memory = new Dataset[1];
		datasetArray_memory[0] = new Dataset("In Use", "FFBB37", 50, dataArray_memory_inuse);
		
		// Disk I/O
		Data[] dataArray_diskio_read = new Data[itemsCount];
		Data[] dataArray_diskio_write = new Data[itemsCount];
		
		Dataset[] datasetArray_diskio = new Dataset[2];
		datasetArray_diskio[0] = new Dataset("Read", "00D789", 50, dataArray_diskio_read);
		datasetArray_diskio[1] = new Dataset("Write", "AC33CC", 50, dataArray_diskio_write);
		
		// Network
		Data[] dataArray_network_read = new Data[itemsCount];
		Data[] dataArray_network_write = new Data[itemsCount];
		
		Dataset[] datasetArray_network = new Dataset[2];
		datasetArray_network[0] = new Dataset("Read", "00A6C0", 50, dataArray_network_read);
		datasetArray_network[1] = new Dataset("Write", "FF8500", 50, dataArray_network_write);
		
		// Load
		Data[] dataArray_load_1 = new Data[itemsCount];
		Data[] dataArray_load_5 = new Data[itemsCount];
		Data[] dataArray_load_15 = new Data[itemsCount];
		
		Dataset[] datasetArray_load = new Dataset[3];
		datasetArray_load[0] = new Dataset("1 min", "FFBB37", 50, dataArray_load_1);
		datasetArray_load[1] = new Dataset("5 min", "00D789", 50, dataArray_load_5);
		datasetArray_load[2] = new Dataset("15 min", "8844FC", 50, dataArray_load_15);
		
		// Swap
		Data[] dataArray_swap_inuse = new Data[itemsCount];
		
		Dataset[] datasetArray_swap = new Dataset[1];
		datasetArray_swap[0] = new Dataset("In Use", "FF216D", 50, dataArray_swap_inuse);
		
		for (int i = itemsCount - 1, j = 0; i >= 0; i--, j++) {
			System system = systemHistoryList.get(i);
			categoryArray[j] = new Category(sdf.format(system.getCtime()));
			
			// CPU Data
			dataArray_cpu_system[j] = new Data(String.valueOf(system.getCpu_sys()));
			dataArray_cpu_user[j] = new Data(String.valueOf(system.getCpu_user()));
			// Memory Data
			dataArray_memory_inuse[j] = new Data(String.valueOf(system.getMem_used_percent()));
			// Disk I/O Data
			dataArray_diskio_read[j] = new Data(String.valueOf(formatPoint2((double) system.getDisk_rb_rate() / 1024 / 1024)));
			dataArray_diskio_write[j] = new Data(String.valueOf(formatPoint2((double) system.getDisk_wb_rate() / 1024 / 1024)));
			// Network Data
			dataArray_network_read[j] = new Data(String.valueOf(formatPoint2((double) system.getNet_rb_rate() / 1024 / 1024)));
			dataArray_network_write[j] = new Data(String.valueOf(formatPoint2((double) system.getNet_wb_rate() / 1024 / 1024)));
			// Load Data
			dataArray_load_1[j] = new Data(String.valueOf(system.getLoad0()));
			dataArray_load_5[j] = new Data(String.valueOf(system.getLoad1()));
			dataArray_load_15[j] = new Data(String.valueOf(system.getLoad2()));
			// Swap Data
			dataArray_swap_inuse[j] = new Data(String.valueOf(formatPoint2(system.getSwap_used() / (double) system.getSwap_total() * 100)));
		}
//		chartBody_historyUsageCpu = ChartUtil.buildZoomline2Json("CPU History", categoryArray, datasetArray_cpu);
		FusionCharts fusionCharts = ChartUtil.buildZoomline("CPU History", categoryArray, datasetArray_cpu);
		fusionCharts.setRenderAt("chart-container1");
		fusionCharts.setHeight("300");
		chartBody_historyUsageCpu = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_historyUsageMemory = ChartUtil.buildZoomline2Json("Memory History", categoryArray, datasetArray_memory);
		fusionCharts = ChartUtil.buildZoomline("Memory History", categoryArray, datasetArray_memory);
		fusionCharts.setRenderAt("chart-container2");
		fusionCharts.setHeight("300");
		chartBody_historyUsageMemory = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_historyUsageDiskIO = ChartUtil.buildZoomline2Json("Disk I/O History", categoryArray, datasetArray_diskio);
		fusionCharts = ChartUtil.buildZoomline("Disk I/O History", categoryArray, datasetArray_diskio);
		fusionCharts.setRenderAt("chart-container3");
		fusionCharts.setHeight("300");
		chartBody_historyUsageDiskIO = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_historyUsageNetwork = ChartUtil.buildZoomline2Json("Network History", categoryArray, datasetArray_network);
		fusionCharts = ChartUtil.buildZoomline("Network History", categoryArray, datasetArray_network);
		fusionCharts.setRenderAt("chart-container4");
		fusionCharts.setHeight("300");
		chartBody_historyUsageNetwork = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_historyUsageLoad = ChartUtil.buildZoomline2Json("Load History", categoryArray, datasetArray_load);
		fusionCharts = ChartUtil.buildZoomline("Load History", categoryArray, datasetArray_load);
		fusionCharts.setRenderAt("chart-container5");
		fusionCharts.setHeight("300");
		chartBody_historyUsageLoad = ChartUtil.toJsonChart(fusionCharts);
		
//		chartBody_historyUsageSwap = ChartUtil.buildZoomline2Json("Swap History", categoryArray, datasetArray_swap);
		fusionCharts = ChartUtil.buildZoomline("Swap History", categoryArray, datasetArray_swap);
		fusionCharts.setRenderAt("chart-container6");
		fusionCharts.setHeight("300");
		chartBody_historyUsageSwap = ChartUtil.toJsonChart(fusionCharts);
	}
	
	private double formatPoint2(double var) {
		try {
			return new BigDecimal(var).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_cpu.jsp
	public String getChartBody_hostUsageCpu() {
		return chartBody_hostUsageCpu;
	}
	public String getDataLabel_hostUsageCpu() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_hostUsageCpu() {
		String value = "";
		for (System system : systemNowList)
			value += system.getCpu_used() + "|";
		return value;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_memory.jsp
	public String getChartBody_hostUsageMemory() {
		return chartBody_hostUsageMemory;
	}
	public String getDataLabel_hostUsageMemory() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_hostUsageMemory() {
		String value = "";
		for (System system : systemNowList)
			value += system.getMem_used_percent() + "|";
		return value;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_skew.jsp
	public String getChartBody_hostUsageSkew() {
		return chartBody_hostUsageSkew;
	}
	public String getDataLabel_hostUsageSkew() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_hostUsageSkew() {
		String value = "";
		for (System system : systemNowList) {
			value += formatPoint2((double) system.getDisk_rb_rate() / 1024 / 1024) + "|";
			value += formatPoint2((double) system.getDisk_wb_rate() / 1024 / 1024) + "|";
			value += formatPoint2((double) system.getNet_rb_rate() / 1024 / 1024) + "|";
			value += formatPoint2((double) system.getNet_wb_rate() / 1024 / 1024) + "|";
		}
		return value;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_cpu.jsp
	public String getChartBody_clusterUsageCpu() {
		return chartBody_clusterUsageCpu;
	}
	public String getDataLabel_clusterUsageCpu() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageCpu() {
		return ave_cpu_sys + "|" + ave_cpu_user;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_diskio.jsp
	public String getChartBody_clusterUsageDiskIO() {
		return chartBody_clusterUsageDiskIO;
	}
	public String getDataLabel_clusterUsageDiskIO() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageDiskIO() {
		return formatPoint2((double) ave_disk_rb / 1024 / 1024) + "|" + formatPoint2((double) ave_disk_wb / 1024 / 1024);
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_load.jsp
	public String getChartBody_clusterUsageLoad() {
		return chartBody_clusterUsageLoad;
	}
	public String getDataLabel_clusterUsageLoad() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageLoad() {
		return ave_load0 + "|" + ave_load1 + "|" + ave_load2;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_memory.jsp
	public String getChartBody_clusterUsageMemory() {
		return chartBody_clusterUsageMemory;
	}
	public String getDataLabel_clusterUsageMemory() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageMemory() {
		return mem_actual_used_percent + "|";
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_network.jsp
	public String getChartBody_clusterUsageNetwork() {
		return chartBody_clusterUsageNetwork;
	}
	public String getDataLabel_clusterUsageNetwork() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageNetwork() {
		return formatPoint2((double) ave_net_rb / 1024 / 1024) + "|" + formatPoint2((double) ave_net_wb / 1024 / 1024);
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_swap.jsp
	public String getChartBody_clusterUsageSwap() {
		return chartBody_clusterUsageSwap;
	}
	public String getDataLabel_clusterUsageSwap() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = systemNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageSwap() {
		return String.valueOf(swap_used_percent);
	}

	
	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_cpu.jsp
	public String getChartBody_historyUsageCpu() {
		return chartBody_historyUsageCpu;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskio.jsp
	public String getChartBody_historyUsageDiskIO() {
		return chartBody_historyUsageDiskIO;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_load.jsp
	public String getChartBody_historyUsageLoad() {
		return chartBody_historyUsageLoad;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_memory.jsp
	public String getChartBody_historyUsageMemory() {
		return chartBody_historyUsageMemory;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_network.jsp
	public String getChartBody_historyUsageNetwork() {
		return chartBody_historyUsageNetwork;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_swap.jsp
	public String getChartBody_historyUsageSwap() {
		return chartBody_historyUsageSwap;
	}
}
