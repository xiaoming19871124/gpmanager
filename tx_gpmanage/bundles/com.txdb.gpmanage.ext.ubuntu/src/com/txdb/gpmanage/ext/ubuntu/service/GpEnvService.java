package com.txdb.gpmanage.ext.ubuntu.service;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpEnvService;
import com.txdb.gpmanage.core.log.LogUtil;

public class GpEnvService extends BaseGpEnvService {

	/**
	 * 设置用户密码
	 * echo [username]:[password] | chpasswd
	 */
	public final static String USER_PASSWD = "echo %s:%s | chpasswd";
	
	public GpEnvService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
		super.initialize(dao);
	}
	
	@Override
	public GPResultSet closeIptables() {
		// TODO Auto-generated method stub
		LogUtil.info("(" + dao.getExecutorName() + ") closeIptables");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			resultSet.addChildResultSet(dao.executeCommand("ufw disable"));
			resultSet.collectResult();

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") closeIptables exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public GPResultSet closeSeLinux() {
		// TODO Auto-generated method stub
		LogUtil.info("(" + dao.getExecutorName() + ") closeSeLinux");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			resultSet.addChildResultSet(dao.executeCommand("getenforce"));
			resultSet.collectResult();
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") closeSeLinux exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public GPResultSet blockDev(int number) {
		// TODO Auto-generated method stub
		LogUtil.info("(" + dao.getExecutorName() + ") blockDev");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
	        String cmd = String.format(IConstantsCmds.BLOCKDEV_UPD, String.valueOf(number));
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			resultSet.addChildResultSet(dao.executeCommand(IConstantsCmds.BLOCKDEV_INFO));
			
			resultSet.collectResult();
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") blockDev exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public GPResultSet userPassword(String userName, String password) {
		try {
			String cmd = String.format(USER_PASSWD, userName, password);
			return dao.executeCommand(cmd);

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new GPResultSet(dao);
	}
}
