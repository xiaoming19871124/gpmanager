package com.txdb.gpmanage.core.gp.service;

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

public interface IGpManageService {
	
	public static final String PG_HBA_CONF = "pg_hba.conf";
	
	public static final String DEFAULT_SYSTEM_DB_POSTGRES = "postgres";
	public static final String DEFAULT_SYSTEM_DB_TEMPLATE0 = "template0";
	public static final String DEFAULT_SYSTEM_DB_TEMPLATE1 = "template1";
	public static final String DEFAULT_EXPAND_DB = "expand_greenplum_database";
	public static final String DEFAULT_SYSTEM_DB_GPPERFMON = "gpperfmon";
	
	void initialize(IExecuteDao dao);
	
	// 正常启动GP
	GPResultSet gpStart();
	
	// 启动GP
	GPResultSet gpStart(String args);
	
	// 关闭GP
	GPResultSet gpStop();
	
	// （正常/快速）关闭、重启GP
	GPResultSet gpStop(String args);
	
	// Greenplum 版本号
	String gpVersion();
	
	// PostgreSQL 版本号
	String pgVersion();
	
	// 获取Greenplum安装路径
	String gpHome();

	// 获取本地当前路径
	String localPath();
	String localPath(boolean usePhysical);
	
//	// 获取Master数据目录
//	String masterDataDirectory();
	
	// GP数据库运行状态
	GPResultSet gpState();
	
	// GP数据库运行状态明细
	GPResultSet gpState(String args);
	
	// 系统环境参数检测
	GPResultSet gpCheck(String args);
	
	// 硬件环境检测
	GPResultSet gpCheckPerf(String args);
	
	// 校验数据库系统目录完整性
	GPResultSet gpCheckCat(int port, String args);
	
	// 恢复镜像到Segment
	GPResultSet gpRecoverSeg();
	GPResultSet gpRecoverSeg(String args);
	
	// 切换到Standby
	GPResultSet gpActivateStandby();
	GPResultSet gpActivateStandby(int pgPort);
	GPResultSet gpActivateStandby(String standbyDataDir);
	GPResultSet gpActivateStandby(int pgPort, String standbyDataDir);
	
	// 查询/修改GP参数
	List<GPConfig> gpconfigList();
	GPConfigValue gpconfig(String paramName);
	GPResultSet gpconfig(String paramName, String defaultValue);
	GPResultSet gpconfig(String paramName, String defaultValue, String masterValue);
	
	// 删除GP数据库
	GPResultSet gpDeleteSystem();
	GPResultSet gpDeleteSystem(String masterDataDir);
	
	// 登录权限查询
	List<PGHbaInfo> checkAuthority(String masterDataDirectory);
	
	// 登录权限配置
	GPResultSet updateAuthority(String masterDataDirectory, List<PGHbaInfo> pgHbaInfoList);
	
	// 执行psql命令
	GPResultSet psql(String args);
	
	// JDBC连接
	boolean connectJdbc(String username, String password);
	boolean connectJdbc(String username, String password, String database);
	boolean connectJdbc(String username, String password, int port, String database);
	
	// 执行SQL查询
	List<Map<String,Object>> executeQuery(String sql);
	
	// 执行SQL更新
	int executeUpdate(String sql);
	
	// 查询所有Segment配置信息
	List<GPSegmentInfo> queryGPSegmentInfo();
	
	// 查询所有Database信息
	List<PGDatabaseInfo> queryPGDatabaseInfo();
	
	// 查询活动的连接
	List<PGStatActivity> queryPGStatActivities();
	
	// 查看数据库运行时长
	String queryUptime();
	
	// 查询所有Hostname
	List<String> queryAllHost();
	
	
	// ------------------ 监控管理 ------------------
	
	// 安装gpperfmon监控服务
	GPResultSet gpperfmonInstall(String password, String port);
	
	// 查询pg_log（监控）
	List<LogAlert> queryLogAlert();
	List<LogAlert> queryLogAlert(boolean isHistory);
	List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo);
	List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo, boolean isHistory);
	List<LogAlert> queryLogAlert(LogAlert condition, boolean isHistory);
	
	// 查询queries（监控）
	List<Queries> queryQueries();
	List<Queries> queryQueries(boolean isHistory);
	List<Queries> queryQueries(Date dateFrom, Date dateTo);
	List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory);
	List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory, String username, String db);
	
	// 查询system（监控）
	List<System> querySystem();
	List<System> querySystem(boolean isHistory);
	List<System> querySystem(Date dateFrom, Date dateTo);
	List<System> querySystem(Date dateFrom, Date dateTo, boolean isHistory);
	
	// 查询system平均值（监控）
	List<System> querySystemAvg();
	List<System> querySystemAvg(boolean isHistory);
	List<System> querySystemAvg(Date dateFrom, Date dateTo);
	List<System> querySystemAvg(Date dateFrom, Date dateTo, boolean isHistory);
	
	// 查询database queries（监控）
	List<Database> queryDatabase();
	List<Database> queryDatabase(boolean isHistory);
	List<Database> queryDatabase(Date dateFrom, Date dateTo);
	List<Database> queryDatabase(Date dateFrom, Date dateTo, boolean isHistory);
	
	// 查询diskspace（监控）
	List<Diskspace> queryDiskspace();
	List<Diskspace> queryDiskspace(boolean isHistory);
	List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo);
	List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo, boolean isHistory);
	
	// 查询diskspace平均值（监控）
	List<Diskspace> queryDiskspaceAve();
	List<Diskspace> queryDiskspaceAve(boolean isHistory);
	List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo);
	List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo, boolean isHistory);
	
	
	// ------------------ 审计管理 ------------------
	// 审计查询
	GPResultSet gpLogFilter(String filterCommand);
	GPResultSet gpLogFilter(String filterCommand, String extraCommand);
	List<AuditEntity> queryAuditData(AuditEntity condition, String serialAuditTable);
	
	// 审计导出
	boolean downloadLogData(String localFileDir, String remoteFileDir, String serialNo, String sshCommand) throws Exception;
	
	// 审计导入
//	boolean uploadLogData();
}
