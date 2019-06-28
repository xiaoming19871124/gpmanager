package com.txdb.gpmanage.ext.redhat.v7.service;

import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpInstallService;
import com.txdb.gpmanage.core.log.LogUtil;

public class GpInstallService extends BaseGpInstallService {

	private static final String SOURCE_SO = "/usr/lib64/libpython2.7.so.1.0";
	private static final String TARGET_SO = "/usr/lib64/libpython2.6.so.1.0";
	
	public GpInstallService() {}
	
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
	
	@Override
	public GPResultSet gpdbTargz(String remoteTarfile, String installPath) {
		GPResultSet resultSet = super.gpdbTargz(remoteTarfile, installPath);
		
		String cmd = String.format("sudo ln -s %s %s", SOURCE_SO, TARGET_SO);
		try {
			boolean sourceExist = dao.isDirExists("f", SOURCE_SO);
			boolean targetExist = dao.isDirExists("f", TARGET_SO);
			if (sourceExist && !targetExist)
				resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			if (!dao.isDirExists("f", TARGET_SO))
				LogUtil.warn("(" + dao.getExecutorName() + ") gpdbTargz_Ver7 Cannot find library [" + TARGET_SO + "], it may cause problem if ignored.");
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") gpdbTargz_Ver7 exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpSegInstall(String hostFile, String gpUsername, String gpPassword) {
		String cmd = String.format("sudo ln -s %s %s", SOURCE_SO, TARGET_SO);
		cmd = String.format(IConstantsCmds.GP_SSH_F, hostFile, cmd);
		try {
			GPResultSet soRs = dao.executeInteractiveCommand(cmd, new String[] {});
			LogUtil.info("(" + dao.getExecutorName() + ") gpSegInstall_Ver7 make link so file[" + TARGET_SO + "] is successed: " + soRs.isSuccessed());
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") gpSegInstall_Ver7 exception: " + e.getMessage());
		}
		return super.gpSegInstall(hostFile, gpUsername, gpPassword);
	}
}
