package com.txdb.gpmanage.ext.ubuntu.service;

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
		// TODO Auto-generated method stub
		String userHome = "/home/" + gpUsername + "/";
		String[] tempEnvFiles = {userHome + ".bashrc", userHome + ".profile", "/etc/profile"};
		if (envFiles != null)
			tempEnvFiles = envFiles;
		return super.configGpEnv(port, gpHome, masterDataDirectory, gpUsername, tempEnvFiles);
	}
}
