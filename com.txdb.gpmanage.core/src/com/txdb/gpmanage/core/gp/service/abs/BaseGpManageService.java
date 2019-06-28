package com.txdb.gpmanage.core.gp.service.abs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.JSchException;
import com.txdb.gpmanage.core.entity.AuditEntity;
import com.txdb.gpmanage.core.entity.AuditModel;
import com.txdb.gpmanage.core.gp.constant.IConstantSteps;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
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
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseGpManageService implements IGpManageService {

	protected IExecuteDao dao;
	
	private String gp_version = "N/A";
	private String pg_version = "N/A";
	
	@Override
	public void initialize(IExecuteDao dao) {
		this.dao = dao;
	}

	@Override
	public GPResultSet gpStart() {
		return gpStart(null);
	}

	@Override
	public GPResultSet gpStart(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpStart");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.AGREEMENT_STARTUP_GP };
			
			String cmd = String.format(IConstantsCmds.GP_START, args == null ? "" : args);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1]) || outputMsg.contains(conditions[2])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpStart exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpStop() {
		return gpStop(null);
	}

	@Override
	public GPResultSet gpStop(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpStop");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.AGREEMENT_SHUTDOWN_GP };
			
			String cmd = String.format(IConstantsCmds.GP_STOP, args == null ? "" : args);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1]) || outputMsg.contains(conditions[2])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpStop exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public String gpVersion() {
		return gp_version;
	}
	
	@Override
	public String pgVersion() {
		return pg_version;
	}
	
	@Override
	public String gpHome() {
		LogUtil.info("(" + dao.getExecutorName() + ") gpHome");
		String cmd_gpHome = String.format(IConstantsCmds.ECHO, "$GPHOME");
		try {
			GPResultSet rs_gpHome = dao.executeInteractiveCommand(cmd_gpHome, new String[] {});
			String gphomeStr = rs_gpHome.getOutputMsg();
			return gphomeStr.split("\n")[1].trim();
			
		} catch (Exception e) {
			LogUtil.warn("(" + dao.getExecutorName() + ") gpHome: get GPHOME failed: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public String localPath() {
		return localPath(false);
	}
	
	@Override
	public String localPath(boolean usePhysical) {
		LogUtil.info("(" + dao.getExecutorName() + ") localPath");
		String cmd_pwd = String.format(IConstantsCmds.PWD, usePhysical ? "-P" : "-L");
		try {
			GPResultSet rs_pwd = dao.executeInteractiveCommand(cmd_pwd, new String[] {});
			String pwdStr = rs_pwd.getOutputMsg();
			return pwdStr.split("\n")[1].trim();
			
		} catch (Exception e) {
			LogUtil.warn("(" + dao.getExecutorName() + ") localPath: get localPath failed: " + e.getMessage());
			return null;
		}
	}
	
	private void loadVersionAbout() {
		LogUtil.info("(" + dao.getExecutorName() + ") loadVersionAbout");
		
		String cmd_state = String.format(IConstantsCmds.GP_STATE, "--version");
		String cmd_psql  = String.format(IConstantsCmds.PSQL, "--version");
		try {
			GPResultSet rs_state = dao.executeInteractiveCommand(cmd_state, new String[] {});
			gp_version = formatGP_PGVersionStr(rs_state.getOutputMsg());
		} catch (Exception e) {
			gp_version = "N/A";
			LogUtil.warn("(" + dao.getExecutorName() + ") loadVersionAbout: get Greenplum version failed: " + e.getMessage());
		}
		try {
			GPResultSet rs_psql  = dao.executeInteractiveCommand(cmd_psql, new String[] {});
			pg_version = formatGP_PGVersionStr(rs_psql.getOutputMsg());
		} catch (Exception e) {
			pg_version = "N/A";
			LogUtil.warn("(" + dao.getExecutorName() + ") loadVersionAbout: get PostgreSQL version failed: " + e.getMessage());
		}
	}
	
	private String formatGP_PGVersionStr(String versionStr) {
		versionStr = versionStr.split("\n")[1].trim();
		int fromIndex = versionStr.indexOf(" ") + 1;
		return versionStr.substring(versionStr.indexOf(" ", fromIndex)).trim();
	}
	
	@Override
	public GPResultSet gpState() {
		return gpState(null);
	}
	
	@Override
	public GPResultSet gpState(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpState");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_STATE, args == null ? "" : args);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
			// Load GP & PG version about
			loadVersionAbout();
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpState exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheck(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpCheck");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {};
			
			String cmd = String.format(IConstantsCmds.GP_CHECK, args == null ? "" : args);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, conditions));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpCheck exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheckPerf(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpCheckPerf");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {};
			
			String cmd = String.format(IConstantsCmds.GP_CHECK_PERF, args == null ? "" : args);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, conditions));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpCheckPerf exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpCheckCat(int port, String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpCheckPerf");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {};
			
			String cmd = String.format(IConstantsCmds.GP_CHECK_CAT, "-p " + port + " " + (args == null ? "" : args));
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, conditions));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpCheckPerf exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpRecoverSeg() {
		return gpRecoverSeg(null);
	}
	
	@Override
	public GPResultSet gpRecoverSeg(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpRecoverSeg");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2 };
			
			String cmd = String.format(IConstantsCmds.GP_RECOVERSEG, (args == null ? "" : args));
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1])) {
				// Interactive
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpRecoverSeg exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpActivateStandby() {
		return gpActivateStandby(-1, null);
	}
	
	@Override
	public GPResultSet gpActivateStandby(int pgPort) {
		return gpActivateStandby(pgPort, null);
	}
	
	@Override
	public GPResultSet gpActivateStandby(String standbyDataDir) {
		return gpActivateStandby(-1, standbyDataDir);
	}
	
	@Override
	public GPResultSet gpActivateStandby(int pgPort, String standbyDataDir) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpActivateStandby");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2 };
			
			String cmd = String.format(IConstantsCmds.GP_ACTIVATE_STANDBY, (standbyDataDir == null ? "" : "-d " + standbyDataDir));
			if (pgPort > 0)
				cmd = "export PGPORT=" + pgPort + " && " + cmd;
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpActivateStandby exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<GPConfig> gpconfigList() {
		LogUtil.info("(" + dao.getExecutorName() + ") gpconfigList");
		try {
			String export="export PGPORT="+dao.getJdbcPort()+";";
			String cmd = String.format(export+IConstantsCmds.GP_CONFIG, "-l", "", "");
			GPResultSet rs = dao.executeCommand(cmd);
			if (!rs.isSuccessed())
				throw new Exception(rs.getOutputErr());
			
			List<GPConfig> gpconfigList = GPConfig.toList(rs.getOutputMsg());
			return gpconfigList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") gpconfigList exception: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public GPConfigValue gpconfig(String paramName) {
		GPResultSet rs = gpconfig(paramName, null, null);
		if (!rs.isSuccessed())
			return null;
		
		String msg = rs.getChildResultSetList().get(0).getOutputMsg();
		return GPConfigValue.toEntry(msg);
	}
	
	@Override
	public GPResultSet gpconfig(String paramName, String defaultValue) {
		return gpconfig(paramName, defaultValue, null);
	}
	
	@Override
	public GPResultSet gpconfig(String paramName, String defaultValue, String masterValue) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpconfig");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			// gpconfig -s max_connections
			// gpconfig -c max_connections -v 750 -m 150
			
			String paramN = (defaultValue == null ? "-s " : "-c ") + paramName;
			String cValue = (defaultValue == null ? "" : "-v " + defaultValue);
			String mValue = (masterValue == null ? "" : "-m " + masterValue);
			String cmd = String.format(IConstantsCmds.GP_CONFIG, paramN, cValue, mValue);
			
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpconfig exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpDeleteSystem() {
		return gpDeleteSystem(null);
	}
	
	@Override
	public GPResultSet gpDeleteSystem(String masterDataDir) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpDeleteSystem");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2 };
			
			String cmd = String.format(IConstantsCmds.GP_DELETE_SYSTEM, (masterDataDir == null ? "" : "-d " + masterDataDir));
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpDeleteSystem exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<PGHbaInfo> checkAuthority(String masterDataDirectory) {
		LogUtil.info("(" + dao.getExecutorName() + ") checkAuthority");
		List<PGHbaInfo> pgHbaInfoList = new ArrayList<PGHbaInfo>();
		try {
			masterDataDirectory += "/";
			masterDataDirectory = masterDataDirectory.replaceAll("//", "/");
			String remoteCfgFile = masterDataDirectory + PG_HBA_CONF;
			
//			GPResultSet rs = dao.executeActiveCommand("cat " + remoteCfgFile, new String[] {});
			GPResultSet rs = dao.executeCommand("cat " + remoteCfgFile);
			if (!rs.isSuccessed())
				return null;
			
			String msg = rs.getOutputMsg();
			String[] msgFragments = msg.split("\n");
			for (String fragment : msgFragments) {
				fragment = fragment.trim();
				if (fragment.startsWith("#") || fragment.length() <= 0)
					continue;
				
				LogUtil.info("(" + dao.getExecutorName() + ") pg_hba.conf: " + fragment);
				fragment = fragment.replaceAll("\t+", " ");
				fragment = fragment.replaceAll("\\ +", " ");
				fragment = fragment.replaceAll(",\\ +", ",");
				fragment = fragment.replaceAll("\\ +,", ",");
				
				// local    all         gpadmin         ident
				// host     all         gpadmin         127.0.0.1/28    trust
				int fragCount = fragment.split(" ").length;
				if (fragCount != 4 && fragCount != 5)
					continue;
				
				pgHbaInfoList.add(new PGHbaInfo(fragment));
			}
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") checkAuthority exception: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
		return pgHbaInfoList;
	}
	
	@Override
	public GPResultSet updateAuthority(String masterDataDirectory, List<PGHbaInfo> pgHbaInfoList) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateAuthority");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			masterDataDirectory += "/";
			masterDataDirectory = masterDataDirectory.replaceAll("//", "/");
			String remoteCfgFile = masterDataDirectory + PG_HBA_CONF;
			
			for (PGHbaInfo pgHbaInfo : pgHbaInfoList) {
				StringBuffer fmtBuff = new StringBuffer();
				fmtBuff.append(pgHbaInfo.getType() + " ");
				fmtBuff.append(pgHbaInfo.getDatabaseArrayString() + " ");
				fmtBuff.append(pgHbaInfo.getUserArrayString() + " ");
				if (pgHbaInfo.getAddress() != null)
					fmtBuff.append(pgHbaInfo.getAddress() + " ");
				fmtBuff.append(pgHbaInfo.getMethod() + " ");
				
				String fmtStr = fmtBuff.toString().trim();
				if (fmtStr.split(" ").length != 4 && fmtStr.split(" ").length != 5)
					continue;
				
				String fmtStr_sed = fmtStr;
				fmtStr_sed = fmtStr_sed.replaceAll(" ", "[ ]*");
				fmtStr_sed = fmtStr_sed.replaceAll("/", "\\\\/");
				fmtStr_sed = fmtStr_sed.replaceAll(",", "[ ]*,[ ]*");
				String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, fmtStr_sed, remoteCfgFile);
				resultSet.addChildResultSet(dao.executeCommand(cmd));
				
				if (PGHbaInfo.MODIFY_ADD == pgHbaInfo.getModifyType()) {
					cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, fmtStr, remoteCfgFile);
					resultSet.addChildResultSet(dao.executeCommand(cmd));
				}
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") updateAuthority exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet psql(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") psql");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.PSQL, (args == null ? "" : args));
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
//			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") psql exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public boolean connectJdbc(String username, String password) {
		return connectJdbc(username, password, null);
	}
	
	@Override
	public boolean connectJdbc(String username, String password, String database) {
		return connectJdbc(username, password, -1, database);
	}
	
	@Override
	public boolean connectJdbc(String username, String password, int port, String database) {
		LogUtil.info("(" + dao.getExecutorName() + ") connectJdbc");
		try {
			dao.createPSQL(username, password, port, database == null ? DEFAULT_SYSTEM_DB_POSTGRES : database);
			return true;
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") Create connectJdbc failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return false;
		}
	}
	
	@Override
	public List<Map<String,Object>> executeQuery(String sql) {
		LogUtil.info("(" + dao.getExecutorName() + ") executeQuery");
		try {
			return dao.executeQuery(sql);
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") Execute Query failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public int executeUpdate(String sql) {
		LogUtil.info("(" + dao.getExecutorName() + ") executeUpdate");
		try {
			LogUtil.info("(" + dao.getExecutorName() + ") Begin to execute sql: " + sql);
			return dao.executeUpdate(sql);
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") Execute Update failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return -1;
		}
	}
	
	@Override
	public List<GPSegmentInfo> queryGPSegmentInfo() {
		LogUtil.info("(" + dao.getExecutorName() + ") queryGPSegmentInfo");
		try {
			if ("N/A".equals(gpVersion()))
				loadVersionAbout();
			
			int compare = GPRequiredSw.compareVersion(gpVersion(), "6.0.0");
			String querySql = "select s.*, f.* from gp_segment_configuration s, pg_filespace_entry f " + "where s.dbid = f.fsedbid order by s.dbid";
			if (compare >= 0)
				querySql = "select * from gp_segment_configuration order by dbid";
			
//			ResultSet sqlRs = dao.executeQuery(querySql);
//			if (sqlRs == null)
//				return null;
			List<Map<String,Object>> dataList = dao.executeQuery(querySql);
			if (dataList == null)
				return null;
			
			List<GPSegmentInfo> segInfoList = new ArrayList<GPSegmentInfo>();
			GPSegmentInfo tempSegInfo = null;
			for (Map<String,Object> rowMap : dataList) {
				tempSegInfo = new GPSegmentInfo();
				tempSegInfo.setDbid((int) rowMap.get("dbid"));
				tempSegInfo.setContent((int) rowMap.get("content"));
				tempSegInfo.setRole((String) rowMap.get("role"));
				tempSegInfo.setPreferred_role((String) rowMap.get("preferred_role"));
				tempSegInfo.setMode((String) rowMap.get("mode"));
				tempSegInfo.setStatus((String) rowMap.get("status"));
				tempSegInfo.setPort((int) rowMap.get("port"));
				tempSegInfo.setHostname((String) rowMap.get("hostname"));
				tempSegInfo.setAddress((String) rowMap.get("address"));
				tempSegInfo.setDatadir((String) rowMap.get(compare >= 0 ? "datadir" : "fselocation"));
				segInfoList.add(tempSegInfo);
			}
//			sqlRs.close();
			return segInfoList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryGPSegmentInfo failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<PGDatabaseInfo> queryPGDatabaseInfo() {
		LogUtil.info("(" + dao.getExecutorName() + ") queryPGDatabaseInfo");
		try {
			List<Map<String,Object>> dataList = dao.executeQuery("select * from pg_database order by datname");
			if (dataList == null)
				return null;
			
			List<PGDatabaseInfo> segInfoList = new ArrayList<PGDatabaseInfo>();
			PGDatabaseInfo tempDBInfo = null;
			for (Map<String,Object> rowMap : dataList) {
				// gp5.10
				tempDBInfo = new PGDatabaseInfo();
				tempDBInfo.setDatname((String) rowMap.get("datname"));
				tempDBInfo.setDatdba((int) rowMap.get("datdba"));
				tempDBInfo.setEncoding((int) rowMap.get("encoding"));
				tempDBInfo.setDatistemplate((boolean) rowMap.get("datistemplate"));
				tempDBInfo.setDatallowconn((boolean) rowMap.get("datallowconn"));
				tempDBInfo.setDatconnlimit((int) rowMap.get("datconnlimit"));
				tempDBInfo.setDatlastsysoid((int) rowMap.get("datlastsysoid"));
				tempDBInfo.setDatfrozenxid((int) rowMap.get("datfrozenxid"));
				tempDBInfo.setDattablespace((int) rowMap.get("dattablespace"));
				tempDBInfo.setDatconfig((String) rowMap.get("datconfig"));
				tempDBInfo.setDatacl((String) rowMap.get("datacl"));
				
				// gp6.0
//				tempDBInfo.setDatname(sqlRs.getString("datname"));
//				tempDBInfo.setDatdba(sqlRs.getInt("datdba"));
//				tempDBInfo.setEncoding(sqlRs.getInt("encoding"));
//				tempDBInfo.setDatcollate(sqlRs.getString("datcollate"));
//				tempDBInfo.setDatctype(sqlRs.getString("datctype"));
//				tempDBInfo.setDatistemplate(sqlRs.getString("datistemplate"));
//				tempDBInfo.setDatallowconn(sqlRs.getString("datallowconn"));
//				tempDBInfo.setDatconnlimit(sqlRs.getInt("datconnlimit"));
//				tempDBInfo.setDatlastsysoid(sqlRs.getInt("datlastsysoid"));
//				tempDBInfo.setDatfrozenxid(sqlRs.getInt("datfrozenxid"));
//				tempDBInfo.setDattablespace(sqlRs.getString("dattablespace"));
//				tempDBInfo.setDatacl(sqlRs.getString("datacl"));
				segInfoList.add(tempDBInfo);
			}
//			sqlRs.close();
			return segInfoList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryPGDatabaseInfo failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<PGStatActivity> queryPGStatActivities() {
		LogUtil.info("(" + dao.getExecutorName() + ") queryPGStatActivities");
		try {
			List<Map<String,Object>> dataList = dao.executeQuery("select datid, datname, sess_id, usesysid, usename from pg_stat_activity");
			if (dataList == null)
				return null;
			
			List<PGStatActivity> activityList = new ArrayList<PGStatActivity>();
			PGStatActivity tempActivity = null;
			for (Map<String,Object> rowMap : dataList) {
				tempActivity = new PGStatActivity();
				tempActivity.setDatid(Integer.parseInt(rowMap.get("datid") + ""));
				tempActivity.setDatname((String) rowMap.get("datname"));
				tempActivity.setSess_id(Integer.parseInt(rowMap.get("sess_id") + ""));
				tempActivity.setUsesysid(Integer.parseInt(rowMap.get("usesysid") + ""));
				tempActivity.setUsename((String) rowMap.get("usename"));
				activityList.add(tempActivity);
			}
//			sqlRs.close();
			return activityList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryPGStatActivities failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String queryUptime() {
		LogUtil.info("(" + dao.getExecutorName() + ") queryUptime");
		try {
			List<Map<String,Object>> dataList = dao.executeQuery("select current_timestamp - pg_postmaster_start_time() as uptime");
			if (dataList == null || dataList.size() <= 0)
				return null;
			
			String uptime = "Unknow";
			uptime = String.valueOf(dataList.get(0).get("uptime") + "");
//			sqlRs.close();
			
			return uptime.split("\\.")[0];
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryUptime failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<String> queryAllHost() {
		List<String> hostnameList = new ArrayList<String>();
		List<GPSegmentInfo> GPSegmentInfoList = queryGPSegmentInfo();
		for (GPSegmentInfo info : GPSegmentInfoList) {
			String hostname = info.getHostname();
			if (!hostnameList.contains(hostname))
				hostnameList.add(hostname);
		}
		return hostnameList;
	}
	
	@Override
	public GPResultSet gpperfmonInstall(String password, String port) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpperfmonInstall");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_GPPERFMON_INSTALL, password, port);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpperfmonInstall exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<LogAlert> queryLogAlert() {
		return queryLogAlert(null, null, false);
	}
	@Override
	public List<LogAlert> queryLogAlert(boolean isHistory) {
		return queryLogAlert(null, null, isHistory);
	}
	@Override
	public List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo) {
		return queryLogAlert(dateFrom, dateTo, false);
	}
	@Override
	public List<LogAlert> queryLogAlert(Date dateFrom, Date dateTo, boolean isHistory) {
		LogAlert condition = new LogAlert();
		condition.set_logtimeFrom(dateFrom);
		condition.set_logtimeTo(dateTo);
		return queryLogAlert(condition, isHistory);
	}
	@Override
	public List<LogAlert> queryLogAlert(LogAlert condition, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryLogAlert");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "log_alert_history" : "log_alert_now");
			String sql_condition = "";
			
			Date dateFrom = condition.get_logtimeFrom();
			Date dateTo = condition.get_logtimeTo();
			String user = condition.getLoguser();
			String database = condition.getLogdatabase();
			String host = condition.getLoghost();
			if (dateFrom != null)
				sql_condition += " and logtime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql_condition += " and logtime <= '" + sdf.format(dateTo) + "'";
			if (user != null && user.length() > 0)
				sql_condition += " and loguser = '" + user + "'";
			if (database != null && database.length() > 0)
				sql_condition += " and logdatabase = '" + database + "'";
			if (host != null && host.length() > 0)
				sql_condition += " and loghost = '" + host + "'";
			
			sql_condition += " and logseverity in ("
					+ "'" + (condition.is_logseverity_panic() ? LogAlert.LOG_PANIC : "") + "', "
					+ "'" + (condition.is_logseverity_fatal() ? LogAlert.LOG_FATAL : "") + "', "
					+ "'" + (condition.is_logseverity_error() ? LogAlert.LOG_ERROR : "") + "', "
					+ "'" + (condition.is_logseverity_warning() ? LogAlert.LOG_WARNING : "") + "')";
			
			// 1.0 查询总条数（计算分页）
			String sql_count = "select count(*) as count from " + tablename + " where 1=1";
//			ResultSet sqlRs = dao.executeQuery(sql_count + sql_condition);
			List<Map<String, Object>> dataList = dao.executeQuery(sql_count + sql_condition);
			if (dataList.size() > 0)
				condition.setTotalCount(Integer.parseInt(dataList.get(0).get("count") + ""));
//			sqlRs.close();
			
			// 2.0 查询数据实体
			int limit = condition.getLimit();
			int offset = condition.getOffset();
			sql_condition += " order by logtime desc";
			if (limit > 0)
				sql_condition += " limit " + limit + " offset " + offset;
			
			String sql_query = "select * from " + tablename + " where 1=1";
			dataList = dao.executeQuery(sql_query + sql_condition);
			if (dataList == null)
				return null;
			List<LogAlert> logAlertList = LogAlert.toList(dataList);
			
//			sqlRs.close();
			return logAlertList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryLogAlert failed: " + e.getMessage());
			e.printStackTrace();
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<Queries> queryQueries() {
		return queryQueries(null, null, false);
	}
	@Override
	public List<Queries> queryQueries(boolean isHistory) {
		return queryQueries(null, null, isHistory);
	}
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo) {
		return queryQueries(dateFrom, dateTo, false);
	}
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory) {
		return queryQueries(dateFrom, dateTo, false, null, null);
	}
	
	@Override
	public List<Queries> queryQueries(Date dateFrom, Date dateTo, boolean isHistory, String username, String db) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryQueries");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "queries_history" : "queries_now");
			String sql = "select * from " + tablename + " where 1=1";
			if (dateFrom != null)
				sql += " and ctime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql += " and ctime <= '" + sdf.format(dateTo) + "'";
			if (username != null)
				sql += " and username = '" + username + "'";
			if (db != null)
				sql += " and db = '" + db + "'";
			sql += " order by tmid, ssid, ccnt asc";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<Queries> queriesList = Queries.toList(dataList);
			
//			sqlRs.close();
			return queriesList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryQueries failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<System> querySystem() {
		return querySystem(null, null, false);
	}
	@Override
	public List<System> querySystem(boolean isHistory) {
		return querySystem(null, null, isHistory);
	}
	@Override
	public List<System> querySystem(Date dateFrom, Date dateTo) {
		return querySystem(dateFrom, dateTo, false);
	}
	@Override
	public List<System> querySystem(Date dateFrom, Date dateTo, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") querySystem");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "system_history" : "system_now");
			String sql = "select * from " + tablename + " where 1=1";
			if (dateFrom != null)
				sql += " and ctime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql += " and ctime <= '" + sdf.format(dateTo) + "'";
			sql += " order by hostname";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<System> systemList = System.toList(dataList);
			
//			sqlRs.close();
			return systemList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") querySystem failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<System> querySystemAvg() {
		return querySystemAvg(null, null, false);
	}
	@Override
	public List<System> querySystemAvg(boolean isHistory) {
		return querySystemAvg(null, null, isHistory);
	}
	@Override
	public List<System> querySystemAvg(Date dateFrom, Date dateTo) {
		return querySystemAvg(dateFrom, dateTo, false);
	}
	@Override
	public List<System> querySystemAvg(Date dateFrom, Date dateTo, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") querySystemAvg");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "system_history" : "system_now");
			String sql = "SELECT ctime, cast(SUM(mem_total) / COUNT(ctime) as bigint) AS mem_total "
					+ "	, cast(SUM(mem_used) / COUNT(ctime) as bigint) AS mem_used "
					+ "	, cast(SUM(mem_actual_used) / COUNT(ctime) as bigint) AS mem_actual_used "
					+ "	, cast(SUM(mem_actual_free) / COUNT(ctime) as bigint) AS mem_actual_free "
					+ "	, cast(SUM(swap_total) / COUNT(ctime) as bigint) AS swap_total "
					+ "	, cast(SUM(swap_used) / COUNT(ctime) as bigint) AS swap_used "
					+ "	, cast(SUM(swap_page_in) / COUNT(ctime) as bigint) AS swap_page_in "
					+ "	, cast(SUM(swap_page_out) / COUNT(ctime) as bigint) AS swap_page_out "
					+ "	, round(CAST(SUM(cpu_user) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS cpu_user "
					+ "	, round(CAST(SUM(cpu_sys) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS cpu_sys "
					+ "	, round(CAST(SUM(cpu_idle) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS cpu_idle "
					+ "	, round(CAST(SUM(load0) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS load0 "
					+ "	, round(CAST(SUM(load1) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS load1 "
					+ "	, round(CAST(SUM(load2) / COUNT(ctime) AS numeric) + 1, 2) - 1 AS load2 "
					+ "	, SUM(quantum) / COUNT(ctime) AS quantum "
					+ "	, cast(SUM(disk_ro_rate) / COUNT(ctime) as bigint) AS disk_ro_rate "
					+ "	, cast(SUM(disk_wo_rate) / COUNT(ctime) as bigint) AS disk_wo_rate "
					+ "	, cast(SUM(disk_rb_rate) / COUNT(ctime) as bigint) AS disk_rb_rate "
					+ "	, cast(SUM(disk_wb_rate) / COUNT(ctime) as bigint) AS disk_wb_rate "
					+ "	, cast(SUM(net_rp_rate) / COUNT(ctime) as bigint) AS net_rp_rate "
					+ "	, cast(SUM(net_wp_rate) / COUNT(ctime) as bigint) AS net_wp_rate "
					+ "	, cast(SUM(net_rb_rate) / COUNT(ctime) as bigint) AS net_rb_rate "
					+ "	, cast(SUM(net_wb_rate) / COUNT(ctime) as bigint) AS net_wb_rate "
					+ "FROM " + tablename + " "
					+ "GROUP BY ctime "
					+ "HAVING 1 = 1 ";
			
			if (dateFrom != null)
				sql += "AND ctime >= '" + sdf.format(dateFrom) + "' ";
			if (dateTo != null)
				sql += "AND ctime <= '" + sdf.format(dateTo) + "' ";
			sql += "ORDER BY ctime DESC";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<System> systemList = System.toList(dataList);
			
//			sqlRs.close();
			return systemList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") querySystemAvg failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<Database> queryDatabase() {
		return queryDatabase(null, null, false);
	}
	@Override
	public List<Database> queryDatabase(boolean isHistory) {
		return queryDatabase(null, null, isHistory);
	}
	@Override
	public List<Database> queryDatabase(Date dateFrom, Date dateTo) {
		return queryDatabase(dateFrom, dateTo, false);
	}
	@Override
	public List<Database> queryDatabase(Date dateFrom, Date dateTo, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") querDatabase");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "database_history" : "database_now");
			String sql = "select * from " + tablename + " where 1=1";
			if (dateFrom != null)
				sql += " and ctime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql += " and ctime <= '" + sdf.format(dateTo) + "'";
			sql += " order by ctime desc";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<Database> databaseList = Database.toList(dataList);
			
//			sqlRs.close();
			return databaseList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") querDatabase failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<Diskspace> queryDiskspace() {
		return queryDiskspace(null, null, false);
	}
	@Override
	public List<Diskspace> queryDiskspace(boolean isHistory) {
		return queryDiskspace(null, null, isHistory);
	}
	@Override
	public List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo) {
		return queryDiskspace(dateFrom, dateTo, false);
	}
	@Override
	public List<Diskspace> queryDiskspace(Date dateFrom, Date dateTo, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryDiskspace");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "diskspace_history" : "diskspace_now");
			String sql = "select * from " + tablename + " where 1=1";
			if (dateFrom != null)
				sql += " and ctime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql += " and ctime <= '" + sdf.format(dateTo) + "'";
			sql += " order by ctime, hostname, filesystem desc";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<Diskspace> diskspaceList = Diskspace.toList(dataList);
			
//			sqlRs.close();
			return diskspaceList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryDiskspace failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public List<Diskspace> queryDiskspaceAve() {
		return queryDiskspaceAve(null, null, false);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(boolean isHistory) {
		return queryDiskspaceAve(null, null, isHistory);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo) {
		return queryDiskspaceAve(dateFrom, dateTo, false);
	}
	@Override
	public List<Diskspace> queryDiskspaceAve(Date dateFrom, Date dateTo, boolean isHistory) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryDiskspaceAve");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tablename = (isHistory ? "diskspace_history" : "diskspace_now");
			
			String sql = "SELECT ctime, filesystem, "
					+ "CAST(SUM(total_bytes) / COUNT(filesystem) AS bigint) AS total_bytes, "
					+ "CAST(SUM(bytes_used) / COUNT(filesystem) AS bigint) AS bytes_used, "
					+ "CAST(SUM(bytes_available) / COUNT(filesystem) AS bigint) AS bytes_available "
					+ "FROM " + tablename + " "
					+ "GROUP BY ctime, filesystem "
					+ "HAVING 1 = 1";
			
			if (dateFrom != null)
				sql += " and ctime >= '" + sdf.format(dateFrom) + "'";
			if (dateTo != null)
				sql += " and ctime <= '" + sdf.format(dateTo) + "'";
			sql += " ORDER BY ctime, filesystem";
			
			List<Map<String, Object>> dataList = dao.executeQuery(sql);
			if (dataList == null)
				return null;
			List<Diskspace> diskspaceList = Diskspace.toList(dataList);
			
//			sqlRs.close();
			return diskspaceList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryDiskspaceAve failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public GPResultSet gpLogFilter(String filterCommand) {
		return gpLogFilter(filterCommand, null);
	}
	
	@Override
	public GPResultSet gpLogFilter(String filterCommand, String extraCommand) {
		LogUtil.info("(" + dao.getExecutorName() + ") gplogfilter");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			// Output Options
			String keyWord = " -o ";
			String cmd_mkdir_output = "";
			String cmd_mkdir_gather = "";
			int o_idx = filterCommand.indexOf(keyWord);
			if (o_idx != -1) {
				// 【需要】导出文件
				int i_from = filterCommand.indexOf(keyWord) + keyWord.length();
				int i_to   = filterCommand.indexOf(" ", i_from);
				String cmdFragment = filterCommand.substring(i_from, i_to);
				
				String logFile_output = cmdFragment.substring(cmdFragment.lastIndexOf("/") + 1);
				String logFile_output_convert = logFile_output + ".convert";
				
				int serial_from = logFile_output.indexOf("_", logFile_output.indexOf("_") + 1) + 1;
				int serial_to   = logFile_output.lastIndexOf(".");
				String serialNo = logFile_output.substring(serial_from, serial_to);
				
				// Prepare Step 1: Generate command of output directory creation.
				String outputDir = cmdFragment.substring(0, cmdFragment.lastIndexOf("/"));
				cmd_mkdir_output = String.format(IConstantsCmds.MKDIR, outputDir);
				
				// Prepare Step 2: Generate command of gather directory creation.
				String gatherDir = outputDir.substring(0, outputDir.lastIndexOf("/")) + "/pgLogGather";
				cmd_mkdir_gather = String.format(IConstantsCmds.MKDIR, gatherDir);
				
				// Prepare Step 3: 
				String sourceCmd = "source " + gpHome() + "/greenplum_path.sh";
				
				// Prepare Step 4: Exit Flag
				String cmdHeader = dao.getCmdHeader();
				
				// 0.0 mkdir -p /home/gpadmin/pgLogGather && gpssh
				resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd_mkdir_gather + "\n", new String[] {}));
				if (extraCommand != null)
					resultSet.addChildResultSet(dao.executeInteractiveCommand(extraCommand + "\n", new String[] { "=>" }));
				
				// 1.0 source greenplum.sh
				resultSet.addChildResultSet(dao.executeInteractiveCommand(sourceCmd + "\n", new String[] {}));
				
				// 2.0 mkdir -p /home/gpadmin/pgLogOutput
				resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd_mkdir_output + "\n", new String[] {}));
				
				// 3.0 gplogfilter -o xxx
				resultSet.addChildResultSet(dao.executeInteractiveCommand(filterCommand + "\n", new String[] {}));
				
				// 4.0 convert *.output to *.output.convert cmd1:去掉所有换行; cmd2:按照年月日日期格式分隔每行; cmd3:去掉空行
				String sed_cmd1 = "sed \":a;N;s/\\n//g;ta\" " + outputDir + "/" + logFile_output + " > " + outputDir + "/" + logFile_output_convert;
				resultSet.addChildResultSet(dao.executeInteractiveCommand(sed_cmd1 + "\n", new String[] {}));
				String sed_cmd2 = "sed -i 's/\\([0-9]\\{4\\}-[0-9]\\{2\\}-[0-9]\\{2\\} [0-9]\\{2\\}:[0-9]\\{2\\}:[0-9]\\{2\\}\\.[0-9]\\{6\\} CST\\)/\\n\\1/g' " + outputDir + "/" + logFile_output_convert;
				resultSet.addChildResultSet(dao.executeInteractiveCommand(sed_cmd2 + "\n", new String[] {}));
				String sed_cmd3 = "sed -i '/^[[:space:]]*$/d' " + outputDir + "/" + logFile_output_convert;
				resultSet.addChildResultSet(dao.executeInteractiveCommand(sed_cmd3 + "\n", new String[] {}));
				
				// 5.0 gpscp *.output.convert
				String scp_cmd = String.format(IConstantsCmds.GP_SCP_H, dao.getHostname(), outputDir + "/" + logFile_output_convert, gatherDir);
				resultSet.addChildResultSet(dao.executeInteractiveCommand(scp_cmd + "\n", new String[] {}));
				
				// 6.0 exit gpssh
				if (extraCommand != null)
					resultSet.addChildResultSet(dao.executeInteractiveCommand("exit\n", new String[] { cmdHeader }));
				
				// 7.0 create audit table
				AuditModel auditInfo = new AuditModel();
				String createSql = auditInfo.generateCreateSql(serialNo);
				if (executeUpdate(createSql) != 0)
					throw new Exception("Create audit table failed!");
				
				// 8.0 copy audit_xxx from *.output.convert
				String[] sshFragments = new String[] { "gpssh", dao.getHostname() };
				if (extraCommand != null)
					sshFragments = extraCommand.split(" ");
				for (int i = 0; i < sshFragments.length; i++) {
					if (i == 0 || sshFragments[i].startsWith("-") || sshFragments[i].trim().length() < 0)
						continue;
					String fileName = logFile_output_convert.replace("$(hostname)", sshFragments[i].trim());
					String copy_cmd = String.format(IConstantsCmds.SQL_COPY, auditInfo.tableName(serialNo), gatherDir + "/" + fileName, "|");
					int rowCount = executeUpdate(copy_cmd);
					LogUtil.info("Found " + rowCount + " rows from " + fileName + ".");
				}
				
			} else {
				// 【不需要】导出文件
				// Execute gplogfilter
				String finalCommand = filterCommand + "\n";
				if (extraCommand != null) {
					String gpShPath = gpHome() + "/greenplum_path.sh";
					finalCommand = extraCommand + "-e \"source " + gpShPath + "; " + filterCommand + "\"\n";
				}
				java.lang.System.err.println("finalCommand: 【" + finalCommand + "】");
				resultSet.addChildResultSet(dao.executeInteractiveCommand(finalCommand, new String[] {}));
			}
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gplogfilter exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<AuditEntity> queryAuditData(AuditEntity condition, String serialAuditTable) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryAuditData");
		try {
			// TODO 需要追加局部搜索条件
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sql_condition = "";
			
//			Date dateFrom = condition.get_logtimeFrom();
//			Date dateTo = condition.get_logtimeTo();
//			String user = condition.getLoguser();
//			String database = condition.getLogdatabase();
//			String host = condition.getLoghost();
//			if (dateFrom != null)
//				sql_condition += " and logtime >= '" + sdf.format(dateFrom) + "'";
//			if (dateTo != null)
//				sql_condition += " and logtime <= '" + sdf.format(dateTo) + "'";
//			if (user != null && user.length() > 0)
//				sql_condition += " and loguser = '" + user + "'";
//			if (database != null && database.length() > 0)
//				sql_condition += " and logdatabase = '" + database + "'";
//			if (host != null && host.length() > 0)
//				sql_condition += " and loghost = '" + host + "'";
//			
//			sql_condition += " and logseverity in ("
//					+ "'" + (condition.is_logseverity_panic() ? LogAlert.LOG_PANIC : "") + "', "
//					+ "'" + (condition.is_logseverity_fatal() ? LogAlert.LOG_FATAL : "") + "', "
//					+ "'" + (condition.is_logseverity_error() ? LogAlert.LOG_ERROR : "") + "', "
//					+ "'" + (condition.is_logseverity_warning() ? LogAlert.LOG_WARNING : "") + "')";
			
			// 1.0 查询总条数（计算分页）
			String sql_count = "select count(*) as count from " + serialAuditTable + " where 1=1";
//			ResultSet sqlRs = dao.executeQuery(sql_count + sql_condition);
			List<Map<String, Object>> dataList = dao.executeQuery(sql_count + sql_condition);
			if (dataList.size() > 0)
				condition.setTotalCount(Integer.parseInt(dataList.get(0).get("count") + ""));
//			sqlRs.close();
			
			// 2.0 查询数据实体
			int limit = condition.getLimit();
			int offset = condition.getOffset();
			sql_condition += " order by event_time desc";
			if (limit > 0)
				sql_condition += " limit " + limit + " offset " + offset;
			
			String sql_query = "select * from " + serialAuditTable + " where 1=1";
			dataList = dao.executeQuery(sql_query + sql_condition);
			if (dataList == null)
				return null;
			List<AuditEntity> alertInfoList = AuditEntity.toList(dataList);
			
//			sqlRs.close();
			return alertInfoList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryAuditData failed: " + e.getMessage());
			e.printStackTrace();
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean downloadLogData(String localFileDir, String remoteFileDir, String serialNo, String sshCommand) throws JSchException {
		String nodeType = sshCommand == null ? "master" : "segment";
		String[] sshFragments = new String[] { "", dao.getHostname() };
		if (sshCommand != null)
			sshFragments = sshCommand.split(" ");
		
		boolean result = true;
		for (int i = 0; i < sshFragments.length; i++) {
			if (i == 0 || sshFragments[i].startsWith("-") || sshFragments[i].trim().length() < 0)
				continue;
			
			String fileName = nodeType + "_" + sshFragments[i].trim() + "_" + serialNo + ".output.convert";
			java.lang.System.out.println("fileName: " + fileName);
			
			boolean tempResult = dao.download(remoteFileDir + "/" + fileName, localFileDir);
			if (result)
				result = tempResult;
		}
		return result;
	}
}
