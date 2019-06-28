package com.txdb.gpmanage.charts.dataproviders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.txdb.gpmanage.gpmon.entries.Diskspace;
import com.txdb.gpmanage.gpmon.entries.GpSegmentConf;
import com.txdb.gpmanage.gpmon.entries.RequireConnection;

public class DiskspaceDataProvider {

	private static Map<String, DiskspaceDataProvider> providerMap = new HashMap<String, DiskspaceDataProvider>();
	
	private String monitorName;
	
	private List<Diskspace> diskspaceNowList;
	private List<Diskspace> diskspaceNowList_masterAndSegs = new ArrayList<Diskspace>();
	private List<Diskspace> diskspaceHistoryList;
	
	private long master_sum_used = 0, master_sum_free = 0, master_sum_total = 1;
	private long segment_sum_used = 0, segment_sum_free = 0, segment_sum_total = 1;
	
	// Chart Json Body
	private String chartBody_diskspace_master;
	private String chartBody_diskspace_segments;
	private String chartBody_diskspace_segments_history;
	private String chartBody_diskspace_details;
	
	public static DiskspaceDataProvider getInstance(String monitorName) {
		if (!providerMap.containsKey(monitorName))
			providerMap.put(monitorName, new DiskspaceDataProvider(monitorName));
		return providerMap.get(monitorName);
	}
	
	public DiskspaceDataProvider(String monitorName) {
		this.monitorName = monitorName;
	}
	
	public String getMonitorName() {
		return monitorName;
	}
	
	public void updateDiskspaceNowJson(String diskspaceListJson) {
		diskspaceNowList = JsonUtils.toCollection(diskspaceListJson, Diskspace.class);
		
		// Build Chart Body
		FusionCharts masterChart = ChartUtil.buildCylinder("GP Master", "dp/usage_diskspace_master_dp.jsp?monitorName=" + getMonitorName());
		masterChart.setRenderAt("chart-container1");
		masterChart.getDataSource().getChart().setPlotToolText("Master Usage: <b>$dataValue</b>");
		chartBody_diskspace_master = ChartUtil.toJsonChart(masterChart);
		
		FusionCharts segmentsChart = ChartUtil.buildCylinder("GP Segments", "dp/usage_diskspace_segments_dp.jsp?monitorName=" + getMonitorName());
		segmentsChart.setRenderAt("chart-container2");
		segmentsChart.getDataSource().getChart().setPlotToolText("Segments Usage: <b>$dataValue</b>");
		chartBody_diskspace_segments = ChartUtil.toJsonChart(segmentsChart);
		
		diskspaceNowList_masterAndSegs.clear();
		for (Diskspace diskspace : diskspaceNowList) {
			if (!"/".equals(diskspace.getFilesystem()))
				continue;
			diskspaceNowList_masterAndSegs.add(diskspace);
		}
		String[] dataArray = new String[diskspaceNowList_masterAndSegs.size()];
		for (int i = 0; i < diskspaceNowList_masterAndSegs.size(); i++)
			dataArray[i] = diskspaceNowList_masterAndSegs.get(i).getHostname();
		chartBody_diskspace_details = ChartUtil.buildRealtimeLine2Json("Diskspace Usage(%)", dataArray, "dp/usage_diskspace_details_dp.jsp?monitorName=" + getMonitorName());
		
		// Prepare Cluster Data
		Map<String, Boolean> masterKeyMap = new HashMap<String, Boolean>();
		Map<String, Boolean> segmentKeyMap = new HashMap<String, Boolean>();
		
		List<GpSegmentConf> segmentConfList = GpSegmentDataProvider.getInstance(getMonitorName()).getGpSegmentConfList();
		for (GpSegmentConf segmentConf : segmentConfList) {
			if (segmentConf == null)
				continue;
			
			String hostname = segmentConf.getHostname();
			if (segmentConf.getContent() == -1) {
				if (!masterKeyMap.containsKey(hostname))
					masterKeyMap.put(hostname, true);
			} else {
				if (!segmentKeyMap.containsKey(hostname))
					segmentKeyMap.put(hostname, true);
			}
		}
		master_sum_used = 0; master_sum_free = 0; master_sum_total = 1;
		segment_sum_used = 0; segment_sum_free = 0; segment_sum_total = 1;
		
		for (Diskspace diskspace : diskspaceNowList_masterAndSegs) {
			String hostname = diskspace.getHostname();
			
			// Gathering data for diskspace
			if (masterKeyMap.containsKey(hostname)) {
				master_sum_used += diskspace.getBytes_used();
				master_sum_free += diskspace.getBytes_available();
				master_sum_total += diskspace.getTotal_bytes();
			}
			if (segmentKeyMap.containsKey(hostname)) {
				segment_sum_used += diskspace.getBytes_used();
				segment_sum_free += diskspace.getBytes_available();
				segment_sum_total += diskspace.getTotal_bytes();
			}
		}
	}
	
