package com.txdb.gpmanage.core.gp.service.abs;

import java.util.Map;
import java.util.Properties;

import com.txdb.gpmanage.core.gp.constant.IConstantSteps;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseGpInstallService implements IGpInstallService {

	protected IExecuteDao dao;
	
	@Override
	public void initialize(IExecuteDao dao) {
		this.dao = dao;
	}
	
	@Override
	public GPResultSet sendGpFile(String targetDir, String localFile) {
		LogUtil.info("(" + dao.getExecutorName() + ") sendGPFile");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			LogUtil.info("(" + dao.getExecutorName() + ") sending file: [" + localFile + "]");
			resultSet.setSuccessed(dao.upload(targetDir, localFile));

		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") sendGpFile exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet rpmGpFile(String remoteRpmFilePath, String rpmFileName, String installPath) {
		LogUtil.info("(" + dao.getExecutorName() + ") rpmGpFile");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			resultSet.addChildResultSet(dao.executeCommand("cd " + remoteRpmFilePath));
			resultSet.addChildResultSet(dao.executeCommand("chmod u+x " + rpmFileName));
			
			if (!dao.isDirExists("d", installPath))
				resultSet.addChildResultSet(dao.executeCommand("mkdir -p " + installPath));
			
			// 启动安装  -> 浏览协议
			GPResultSet licenseRs = dao.executeProcessCommand("./" + rpmFileName, new String[] { 
					IConstantSteps.AGREEMENT_INSTALL_LICENSE, IConstantSteps.ABNORMAL_INSTALLER }, true);
			resultSet.addChildResultSet(licenseRs);
			
			if (IConstantSteps.AGREEMENT_INSTALL_LICENSE.equals(licenseRs.getMsgKeyword())) {
				// 接受协议 -> 准备安装路径
				GPResultSet installPathRs = dao.executeProcessCommand("y \n", new String[] { 
						IConstantSteps.INPUT_INSTALL_PATH });
				resultSet.addChildResultSet(installPathRs);
				
				// 输入安装路径 -> 确认安装路径
				GPResultSet confirmPathRs = dao.executeProcessCommand(installPath + "\n", new String[] { 
						IConstantSteps.AGREEMENT_INSTALL_CONFIRM });
				resultSet.addChildResultSet(confirmPathRs);
				
				// 确认安装 -> 安装完成
				GPResultSet installEndRs = dao.executeProcessCommand("y \n", new String[] { 
						IConstantSteps.FINAL_SUCCESS_INSTALL });
				resultSet.addChildResultSet(installEndRs);
			} else {
				licenseRs.setSuccessed(false);
				LogUtil.warn("(" + dao.getExecutorName() + ") Installer will only install on RedHat/CentOS x86_64");
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") rpmGpFile exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpdbTargz(String remoteTarfile, String installPath) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpdbTargz");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.TAR_ZXF, remoteTarfile, installPath);
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			int bi = remoteTarfile.lastIndexOf("/");
			int ei = remoteTarfile.indexOf(".tar.gz");
			cmd = String.format(IConstantsCmds.LN_S, remoteTarfile.substring(bi + 1, ei), "greenplum-db");
			resultSet.addChildResultSet(dao.executeCommand("cd " + installPath + " && " + cmd));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpdbTargz exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createNodeListFile(String remotePath, String[] all_host, String[] all_segment) {
		LogUtil.info("(" + dao.getExecutorName() + ") createNodeListFile");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.MKDIR_CD, remotePath, remotePath);
			String cd = "cd " + remotePath;
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
			cmd = String.format(IConstantsCmds.TOUCH_REWRITE_BLANK_FILE, DEFAULT_ALL_HOST, DEFAULT_ALL_HOST);
			resultSet.addChildResultSet(dao.executeCommand(cd + " && " + cmd));
			for (String host : all_host)
				resultSet.addChildResultSet(dao.executeCommand(cd + " && " + String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, host, DEFAULT_ALL_HOST)));
			
			cmd = String.format(IConstantsCmds.TOUCH_REWRITE_BLANK_FILE, DEFAULT_ALL_SEGMENT, DEFAULT_ALL_SEGMENT);
			resultSet.addChildResultSet(dao.executeCommand(cd + " && " + cmd));
			for (String segment : all_segment)
				resultSet.addChildResultSet(dao.executeCommand(cd + " && " + String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, segment, DEFAULT_ALL_SEGMENT)));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") createNodeListFile exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet configGpEnv(int port, String gpHome, String masterDataDirectory, String gpUsername, String[] envFiles) {
		LogUtil.info("(" + dao.getExecutorName() + ") configPgEnv");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
