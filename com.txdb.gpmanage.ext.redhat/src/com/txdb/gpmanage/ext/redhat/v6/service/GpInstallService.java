package com.txdb.gpmanage.ext.redhat.v6.service;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpInstallService;

public class GpInstallService extends BaseGpInstallService {

	public GpInstallService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
		super.initialize(dao);
	}
	
	@Override
	public GPResultSet configGpEnv(int port, String gpHome, String masterDataDirectory, String gpUsername, String[] envFiles) {
		String userHome = "/home/" + gpUsername + "/";
		String[] tempEnvFiles = {userHome + ".bashrc", userHome + ".bash_profile", "/etc/profile"};
		if (envFiles != null)
			tempEnvFiles = envFiles;
		return super.configGpEnv(port, gpHome, masterDataDirectory, gpUsername, tempEnvFiles);
	}
}
