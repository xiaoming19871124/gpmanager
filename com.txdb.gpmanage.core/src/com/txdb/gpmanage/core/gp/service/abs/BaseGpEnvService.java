package com.txdb.gpmanage.core.gp.service.abs;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpEnvService;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseGpEnvService implements IGpEnvService {

	protected IExecuteDao dao;
	
	@Override
	public void initialize(IExecuteDao dao) {
		this.dao = dao;
	}
	
	@Override
	public GPResultSet checkRequiredSoftware() {
		LogUtil.info("(" + dao.getExecutorName() + ") checkEnvironment");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			
//			// 编译版需要(python)，产品版不需要该依赖
//			GPResultSet pyRs = dao.executeCommand("python -V");
//			checkPython(pyRs);
//			resultSet.addChildResultSet(pyRs);
//			
//			// 编译版需要(pip)，产品版不需要该依赖
//			GPResultSet pipRs = dao.executeCommand("pip -V");
//			checkPip(pipRs);
//			resultSet.addChildResultSet(pipRs);
			
			// 通用(ntpd)
//			GPResultSet ntpdRs = dao.executeCommand("ntpd -!");
			GPResultSet ntpdRs = dao.executeInteractiveCommand("ntpd -!", new String[]{});
			checkNtpd(ntpdRs);
			resultSet.addChildResultSet(ntpdRs);
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") checkEnvironment exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
//	private void checkPython(GPResultSet rs) {
//		GPRequiredSw sw = new GPRequiredSw("Python");
//		sw.setRequiredVer("2.7.0");
//		
//		String optMsg = rs.getOutputMsg();
//		if (optMsg.trim().length() <= 0)
//			optMsg = rs.getOutputErr();
//
//		if (!optMsg.contains("Python"))
//			sw.setStatus(GPRequiredSw.STATUS_UNINSTALL);
//		else {
//			sw.setCurrentVer(optMsg.split(" ")[1]);
//			sw.setStatus(sw.compareVersion() >= 0 ? GPRequiredSw.STATUS_SATISFIED : GPRequiredSw.STATUS_UPGRADE);
//		}
//		rs.setSuccessed(sw.getStatus() == GPRequiredSw.STATUS_SATISFIED);
//		if (!rs.isSuccessed()) {
//			rs.setOutputMsg("");
//			rs.setOutputErr(optMsg);
//		}
//		rs.addRequiredSw(sw);
//	}
	
//	private void checkPip(GPResultSet rs) throws JSchException, IOException {
//		String softwareName = "Pip";
//		String currentVer = "";
//		int status = GPRequiredSw.STATUS_UNKNOW;
//		
//		String optMsg = rs.getOutputMsg();
//		
//		String[] verFragments = optMsg.split(" ");
//		if (rs.isSuccessed() && verFragments.length >= 2) {
//			currentVer = verFragments[1];
//			status = GPRequiredSw.STATUS_SATISFIED;
//		} else 
//			status = GPRequiredSw.STATUS_UNINSTALL;
//		
//		GPRequiredSw sw = new GPRequiredSw(softwareName);
//		sw.setCurrentVer(currentVer);
//		sw.setStatus(status);
//		rs.addRequiredSw(sw);
//		
//		checkPipList(rs);
//		rs.collectResult();
//	}
	
//	private void checkPipList(GPResultSet rs) throws JSchException, IOException {
//		GPResultSet pipListRs = dao.executeCommand("pip list");
//		
//		GPRequiredSw sw_psutil = new GPRequiredSw("psutil");
//		GPRequiredSw sw_paramiko = new GPRequiredSw("paramiko");
//		GPRequiredSw sw_lockfile = new GPRequiredSw("lockfile");
//		sw_lockfile.setRequiredVer("0.9.1");
//		
//		sw_psutil.setStatus(GPRequiredSw.STATUS_UNINSTALL);
//		sw_paramiko.setStatus(GPRequiredSw.STATUS_UNINSTALL);
//		sw_lockfile.setStatus(GPRequiredSw.STATUS_UNINSTALL);
//		
//		String[] pipListArray = pipListRs.getOutputMsg().replaceAll(" +", " ").split("\n");
//		for (String dep : pipListArray) {
//			String[] k_v = dep.split(" ");
//			if (k_v.length < 2)
//				continue;
//			
//			if (k_v[0].contains("psutil")) {
//				sw_psutil.setCurrentVer(k_v[1]);
//				sw_psutil.setStatus(GPRequiredSw.STATUS_SATISFIED);
//				
//			} else if (k_v[0].contains("paramiko")) {
//				sw_paramiko.setCurrentVer(k_v[1]);
//				sw_paramiko.setStatus(GPRequiredSw.STATUS_SATISFIED);
//				
//			} else if (k_v[0].contains("lockfile")) {
//				sw_lockfile.setCurrentVer(k_v[1]);
//				sw_lockfile.setStatus(sw_lockfile.compareVersion() >= 0 ? GPRequiredSw.STATUS_SATISFIED : GPRequiredSw.STATUS_UPGRADE);
//			}
//		}
//		rs.addRequiredSw(sw_psutil);
//		rs.addRequiredSw(sw_paramiko);
//		rs.addRequiredSw(sw_lockfile);
//		
//		if (sw_psutil.getStatus() != GPRequiredSw.STATUS_SATISFIED
//				|| sw_paramiko.getStatus() != GPRequiredSw.STATUS_SATISFIED
//				|| sw_lockfile.getStatus() != GPRequiredSw.STATUS_SATISFIED)
//			rs.setSuccessed(false);
//	}
	
	private void checkNtpd(GPResultSet rs) {
		GPRequiredSw sw = new GPRequiredSw("ntpd");
		sw.setRequiredVer("4.0.0");
		
		String optMsg = rs.getOutputMsg();
//		if (optMsg.contains("exit 0") || optMsg.equals(""))
//			optMsg = rs.getOutputErr();

		if (!optMsg.contains("ntpd") || !rs.isSuccessed())
			sw.setStatus(GPRequiredSw.STATUS_UNINSTALL);
		else {
			// ntpd - NTP daemon program - Ver. 4.2.4p8
			// ntpd - NTP daemon program - Ver. 4.2.6p5
			String verStr = optMsg.split("\n")[1];
			String[] verFmt = verStr.split(" ");
			sw.setCurrentVer(verFmt[verFmt.length - 1]);
			sw.setStatus(sw.compareVersion() >= 0 ? GPRequiredSw.STATUS_SATISFIED : GPRequiredSw.STATUS_UPGRADE);
		}
		rs.setSuccessed(sw.getStatus() == GPRequiredSw.STATUS_SATISFIED);
		if (!rs.isSuccessed()) {
			rs.setOutputMsg("");
			rs.setOutputErr(optMsg);
		}
		rs.addRequiredSw(sw);
	}
	
	@Override
	public GPResultSet updateHostname(String hostname) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateHostname");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "HOSTNAME", "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "HOSTNAME=" + hostname, "/etc/sysconfig/network");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
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
	public GPResultSet updateResolvConf(String nameserverIp) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateResolvConf");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String txtContent = "nameserver " + nameserverIp;
			String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, txtContent, "/etc/resolv.conf");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent, "/etc/resolv.conf");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") updateResolvConf exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet updateHosts(Map<String, String[]> hostMap) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateHosts");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			Iterator<Entry<String, String[]>> iterator = hostMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String[]> entry = iterator.next();
				String   key    = entry.getKey();
				String[] values = entry.getValue();
				
				for (String value : values) {
	        		System.out.println(key + " --> " + value);   
	        		
	        		String txtContent_search = key + " [ ]*" + value;
					String txtContent_append = key + " " + value;
					
			        String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, txtContent_search, "/etc/hosts");
			        resultSet.addChildResultSet(dao.executeCommand(cmd));
			        
					cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent_append, "/etc/hosts");
					resultSet.addChildResultSet(dao.executeCommand(cmd));
				}
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") updateHosts exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet closeIptables() {
		LogUtil.info("(" + dao.getExecutorName() + ") closeIptables");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			resultSet.addChildResultSet(dao.executeCommand("service iptables stop"));
			resultSet.addChildResultSet(dao.executeCommand("chkconfig iptables off"));
			resultSet.addChildResultSet(dao.executeCommand("service iptables status"));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") closeIptables exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet closeSeLinux() {
		LogUtil.info("(" + dao.getExecutorName() + ") closeSeLinux");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "SELINUX=enforcing", "/etc/selinux/config");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "SELINUX=disabled", "/etc/selinux/config");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "SELINUX=disabled", "/etc/selinux/config");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			// Set SELinux to permissive mode(0-permissive, 1-enforcing)
			GPResultSet setenforceRs = dao.executeCommand("setenforce 0");
			if (!setenforceRs.isSuccessed() && setenforceRs.getOutputErr().contains("SELinux is disabled")) {
				setenforceRs.setSuccessed(true);
				setenforceRs.setOutputMsg(setenforceRs.getOutputErr());
				setenforceRs.setOutputErr("");
			}
			resultSet.addChildResultSet(setenforceRs);
			resultSet.addChildResultSet(dao.executeCommand("getenforce"));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") closeSeLinux exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	protected String conf_sysctl = G_CONF_SYSCTL;
	protected String conf_limits = G_CONF_LIMITS;
	protected String conf_nproc  = G_CONF_NPROC;
	
	@Override
	public GPResultSet updateSysctl_limits_90_nproc(Properties sysctlProps, Properties limitsProps, Properties nprocProps) {
		LogUtil.info("(" + dao.getExecutorName() + ") updateSysctl_limits_90_nproc");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			if (sysctlProps != null && !sysctlProps.isEmpty()) {
				resultSet.addChildResultSet(dao.executeCommand("touch " + conf_sysctl));
				for (String key : sysctlProps.stringPropertyNames()) {
					String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, key, conf_sysctl);
					resultSet.addChildResultSet(dao.executeCommand(cmd));

					String txtContent = key + " = " + sysctlProps.getProperty(key);
					cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent, conf_sysctl);
					resultSet.addChildResultSet(dao.executeCommand(cmd));
				}
				String sysctlApplyCmd = String.format(IConstantsCmds.SYSCTL, "-p");
				resultSet.addChildResultSet(dao.executeCommand(sysctlApplyCmd));
			}
			if (limitsProps != null && !limitsProps.isEmpty()) {
				resultSet.addChildResultSet(dao.executeCommand("touch " + conf_limits));
				for (String key : limitsProps.stringPropertyNames()) {
					String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, key, conf_limits);
					resultSet.addChildResultSet(dao.executeCommand(cmd));

					String txtContent = "" + key + " " + limitsProps.getProperty(key) + "";
					cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent, conf_limits);
					resultSet.addChildResultSet(dao.executeCommand(cmd));
				}
			}
			if (nprocProps != null && !nprocProps.isEmpty()) {
				resultSet.addChildResultSet(dao.executeCommand("touch " + conf_nproc));
				for (String key : nprocProps.stringPropertyNames()) {
					String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, key.replaceAll(" ", " [ ]*"), conf_nproc);
					resultSet.addChildResultSet(dao.executeCommand(cmd));

					String txtContent = "" + key + " " + nprocProps.getProperty(key) + "";
					cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent, conf_nproc);
					resultSet.addChildResultSet(dao.executeCommand(cmd));
				}
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") updateSysctl_limits_90_nproc exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public Properties getSysctlParams() {
		return getSysctlParams(null);
	}
	
	@Override
	public Properties getSysctlParams(String key) {
		Properties properties = new Properties();
		String sysctlCmd = String.format(IConstantsCmds.SYSCTL, (key == null ? "-A" : key));
		try {
			GPResultSet sysctlRs = dao.executeInteractiveCommand(sysctlCmd, new String[] {});
			if (!sysctlRs.isSuccessed())
				return null;
			
			String[] msgFragments = sysctlRs.getOutputMsg().split("\n");
			for (String msgfragment : msgFragments) {
				String [] k_v = msgfragment.split("=");
				if (k_v.length != 2)
					continue;
				
				String paramKey = k_v[0].trim();
				String paramValue = k_v[1].trim();
				properties.put(paramKey, paramValue);
			}
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") getSysctlParams exception: " + e.getMessage());
			dao.callback(e.getMessage());
			return null;
		}
		return properties;
	}
	
	@Override
	public GPResultSet blockDev(int number) {
		LogUtil.info("(" + dao.getExecutorName() + ") blockDev");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TL, "blockdev --setra", "/etc/rc.d/rc.local");
	        resultSet.addChildResultSet(dao.executeCommand(cmd));
	        
	        String cmd_blockDev = String.format(IConstantsCmds.BLOCKDEV_UPD, String.valueOf(number));
			cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, cmd_blockDev, "/etc/rc.d/rc.local");
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			resultSet.addChildResultSet(dao.executeCommand(cmd_blockDev));
			resultSet.addChildResultSet(dao.executeCommand(IConstantsCmds.BLOCKDEV_INFO));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") blockDev exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createOrReplaceGpUser(String userName, String password) {
		LogUtil.info("(" + dao.getExecutorName() + ") createOrReplaceGpUser");
		GPResultSet resultSet = new GPResultSet(dao);

		boolean isExist = userExist(userName);
		LogUtil.info("(" + dao.getExecutorName() + ") User <" + userName + "> is" + (isExist ? " " : " not ") + "exist");
		
		if (isExist)
			resultSet.addChildResultSet(deleteUser(userName));
		
		resultSet.addChildResultSet(createUser(userName));
		resultSet.addChildResultSet(userPassword(userName, password));
		
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet clockSynchronize(String hostfile) {
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_SSH_F, hostfile, "ntpd");
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
			cmd = String.format(IConstantsCmds.GP_SSH_F, hostfile, "date");
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") createUser exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createUser(String userName) {
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.USER_ADD, userName, userName);
			return dao.executeCommand(cmd);

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") createUser exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public GPResultSet deleteUser(String userName) {
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.USER_DELETE, userName);
			return dao.executeCommand(cmd);

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") deleteUser exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public boolean userExist(String userName) {
		boolean isExist = true;
		try {
			String cmd = String.format(IConstantsCmds.USER_EXIST, userName);
			GPResultSet resultSet = dao.executeCommand(cmd);
			
			if (resultSet.isSuccessed() && "0".equals(resultSet.getOutputMsg().trim()))
				isExist = false;
			
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") userExist exception: " + e.getMessage());
			dao.callback(e.getMessage());
		}
		return isExist;
	}
	
	@Override
	public GPResultSet userPassword(String userName, String password) {
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.USER_PASSWD, password, userName);
			return dao.executeCommand(cmd);

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") userPassword exception: " + e.getMessage());
		}
		return resultSet;
	}
	
	@Override
	public boolean uploadFile(String directory, String uploadFile) {
		try {
			return dao.upload(directory, uploadFile);
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") uploadFile exception: " + e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean downloadFile(String src, String dst) {
		try {
			return dao.download(src, dst);
		} catch (Exception e) {
			LogUtil.error("(" + dao.getExecutorName() + ") downloadFile exception: " + e.getMessage());
		}
		return false;
	}
}
