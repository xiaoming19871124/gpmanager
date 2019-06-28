package com.txdb.gpmanage.ext.redhat.v7.service;

import java.util.Properties;

import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpEnvService;
import com.txdb.gpmanage.core.log.LogUtil;

public class GpEnvService extends BaseGpEnvService {

	public GpEnvService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(IExecuteDao dao) {
		super.initialize(dao);
	}
	
	@Override
	public GPResultSet updateHostname(String hostname) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateHostname");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			// 更新/etc/sysconfig/network
			String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "NETWORKING", "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "HOSTNAME", "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "NETWORKING=yes", "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "HOSTNAME=" + hostname, "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			// 更新/etc/hostname
			cmd = "echo '" + hostname + "' > /etc/hostname";
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			// 更新hostname
			cmd = "hostname " + hostname;
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") updateHostname exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet closeIptables() {
		LogUtil.info("(" + dao.getExecutorName() + ") closeIptables");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			resultSet.addChildResultSet(dao.executeCommand("systemctl stop firewalld"));
			resultSet.addChildResultSet(dao.executeCommand("systemctl disable firewalld"));
//			resultSet.addChildResultSet(dao.executeCommand("systemctl status firewalld"));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") closeIptables exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet updateSysctl_limits_90_nproc(Properties sysctlProps, Properties limitsProps, Properties nprocProps) {
		
		// 针对 Redhat7和CentOS7及以上版本，文件位置有所改变，需要指定
		
//		conf_sysctl = "/usr/lib/sysctl.d/90-override.conf";
//		conf_limits = "/etc/security/limits.conf";
		conf_nproc = "/etc/security/limits.d/20-nproc.conf";
		
		return super.updateSysctl_limits_90_nproc(sysctlProps, limitsProps, nprocProps);
	}
}
