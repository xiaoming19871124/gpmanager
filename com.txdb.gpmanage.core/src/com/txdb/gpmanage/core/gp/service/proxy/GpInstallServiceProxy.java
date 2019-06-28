package com.txdb.gpmanage.core.gp.service.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;

public class GpInstallServiceProxy implements IGpInstallService {

	private List<IGpInstallService> installServiceList;
	
	public GpInstallServiceProxy() {
		installServiceList = new ArrayList<IGpInstallService>();
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
	}
	
	public void addService(IGpInstallService gpInstallService) {
		installServiceList.add(gpInstallService);
	}
	
	public void clear() {
		installServiceList.clear();
	}
	
	@Override
	public GPResultSet sendGpFile(String targetDir, String localFile) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).sendGpFile(targetDir, localFile));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet rpmGpFile(String remoteRpmFilePath, String rpmFileName, String installPath) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).rpmGpFile(remoteRpmFilePath, rpmFileName, installPath));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpdbTargz(String remoteTarfile, String installPath) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpdbTargz(remoteTarfile, installPath));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet configGpEnv(int port, String gpHome, String masterDataDirectory, String gpuserName, String[] envFiles) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).configGpEnv(port, gpHome, masterDataDirectory, gpuserName, envFiles));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet createNodeListFile(String remotePath, String[] all_host, String[] all_segment) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).createNodeListFile(remotePath, all_host, all_segment));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpSSHExKeys(String hostFile, Map<String, String> passwordMap) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpSSHExKeys(hostFile, passwordMap));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpSegInstall(String hostFile, String gpUsername, String gpPassword) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpSegInstall(hostFile, gpUsername, gpPassword));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpChkInstallDir(String hostFile) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpChkInstallDir(hostFile));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpMakeDataDir(String dataDir, String gpadmin) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpMakeDataDir(dataDir, gpadmin));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpMakeDataDir(String dataDir, String hostFile, String gpadmin) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpMakeDataDir(dataDir, hostFile, gpadmin));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpInitSystemCfg(String gpDir, String customDir, Properties initSystemProps) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpInitSystemCfg(gpDir, customDir, initSystemProps));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpSystemDeploy(customParamFileDir, hostFile, false));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile, boolean spreadMode) {
		GPResultSet resultSet = new GPResultSet(null);
		if (installServiceList.size() > 0)
			resultSet.addChildResultSet(installServiceList.get(0).gpSystemDeploy(customParamFileDir, hostFile, spreadMode));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpScp(String[] hostnameArray, String file) {
		return installServiceList.get(0).gpScp(hostnameArray, file, null);
	}
	
	@Override
	public GPResultSet gpScp(String[] hostnameArray, String file, Map<String, String> passwordMap) {
		return installServiceList.get(0).gpScp(hostnameArray, file, passwordMap);
	}
	
	@Override
	public GPResultSet gpScp(String hostFile, String file) {
		return installServiceList.get(0).gpScp(hostFile, file, null);
	}
	
	@Override
	public GPResultSet gpScp(String hostFile, String file, Map<String, String> passwordMap) {
		return installServiceList.get(0).gpScp(hostFile, file, passwordMap);
	}
}