	public void requireDiskspaceHistoryJson(RequireConnection requireConnection) {
		// TODO
		IGPConnector connector = new GPConnectorImpl(
				requireConnection.getHost(), 
				requireConnection.getSshUsername(), 
				requireConnection.getSshPassword(), 
				requireConnection.getSshPort());
		
		if (!connector.connect().isSuccessed()) {
			System.err.println("(requireSystemHistoryJson) Jetty SSH Connect failed!");
			return;
		}
		try {
			connector.getDao().createPSQL(
					requireConnection.getJdbcUsername(), 
					requireConnection.getJdbcPassword(), 
					requireConnection.getJdbcPort(), 
					requireConnection.getDatabase());
			
		} catch (Exception e) {
			System.err.println("(requireSystemHistoryJson) Jetty JDBC Connect failed!");
			connector.disconnect();
			return;
		}
		GpManageServiceProxy proxy = connector.getManageServiceProxy();
		String diskspaceListJson = "";
		try {
			List<?> diskspaceList = proxy.queryDiskspaceAve(requireConnection.getDateFrom(), requireConnection.getDateTo(), true);
			diskspaceListJson = JsonUtils.toJsonArray(diskspaceList, true);
			updateDiskspaceHistoryJson(diskspaceListJson);
			
		} catch (ParseException e) {
			System.err.println("(requireSystemHistoryJson) Jetty query system failed.");
			return;
			
		} finally {
			connector.disconnect();
		}
	}
	
	public void updateDiskspaceHistoryJson(String diskspaceListJson) {
		diskspaceHistoryList = JsonUtils.toCollection(diskspaceListJson, Diskspace.class);
		
		// Build History Chart Body
		int itemsCount = diskspaceHistoryList.size();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		// Prepare Dataset
		List<Category> categoryList = new ArrayList<Category>();
		List<Data> dataList_diskspace_inUse = new ArrayList<Data>();
		for (int i = 0; i < itemsCount; i++) {
			Diskspace diskspace = diskspaceHistoryList.get(i);
			if (!"/".equals(diskspace.getFilesystem()))
				continue;
			
			categoryList.add(new Category(sdf.format(diskspace.getCtime())));
			
			// Diskspace Data
			double percentInUse = formatPoint2((double) diskspace.getBytes_used() / diskspace.getTotal_bytes() * 100);
			dataList_diskspace_inUse.add(new Data(String.valueOf(percentInUse)));
		}
		Data[] dataArray = new Data[dataList_diskspace_inUse.size()];
		dataList_diskspace_inUse.toArray(dataArray);
		Category[] categoryArray = new Category[dataList_diskspace_inUse.size()];
		categoryList.toArray(categoryArray);
		
		Dataset[] datasetArray_inUse = new Dataset[1];
		datasetArray_inUse[0] = new Dataset("In Use", "00A6C0", 50, dataArray);
		
		chartBody_diskspace_segments_history = ChartUtil.buildZoomline2Json(
				"Diskspace History", categoryArray, datasetArray_inUse);
		System.out.println(chartBody_diskspace_segments_history);
	}
	
	private double formatPoint2(double var) {
		return new BigDecimal(var).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace.jsp
	public String getChartBody_diskspace_master() {
		return chartBody_diskspace_master;
	}
	public String getDataLabel_diskspace_master() {
		// Used
		String used = formatPoint2((double) master_sum_used / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) master_sum_used / master_sum_total * 100) + "%)";
		// Free
		String free = formatPoint2((double) master_sum_free / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) master_sum_free / master_sum_total * 100) + "%)";
		// Total
		String total = formatPoint2((double) master_sum_total / 1024 / 1024 / 1024) + "GB";
		return used + " / " + total;
	}
	public String getDataValue_diskspace_master() {
		return formatPoint2((double) master_sum_used / master_sum_total * 100) + "|";
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace.jsp
	public String getChartBody_diskspace_segments() {
		return chartBody_diskspace_segments;
	}
	public String getDataLabel_diskspace_segments() {
		// Used
		String used = formatPoint2((double) segment_sum_used / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) segment_sum_used / segment_sum_total * 100) + "%)";
		// Free
		String free = formatPoint2((double) segment_sum_free / 1024 / 1024 / 1024) + "GB (" + formatPoint2((double) segment_sum_free / segment_sum_total * 100) + "%)";
		// Total
		String total = formatPoint2((double) segment_sum_total / 1024 / 1024 / 1024) + "GB";
		return used + " / " + total;
	}
	public String getDataValue_diskspace_segments() {
		return formatPoint2((double) segment_sum_used / segment_sum_total * 100) + "|";
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/system/usage_diskspace_details.jsp
	public String getChartBody_diskspace_details() {
		return chartBody_diskspace_details;
	}
	public String getDataLabel_diskspace_details() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = diskspaceNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_diskspace_details() {
		String value = "";
		for (Diskspace diskspace : diskspaceNowList_masterAndSegs)
			value += formatPoint2((double) diskspace.getBytes_used() / diskspace.getTotal_bytes() * 100) + "|";
		return value;
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_diskspace.jsp
	public String getChartBody_diskspace_segments_history() {
		return chartBody_diskspace_segments_history;
	}
}
