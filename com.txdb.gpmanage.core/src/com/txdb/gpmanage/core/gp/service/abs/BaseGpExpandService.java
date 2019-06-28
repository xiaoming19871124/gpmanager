package com.txdb.gpmanage.core.gp.service.abs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.core.gp.constant.IConstantSteps;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpExpandService;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;
import com.txdb.gpmanage.core.gp.service.IGpManageService;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseGpExpandService implements IGpExpandService {

	protected IExecuteDao dao;
	
	@Override
	public void initialize(IExecuteDao dao) {
		this.dao = dao;
	}
	
	@Override
	public GPResultSet addStandby(String standbyName, String standbyPassword) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpAddStandby");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.AGREEMENT_EXPAND_STANDBY,
					IConstantSteps.INPUT_PASSWORD_STANDBY };
			
			String cmd = String.format(IConstantsCmds.GP_ADD_STANDBY, standbyName);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) 
					|| outputMsg.contains(conditions[1]) 
					|| outputMsg.contains(conditions[2]) 
					|| outputMsg.contains(conditions[3])) {
				
				// 节点交互
				String stepCmd = "y \n";
				if (outputMsg.contains(conditions[3]))
					stepCmd = standbyPassword + "\n";
				
				tempRs = dao.executeInteractiveCommand(stepCmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpAddStandby exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet removeStandby() {
		LogUtil.info("(" + dao.getExecutorName() + ") gpRemoveStandby");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2 };
			
			GPResultSet topRs = dao.executeInteractiveCommand(IConstantsCmds.GP_REMOVE_STANDBY, conditions);
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
			LogUtil.error("(" + dao.getExecutorName() + ") gpRemoveStandby exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet generateMirrorsCfg(String[] mirrorDataDir) {
		return generateMirrorsCfg(mirrorDataDir, false);
	}
	
	@Override
	public GPResultSet generateMirrorsCfg(String[] mirrorDataDir, boolean spreadMode) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpGenerateMirrorsCfg");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.INPUT_INSTALL_PATH_MIRROR };
			
			String cmd = String.format(IConstantsCmds.GP_ADD_MIRRORS_O, spreadMode ? "-s" : "");
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0])) {
				// 节点交互
				String stepCmd = "y \n";
				if (outputMsg.contains(conditions[0])) {
					int s = outputMsg.lastIndexOf(conditions[0]) + conditions[0].length();
					int e = outputMsg.lastIndexOf("of") - 1;
					int index = Integer.parseInt(outputMsg.substring(s + 1, e));
					stepCmd = mirrorDataDir[index - 1] + "\n";
				}
				tempRs = dao.executeInteractiveCommand(stepCmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpGenerateMirrorsCfg exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet addMirrorByCfg() {
		LogUtil.info("(" + dao.getExecutorName() + ") gpAddMirrorByCfg");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_INSTALL_MIRRORS };
			
			GPResultSet topRs = dao.executeInteractiveCommand(IConstantsCmds.GP_ADD_MIRRORS_I, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpAddMirrorByCfg exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet addMirrors(String... mirrorDataDir) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpAddMirrors");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.INPUT_INSTALL_PATH_MIRROR,
					IConstantSteps.AGREEMENT_INSTALL_MIRRORS };
			
			GPResultSet topRs = dao.executeInteractiveCommand(IConstantsCmds.GP_ADD_MIRRORS_P, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1])) {
				// 节点交互
				String cmd = "y \n";
				if (outputMsg.contains(conditions[0])) {
					int s = outputMsg.lastIndexOf(conditions[0]) + conditions[0].length();
					int e = outputMsg.lastIndexOf("of") - 1;
					int index = Integer.parseInt(outputMsg.substring(s + 1, e));
					cmd = mirrorDataDir[index - 1] + "\n";
				}
				tempRs = dao.executeInteractiveCommand(cmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpAddMirrors exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createExpandNodeListFile(String remotePath, String[] new_host) {
		return createExpandNodeListFile(remotePath, new_host, null);
	}
	
	@Override
	public GPResultSet createExpandNodeListFile(String remotePath, String[] new_host, String[] exist_host) {
		LogUtil.info("(" + dao.getExecutorName() + ") createExpandNodeListFile");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.MKDIR_CD, remotePath, remotePath);
			String cd = "cd " + remotePath;
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			if (new_host != null && new_host.length > 0) {
				cmd = String.format(IConstantsCmds.TOUCH_REWRITE_BLANK_FILE, DEFAULT_HOST_EXPAND, DEFAULT_HOST_EXPAND);
				resultSet.addChildResultSet(dao.executeCommand(cd + " && " + cmd));
				for (String host : new_host)
					resultSet.addChildResultSet(dao.executeCommand(cd + " && " + String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, host, DEFAULT_HOST_EXPAND)));
			}
			cmd = String.format(IConstantsCmds.TOUCH_REWRITE_BLANK_FILE, IGpInstallService.DEFAULT_ALL_HOST, IGpInstallService.DEFAULT_ALL_HOST);
			GPResultSet existRs = dao.executeCommand(cd + " && " + cmd);
			if (exist_host == null) {
				LogUtil.info("(" + dao.getExecutorName() + ") exist_host is null, try to query segments info by jdbc...");
				if (dao.isJdbcConnected()) {
//					ResultSet sqlRs = dao.executeQuery("select * from gp_segment_configuration order by dbid");
					List<Map<String,Object>> dataList = dao.executeQuery("select * from gp_segment_configuration order by dbid");
					List<String> hostnameList = new ArrayList<String>();
//					while (sqlRs.next()) {
					for (Map<String,Object> rowMap : dataList) {
//						String hostname = sqlRs.getString("hostname");
						String hostname = String.valueOf(rowMap.get("hostname"));
						if (!hostnameList.contains(hostname))
							hostnameList.add(hostname);
					}
					exist_host = new String[hostnameList.size()];
					for (int i = 0; i < hostnameList.size(); i++)
						exist_host[i] = hostnameList.get(i);
						
				} else {
					LogUtil.info("(" + dao.getExecutorName() + ") jdbc is not connecting, set nothing into exist_host file.");
					exist_host = new String[] {};
					existRs.setSuccessed(false);
				}
			}
			resultSet.addChildResultSet(existRs);
			for (String host : exist_host)
				resultSet.addChildResultSet(dao.executeCommand(cd + " && " + String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, host, IGpInstallService.DEFAULT_ALL_HOST)));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") createExpandNodeListFile exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expandSSHExKeys(String remoteHostFileDir, Map<String, String> passwordMap) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpExpandSSHExKeys");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] { 
					IConstantSteps.INPUT_PASSWORD_EXKEYS };
			
			remoteHostFileDir += "/";
			remoteHostFileDir = remoteHostFileDir.replaceAll("//", "/");
			String existHostFile = remoteHostFileDir + IGpInstallService.DEFAULT_ALL_HOST;
			String expandHostFile = remoteHostFileDir + DEFAULT_HOST_EXPAND;
			
			String cmd = String.format(IConstantsCmds.GP_EXKEYS_EXPAND, existHostFile, expandHostFile);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0])) {
				// 节点密码交互
				int bi = outputMsg.lastIndexOf(conditions[0]) + conditions[0].length() + 1;
				int ei = outputMsg.lastIndexOf(":");
				String hostname = outputMsg.substring(bi, ei);
				String hostpass = passwordMap.get(hostname);
				if (hostpass == null || outputMsg.contains(IConstantSteps.FINAL_PASSWORD_BAD)) {
					outputMsg += "\nFound password is [" + hostpass + "] for host[" + hostname + "] is not correct!";
					break;
				}
				tempRs = dao.executeInteractiveCommand(hostpass + "\n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpExpandSSHExKeys exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	// 生成扩展的配置文件名（例如：/home/gpadmin/gpexpand_inputfile_20180515_142428）
	private String generated_configuration_file = "";
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile) {
		String[] dataDirs = {};
		return genGpexpandInputfile(expandHostFile, dataDirs, dataDirs, false);
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, boolean spreadMode) {
		String[] dataDirs = {};
		return genGpexpandInputfile(expandHostFile, dataDirs, dataDirs, false);
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDataDirs) {
		return genGpexpandInputfile(expandHostFile, segmentDataDirs, mirrorDataDirs, false);
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDataDirs, boolean spreadMode) {
		LogUtil.info("(" + dao.getExecutorName() + ") generateExpandCfgFile");
		GPResultSet resultSet = new GPResultSet(dao);
		
		// 创建临时扩展数据库
		try {
			boolean s = dao.execute("create database " + IGpManageService.DEFAULT_EXPAND_DB);
			LogUtil.info("(" + dao.getExecutorName() + ") Create database [" + IGpManageService.DEFAULT_EXPAND_DB + "] successed: " + s);
		} catch (SQLException e) {
			LogUtil.warn("(" + dao.getExecutorName() + ") Create database: " + e.getMessage());
		}
		try {
			String[] conditions = new String[] { 
					IConstantSteps.AGREEMENT_EXPAND_GENERATE, 
					IConstantSteps.INPUT_EXPAND_SEGMENT_NUMBER, 
					IConstantSteps.INPUT_EXPAND_SEGMENT_DIR, 
					IConstantSteps.INPUT_EXPAND_MIRROR_DIR, 
					IConstantSteps.INPUT_EXPAND_STRATEGY };
			
			String cmd = String.format(IConstantsCmds.GP_GENERATE_CFG, expandHostFile, IGpManageService.DEFAULT_EXPAND_DB);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) 
					|| outputMsg.contains(conditions[1]) 
					|| outputMsg.contains(conditions[2]) 
					|| outputMsg.contains(conditions[3]) 
					|| outputMsg.contains(conditions[4])) {
				// 节点交互
				String tempCmd = "y \n";
				if (outputMsg.contains(conditions[1])) {
					tempCmd = segmentDataDirs.length + " \n";
				} else if (outputMsg.contains(conditions[2])) {
					int bi = outputMsg.lastIndexOf(conditions[2]) + conditions[2].length() + 1;
					int ei = outputMsg.lastIndexOf(":");
					int index = Integer.parseInt(outputMsg.substring(bi, ei).trim());
					tempCmd = segmentDataDirs[index - 1] + "\n";
				} else if (outputMsg.contains(conditions[3])) {
					int bi = outputMsg.lastIndexOf(conditions[3]) + conditions[3].length() + 1;
					int ei = outputMsg.lastIndexOf(":");
					int index = Integer.parseInt(outputMsg.substring(bi, ei).trim());
					tempCmd = mirrorDataDirs[index - 1] + "\n";
				} else if (outputMsg.contains(conditions[4])) {
					tempCmd = (spreadMode ? STRATEGY_SPREAD : STRATEGY_GROUPED) + " \n";
				}
				tempRs = dao.executeInteractiveCommand(tempCmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
			if (!resultSet.contains(tempRs))
				tempRs = topRs;
			
			if (tempRs.isSuccessed()) {
				int bi = outputMsg.indexOf("'") + 1;
				int ei = outputMsg.indexOf("'", bi);
				generated_configuration_file = outputMsg.substring(bi, ei);
				LogUtil.info("(" + dao.getExecutorName() + ") got gpexpand_inputfile: \"" + generated_configuration_file + "\"");
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") generateExpandCfgFile exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expand(String args) {
		LogUtil.info("(" + dao.getExecutorName() + ") expand");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.INPUT_EXPAND_SEGMENT_NUMBER,
					IConstantSteps.INPUT_EXPAND_SEGMENT_BLANK };
			
			String cmd = String.format(IConstantsCmds.GP_EXPAND, args == null ? "" : args);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) 
					|| outputMsg.contains(conditions[1]) 
					|| outputMsg.contains(conditions[2]) 
					|| outputMsg.contains(conditions[3])) {
				// 节点交互
				String stepCmd = "y \n";
				if (outputMsg.contains(conditions[2]) || outputMsg.contains(conditions[3]))
					stepCmd = " \n";
				tempRs = dao.executeInteractiveCommand(stepCmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") expand exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expandSegment() {
		return expandSegment(null);
	}
	
	@Override
	public GPResultSet expandSegment(String input_file) {
		// gpexpand -i %s -D %s
		GPResultSet resultSet = new GPResultSet(dao);
		String tempCfgFile = (input_file == null ? generated_configuration_file : input_file);
		if (tempCfgFile == null || "".equals(tempCfgFile)) {
			resultSet.setOutputErr("Cann't find gpexpand_inputfile!");
			LogUtil.warn("(" + dao.getExecutorName() + ") " + resultSet.getOutputErr());
			return resultSet;
		}
		String args = String.format("-i %s -D %s", tempCfgFile, IGpManageService.DEFAULT_EXPAND_DB);
		return expand(args);
	}
	
	@Override
	public GPResultSet expandCheanSchema() {
		// gpexpand -c -D %s
		String args = String.format("-c -D %s", IGpManageService.DEFAULT_EXPAND_DB);
		GPResultSet rs = expand(args);
		
		LogUtil.info("(" + dao.getExecutorName() + ") expandCheanSchema");
		GPResultSet dropDbRs = new GPResultSet(dao);
		try {
			dao.execute("drop database " + IGpManageService.DEFAULT_EXPAND_DB);
			LogUtil.info("(" + dao.getExecutorName() + ") Drop database [" + IGpManageService.DEFAULT_EXPAND_DB + "] successed.");
			
			dropDbRs.setOutputMsg("drop database " + IGpManageService.DEFAULT_EXPAND_DB + " successed.");
			dropDbRs.setSuccessed(true);
			
		} catch (SQLException e) {
			dropDbRs.setOutputEpt(e.getMessage());
			dropDbRs.setSuccessed(false);
			LogUtil.error("(" + dao.getExecutorName() + ") expandCheanSchema exception: " + e.getMessage());
		}
		rs.addChildResultSet(dropDbRs);
		rs.collectResult();
		return rs;
	}
	
	@Override
	public GPResultSet rollback() {
		LogUtil.info("(" + dao.getExecutorName() + ") gpRollback");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_ROLLBACK, IGpManageService.DEFAULT_EXPAND_DB);
			GPResultSet tempRs = dao.executeInteractiveCommand(cmd, new String[] {});
			resultSet.addChildResultSet(tempRs);

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpRollback exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<GPExpandStatusDetail> queryExpandStatusDetail() {
		return queryExpandStatusDetail(null);
	}
	
	@Override
	public List<GPExpandStatusDetail> queryExpandStatusDetail(String tableName) {
		LogUtil.info("(" + dao.getExecutorName() + ") queryExpandStatusDetail");
		try {
			String sql = "select * from gpexpand.status_detail";
			if (tableName != null)
				sql += " where fq_name like '%" + tableName + "%'";
			
//			ResultSet sqlRs = dao.executeQuery(sql);
//			if (sqlRs == null)
//				return null;
			
			List<Map<String,Object>> dataList = dao.executeQuery(sql);
			List<GPExpandStatusDetail> statusDetailList = new ArrayList<GPExpandStatusDetail>();
			GPExpandStatusDetail statusDetail = null;
//			while (sqlRs.next()) {
			for (Map<String,Object> rowMap : dataList) {
				statusDetail = new GPExpandStatusDetail();
				statusDetail.setDbname(String.valueOf(rowMap.get("dbname")));
				statusDetail.setFq_name(String.valueOf(rowMap.get("fq_name")));
				statusDetail.setSchema_oid((int) rowMap.get("schema_oid"));
				statusDetail.setTable_oid((int) rowMap.get("table_oid"));
				statusDetail.setDistribution_policy(String.valueOf(rowMap.get("distribution_policy")));
				statusDetail.setDistribution_policy_names(String.valueOf(rowMap.get("distribution_policy_names")));
				statusDetail.setDistribution_policy_coloids(String.valueOf(rowMap.get("distribution_policy_coloids")));
				
				// GP6.0新增
//				statusDetail.setDistribution_policy_type(String.valueOf(rowMap.get("distribution_policy_type")));
				
				statusDetail.setStorage_options(String.valueOf(rowMap.get("storage_options")));
				statusDetail.setRank((int) rowMap.get("rank"));
				statusDetail.setStatus(String.valueOf(rowMap.get("status")));
				statusDetail.setExpansion_started(String.valueOf(rowMap.get("expansion_started")));
				statusDetail.setExpansion_finished(String.valueOf(rowMap.get("expansion_finished")));
				statusDetail.setSource_bytes((int) rowMap.get("source_bytes"));
				statusDetailList.add(statusDetail);
			}
//			sqlRs.close();
			return statusDetailList;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") queryExpandStatusDetail failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean updateExpandRank(String dbName,String tablename, int rank) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateExpandRank");
		try {
			String sql = "update gpexpand.status_detail set rank = %s where dbname = '%s' and fq_name = '%s'";
			sql = String.format(sql, rank,dbName, tablename);
			int row = dao.executeUpdate(sql);
			return row > 0;

		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") updateExpandRank failed: " + e.getMessage());
			dao.callback(e.getMessage());
			return false;
		}
	}
	
	@Override
	public GPResultSet redistribute() {
		// TODO Auto-generated method stub
		return redistribute(null);
	}
	
	@Override
	public GPResultSet redistribute(String time) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpRedistribute");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] { 
					IConstantSteps.AGREEMENT_EXPAND_GENERATE, 
					IConstantSteps.INPUT_EXPAND_SEGMENT_BLANK, 
					IConstantSteps.INPUT_EXPAND_SEGMENT_NUMBER };
			
			// --duration <hh:mm:ss>
			if (time != null)
				time = "-d " + time;
			else
				time="";
			String exeCmd = String.format(IConstantsCmds.GP_REDISTRIBUTE, time, IGpManageService.DEFAULT_EXPAND_DB);
			GPResultSet topRs = dao.executeInteractiveCommand(exeCmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1]) || outputMsg.contains(conditions[2])) {
				// 节点交互
				String cmd = "\n";
				if (outputMsg.contains(conditions[0]))
					cmd = "y \n";
				tempRs = dao.executeInteractiveCommand(cmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpRedistribute exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
}
