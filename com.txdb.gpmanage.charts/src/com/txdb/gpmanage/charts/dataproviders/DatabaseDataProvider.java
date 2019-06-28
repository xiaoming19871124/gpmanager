package com.txdb.gpmanage.charts.dataproviders;

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
import com.txdb.gpmanage.gpmon.entries.Database;
import com.txdb.gpmanage.gpmon.entries.RequireConnection;

public class DatabaseDataProvider {

	private static Map<String, DatabaseDataProvider> providerMap = new HashMap<String, DatabaseDataProvider>();
	
	private String monitorName;
	
	private List<Database> databaseNowList;
	private List<Database> databaseHistoryList;
	
	// Chart Json Body
	private String chartBody_clusterUsageQueries;
	private String chartBody_historyUsageQueries;
	
	public static DatabaseDataProvider getInstance(String monitorName) {
		if (!providerMap.containsKey(monitorName))
			providerMap.put(monitorName, new DatabaseDataProvider(monitorName));
		return providerMap.get(monitorName);
	}
	
	public DatabaseDataProvider(String monitorName) {
		this.monitorName = monitorName;
	}
	
	public String getMonitorName() {
		return monitorName;
	}
	
	public void updateDatabaseNowJson(String databaseListJson) {
		databaseNowList = JsonUtils.toCollection(databaseListJson, Database.class);
		
		// Build Cluster Chart Body
		String[] dataArray = new String[] { "Running", "Queued" };
		FusionCharts fusionCharts = ChartUtil.buildRealtimeLine("Queries", dataArray, "dp/cluster_usage_queries_dp.jsp?monitorName=" + getMonitorName());
		fusionCharts.getDataSource().getChart().setNumberSuffix("");
		chartBody_clusterUsageQueries = ChartUtil.toJsonChart(fusionCharts);
	}
	
	public void requireDatabaseHistoryJson(RequireConnection requireConnection) {
		// TODO
		IGPConnector connector = new GPConnectorImpl(
				requireConnection.getHost(), 
				requireConnection.getSshUsername(), 
				requireConnection.getSshPassword(), 
				requireConnection.getSshPort());
		
		if (!connector.connect().isSuccessed()) {
			System.err.println("(requireDatabaseHistoryJson) Jetty SSH Connect failed!");
			return;
		}
		try {
			connector.getDao().createPSQL(
					requireConnection.getJdbcUsername(), 
					requireConnection.getJdbcPassword(), 
					requireConnection.getJdbcPort(), 
					requireConnection.getDatabase());
			
		} catch (Exception e) {
			System.err.println("(requireDatabaseHistoryJson) Jetty JDBC Connect failed!");
			connector.disconnect();
			return;
		}
		GpManageServiceProxy proxy = connector.getManageServiceProxy();
		String databaseListJson = "";
		try {
			List<?> databaseHistoryList = proxy.queryDatabase(requireConnection.getDateFrom(), requireConnection.getDateTo(), true);
			databaseListJson = JsonUtils.toJsonArray(databaseHistoryList, true);
			updateDatabaseHistoryJson(databaseListJson);
			
		} catch (ParseException e) {
			System.err.println("(requireDatabaseHistoryJson) Jetty query database failed.");
			return;
			
		} finally {
			connector.disconnect();
		}
	}
	
	public void updateDatabaseHistoryJson(String databaseListJson) {
		databaseHistoryList = JsonUtils.toCollection(databaseListJson, Database.class);
		
		// Build Chart Body
		int itemsCount = databaseHistoryList.size();
		Category[] categoryArray = new Category[itemsCount];
		
		Data[] dataArray_running = new Data[itemsCount];
		Data[] dataArray_queued  = new Data[itemsCount];
		
		Dataset[] datasetArray = new Dataset[2];
		datasetArray[0] = new Dataset("Running", "00D789", 50, dataArray_running);
		datasetArray[1] = new Dataset("Queued", "FFD33C", 50, dataArray_queued);
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		for (int i = itemsCount - 1, j = 0; i >= 0; i--, j++) {
			Database database = databaseHistoryList.get(i);
			categoryArray[j] = new Category(sdf.format(database.getCtime()));
			
			dataArray_running[j] = new Data(String.valueOf(database.getQueries_running()));
			dataArray_queued[j]  = new Data(String.valueOf(database.getQueries_queued()));
		}
		FusionCharts chart = ChartUtil.buildZoomline("Queries History", categoryArray, datasetArray);
		chart.getDataSource().getChart().setNumberSuffix("");
		chartBody_historyUsageQueries = ChartUtil.toJsonChart(chart);
	}
	
	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_queries.jsp
	public String getChartBody_clusterUsageQueries() {
		return chartBody_clusterUsageQueries;
	}
	public String getDataLabel_clusterUsageQueries() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date ctime = databaseNowList.get(0).getCtime();
		return sdf.format(ctime);
	}
	public String getDataValue_clusterUsageQueries() {
		Database database = databaseNowList.get(0);
		return database.getQueries_running() + "|" + database.getQueries_queued();
	}

	// http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/history/history_usage_queries.jsp
	public String getChartBody_historyUsageQueries() {
		System.out.println(chartBody_historyUsageQueries);
		return chartBody_historyUsageQueries;
	}
}
