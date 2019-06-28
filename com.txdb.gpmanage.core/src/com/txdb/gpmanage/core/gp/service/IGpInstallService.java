package com.txdb.gpmanage.core.gp.service;

import java.util.Map;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;

public interface IGpInstallService {
	
	public static final String DEFAULT_ALL_HOST = "all_host";
	public static final String DEFAULT_ALL_SEGMENT = "all_segment";
	
	// 默认模板文件相对路径（在GP安装目录下）
	public static final String DEFAULT_GPINITSYSTEM_TEMPLETE_PATH = "greenplum-db/docs/cli_help/gpconfigs/gpinitsystem_config";

	void initialize(IExecuteDao dao);
	
	// 拷贝安装文件
	GPResultSet sendGpFile(String targetDir, String localFile);
	
	// 执行安装文件
	GPResultSet rpmGpFile(String remoteRpmFilePath, String rpmFileName, String installPath);
	
	// 解压tar.gz
	GPResultSet gpdbTargz(String remoteTarfile, String installPath);
	
	// 设置环境变量（用户及系统）
	GPResultSet configGpEnv(int port, String gpHome, String masterDataDirectory, String gpuserName, String[] envFiles);
	
	// 创建节点文件
	GPResultSet createNodeListFile(String remotePath, String[] all_host, String[] all_segment);
	
	// 建立信任（单节点执行）
	GPResultSet gpSSHExKeys(String hostFile, Map<String, String> passwordMap);
	
	// 批量发布文件
	GPResultSet gpSegInstall(String hostFile, String gpUsername, String gpPassword);
	
	// 检查安装情况
	GPResultSet gpChkInstallDir(String hostFile);
	
	// 准备工作目录
	GPResultSet gpMakeDataDir(String dataDir, String gpadmin);
	GPResultSet gpMakeDataDir(String dataDir, String hostFile, String gpadmin);
	
	// 部署配置准备
	GPResultSet gpInitSystemCfg(String gpDir, String customDir, Properties initSystemProps);
	
	// 部署
	GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile);
	GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile, boolean spreadMode);
	
	// 同步系统文件 by hostnames
	GPResultSet gpScp(String[] hostnameArray, String file);
	GPResultSet gpScp(String[] hostnameArray, String file, Map<String, String> passwordMap);
	
	// 同步系统文件 by hostfile
	GPResultSet gpScp(String hostFile, String file);
	GPResultSet gpScp(String hostFile, String file, Map<String, String> passwordMap);
}
