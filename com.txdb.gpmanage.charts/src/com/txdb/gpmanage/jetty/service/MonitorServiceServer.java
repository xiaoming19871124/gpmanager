package com.txdb.gpmanage.jetty.service;

import java.io.IOException;

import com.txdb.gpmanage.charts.dataproviders.DatabaseDataProvider;
import com.txdb.gpmanage.charts.dataproviders.DiskspaceDataProvider;
import com.txdb.gpmanage.charts.dataproviders.GpSegmentDataProvider;
import com.txdb.gpmanage.charts.dataproviders.SystemDataProvider;
import com.txdb.gpmanage.core.gp.monitor.IMonitorService;
import com.txdb.gpmanage.core.utils.JsonUtils;
import com.txdb.gpmanage.gpmon.entries.RequireConnection;

public class MonitorServiceServer implements IMonitorService {
	
	@Override
	public String updateGpSegmentConf(String monitorName, String gpSegmentConfListJson) {
		GpSegmentDataProvider.getInstance(monitorName).updateGpSegmentConfJson(gpSegmentConfListJson);
		return "GpSegmentConfiguration(" + monitorName + ") Information Updated(" + gpSegmentConfListJson.length() + ").";
	}
	
	@Override
	public String updateSystemNow(String monitorName, String systemListJson) {
		SystemDataProvider.getInstance(monitorName).updateSystemNowJson(systemListJson);
		return "System_Now(" + monitorName + ") Information Updated(" + systemListJson.length() + ").";
	}
	
	@Override
	public String updateSystemHistory(String monitorName, String systemListJson) {
		SystemDataProvider.getInstance(monitorName).updateSystemHistoryJson(systemListJson);
		return "System_History(" + monitorName + ") Information Updated(" + systemListJson.length() + ").";
	}
	
	@Override
	public String requireSystemHistory(String requireConnectionJson) throws IOException {
		// TODO Auto-generated method stub
		RequireConnection rc = JsonUtils.toObject(requireConnectionJson, RequireConnection.class);
		SystemDataProvider.getInstance(rc.getMonitorName()).requireSystemHistoryJson(rc);
		return "System_History(" + rc.getMonitorName() + ") Information Required.";
	}
	
	@Override
	public String updateDatabaseNow(String monitorName, String databaseListJson) {
		DatabaseDataProvider.getInstance(monitorName).updateDatabaseNowJson(databaseListJson);
		return "Database_Now(" + monitorName + ") Information Updated(" + databaseListJson.length() + ").";
	}
	
	@Override
	public String updateDatabaseHistory(String monitorName, String databaseListJson) {
		DatabaseDataProvider.getInstance(monitorName).updateDatabaseHistoryJson(databaseListJson);
		return "Database_History(" + monitorName + ") Information Updated(" + databaseListJson.length() + ").";
	}
	
	@Override
	public String requireDatabaseHistory(String requireConnectionJson) throws IOException {
		// TODO Auto-generated method stub
		RequireConnection rc = JsonUtils.toObject(requireConnectionJson, RequireConnection.class);
		DatabaseDataProvider.getInstance(rc.getMonitorName()).requireDatabaseHistoryJson(rc);
		return "Database_History(" + rc.getMonitorName() + ") Information Required.";
	}
	
	@Override
	public String updateDiskspaceNow(String monitorName, String diskspaceListJson) {
		DiskspaceDataProvider.getInstance(monitorName).updateDiskspaceNowJson(diskspaceListJson);
		return "Diskspace_Now(" + monitorName + ") Information Updated(" + diskspaceListJson.length() + ").";
	}
	
	@Override
	public String updateDiskspaceHistory(String monitorName, String diskspaceListJson) {
		DiskspaceDataProvider.getInstance(monitorName).updateDiskspaceHistoryJson(diskspaceListJson);
		return "Diskspace_History(" + monitorName + ") Information Updated(" + diskspaceListJson.length() + ").";
	}
	
	@Override
	public String requireDiskspaceHistory(String requireConnectionJson) throws IOException {
		// TODO Auto-generated method stub
		RequireConnection rc = JsonUtils.toObject(requireConnectionJson, RequireConnection.class);
		DiskspaceDataProvider.getInstance(rc.getMonitorName()).requireDiskspaceHistoryJson(rc);
		return "Diskspace_History(" + rc.getMonitorName() + ") Information Required.";
	}
}
