package com.txdb.gpmanage.core.gp.service;

import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;

public interface IGpExpandService {
	
	public static final String DEFAULT_HOST_EXPAND = "host_expand";
	
	public static final String STRATEGY_GROUPED = "grouped";
	public static final String STRATEGY_SPREAD = "spread";
	
	void initialize(IExecuteDao dao);
	
	// 增加Standby
	GPResultSet addStandby(String standbyName, String standbyPassword);
	
	// 删除Standby
	GPResultSet removeStandby();

	// 生成Mirror配置文件
	GPResultSet generateMirrorsCfg(String[] mirrorDataDir);
	GPResultSet generateMirrorsCfg(String[] mirrorDataDir, boolean spreadMode);
	
	// 执行Mirror扩展
	GPResultSet addMirrorByCfg();
	
	// 增加Mirror
	GPResultSet addMirrors(String... mirrorDataDir);
	
	
	
	// 准备扩展节点host描述文件
	GPResultSet createExpandNodeListFile(String remotePath, String[] new_host);
	GPResultSet createExpandNodeListFile(String remotePath, String[] new_host, String[] exist_host);
	
	// 建立扩展节点信任
	GPResultSet expandSSHExKeys(String remoteHostFileDir, Map<String, String> passwordMap);
	
	// 生成GP扩展的配置文件 gpexpand_inputfile
	GPResultSet genGpexpandInputfile(String expandHostFile);
	GPResultSet genGpexpandInputfile(String expandHostFile, boolean spreadMode);
	GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDataDirs);
	GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDataDirs, boolean spreadMode);
	
	// 扩展命令
	GPResultSet expand(String args);
	
	// GP扩容部署
	GPResultSet expandSegment();
	GPResultSet expandSegment(String input_file);
	
	// 清除扩展的Schema
	GPResultSet expandCheanSchema();
	
	// GP回滚
	GPResultSet rollback();
	
	// 查询数据分布级别
	List<GPExpandStatusDetail> queryExpandStatusDetail();
	List<GPExpandStatusDetail> queryExpandStatusDetail(String tableName);
	
	// 设置数据分布级别
	boolean updateExpandRank(String dbName,String tablename, int rank);
	
	// GP数据重新分布
	GPResultSet redistribute();
	GPResultSet redistribute(String time);
}
