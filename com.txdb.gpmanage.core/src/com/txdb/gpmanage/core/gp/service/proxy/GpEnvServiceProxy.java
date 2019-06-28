package com.txdb.gpmanage.core.gp.service.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpEnvService;

public class GpEnvServiceProxy implements IGpEnvService {
	
	private List<IGpEnvService> envServiceList;
	
	public GpEnvServiceProxy() {
		envServiceList = new ArrayList<IGpEnvService>();
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
		// TODO Auto-generated method stub
	}
	
	public void addService(IGpEnvService gpEnvService) {
		envServiceList.add(gpEnvService);
	}
	
	public void clear() {
		envServiceList.clear();
	}
	
	@Override
	public GPResultSet checkRequiredSoftware() {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.checkRequiredSoftware());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet updateHostname(String hostname) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.updateHostname(hostname));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet updateResolvConf(String nameserverIp) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.updateResolvConf(nameserverIp));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet updateHosts(Map<String, String[]> hostMap) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.updateHosts(hostMap));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet closeIptables() {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.closeIptables());
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet closeSeLinux() {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.closeSeLinux());
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet updateSysctl_limits_90_nproc(Properties sysctlProps, Properties limitsProps, Properties nprocProps) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.updateSysctl_limits_90_nproc(sysctlProps, limitsProps, nprocProps));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public Properties getSysctlParams() {
		return envServiceList.get(0).getSysctlParams(null);
	}
	
	@Override
	public Properties getSysctlParams(String key) {
		return envServiceList.get(0).getSysctlParams(key);
	}

	@Override
	public GPResultSet blockDev(int number) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.blockDev(number));
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet createOrReplaceGpUser(String userName, String password) {
		GPResultSet resultSet = new GPResultSet(null);
		for (IGpEnvService service : envServiceList)
			resultSet.addChildResultSet(service.createOrReplaceGpUser(userName, password));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet clockSynchronize(String hostfile) {
		return envServiceList.get(0).clockSynchronize(hostfile);
	}

	@Override
	public GPResultSet createUser(String userName) {
		return null;
	}

	@Override
	public GPResultSet deleteUser(String userName) {
		return null;
	}

	@Override
	public boolean userExist(String userName) {
		return false;
	}

	@Override
	public GPResultSet userPassword(String userName, String password) {
		return null;
	}
	
	@Override
	public boolean uploadFile(String directory, String uploadFile) {
		return envServiceList.get(0).uploadFile(directory, uploadFile);
	}
	
	@Override
	public boolean downloadFile(String src, String dst) {
		return envServiceList.get(0).downloadFile(src, dst);
	}
}
