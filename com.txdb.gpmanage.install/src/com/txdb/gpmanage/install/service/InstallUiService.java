package com.txdb.gpmanage.install.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.txdb.gpmanage.core.common.CommonUtil;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.InstallInfo;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;
import com.txdb.gpmanage.core.log.LogUtil;
import com.txdb.gpmanage.core.service.AbstractUIService;
import com.txdb.gpmanage.install.i18n.ResourceHandler;

/**
 * 安装界面服务类
 * 
 * @author ws
 *
 */
public class InstallUiService extends AbstractUIService {
	public static final String VERSION_GP_SIX = "gpdb.tar.gz";
	public static final String VERSION_GP_SEVEN = "gpdb.tar.gz";
	public static final int GP_PORT = 19200;

	public InstallUiService(Display display) {
		this.display = display;
	}

	/**
	 * closedSelinux
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public void closedSelinux(List<Host> hosts, StyledText text, UICallBack callback) {
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().closeSeLinux();
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.closedSeLunux", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.closedSeLunux", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * 关闭防火墙
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public void closeIptables(List<Host> hosts, StyledText text, UICallBack callback) {
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().closeIptables();
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.closeIptables", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.closeIptables", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}

	}

	/**
	 * 修改主机名
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public void updataHostName(Map<String, Host> hosts, final List<Host> configHost, StyledText text, UICallBack callback) {
		Set<String> key = hosts.keySet();
		String msg = "";
		for (final String newName : key) {
			final Host host = hosts.get(newName);
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().updateHostname(newName);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				// display.syncExec(new Runnable() {
				// @Override
				// public void run() {
				// int confIndex = configHost.indexOf(host);
				// int rIndex = right.indexOf(host.getName());
				// right.remove(rIndex);
				// right.add(newName, rIndex);
				// configHost.get(confIndex).setName(newName);
				// }
				// });
				host.setName(newName);
				msg = ResourceHandler.getValue("hostConfigure.success.hostName", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.hostName", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * 修改hosts文件
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public boolean modifyHostFile(List<Host> hosts, StyledText text, UICallBack callback) {
		Map<String, String[]> hostmap = new HashMap<String, String[]>();
		for (Host host : hosts) {
			hostmap.put(host.getIp(), new String[] { host.getName() });
		}
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				return false;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().updateHosts(hostmap);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.hosts", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.hosts", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
		return true;
	}

	/**
	 * 修改预读扇区
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public void modifyBlockdev(List<Host> hosts, int blockNumb, StyledText text, UICallBack callback) {
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().blockDev(blockNumb);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.blockdev", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.blockdev", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * 修改最大进程限制
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public void modifynproc(List<Host> hosts, String nprocNumb, StyledText text, UICallBack callback) {
		Properties p = new Properties();
		p.setProperty("*soft nproc", nprocNumb);
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().updateSysctl_limits_90_nproc(null, null, p);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.nproc", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.nproc", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * 修改sysctl文件
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public void modifySysctl(List<Host> hosts, java.util.List<String> params, StyledText text, UICallBack callback) {
		Properties sysctlProps = new Properties();
		for (String param : params) {
			String[] p = param.split("=");
			sysctlProps.setProperty(p[0].trim(), p[1].trim());
		}
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().updateSysctl_limits_90_nproc(sysctlProps, null, null);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.sysctl", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.sysctl", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * 修改limit文件
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public void modifyLimit(List<Host> hosts, java.util.List<String> params, StyledText text, UICallBack callback) {
		Properties limitProps = new Properties();
		for (String param : params) {
			String[] p = param.split("=");
			limitProps.setProperty("*" + p[0].trim(), p[1].trim());
		}
		String msg = "";
		for (Host host : hosts) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			GPResultSet rs = gpController.getEnvServiceProxy().updateSysctl_limits_90_nproc(null, limitProps, null);
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				msg = ResourceHandler.getValue("hostConfigure.success.limit", new String[] { host.getName() });
			} else {
				msg = ResourceHandler.getValue("hostConfigure.fail.limit", new String[] { host.getName(), getErrorMsg(rs) });
			}
			setMsg(text, isSuccess, msg);
			gpController.disconnect();
		}
	}

	/**
	 * master节点安装
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public boolean insatallMaster(InstallInfo info, Host master, StyledText text, UICallBack callback) {
		IGPConnector gpController = new GPConnectorImpl(master);
		String msg = "";
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return false;
		}
		// 创建用户
		gpController.setCallback(callback);
		GPResultSet createUserRS = gpController.getEnvServiceProxy().createOrReplaceGpUser(info.getSuperUserName(), info.getSuperUserPassword());
		boolean iscreateUserSuccess = createUserRS.isSuccessed();
		if (iscreateUserSuccess) {
			msg = ResourceHandler.getValue("install.success.user", new String[] { master.getName(), info.getSuperUserName() });
		} else {
			msg = ResourceHandler.getValue("install.fail.user", new String[] { master.getName(), info.getSuperUserName(), getErrorMsg(createUserRS) });
		}
		setMsg(text, iscreateUserSuccess, msg);
		// 创建安装目录
		GPResultSet msrRs = gpController.getInstallServiceProxy().gpMakeDataDir(info.getInstallPath(), master.getUserName());
		boolean isMakeInstallDirSuccess = msrRs.isSuccessed();
		if (isMakeInstallDirSuccess) {
			msg = ResourceHandler.getValue("install.success.installDir");
		} else {
			msg = ResourceHandler.getValue("install.fail.installDir", new String[] { getErrorMsg(msrRs) });
			setMsg(text, isMakeInstallDirSuccess, msg);
			return false;
		}
		setMsg(text, isMakeInstallDirSuccess, msg);
		// 上传文件
		String versionString = gpController.getDao().getCmdLsbInfo().getRelease();
		LogUtil.info(" linux version" + versionString);
		// versionString = versionString.replaceAll("[a-zA-Z]","" );
		versionString = versionString.replaceAll("([^\\d\\.]|\\.(?=[^\\.]*\\.))", "");
		LogUtil.info(" linux version" + versionString);

		// TODO 获取操作系统版本
		float version = 6;
		try {
			version = Float.valueOf(versionString.trim());
		} catch (NumberFormatException e) {
			LogUtil.error("get os version error", e);
		}

		String pg_package = VERSION_GP_SIX;
		if (version >= 7.0)
			pg_package = VERSION_GP_SEVEN;
		LogUtil.info("upload database file " + CommonUtil.getInstallLocation() + "gp" + File.separator + pg_package + "to " + info.getInstallPath());
		// 创建安装目录
		GPResultSet sendGpFileRs = gpController.getInstallServiceProxy().sendGpFile(info.getInstallPath(), CommonUtil.getInstallLocation() + "gp" + File.separator + pg_package);
		boolean isSendSuccess = sendGpFileRs.isSuccessed();
		if (isSendSuccess) {
			msg = ResourceHandler.getValue("install.success.gpdb", new String[] { master.getName() });
		} else {
			msg = ResourceHandler.getValue("install.fail.gpdb", new String[] { master.getName(), getErrorMsg(sendGpFileRs) });
			setMsg(text, isSendSuccess, msg);
			return false;
		}
		setMsg(text, isSendSuccess, msg);
		// 解压
		GPResultSet unzipRs = gpController.getInstallServiceProxy().gpdbTargz(info.getInstallPath() + pg_package, info.getInstallPath());
		boolean isunzipSuccess = sendGpFileRs.isSuccessed();
		if (isunzipSuccess) {
			msg = ResourceHandler.getValue("install.success", new String[] { master.getName() });
		} else {
			msg = ResourceHandler.getValue("install.fail", new String[] { master.getName(), getErrorMsg(unzipRs) });
			return false;
		}
		setMsg(text, isSendSuccess, msg);
		// 设置环境变量
		GPResultSet setEnvRs = gpController.getInstallServiceProxy().configGpEnv(info.getPort() == null ? GP_PORT : Integer.valueOf(info.getPort()), info.getInstallPath() + "greenplum-db", null,
				info.getSuperUserName(), null);
		boolean issetEnvSuccess = sendGpFileRs.isSuccessed();
		if (issetEnvSuccess) {
			msg = ResourceHandler.getValue("install.success.env", new String[] { master.getName() });
		} else {
			msg = ResourceHandler.getValue("install.fail.env", new String[] { master.getName(), getErrorMsg(setEnvRs) });
		}
		setMsg(text, isSendSuccess, msg);
		gpController.disconnect();
		return true;

	}

	/**
	 * 交换秘钥
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public boolean exchangKey(final InstallInfo info, final StyledText text, UICallBack callback) {
		Host master = info.getMasterHost();
		if (master == null)
			return false;
		String msg = "";
		List<Host> installHost = info.getInstallHost();
		if (installHost == null || installHost.size() < 1)
			return false;
		List<String> allInstallHost = new ArrayList<String>();
		List<String> segHost = new ArrayList<String>();
		Map<String, String> passwordMap = new HashMap<String, String>();
		for (Host host : installHost) {
			passwordMap.put(host.getName(), host.getPassword());
			segHost.add(host.getName());
			allInstallHost.add(host.getName());
		}
		if (!installHost.contains(master)) {
			allInstallHost.add(master.getName());
			passwordMap.put(master.getName(), master.getPassword());
		}
		// for (Host host : installHost) {
		// allInstallHost.add(host.getName());
		// passwordMap.put(host.getName(), host.getPassword());
		// if (host.isSegment())
		// segHost.add(host.getName());
		// if (host.isMaster()) {
		// master = host;
		// }
		// }
		final IGPConnector gpController = new GPConnectorImpl(master);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return false;
		}
		gpController.setCallback(callback);
		// 创建包含所有host和包含所有segment主机名称的文件
		gpController.getInstallServiceProxy().createNodeListFile(info.getInstallPath(), allInstallHost.toArray(new String[allInstallHost.size()]), segHost.toArray(new String[segHost.size()]));
		// 交换秘钥
		final GPResultSet rs = gpController.getInstallServiceProxy().gpSSHExKeys(info.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST, passwordMap);
		final boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			for (Host host : installHost) {
				host.setSSHExchange(true);
			}
		}
		if (isSuccess) {
			msg = ResourceHandler.getValue("ssh.success.exchangKey");
		} else {
			msg = ResourceHandler.getValue("ssh.fail.exchangKey", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
		return isSuccess;
	}

	/**
	 * 节点安装gp
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public boolean installSegment(final InstallInfo info, final StyledText text, UICallBack callback) {
		Host master = info.getMasterHost();
		if (master == null)
			return false;
		String msg = "";
		final List<Host> installHost = info.getInstallHost();
		// segment节点创建gp管理用户
		for (final Host host : installHost) {
			// if (host.isSegment() && !host.isMaster()) {
			final IGPConnector gpController = new GPConnectorImpl(host);
			final GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
				setConnectionMsg(text, errorMsg);
				continue;
			}
			gpController.setCallback(callback);
			// 创建用户
			final GPResultSet createUserRS = gpController.getEnvServiceProxy().createOrReplaceGpUser(info.getSuperUserName(), info.getSuperUserPassword());
			final boolean iscreateUserSuccess = createUserRS.isSuccessed();
			if (iscreateUserSuccess) {
				msg = ResourceHandler.getValue("install.success.user", new String[] { host.getName(), info.getSuperUserName() });
			} else {
				msg = ResourceHandler.getValue("install.fail.user", new String[] { host.getName(), info.getSuperUserName(), getErrorMsg(createUserRS) });
			}
			setMsg(text, iscreateUserSuccess, msg);
			gpController.disconnect();
			// } else if (host.isMaster()) {
			// master = host;
			// }
		}
		final IGPConnector gpController = new GPConnectorImpl(master);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return false;
		}
		gpController.setCallback(callback);
		// 节点安装
		final GPResultSet rs = gpController.getInstallServiceProxy().gpSegInstall(info.getInstallPath() + IGpInstallService.DEFAULT_ALL_SEGMENT, info.getSuperUserName(), info.getSuperUserPassword());
		final boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			for (Host host : installHost) {
				host.setInstall(true);
			}
		}
		if (isSuccess) {
			msg = ResourceHandler.getValue("install.success.segment");
		} else {
			msg = ResourceHandler.getValue("install.fail.segment", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
		return isSuccess;
	}

	/**
	 * system检测
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public void systemCheck(final InstallInfo info, final StyledText text, UICallBack callback) {
		Host master = info.getMasterHost();
		if (master == null)
			return;
		String msg = "";

		final IGPConnector gpController = new GPConnectorImpl(master.getIp(), info.getSuperUserName(), info.getSuperUserPassword(), -1);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return;
		}
		gpController.setCallback(callback);
		final GPResultSet rs = gpController.getManageServiceProxy().gpCheck("-f " + info.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST);
		final boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("install.success.checkenv");
		} else {
			msg = ResourceHandler.getValue("install.fail.checkenv", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
	}

	/**
	 * 硬件检测
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public void hardwareCheck(final InstallInfo info, final StyledText text, UICallBack callback) {
		String msg = "";
		Host master = info.getMasterHost();
		if (master == null)
			return;
		final IGPConnector gpController = new GPConnectorImpl(master.getIp(), info.getSuperUserName(), info.getSuperUserPassword(), -1);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return;
		}
		gpController.setCallback(callback);
		final GPResultSet rs = gpController.getManageServiceProxy().gpCheckPerf("-f " + info.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST + " -d gpcheckperf");
		final boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("install.success.checkhardware");
		} else {
			msg = ResourceHandler.getValue("install.fail.checkhardware", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
	}

	/**
	 * initfile配置
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public boolean createInitFile(final InstallInfo info, final StyledText text, UICallBack callback) {
		String msg = "";
		Host master = info.getMasterHost();
		if (master == null)
			return false;
		final IGPConnector gpController = new GPConnectorImpl(master.getIp(), info.getSuperUserName(), info.getSuperUserPassword(), -1);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return false;
		}
		Properties p = new Properties();
		p.setProperty("MASTER_HOSTNAME", master.getName());
		p.setProperty("MASTER_DIRECTORY", info.getMasterDataDir() + "master");
		p.setProperty("MASTER_PORT", info.getPort());
		if (!info.getDatabaseName().isEmpty())
			p.setProperty("DATABASE_NAME", info.getDatabaseName());
		p.setProperty("MACHINE_LIST_FILE", info.getInstallPath() + IGpInstallService.DEFAULT_ALL_SEGMENT);
		if (info.isAddMirror()) {
			p.setProperty("MIRROR_PORT_BASE", info.getMirrorPort());
			p.setProperty("REPLICATION_PORT_BASE", info.getReplicationPort());
			p.setProperty("MIRROR_REPLICATION_PORT_BASE", info.getMirrorReplicationPort());
			String mirrorPath = "";
			for (int i = 0; i < info.getPrimaryNumb(); i++) {
				mirrorPath = mirrorPath + info.getMirrorDataDir() + "mirror ";
			}
			p.setProperty("declare -a MIRROR_DATA_DIRECTORY", "(" + mirrorPath + ")");
		}
		String primaryPath = "";
		for (int i = 0; i < info.getPrimaryNumb(); i++) {
			primaryPath = primaryPath + info.getSegmentDataDir() + "primary ";
		}
		p.setProperty("declare -a DATA_DIRECTORY", "(" + primaryPath + ")");
		gpController.setCallback(callback);
		final GPResultSet rs = gpController.getInstallServiceProxy().gpInitSystemCfg(info.getInstallPath(), "/home/" + info.getSuperUserName() + "/gpconfigs/", p);
		final boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("initFile.success.configure");
		} else {
			msg = ResourceHandler.getValue("initFile.fail.configure", new String[] { getErrorMsg(rs) });
			return false;
		}
		setMsg(text, isSuccess, msg);
		// 设置环境变量
		final IGPConnector evnController = new GPConnectorImpl(master);
		final GPResultSet evnConnectRs = evnController.connect();
		boolean isEvnConnect = evnConnectRs.isSuccessed();
		if (!isEvnConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return false;
		}
		evnController.setCallback(callback);
		final GPResultSet setEnvRs = evnController.getInstallServiceProxy().configGpEnv(Integer.valueOf(info.getPort()), info.getInstallPath() + "greenplum-db",
				info.getMasterDataDir() + "master/gpseg-1", info.getSuperUserName(), null);
		final boolean issetEnvSuccess = setEnvRs.isSuccessed();
		if (issetEnvSuccess) {
			msg = ResourceHandler.getValue("install.success.env");
		} else {
			msg = ResourceHandler.getValue("install.fail.env", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
		return isSuccess;
	}

	/**
	 * 安装实例
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public void installInstance(final InstallInfo info, final StyledText text, UICallBack callback) {
		String msg = "";
		Host master = info.getMasterHost();
		if (master == null)
			return;
		// for (Host host : info.getInstallHost()) {
		// if (host.isMaster()) {
		// master = host;
		// break;
		// }
		// }
		// 创建目录
		final IGPConnector gpController = new GPConnectorImpl(master);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return;
		}
		gpController.setCallback(callback);
		// TODO
		GPResultSet msrRs = gpController.getInstallServiceProxy().gpMakeDataDir(info.getMasterDataDir() + "master/", info.getSuperUserName());
		GPResultSet segRs = gpController.getInstallServiceProxy().gpMakeDataDir(info.getSegmentDataDir() + "primary/", info.getInstallPath() + IGpInstallService.DEFAULT_ALL_SEGMENT,
				info.getSuperUserName());
		GPResultSet mirRs = null;
		if (info.isAddMirror()) {
			mirRs = gpController.getInstallServiceProxy().gpMakeDataDir(info.getMirrorDataDir() + "mirror/", info.getInstallPath() + IGpInstallService.DEFAULT_ALL_SEGMENT, info.getSuperUserName());
		}

		final boolean isSuccess = msrRs.isSuccessed() && segRs.isSuccessed() && (!info.isAddMirror() || mirRs.isSuccessed());

		if (isSuccess) {
			msg = ResourceHandler.getValue("instance.success.createdir");
		} else {
			GPResultSet rs = null;
			if (!msrRs.isSuccessed())
				rs = msrRs;
			else if (!segRs.isSuccessed())
				rs = segRs;
			else if (info.isAddMirror() && !mirRs.isSuccessed())
				rs = mirRs;
			else
				rs = msrRs;
			msg = ResourceHandler.getValue("instance.fail.createdir", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
		// 初始化
		IGPConnector gpInitController = new GPConnectorImpl(master.getIp(), info.getSuperUserName(), info.getSuperUserPassword(), -1);
		GPResultSet connectInitRs = gpInitController.connect();
		boolean isInitConnect = connectInitRs.isSuccessed();
		if (!isInitConnect) {
			final String errorMsg = "[ERROR:]" + (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, errorMsg);
			return;
		}
		gpInitController.setCallback(callback);

		GPResultSet initRs = gpInitController.getInstallServiceProxy().gpSystemDeploy("/home/" + info.getSuperUserName() + "/gpconfigs/",
				info.getInstallPath() + IGpInstallService.DEFAULT_ALL_SEGMENT, info.isAddMirror() ? info.isSpread() : false);
		boolean isInitSuccess = initRs.isSuccessed();
		if (isInitSuccess) {
			msg = ResourceHandler.getValue("instance.success.install");
		} else {
			msg = ResourceHandler.getValue("instance.fail.install", new String[] { getErrorMsg(initRs) });
		}
		setMsg(text, isInitSuccess, msg);
		gpInitController.disconnect();
	}

	/**
	 * 检查依赖软件
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public List<GPRequiredSw> checkSoft(final Host host, final StyledText text, UICallBack callback) {
		List<GPRequiredSw> quiredSw = null;
		final IGPConnector gpController = new GPConnectorImpl(host);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String error = (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			String errorMsg = ResourceHandler.getValue("soft.fail.check", new String[] { host.getName(), error });
			LogUtil.error("checkout " + host.getName() + ":" + host.getIp() + "required soft error", error);
			setConnectionMsg(text, errorMsg);
			return quiredSw;
		}
		gpController.setCallback(callback);
		final GPResultSet rs = gpController.getEnvServiceProxy().checkRequiredSoftware();
		quiredSw = rs.getRequiredSwList();
		final boolean isSuccess = rs.isSuccessed();
		String msg = "";
		if (isSuccess) {
			msg = ResourceHandler.getValue("soft.success.check", new String[] { host.getName() });
		} else {
			msg = ResourceHandler.getValue("soft.fail.check", new String[] { host.getName(), getErrorMsg(rs) });
			LogUtil.error("checkout " + host.getName() + ":" + host.getIp() + "required soft error", msg);
		}
		setMsg(text, isSuccess, msg);
		gpController.disconnect();
		return quiredSw;

	}

	public static void main(String[] args) {
		// String a = "adsfdasfasf12.dsafa34adsfaf.2";
		// System.out.println(a.replaceAll("([^\\d\\.]|\\.(?=[^\\.]*\\.))",
		// ""));
		double b = 7.0;
		float a = 6;
		System.out.println(b > a);

	}
}