//			a、vi .bashrc
//			b、vi .bash_profile or .profile
//			c、vi /etc/profile
			if (envFiles == null) {
				resultSet.setOutputMsg("Can't not find environment files, param<envFiles> is null");
				return resultSet;
			}
			for (String envFile : envFiles) {
				String[] cmd_clear = {
						String.format(IConstantsCmds.SED_DEL_ROW_TI, "greenplum_path.sh", envFile),
						String.format(IConstantsCmds.SED_DEL_ROW_TI, "PGPORT", envFile),
//						String.format(IConstantsCmds.SED_DEL_ROW_TL, "GPHOME=", envFile),
//						String.format(IConstantsCmds.SED_DEL_ROW_TL, "PATH=$GPHOME", envFile),
						String.format(IConstantsCmds.SED_DEL_ROW_TL, "MASTER_DATA_DIRECTORY=", envFile),
//						String.format(IConstantsCmds.SED_DEL_ROW_TL, "export GPHOME", envFile),
//						String.format(IConstantsCmds.SED_DEL_ROW_TL, "export PATH", envFile),
						String.format(IConstantsCmds.SED_DEL_ROW_TL, "export MASTER_DATA_DIRECTORY", envFile) };
				for (String clr : cmd_clear)
					resultSet.addChildResultSet(dao.executeCommand(clr));
				
				String[] cmd_append = {
						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "source " + gpHome + "/greenplum_path.sh", envFile),
						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "export PGPORT=" + port, envFile),
//						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "GPHOME=" + gpHome, envFile),
//						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "PATH=$GPHOME/bin:$GPHOME/ext/python/bin:$PATH", envFile),
						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "MASTER_DATA_DIRECTORY=" + masterDataDirectory, envFile),
