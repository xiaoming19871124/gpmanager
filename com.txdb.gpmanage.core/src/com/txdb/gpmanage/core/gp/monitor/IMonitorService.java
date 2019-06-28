package com.txdb.gpmanage.core.gp.monitor;

import java.io.IOException;

public interface IMonitorService {
	
	public static String address = "http://localhost:9681/services/MonitorService";
	public static String namespaceURI = "http://www.jettywebservice.com/xsd";
	
	/**
	 * 维护Segment状态信息，包括：启动状态、角色状态、同步状态等信息<br>
	 * <b>（来源表：gp_segment_configuration）</b>
	 * @param gpSegmentConfListJson
	 * @return
	 * @throws AxisFault 
	 */
	public String updateGpSegmentConf(String monitorName, String gpSegmentConfListJson) throws IOException;

	/**
	 * 维护系统状态信息，包括：memory, swap, cpu, load, disk I/O, network I/O信息<br>
	 * <b>（来源表：system_now, system_history）</b>
	 * @param systemListJson
	 * @return
	 * @throws AxisFault 
	 */
	public String updateSystemNow(String monitorName, String systemListJson) throws IOException;
	public String updateSystemHistory(String monitorName, String systemListJson) throws IOException;
	public String requireSystemHistory(String requireConnectionJson) throws IOException;
	
	/**
	 * 维护数据库queries队列数信息，包括：total, running, queued<br>
	 * <b>（来源表：database_now, database_history）</b>
	 * @param databaseListJson
	 * @return
	 * @throws AxisFault 
	 */
	public String updateDatabaseNow(String monitorName, String databaseListJson) throws IOException;
	public String updateDatabaseHistory(String monitorName, String databaseListJson) throws IOException;
	public String requireDatabaseHistory(String requireConnectionJson) throws IOException;

	/**
	 * 维护系统磁盘使用量信息，包括：total, used, available<br>
	 * <b>（来源表：diskspace_now, diskspace_history）</b>
	 * @param diskspaceListJson
	 * @return
	 * @throws AxisFault 
	 */
	public String updateDiskspaceNow(String monitorName, String diskspaceListJson) throws IOException;
	public String updateDiskspaceHistory(String monitorName, String diskspaceListJson) throws IOException;
	public String requireDiskspaceHistory(String requireConnectionJson) throws IOException;
}
