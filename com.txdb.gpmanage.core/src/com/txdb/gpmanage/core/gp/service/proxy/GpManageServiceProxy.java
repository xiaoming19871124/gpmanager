package com.txdb.gpmanage.core.gp.service.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.core.entity.AuditEntity;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.PGDatabaseInfo;
import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.core.gp.entry.PGStatActivity;
import com.txdb.gpmanage.core.gp.entry.gpmon.Database;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.entry.gpmon.LogAlert;
import com.txdb.gpmanage.core.gp.entry.gpmon.Queries;
import com.txdb.gpmanage.core.gp.entry.gpmon.System;
import com.txdb.gpmanage.core.gp.service.IGpManageService;

public class GpManageServiceProxy implements IGpManageService {

	private List<IGpManageService> manageServiceList;
	
	public GpManageServiceProxy() {
		manageServiceList = new ArrayList<IGpManageService>();
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
		// TODO Auto-generated method stub
	}
	
	public void addService(IGpManageService gpManageService) {
		manageServiceList.add(gpManageService);
	}
	
	public void clear() {
		manageServiceList.clear();
	}
	
	@Override
	public GPResultSet gpStart() {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpStart());
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpStart(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpStart(args));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpStop() {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpStop());
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpStop(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpStop(args));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public String gpVersion() {
		return manageServiceList.get(0).gpVersion();
	}
	
	@Override
	public String pgVersion() {
		return manageServiceList.get(0).pgVersion();
	}
	
	@Override
	public String gpHome() {
		return manageServiceList.get(0).gpHome();
	}
	
	@Override
	public String localPath() {
		return localPath(false);
	}
	
	@Override
	public String localPath(boolean usePhysical) {
		return manageServiceList.get(0).localPath(usePhysical);
	}
	