//						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "export GPHOME", envFile),
//						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "export PATH", envFile),
						String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "export MASTER_DATA_DIRECTORY", envFile) };
				for (String apd : cmd_append)
					resultSet.addChildResultSet(dao.executeCommand(apd));
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") configGpEnv exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpSSHExKeys(String hostFile, Map<String, String> passwordMap) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpSSHExKeys");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] { 
					IConstantSteps.INPUT_PASSWORD_EXKEYS };
			
			String cmd = String.format(IConstantsCmds.GP_EXKEYS, hostFile);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0])) {
				// 节点密码交互
				int bi = outputMsg.lastIndexOf(conditions[0]) + conditions[0].length() + 1;
				int ei = outputMsg.lastIndexOf(":");
				String hostname = outputMsg.substring(bi, ei);
				String hostpass = passwordMap.get(hostname);
				if (hostpass == null || outputMsg.contains(IConstantSteps.FINAL_PASSWORD_BAD)) {
					outputMsg += "\nFound password is [" + hostpass + "] for host[" + hostname + "] is not correct!";
					break;
				}
				tempRs = dao.executeInteractiveCommand(hostpass + "\n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpSSHExKeys exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpSegInstall(String hostFile, String gpUsername, String gpPassword) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpSegInstall");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.AGREEMENT_YN_S1_F,
					IConstantSteps.INPUT_PASSWORD_EXKEYS,
					IConstantSteps.INPUT_PASSWORD_SEG_INSTALL };
			
			String cmd = String.format(IConstantsCmds.GP_SEG_INSTALL, hostFile, gpUsername, gpPassword);
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1]) || 
					outputMsg.contains(conditions[2]) || outputMsg.contains(conditions[3]) || 
					outputMsg.contains(conditions[4])) {
				// 节点交互
				String stepCmd = "y \n";
				if (outputMsg.contains(conditions[3]) || outputMsg.contains(conditions[4]))
					stepCmd = gpPassword + "\n";
				tempRs = dao.executeInteractiveCommand(stepCmd);
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpSegInstall exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpChkInstallDir(String hostFile) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpChkInstallDir");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_CHK_INSTALL_DIR, hostFile);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpChkInstallDir exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpMakeDataDir(String dataDir, String gpadmin) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpPrepareWorkDir(Local)");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String owner_group = gpadmin + ":" + gpadmin;
			String cmd = "";
			if (!dataDir.endsWith("/"))
				dataDir += "/";
			
			cmd = "mkdir -p " + dataDir;
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			cmd = "chown " + owner_group + " " + dataDir;
			resultSet.addChildResultSet(dao.executeCommand(cmd));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpPrepareWorkDir(Local) exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpMakeDataDir(String dataDir, String hostFile, String gpadmin) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpPrepareWorkDir");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String owner_group = gpadmin + ":" + gpadmin;
			String cmd = IConstantsCmds.GP_SSH_F;
			if (!dataDir.endsWith("/"))
				dataDir += "/";
			
			cmd = String.format(IConstantsCmds.GP_SSH_F, hostFile, "mkdir -p " + dataDir);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			cmd = String.format(IConstantsCmds.GP_SSH_F, hostFile, "chown " + owner_group + " " + dataDir);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpPrepareWorkDir exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
		
	@Override
	public GPResultSet gpInitSystemCfg(String gpDir, String customDir, Properties initSystemProps) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpInitSystemCfg");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			// 1.0 prepare
			resultSet.addChildResultSet(dao.executeCommand("mkdir -p " + customDir));
			
			String initFileName = 
					DEFAULT_GPINITSYSTEM_TEMPLETE_PATH.substring(
					DEFAULT_GPINITSYSTEM_TEMPLETE_PATH.lastIndexOf("/") + 1);
			String targetFile = customDir + "/" + initFileName;
			
			gpDir += "/";
			gpDir = gpDir.replaceAll("//", "/");
			String templeteFile = gpDir + DEFAULT_GPINITSYSTEM_TEMPLETE_PATH;
			
			resultSet.addChildResultSet(dao.executeCommand("cp " + templeteFile + " " + customDir));
			resultSet.addChildResultSet(dao.executeCommand("chmod 775 " + targetFile));
			
			// 2.0 config
			String newLine = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, "", targetFile);
			resultSet.addChildResultSet(dao.executeCommand(newLine));
			
			for (String key : initSystemProps.stringPropertyNames()) {
		        String cmd = String.format(IConstantsCmds.SED_DEL_ROW_TI, key, targetFile);
		        resultSet.addChildResultSet(dao.executeCommand(cmd));
		        
				String txtContent = key + "=" + initSystemProps.getProperty(key);
				cmd = String.format(IConstantsCmds.ECHO_APPEND_LAST_ROW, txtContent, targetFile);
				resultSet.addChildResultSet(dao.executeCommand(cmd));
		    }
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpInitSystemCfg exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile) {
		return gpSystemDeploy(customParamFileDir, hostFile, false);
	}
	
	@Override
	public GPResultSet gpSystemDeploy(String customParamFileDir, String hostFile, boolean spreadMode) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpSystemDeploy");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String[] conditions = new String[] {
					IConstantSteps.AGREEMENT_YN_S1,
					IConstantSteps.AGREEMENT_YN_S2,
					IConstantSteps.AGREEMENT_YN_S1_F };
			
			String initFileName = 
					DEFAULT_GPINITSYSTEM_TEMPLETE_PATH.substring(
					DEFAULT_GPINITSYSTEM_TEMPLETE_PATH.lastIndexOf("/") + 1);
			
			customParamFileDir += "/";
			customParamFileDir = customParamFileDir.replaceAll("//", "/");
			String targetFile = customParamFileDir + initFileName;
			
			String cmd = String.format(IConstantsCmds.GP_DEPLOY, targetFile, hostFile, (spreadMode ? "-S" : ""));
			GPResultSet topRs = dao.executeInteractiveCommand(cmd, conditions);
			resultSet.addChildResultSet(topRs);
			
			String outputMsg = topRs.getOutputMsg();
			String outputErr = topRs.getOutputErr();
			GPResultSet tempRs = new GPResultSet(dao);
			while (outputMsg.contains(conditions[0]) || outputMsg.contains(conditions[1]) || outputMsg.contains(conditions[2])) {
				// 节点交互
				tempRs = dao.executeInteractiveCommand("y \n");
				resultSet.addChildResultSet(tempRs);
				outputMsg = tempRs.getOutputMsg();
				outputErr = tempRs.getOutputErr();
			}
			if (!resultSet.contains(tempRs))
				tempRs = topRs;
			
			// 如果错误是“Failed to kill processes for segment”，则认为是成功的
			if ((outputErr.indexOf(IConstantSteps.ABNORMAL_ERROR) == 
					outputErr.lastIndexOf(IConstantSteps.ABNORMAL_ERROR)) && 
					outputErr.contains(IConstantSteps.ABNORMAL_KILL)) {
				tempRs.setOutputMsg(outputErr);
				tempRs.setOutputErr("");
				tempRs.setSuccessed(true);
			} else if (outputErr.indexOf(IConstantSteps.ABNORMAL_ERROR) == -1 && 
					outputErr.indexOf(IConstantSteps.ABNORMAL_FATAL) == -1 && 
					outputErr.indexOf(IConstantSteps.ABNORMAL_CRITICAL) == -1) {
				tempRs.setOutputMsg(outputErr);
				tempRs.setOutputErr("");
				tempRs.setSuccessed(true);
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpSystemDeploy exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpScp(String[] hostnameArray, String file) {
		return gpScp(hostnameArray, file, null);
	}
	
	@Override
	public GPResultSet gpScp(String[] hostnameArray, String file, Map<String, String> passwordMap) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpScp_h");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			for (String hostname : hostnameArray) {
				String cmd = String.format(IConstantsCmds.GP_SCP_H, hostname, file, file);
				resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
				
				// TODO 输入密码
			}
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpScp_h exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet gpScp(String hostFile, String file) {
		return gpScp(hostFile, file, null);
	}
	
	@Override
	public GPResultSet gpScp(String hostFile, String file, Map<String, String> passwordMap) {
		LogUtil.info("(" + dao.getExecutorName() + ") gpScp_f");
		GPResultSet resultSet = new GPResultSet(dao);
		try {
			String cmd = String.format(IConstantsCmds.GP_SCP_F, hostFile, file, file);
			resultSet.addChildResultSet(dao.executeInteractiveCommand(cmd, new String[] {}));
			
			// TODO 输入密码
			
		} catch (Exception e) {
			resultSet.setOutputEpt(e.getMessage());
			LogUtil.error("(" + dao.getExecutorName() + ") gpScp_f exception: " + e.getMessage());
		}
		resultSet.collectResult();
		return resultSet;
	}
}
