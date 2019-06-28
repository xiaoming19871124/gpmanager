package com.txdb.gpmanage.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.i18n.ResourceHandler;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class AbstractUIService {
	/**
	 * Display对象
	 */
	public Display display;

	/**
	 * 设置信息
	 * 
	 * @param display
	 * @param text
	 * @param isSuccess
	 * @param msg
	 */
	public void setMsg(final StyledText text, final boolean isSuccess,
			final String msg) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (isSuccess) {
					setNormalMsg(text, msg);
				} else {
					setErrorMsg(text, msg);

				}
			}
		});
	}

	/**
	 * 设置连接信息
	 * 
	 * @param display
	 * @param text
	 * @param rs
	 * @param errorMsg
	 */
	protected void setConnectionMsg(final StyledText text, final String errorMsg) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				setErrorMsg(text, errorMsg);
			}
		});
	}

	/**
	 * 设置错误信息
	 * 
	 * @param display
	 * @param text
	 * @param msg
	 */
	public void setErrorMsg(StyledText text, String msg) {
		int startOffset = text.getText().length();
		text.append(msg);
		StyleRange styleRange = new StyleRange(startOffset, msg.length(),
				display.getSystemColor(SWT.COLOR_RED), null);
		text.setStyleRange(styleRange);
		text.setTopIndex(Integer.MAX_VALUE);
	}

	/**
	 * 设置信息
	 * 
	 * @param text
	 * @param msg
	 */
	protected void setNormalMsg(StyledText text, String msg) {
		int startOffset = text.getText().length();
		text.append(msg + "\n");
		StyleRange styleRange = new StyleRange(startOffset, msg.length(),
				display.getSystemColor(SWT.COLOR_BLUE), null);
		text.setStyleRange(styleRange);
		text.setTopIndex(Integer.MAX_VALUE);
	}

	/**
	 * 获取错误信息
	 * 
	 * @param rootRs
	 * @return
	 */
	protected String getErrorMsg(GPResultSet rootRs) {
		StringBuffer sb = new StringBuffer();
		List<GPResultSet> childResultSet = rootRs.getChildResultSetList();
		if (childResultSet == null) {
			sb.append(ResourceHandler.getValue("info.unknown.error"));
			return sb.toString();
		}
		for (GPResultSet childRs : childResultSet) {
			if (!childRs.isSuccessed()) {
				List<GPResultSet> grandsonRs = childRs.getChildResultSetList();
				if (grandsonRs == null)
					continue;
				for (GPResultSet grandson : grandsonRs) {
					if (!grandson.isSuccessed()) {
						String err = grandson.getOutputErr() == null ? grandson
								.getOutputEpt() : grandson.getOutputErr();
						sb.append("[ERROR:]" + err);
						sb.append("\n");
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 验证host是否正确并获取hostname;
	 * 
	 * @param host
	 * @return
	 */
	public boolean verificationHost(Host host) {
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			LogUtil.error("测试连接主机失败： " + getErrorMsg(connectRs));
			return false;
		}
		String hostname = gpController.getOriginHostname();
		if (hostname == null || hostname.isEmpty())
			return false;
		host.setName(hostname);
		gpController.disconnect();
		return true;
	}

	/**
	 * 验证host是否正确ssh;
	 * 
	 * @param host
	 * @return
	 */
	public boolean verificationHost(String ip, String userName, String password) {
		IGPConnector gpController = new GPConnectorImpl(ip, userName, password);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			LogUtil.error("测试连接主机失败： " + getErrorMsg(connectRs));
			return false;
		}
		gpController.disconnect();
		return true;
	}

	// /**
	// * 验证是否正确数据库;
	// *
	// * @param host
	// * @return
	// */
	// public boolean verificationJDBC(String ip, String userName, String
	// password) {
	// IGPConnector gpController = new GPConnectorImpl(ip);
	// boolean isConnect = gpController.getManageServiceProxy().connectJdbc(
	// userName, password);
	//
	// if (!isConnect) {
	// LogUtil.error("测试连接失败" + ip + " " + userName + " " + password);
	// return false;
	// }
	// gpController.disconnect();
	// return true;
	// }

	public List<Map<String, Object>> executeQuery(GPManagerEntity gp,
			String sql, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		 gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(),Integer.valueOf(gp.getGpPort()),gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text,
					ResourceHandler.getValue("manage.error.connectiondb"));
			return null;
		}
		List<Map<String, Object>> objecList = gpadminController
				.getManageServiceProxy().executeQuery(sql);
		gpadminController.disconnect();
		return objecList;
	}

	public int executeUpdate(GPManagerEntity gp, String sql, StyledText text,
			UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		 gpadminController.connect();
		
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(),Integer.valueOf(gp.getGpPort()),gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text,
					ResourceHandler.getValue("manage.error.connectiondb"));
			return -1;
		}
		int isSuccess = gpadminController.getManageServiceProxy()
				.executeUpdate(sql);
		if (isSuccess != -1)
			setMsg(text, true, "[INFO:]" + sql + " success\n");
		else
			setMsg(text, false, "[ERROR:]" + sql + " fail\n");
		gpadminController.disconnect();
		return isSuccess;
	}

	/**
	 * 集群是否启动
	 * 
	 * @param ip
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean checkoutGPState(String ip, String userName, String password) {
		final IGPConnector gpController = new GPConnectorImpl(ip, userName,
				password);
		gpController.connect();
		boolean gpdbExist = gpController.getManageServiceProxy().gpState()
				.isSuccessed();
		return gpdbExist;
	}

	/**
	 * 集群是否存在
	 * 
	 * @param ip
	 * @param userName
	 * @param password
	 * @return
	 */
	public String getGpHome(String ip, String userName, String password) {
		final IGPConnector gpController = new GPConnectorImpl(ip, userName,
				password);
		gpController.connect();
		String gphome = gpController.getManageServiceProxy().gpHome();
		return gphome;
	}

	/**
	 * 验证host是否存在
	 * 
	 * @param host
	 * @return
	 */
	public boolean isExistHost(Host host, List<Host> existHosts) {
		for (Host existHost : existHosts) {
			boolean isExist = isHostEqual(host, existHost);
			if (isExist)
				return isExist;
		}
		return false;
	}

	/**
	 * 验证host是否存在
	 * 
	 * @param host
	 * @return
	 */
	public boolean isHostEqual(Host host, Host compareHost) {
		if (host.getIp().equals(compareHost.getIp()))
			return true;
		return false;
	}

	/**
	 * 主机是否在集群中
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public boolean isHostInGP(Host host, GPManagerEntity gp) {
		List<GPSegmentInfo> segmentsinfo = queryAllSegmentStatus(gp);
		for (GPSegmentInfo info : segmentsinfo) {
			if (info.getHostname().equals(host.getName()))
				return true;
		}
		return false;
	}

	/**
	 * 主机是否在集群中
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public boolean isMaster(Host host, GPManagerEntity gp) {
		List<GPSegmentInfo> segmentsinfo = queryAllSegmentStatus(gp);
		for (GPSegmentInfo info : segmentsinfo) {
			if (info.getContent() == -1
					&& info.getRole().equals(GPSegmentInfo.ROLE_PRIMARY)
					&& info.getHostname().equals(host.getName()))
				return true;
		}
		return false;
	}

	/**
	 * 測試连接JDBC
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public boolean connectionByJDBC(String ip, String user, String pwd,int port) {
		// IGPConnector gpadminController = new GPConnectorImpl(ip, user, pwd);
		//
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect)
		// return false;
		IGPConnector gpadminController = new GPConnectorImpl(ip);
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(user, pwd,port,null);
		gpadminController.disconnect();
		return isJDBCSuccess;
	}
	/**
	 * 測試连接JDBC
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public boolean connectionByJDBC(String ip, String user, String pwd,int port,String database) {
		// IGPConnector gpadminController = new GPConnectorImpl(ip, user, pwd);
		//
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect)
		// return false;
		IGPConnector gpadminController = new GPConnectorImpl(ip);
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(user, pwd,port,database);
		gpadminController.disconnect();
		return isJDBCSuccess;
	}
	/**
	 * 获取用户角色
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public String queryRole(String ip, String user, String pwd,int port,String database) {
		// IGPConnector gpadminController = new GPConnectorImpl(ip, user, pwd);
		//
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect)
		// return false;
		String role ="";
		IGPConnector gpadminController = new GPConnectorImpl(ip);
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(user, pwd,port,database);
		if (!isJDBCSuccess) {
			gpadminController.disconnect();
			return role;
		}
		List<Map<String, Object>> roles = gpadminController
				.getManageServiceProxy().executeQuery(
						"select usesuper from pg_user where usename = '" + user
								+ "'");
		for(Map<String, Object> m:roles){
			role = (String)m.get("usesuper");
		}
		gpadminController.disconnect();
		return role;
	}

	/**
	 * 查询所有节点状态
	 * 
	 * @param gp
	 * @return
	 */
	public List<GPSegmentInfo> queryAllSegmentStatus(GPManagerEntity gp) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(),
		// gp.getGpUserName(), gp.getGpUserPwd(), -1);
		//
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect)
		// return new ArrayList<GPSegmentInfo>();
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		boolean isConnectSuccess = gpadminController.getManageServiceProxy()
				.connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(),Integer.valueOf(gp.getGpPort()),gp.getGpdatabase());
		if (!isConnectSuccess) {
			gpadminController.disconnect();
			return new ArrayList<GPSegmentInfo>();
		}
		List<GPSegmentInfo> segments = gpadminController
				.getManageServiceProxy().queryGPSegmentInfo();
		gpadminController.disconnect();
		if (segments == null)
			return new ArrayList<GPSegmentInfo>();
		return segments;
	}

	/**
	 * 主机是否在集群中
	 * 
	 * @param host
	 * @param gp
	 * @return
	 */
	public GPSegmentInfo queryMaster(List<GPSegmentInfo> info) {
		for (GPSegmentInfo master : info) {
			if (master.getContent() == -1
					&& master.getRole().equals(GPSegmentInfo.ROLE_PRIMARY))
				return master;
		}
		return null;
	}

	/**
	 * 查询standby信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public GPSegmentInfo queryStandby(GPManagerEntity gp) {
		List<GPSegmentInfo> info = queryAllSegmentStatus(gp);
		for (GPSegmentInfo standby : info) {
			if (standby.getContent() == -1
					&& standby.getRole().equals(GPSegmentInfo.ROLE_MIRROR))
				return standby;
		}
		return null;
	}

	/**
	 * 查询standby信息
	 * 
	 * @param info
	 * @return
	 */
	public GPSegmentInfo queryStandby(List<GPSegmentInfo> info) {
		for (GPSegmentInfo standby : info) {
			if (standby.getContent() == -1
					&& standby.getRole().equals(GPSegmentInfo.ROLE_MIRROR))
				return standby;
		}
		return null;
	}

	/**
	 * 主机是否为standby主机
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean isStandby(GPManagerEntity gp, Host host) {
		List<GPSegmentInfo> info = queryAllSegmentStatus(gp);
		for (GPSegmentInfo standby : info) {
			if (standby.getContent() == -1
					&& standby.getRole().equals(GPSegmentInfo.ROLE_MIRROR)) {
				if (standby.getHostname().equals(host.getName()))
					return true;

			}
		}
		return false;
	}

	/**
	 * 获取mirror信息
	 * 
	 * @param gp
	 * @return
	 */
	public GPSegmentInfo queryMirror(GPManagerEntity gp) {
		List<GPSegmentInfo> info = queryAllSegmentStatus(gp);
		for (GPSegmentInfo mirror : info) {
			if (mirror.getContent() != -1
					&& mirror.getRole().equals(GPSegmentInfo.ROLE_MIRROR))
				return mirror;
		}
		return null;
	}

	/**
	 * 获取mirror信息
	 * 
	 * @param gp
	 * @return
	 */
	public GPSegmentInfo querySegment(List<GPSegmentInfo> info) {
		for (GPSegmentInfo segment : info) {
			if (segment.getContent() != -1
					&& segment.getRole().equals(GPSegmentInfo.ROLE_PRIMARY))
				return segment;
		}
		return null;
	}

	/**
	 * 获取mirror信息
	 * 
	 * @param gp
	 * @return
	 */
	public GPSegmentInfo queryMirror(List<GPSegmentInfo> info) {
		for (GPSegmentInfo mirror : info) {
			if (mirror.getContent() != -1
					&& mirror.getRole().equals(GPSegmentInfo.ROLE_MIRROR))
				return mirror;
		}
		return null;
	}

	/**
	 * 集群中是否有mirror
	 * 
	 * @param gp
	 * @return
	 */
	public boolean isHavMirror(GPManagerEntity gp) {
		List<GPSegmentInfo> info = queryAllSegmentStatus(gp);
		for (GPSegmentInfo standby : info) {
			if (standby.getContent() != -1
					&& standby.getRole().equals(GPSegmentInfo.ROLE_MIRROR))
				return true;
		}
		return false;
	}

	/**
	 * 集群中是否有standby
	 * 
	 * @param gp
	 * @return
	 */
	public boolean getStandby(GPManagerEntity gp) {
		List<GPSegmentInfo> info = queryAllSegmentStatus(gp);
		for (GPSegmentInfo standby : info) {
			if (standby.getContent() == -1
					&& standby.getRole().equals(GPSegmentInfo.ROLE_MIRROR)) {
				gp.setHasStandby(1);
				gp.setStandbyHostName(standby.getHostname());
				return true;
			}

		}
		return false;
	}

	/**
	 * 检查依赖软件
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public List<GPRequiredSw> checkSoft(final Host host) {
		List<GPRequiredSw> quiredSw = null;
		final IGPConnector gpController = new GPConnectorImpl(host);
		final GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			final String error = (connectRs.getOutputErr() != null ? connectRs
					.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			return quiredSw;
		}
		final GPResultSet rs = gpController.getEnvServiceProxy()
				.checkRequiredSoftware();
		quiredSw = rs.getRequiredSwList();
		gpController.disconnect();
		return quiredSw;

	}

	/**
	 * closedSelinux
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public String closedSelinux(Host host) {
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			return "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
		}
		GPResultSet rs = gpController.getEnvServiceProxy().closeSeLinux();
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue(
					"hostConfigure.success.closedSeLunux",
					new String[] { host.getIp() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.closedSeLunux",
					new String[] { host.getIp(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;
	}

	/**
	 * 关闭防火墙
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public String closeIptables(Host host) {
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			String errorMsg = "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
			return errorMsg;
		}
		GPResultSet rs = gpController.getEnvServiceProxy().closeIptables();
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue(
					"hostConfigure.success.closeIptables",
					new String[] { host.getIp() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.closeIptables",
					new String[] { host.getIp(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;

	}

	/**
	 * 修改预读扇区
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public String modifyBlockdev(Host host, int blockNumb) {
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			return "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
		}
		GPResultSet rs = gpController.getEnvServiceProxy().blockDev(blockNumb);
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("hostConfigure.success.blockdev",
					new String[] { host.getName() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.blockdev",
					new String[] { host.getName(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;
	}

	/**
	 * 修改最大进程限制
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public String modifynproc(Host host, String nprocNumb) {
		Properties p = new Properties();
		p.setProperty("*soft nproc", nprocNumb);
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			return "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
		}
		GPResultSet rs = gpController.getEnvServiceProxy()
				.updateSysctl_limits_90_nproc(null, null, p);
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("hostConfigure.success.nproc",
					new String[] { host.getName() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.nproc",
					new String[] { host.getName(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;
	}

	/**
	 * 修改sysctl文件
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public String modifySysctl(Host host, java.util.List<String> params) {
		Properties sysctlProps = new Properties();
		for (String param : params) {
			String[] p = param.split("=");
			sysctlProps.setProperty(p[0].trim(), p[1].trim());
		}
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			return "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
		}
		GPResultSet rs = gpController.getEnvServiceProxy()
				.updateSysctl_limits_90_nproc(sysctlProps, null, null);
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("hostConfigure.success.sysctl",
					new String[] { host.getName() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.sysctl",
					new String[] { host.getName(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;
	}

	/**
	 * 修改limit文件
	 * 
	 * @param display
	 * @param hosts
	 * @param params
	 * @param text
	 */
	public String modifyLimit(Host host, java.util.List<String> params) {
		Properties limitProps = new Properties();
		for (String param : params) {
			String[] p = param.split("=");
			limitProps.setProperty("*" + p[0].trim(), p[1].trim());
		}
		String msg = "";
		IGPConnector gpController = new GPConnectorImpl(host);
		GPResultSet connectRs = gpController.connect();
		boolean isConnect = connectRs.isSuccessed();
		if (!isConnect) {
			return "[ERROR:]"
					+ (connectRs.getOutputErr() != null ? connectRs
							.getOutputErr() : connectRs.getOutputEpt()) + "\n";
		}
		GPResultSet rs = gpController.getEnvServiceProxy()
				.updateSysctl_limits_90_nproc(null, limitProps, null);
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("hostConfigure.success.limit",
					new String[] { host.getName() });
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.limit",
					new String[] { host.getName(), getErrorMsg(rs) });
		}
		gpController.disconnect();
		return msg;
	}

	/**
	 * 验证是否为数字
	 * 
	 * @param value
	 * @return
	 */
	public boolean isInt(String value) {
		if (value == null || value.isEmpty())
			return false;
		try {
			int primaryNum = Integer.valueOf(value);
		} catch (NumberFormatException e1) {
			return false;
		}
		return true;
	}
}