	@Override
	public GPResultSet gpState() {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpState());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpState(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpState(args));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheck(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpCheck(args));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheckPerf(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpCheckPerf(args));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheckCat(int port, String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).gpCheckCat(port, args));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpRecoverSeg() {
		return manageServiceList.get(0).gpRecoverSeg(null);
	}
	
	@Override
	public GPResultSet gpRecoverSeg(String args) {
		return manageServiceList.get(0).gpRecoverSeg(args);
	}
	
	@Override
	public GPResultSet gpActivateStandby() {
		return manageServiceList.get(0).gpActivateStandby(-1, null);
	}
	
	@Override
	public GPResultSet gpActivateStandby(int pgPort) {
		return manageServiceList.get(0).gpActivateStandby(pgPort, null);
	}
	
	@Override
	public GPResultSet gpActivateStandby(String standbyDataDir) {
		return manageServiceList.get(0).gpActivateStandby(-1, standbyDataDir);
	}
	
	@Override
	public GPResultSet gpActivateStandby(int pgPort, String standbyDataDir) {
		return manageServiceList.get(0).gpActivateStandby(pgPort, standbyDataDir);
	}
	
	@Override
	public List<GPConfig> gpconfigList() {
		return manageServiceList.get(0).gpconfigList();
	}
	
	@Override
	public GPConfigValue gpconfig(String paramName) {
		return manageServiceList.get(0).gpconfig(paramName);
	}
	
	@Override
	public GPResultSet gpconfig(String paramName, String defaultValue) {
		return manageServiceList.get(0).gpconfig(paramName, defaultValue, null);
	}
	
	@Override
	public GPResultSet gpconfig(String paramName, String defaultValue, String masterValue) {
		return manageServiceList.get(0).gpconfig(paramName, defaultValue, masterValue);
	}
	
	@Override
	public GPResultSet gpDeleteSystem() {
		return manageServiceList.get(0).gpDeleteSystem(null);
	}
	
	@Override
	public GPResultSet gpDeleteSystem(String masterDataDir) {
		return manageServiceList.get(0).gpDeleteSystem(masterDataDir);
	}
	
	@Override
	public List<PGHbaInfo> checkAuthority(String masterDataDirectory) {
		return manageServiceList.get(0).checkAuthority(masterDataDirectory);
	}

	@Override
	public GPResultSet updateAuthority(String masterDataDirectory, List<PGHbaInfo> pgHbaInfoList) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).updateAuthority(masterDataDirectory, pgHbaInfoList));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet psql(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (manageServiceList.size() > 0)
			resultSet.addChildResultSet(manageServiceList.get(0).psql(args));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public boolean connectJdbc(String username, String password) {
		return connectJdbc(username, password, -1, null);
	}

	@Override
	public boolean connectJdbc(String username, String password, String database) {
		return connectJdbc(username, password, -1, database);
	}
	
	@Override
	public boolean connectJdbc(String username, String password, int port, String database) {
		return manageServiceList.get(0).connectJdbc(username, password, port, database);
	}
	
	@Override
	public List<Map<String, Object>> executeQuery(String sql) {
		return manageServiceList.get(0).executeQuery(sql);
	}
	
	@Override
	public int executeUpdate(String sql) {
		return manageServiceList.get(0).executeUpdate(sql);
	}
	
	@Override
	public List<GPSegmentInfo> queryGPSegmentInfo() {
		return manageServiceList.get(0).queryGPSegmentInfo();
	}
	
	@Override
	public List<PGDatabaseInfo> queryPGDatabaseInfo() {
		return manageServiceList.get(0).queryPGDatabaseInfo();
	}
	
	@Override
	public List<PGStatActivity> queryPGStatActivities() {
		return manageServiceList.get(0).queryPGStatActivities();
	}
	
	@Override
	public String queryUptime() {
		return manageServiceList.get(0).queryUptime();
	}
	
	@Override
	public List<String> queryAllHost() {
		return manageServiceList.get(0).queryAllHost();
	}
	
	
	@Override
	public GPResultSet gpperfmonInstall(String password, String port) {
		return manageServiceList.get(0).gpperfmonInstall(password, port);
	}

	
	@Override
	public List<LogAlert> queryLogAlert() {
		return manageServiceList.get(0).queryLogAlert(null, null, false);
	}
	@Override
	public List<LogAlert> queryLogAlert(boolean isHistory) {
		return manageServiceList.get(0).queryLogAlert(null, null, isHistory);
	}
	@Override
	public List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).queryLogAlert(dateFrom, dateTo, false);
	}
	@Override
	public List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).queryLogAlert(dateFrom, dateTo, isHistory);
	}
	@Override
	public List<LogAlert> queryLogAlert(LogAlert condition, boolean isHistory) {
		return manageServiceList.get(0).queryLogAlert(condition, isHistory);
	}
	
	
	@Override
	public List<Queries> queryQueries() {
		return manageServiceList.get(0).queryQueries(null, null, false);
	}
	@Override
	public List<Queries> queryQueries(boolean isHistory) {
		return manageServiceList.get(0).queryQueries(null, null, isHistory);
	}
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).queryQueries(dateFrom, dateTo, false);
	}
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).queryQueries(dateFrom, dateTo, isHistory);
	}
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory, String username, String db) {
		return manageServiceList.get(0).queryQueries(dateFrom, dateTo, isHistory, username, db);
	}
	
	
	@Override
	public List<System> querySystem() {
		return manageServiceList.get(0).querySystem(null, null, false);
	}
	@Override
	public List<System> querySystem(boolean isHistory) {
		return manageServiceList.get(0).querySystem(null, null, isHistory);
	}
	@Override
	public List<System> querySystem(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).querySystem(dateFrom, dateTo, false);
	}
	@Override
	public List<System> querySystem(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).querySystem(dateFrom, dateTo, isHistory);
	}
	
	
	@Override
	public List<System> querySystemAvg() {
		return manageServiceList.get(0).querySystemAvg(null, null, false);
	}
	@Override
	public List<System> querySystemAvg(boolean isHistory) {
		return manageServiceList.get(0).querySystemAvg(null, null, isHistory);
	}
	@Override
	public List<System> querySystemAvg(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).querySystemAvg(dateFrom, dateTo, false);
	}
	@Override
	public List<System> querySystemAvg(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).querySystemAvg(dateFrom, dateTo, isHistory);
	}
	
	
	@Override
	public List<Database> queryDatabase() {
		return manageServiceList.get(0).queryDatabase(null, null, false);
	}
	@Override
	public List<Database> queryDatabase(boolean isHistory) {
		return manageServiceList.get(0).queryDatabase(null, null, isHistory);
	}
	@Override
	public List<Database> queryDatabase(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).queryDatabase(dateFrom, dateTo, false);
	}
	@Override
	public List<Database> queryDatabase(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).queryDatabase(dateFrom, dateTo, isHistory);
	}
	
	
	@Override
	public List<Diskspace> queryDiskspace() {
		return manageServiceList.get(0).queryDiskspace(null, null, false);
	}
	@Override
	public List<Diskspace> queryDiskspace(boolean isHistory) {
		return manageServiceList.get(0).queryDiskspace(null, null, isHistory);
	}
	@Override
	public List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).queryDiskspace(dateFrom, dateTo, false);
	}
	@Override
	public List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).queryDiskspace(dateFrom, dateTo, isHistory);
	}
	
	
	@Override
	public List<Diskspace> queryDiskspaceAve() {
		return manageServiceList.get(0).queryDiskspaceAve(null, null, false);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(boolean isHistory) {
		return manageServiceList.get(0).queryDiskspaceAve(null, null, isHistory);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo) {
		return manageServiceList.get(0).queryDiskspaceAve(dateFrom, dateTo, false);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo, boolean isHistory) {
		return manageServiceList.get(0).queryDiskspaceAve(dateFrom, dateTo, isHistory);
	}
	
	
	@Override
	public GPResultSet gpLogFilter(String filterCommand) {
		return manageServiceList.get(0).gpLogFilter(filterCommand);
	}
	@Override
	public GPResultSet gpLogFilter(String filterCommand, String extraCommand) {
		return manageServiceList.get(0).gpLogFilter(filterCommand, extraCommand);
	}
	@Override
	public List<AuditEntity> queryAuditData(AuditEntity condition, String serialAuditTable) {
		return manageServiceList.get(0).queryAuditData(condition, serialAuditTable);
	}
	
	
	@Override
	public boolean downloadLogData(String localFileDir, String remoteFileDir, String serialNo, String sshCommand) throws Exception {
		return manageServiceList.get(0).downloadLogData(localFileDir, remoteFileDir, serialNo, sshCommand);
	}
}
