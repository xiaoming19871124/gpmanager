package com.txdb.gpmanage.manage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.gp.connector.GPConnectorImpl;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;
import com.txdb.gpmanage.core.gp.service.IGpExpandService;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;
import com.txdb.gpmanage.core.gp.service.IGpManageService;
import com.txdb.gpmanage.core.service.AbstractUIService;
import com.txdb.gpmanage.manage.entity.DatabaseEntity;
import com.txdb.gpmanage.manage.entity.GPConfigParam;
import com.txdb.gpmanage.manage.entity.ObjectAuth;
import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.entity.SchameEntity;
import com.txdb.gpmanage.manage.entity.SystenAuth;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 集群管理服务类
 * 
 * @author ws
 *
 */
public class ManageUiService extends AbstractUIService {
	public ManageUiService(Display display) {
		this.display = display;
	}

	/**
	 * 扩容segment
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public boolean initSegment(GPManagerEntity gp, List<Host> segHost, boolean isSpread, int numb, StyledText text, UICallBack callback) {
		boolean isNeedRollBack = false;
		boolean isSuccess = beforExpend(gp, segHost, false, text, callback);
		if (!isSuccess)
			return isNeedRollBack;
		// 生成配置文件
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet gpadminRs = gpadminController.connect();
		boolean isgpadminConnect = gpadminRs.isSuccessed();
		if (!isgpadminConnect) {
			setConnectionMsg(text, ResourceHandler.getValue("error", new String[] { (gpadminRs.getOutputErr() != null ? gpadminRs.getOutputErr() : gpadminRs.getOutputEpt()) + "\n" }));
			return isNeedRollBack;
		}
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return isNeedRollBack;
		}
		gpadminController.setCallback(callback);
		// List<String> dataDir = new ArrayList<String>();
		// for (int i = 0; i < numb; i++) {
		// dataDir.add(gp.getDatadir());
		// }
		// TODO
		String[] segmentDataDirs = new String[numb];
		String[] mirrorDdataDirs = new String[numb];
		String mirrorDir = (gp.getMirrorDataDir() == null || gp.getMirrorDataDir().isEmpty()) ? gp.getDatadir() + "mirror" : gp.getMirrorDataDir();
		for (int i = 0; i < numb; i++) {
			// String dataPrefixDir = dataDir.get(i);
			segmentDataDirs[i] = gp.getSegmentDataDir();
			mirrorDdataDirs[i] = mirrorDir;
		}
		GPResultSet eCfgFileRs = gpadminController.getExpandServiceProxy().genGpexpandInputfile(gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, segmentDataDirs, mirrorDdataDirs, isSpread);
		boolean isECfgFileSuccess = eCfgFileRs.isSuccessed();
		if (isECfgFileSuccess) {
			setMsg(text, isECfgFileSuccess, ResourceHandler.getValue("manage.createexpandfile.success"));
		} else {
			setMsg(text, isECfgFileSuccess, ResourceHandler.getValue("manage.createexpandfile.error", new String[] { getErrorMsg(eCfgFileRs) }));
			gpadminController.disconnect();
			return isNeedRollBack;
		}

		// 执行
		GPResultSet expandSegmentRs = gpadminController.getExpandServiceProxy().expandSegment();
		boolean isExpandSegmentSuccess = expandSegmentRs.isSuccessed();
		String msg = "";
		if (isExpandSegmentSuccess) {
			msg = ResourceHandler.getValue("segment.expand.success");
		} else {
			msg = ResourceHandler.getValue("segment.expand.error", new String[] { getErrorMsg(expandSegmentRs) });
			isNeedRollBack = true;
		}
		setMsg(text, isExpandSegmentSuccess, msg);
		gpadminController.disconnect();
		return isNeedRollBack;
	}

	/**
	 * 扩容standby
	 * 
	 * @param display
	 * @param hosts
	 * @param text
	 */
	public boolean expandStandby(GPManagerEntity gp, List<Host> standbyHost, StyledText text, UICallBack callback) {
		boolean isSuccess = false;
		if (standbyHost.get(0).isInGP()) {
			isSuccess = beforAddStandby(gp, standbyHost, text, callback);
		} else {
			isSuccess = beforExpend(gp, standbyHost, true, text, callback);

		}
		if (!isSuccess)
			return false;
		String msg = "";
		final IGPConnector evnController = new GPConnectorImpl(standbyHost.get(0));
		final GPResultSet evnConnectRs = evnController.connect();
		boolean isEvnConnect = evnConnectRs.isSuccessed();
		if (!isEvnConnect) {
			setConnectionMsg(text, ResourceHandler.getValue("error", new String[] { (evnConnectRs.getOutputErr() != null ? evnConnectRs.getOutputErr() : evnConnectRs.getOutputEpt()) + "\n" }));
			return false;
		}
		final GPResultSet setEnvRs = evnController.getInstallServiceProxy().configGpEnv(Integer.valueOf(gp.getGpPort()), gp.getInstallPath() + "greenplum-db", gp.getMasterDataDir() + "gpseg-1", gp.getGpUserName(), null);
		final boolean issetEnvSuccess = setEnvRs.isSuccessed();
		if (issetEnvSuccess) {
			msg = ResourceHandler.getValue("install.success.env");
		} else {
			msg = ResourceHandler.getValue("install.fail.env", new String[] { getErrorMsg(setEnvRs) });
		}
		setMsg(text, issetEnvSuccess, msg);
		evnController.disconnect();
		// 生成配置文件
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet gpadminRs = gpadminController.connect();
		boolean isgpadminConnect = gpadminRs.isSuccessed();
		if (!isgpadminConnect) {
			setConnectionMsg(text, ResourceHandler.getValue("error", new String[] { (gpadminRs.getOutputErr() != null ? gpadminRs.getOutputErr() : gpadminRs.getOutputEpt()) + "\n" }));
			return isgpadminConnect;
		}
		gpadminController.setCallback(callback);

		// 执行
		GPResultSet expandStandbyRs = gpadminController.getExpandServiceProxy().addStandby(standbyHost.get(0).getName(), standbyHost.get(0).getPassword());
		boolean isExpandStandbySuccess = expandStandbyRs.isSuccessed();
		if (!isExpandStandbySuccess) {
			msg = ResourceHandler.getValue("standby.expand.error", new String[] { getErrorMsg(expandStandbyRs) });
			setMsg(text, isExpandStandbySuccess, msg);
			gpadminController.disconnect();
			return isExpandStandbySuccess;
		}
		msg = ResourceHandler.getValue("standby.expand.success");
		setMsg(text, isExpandStandbySuccess, msg);
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 添加mirror
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public boolean addMirror(GPManagerEntity gp, boolean isSpread, StyledText text, UICallBack callback) {
		IGPConnector rootController = new GPConnectorImpl(gp.getMasterIp(), gp.getMasterRootName(), gp.getMasterRootPwd(), -1);
		GPResultSet rootConnectRs = rootController.connect();
		boolean isRootConnect = rootConnectRs.isSuccessed();
		String msg = "";
		if (!isRootConnect) {
			msg = "[ERROR:]" + (rootConnectRs.getOutputErr() != null ? rootConnectRs.getOutputErr() : rootConnectRs.getOutputEpt()) + "\n";
			setConnectionMsg(text, msg);
			return false;
		}
		rootController.setCallback(callback);
		boolean isJDBCSuccess = rootController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			rootController.disconnect();
			return isJDBCSuccess;
		}

		// 创建扩展配置文件
		GPResultSet expandFileRs = rootController.getExpandServiceProxy().createExpandNodeListFile(gp.getInstallPath(), new String[0]);
		boolean isExpanFileSuccess = expandFileRs.isSuccessed();

		if (isExpanFileSuccess) {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.success"));
		} else {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.error", new String[] { getErrorMsg(expandFileRs) }));
			rootController.disconnect();
			return isExpanFileSuccess;
		}

		// 创建数据目录
		// TODO
		String mirrorDir = (gp.getMirrorDataDir() == null || gp.getMirrorDataDir().isEmpty()) ? gp.getDatadir() + "mirror" : gp.getMirrorDataDir();
		final GPResultSet mirRs = rootController.getInstallServiceProxy().gpMakeDataDir(mirrorDir, gp.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST, gp.getGpUserName());
		boolean isPrepareWorkDirSuccess = mirRs.isSuccessed();

		if (isPrepareWorkDirSuccess) {
			setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.success.createdir"));
		} else {
			setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.fail.createdir", new String[] { getErrorMsg(mirRs) }));
			rootController.disconnect();
			return isPrepareWorkDirSuccess;
		}
		rootController.disconnect();
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();

		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return false;
		}
		boolean isConnectSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), null);
		if (!isConnectSuccess) {
			setMsg(text, isConnectSuccess, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return false;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery(
				"select count(role) as count from gp_segment_configuration where content>=0 group by hostname limit 1");
		long segmentNumb = 0;
		try {
			for (Map<String, Object> rowMap : dataList)
				segmentNumb = (long) rowMap.get("count");

		} catch (Exception e) {
			setMsg(text, false, ResourceHandler.getValue("mirror.checknumb.error", new String[] { e.getMessage() }));
			gpadminController.disconnect();
			return false;
		}
		boolean isSuccess = true;
		List<String> dataDir = new ArrayList<String>();
		for (int i = 0; i < segmentNumb; i++) {
			dataDir.add(gp.getDatadir() + "mirror");
		}
		gpadminController.setCallback(callback);
		GPResultSet addMirrorByCfgRs = gpadminController.getExpandServiceProxy().generateMirrorsCfg(dataDir.toArray(new String[dataDir.size()]), isSpread);
		boolean isAddMirrorByCfgSuccess = addMirrorByCfgRs.isSuccessed();
		if (isAddMirrorByCfgSuccess) {
			setMsg(text, isAddMirrorByCfgSuccess, ResourceHandler.getValue("manage.createexpandfile.success"));
		} else {
			setMsg(text, isAddMirrorByCfgSuccess, ResourceHandler.getValue("manage.createexpandfile.error", new String[] { getErrorMsg(addMirrorByCfgRs) }));
			gpadminController.disconnect();
			return false;
		}

		GPResultSet addMirrorRs = gpadminController.getExpandServiceProxy().addMirrorByCfg();
		boolean isAddMirrorSuccess = addMirrorRs.isSuccessed();
		if (isAddMirrorSuccess) {
			msg = ResourceHandler.getValue("miiror.open.success");
		} else {
			msg = ResourceHandler.getValue("miiror.open.error", new String[] { getErrorMsg(addMirrorRs) });
			isSuccess = false;
		}
		setMsg(text, isAddMirrorSuccess, msg);
		gpadminController.disconnect();
		return isSuccess;
	}

	/**
	 * 查询mirror状态
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public List<GPSegmentInfo> queryMirrorStatus(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<GPSegmentInfo> segmentInfo = new ArrayList<GPSegmentInfo>();
		List<GPSegmentInfo> segments = queryAllSegmentStatus(gp, text, callback);
		for (GPSegmentInfo info : segments) {
			if (info.getRole().equals(GPSegmentInfo.ROLE_MIRROR) && info.getContent() != -1)
				segmentInfo.add(info);
		}
		if (segmentInfo.size() < 1) {
			setMsg(text, false, ResourceHandler.getValue("mirrir_no_error"));
		}
		return segmentInfo;
	}

	/**
	 * 查询standby状态
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public GPSegmentInfo queryStandbyStatus(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<GPSegmentInfo> segments = queryAllSegmentStatus(gp, text, callback);
		GPSegmentInfo segmentInfo = null;
		for (GPSegmentInfo info : segments) {
			if (info.getRole().equals(GPSegmentInfo.ROLE_MIRROR) && info.getContent() == -1)
				segmentInfo = info;
		}
		return segmentInfo;
	}

	/**
	 * 查询所有节点状态
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<GPSegmentInfo> queryAllSegmentStatus(GPManagerEntity gp, StyledText text, UICallBack callback) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// String msg = "";
		// if (!isMasterConnect) {
		// msg = "[ERROR:]" + (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n";
		// setConnectionMsg(text, msg);
		// return new ArrayList<GPSegmentInfo>();
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.connect();
		boolean isConnectSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isConnectSuccess) {
			setMsg(text, isConnectSuccess, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return new ArrayList<GPSegmentInfo>();
		}
		gpadminController.setCallback(callback);
		List<GPSegmentInfo> segments = gpadminController.getManageServiceProxy().queryGPSegmentInfo();
		gpadminController.disconnect();
		if (segments == null)
			return new ArrayList<GPSegmentInfo>();
		return segments;

	}

	/**
	 * 扩容失败回滚
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<GPSegmentInfo> rollBack(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		List<GPSegmentInfo> segmentInfo = new ArrayList<GPSegmentInfo>();
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return segmentInfo;
		}
		gpadminController.getManageServiceProxy().gpStart("-R");
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getExpandServiceProxy().rollback();
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("rollback.success");
		} else {
			msg = ResourceHandler.getValue("rollback.error", new String[] { getErrorMsg(rs) });
		}
		setMsg(text, isSuccess, msg);
		gpadminController.disconnect();
		return segmentInfo;
	}

	/**
	 * 加载pg_hba.conf
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<PGHbaInfo> loadPGHba(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		List<PGHbaInfo> pgHbaInfo = new ArrayList<PGHbaInfo>();
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return pgHbaInfo;
		}
		gpadminController.setCallback(callback);
		pgHbaInfo = gpadminController.getManageServiceProxy().checkAuthority(gp.getMasterDataDir() + "gpseg-1");
		gpadminController.disconnect();
		return pgHbaInfo;
	}

	/**
	 * 重新加载pg_hba.conf
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<PGHbaInfo> reloadPGHba(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		List<PGHbaInfo> pgHbaInfo = new ArrayList<PGHbaInfo>();
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return pgHbaInfo;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStop("-u");
		boolean isSuccess = rs.isSuccessed();
		if (!isSuccess) {
			setMsg(text, isSuccess, ResourceHandler.getValue("reload.error", new String[] { getErrorMsg(rs) }));
			gpadminController.disconnect();
			return pgHbaInfo;
		}
		// TODO COPY TO STANDBY
		GPSegmentInfo standby = queryStandby(gp);
		if (standby != null) {
			GPResultSet scpRs = gpadminController.getInstallServiceProxy().gpScp(standby.getHostname(), gp.getMasterDataDir() + "gpseg-1");
			boolean isScpSuccess = scpRs.isSuccessed();
			if (isScpSuccess) {
				setMsg(text, isScpSuccess, ResourceHandler.getValue("sync_standby_success"));
			} else {
				setMsg(text, isScpSuccess, ResourceHandler.getValue("sync_standby_success", new String[] { getErrorMsg(scpRs) }));
			}
		}
		pgHbaInfo = gpadminController.getManageServiceProxy().checkAuthority(gp.getDatadir() + "master/gpseg-1");
		gpadminController.disconnect();
		setMsg(text, isSuccess, ResourceHandler.getValue("reload.success"));
		return pgHbaInfo;
	}

	/**
	 * 更新权限
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean updateAuthority(GPManagerEntity gp, List<PGHbaInfo> pgHbaInfo, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return isMasterConnect;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().updateAuthority(gp.getMasterDataDir() + "gpseg-1", pgHbaInfo);
		boolean isSuccess = rs.isSuccessed();
		if (!isSuccess) {
			setMsg(text, isSuccess, ResourceHandler.getValue("update.authority.error", new String[] { getErrorMsg(rs) }));
			gpadminController.disconnect();
			return isSuccess;
		}
		setMsg(text, isSuccess, ResourceHandler.getValue("update.authority.success"));
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 获取所有可配置的参数
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<GPConfig> queryAllParam(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<GPConfig> gpconfigs = new ArrayList<GPConfig>();
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return gpconfigs;
		}
		gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd()	, Integer.valueOf(gp.getGpPort()),gp.getGpdatabase());
		gpadminController.setCallback(callback);
		gpconfigs = gpadminController.getManageServiceProxy().gpconfigList();
		gpadminController.disconnect();
		return gpconfigs;
	}

	/**
	 * 查询可配置参数当前值
	 * 
	 * @param gp
	 * @param paramName
	 * @return
	 */
	public GPConfigValue queryParamValueByName(GPManagerEntity gp, String paramName) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			return null;
		}
		GPConfigValue gpConfigValue = gpadminController.getManageServiceProxy().gpconfig(paramName);
		gpadminController.disconnect();
		return gpConfigValue;
	}

	/**
	 * 设置参数
	 * 
	 * @param gp
	 * @param paramName
	 * @return
	 */
	public boolean updateParam(GPManagerEntity gp, List<GPConfigParam> params, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return false;
		}
		gpadminController.setCallback(callback);
		List<GPConfigParam> successList = new ArrayList<GPConfigParam>();
		for (GPConfigParam param : params) {
			GPResultSet rs = gpadminController.getManageServiceProxy().gpconfig(param.getGpconfig().getName(), param.getSegValue(), param.getMasterValue());
			boolean isSuccess = rs.isSuccessed();
			if (isSuccess) {
				setMsg(text, isSuccess, ResourceHandler.getValue("config_param_success", new String[] { param.getGpconfig().getName() }));
				successList.add(param);

			} else {
				setMsg(text, isSuccess, ResourceHandler.getValue("config_param_error", new String[] { param.getGpconfig().getName(), getErrorMsg(rs) }));
			}
		}
		// 重新加载
		GPResultSet reloadRs = gpadminController.getManageServiceProxy().gpStop("-u");
		boolean isReloadSuccess = reloadRs.isSuccessed();
		if (isReloadSuccess) {
			setMsg(text, isReloadSuccess, ResourceHandler.getValue("reload.success"));
		} else {
			setMsg(text, isReloadSuccess, ResourceHandler.getValue("reload.error", new String[] { getErrorMsg(reloadRs) }));
		}
		params.removeAll(successList);
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 查询所有表
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<GPExpandStatusDetail> gpExpandStatusDetailqueryTableByName(GPManagerEntity gp, String tableName, StyledText text, UICallBack callback) {
		List<GPExpandStatusDetail> gpExpandStatusDetail = new ArrayList<GPExpandStatusDetail>();
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return gpExpandStatusDetail;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return gpExpandStatusDetail;
		}

		if (tableName == null || tableName.isEmpty()) {
			gpExpandStatusDetail = gpadminController.getExpandServiceProxy().queryExpandStatusDetail();
		} else {
			gpExpandStatusDetail = gpadminController.getExpandServiceProxy().queryExpandStatusDetail(tableName);
		}
		gpadminController.disconnect();
		return gpExpandStatusDetail;
	}

	/**
	 * 更新表分布级别
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean updateTableRank(GPManagerEntity gp, List<GPExpandStatusDetail> details, StyledText text, UICallBack callback) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return false;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return false;
		}
		for (GPExpandStatusDetail detail : details) {
			boolean isSuccess = gpadminController.getExpandServiceProxy().updateExpandRank(detail.getDbname(), detail.getSchema_name() + "." + detail.getFq_name(), detail.getRank());
			if (isSuccess) {
				msg = ResourceHandler.getValue("table.rank.success", new String[] { detail.getDbname() + "." + detail.getSchema_name() + "." + detail.getFq_name(), detail.getRank() + "" });
			} else {
				msg = ResourceHandler.getValue("table.rank.fail", new String[] { detail.getDbname() + "." + detail.getSchema_name() + "." + detail.getFq_name(), detail.getRank() + "" });
			}
			setMsg(text, isSuccess, msg);
		}
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 执行重分布
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void exeExpand(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet redistributeRs = gpadminController.getExpandServiceProxy().redistribute();
		boolean isRedistributeSuccess = redistributeRs.isSuccessed();
		String msg = "";
		if (isRedistributeSuccess) {
			msg = ResourceHandler.getValue("redistribution.success");
		} else {
			msg = ResourceHandler.getValue("redistribution.error", new String[] { getErrorMsg(redistributeRs) });
		}
		gpadminController.disconnect();
		setMsg(text, isRedistributeSuccess, msg);
	}

	/**
	 * 执行重分布
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void exeExpand(GPManagerEntity gp, StyledText text, String time, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet redistributeRs = gpadminController.getExpandServiceProxy().redistribute(time);
		boolean isRedistributeSuccess = redistributeRs.isSuccessed();
		String msg = "";
		if (isRedistributeSuccess) {
			msg = ResourceHandler.getValue("redistribution.success");
		} else {
			msg = ResourceHandler.getValue("redistribution.error", new String[] { getErrorMsg(redistributeRs) });
		}
		gpadminController.disconnect();
		setMsg(text, isRedistributeSuccess, msg);
	}

	/**
	 * 清除扩展SCHAME
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void cleanSchame(GPManagerEntity gp, StyledText text, UICallBack callback) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet redistributeRs = gpadminController.getExpandServiceProxy().expandCheanSchema();
		boolean isRedistributeSuccess = redistributeRs.isSuccessed();
		if (isRedistributeSuccess) {
			msg = ResourceHandler.getValue("clean.schema.success");
		} else {
			msg = ResourceHandler.getValue("clean.schema.error", new String[] { getErrorMsg(redistributeRs) });
		}
		gpadminController.disconnect();
		setMsg(text, isRedistributeSuccess, msg);
	}

	/**
	 * 启动集群
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void startGp(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStart("-a");
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("gp.start.success");
		} else {
			msg = ResourceHandler.getValue("gp.start.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 启动集群
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public boolean startGp(String ip, String userName, String password) {
		IGPConnector gpadminController = new GPConnectorImpl(ip, userName, password, -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			return isMasterConnect;
		}
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStart("-a");
		boolean isSuccess = rs.isSuccessed();
		gpadminController.disconnect();
		return isSuccess;
	}

	/**
	 * 停止集群
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void stopGp(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStop();
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("gp.stop.success");
		} else {
			msg = ResourceHandler.getValue("gp.stop.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 重启集群
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void restartGp(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStop("-r");
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("gp.restart.success");
		} else {
			ResourceHandler.getValue("gp.restart.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 启动master
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void startMasterGp(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpStart("-m");
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("gp.startmaster.success");
		} else {
			msg = ResourceHandler.getValue("gp.startmaster.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 检测集群状态
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void stateGp(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpState();
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("gp.state.success");
		} else {
			msg = ResourceHandler.getValue("gp.state.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 检查失败的segment\mirror
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void checkStatus(GPManagerEntity gp, StyledText text, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpState("-e");
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue("segment.state.success");
		} else {
			msg = ResourceHandler.getValue("segment.state.error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 修复失败的segment\mirror
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public void recoverSeg(GPManagerEntity gp, StyledText text, String param, String operation, UICallBack callback) {
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = gpadminController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		String msg = "";
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return;
		}
		gpadminController.setCallback(callback);
		GPResultSet rs = null;
		if (param == null) {
			rs = gpadminController.getManageServiceProxy().gpRecoverSeg();

		} else {
			rs = gpadminController.getManageServiceProxy().gpRecoverSeg(param);
		}
		boolean isSuccess = rs.isSuccessed();
		if (isSuccess) {
			msg = ResourceHandler.getValue(operation + ".success");
		} else {
			msg = ResourceHandler.getValue(operation + ".error", new String[] { getErrorMsg(rs) });
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, msg);
	}

	/**
	 * 切到standby
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 */
	public boolean activateStandby(GPManagerEntity gp, StyledText text, UICallBack callback) {
		// gp管理用户连接到master
		IGPConnector masterController = new GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet masterConnectRs = masterController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return isMasterConnect;
		}
		// 停止集群
		masterController.setCallback(callback);
		masterController.getManageServiceProxy().gpStop();
		masterController.disconnect();
		// gp管理用户连接到standby
		IGPConnector gpadminController = new GPConnectorImpl(gp.getStandbyIp(), gp.getGpUserName(), gp.getGpUserPwd(), -1);
		GPResultSet standbyConnectRs = gpadminController.connect();
		boolean isstandbyConnect = standbyConnectRs.isSuccessed();
		if (!isstandbyConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (standbyConnectRs.getOutputErr() != null ? standbyConnectRs.getOutputErr() : standbyConnectRs.getOutputEpt()) + "\n" }));
			return isstandbyConnect;
		}
		// 切换到standby
		gpadminController.setCallback(callback);
		GPResultSet rs = gpadminController.getManageServiceProxy().gpActivateStandby(Integer.valueOf(gp.getGpPort()), gp.getMasterDataDir() + "gpseg-1");
		boolean isSuccess = rs.isSuccessed();
		if (!isSuccess) {
			setMsg(text, isSuccess, ResourceHandler.getValue("activateStandby.error", new String[] { getErrorMsg(rs) }));
			gpadminController.disconnect();
			return isSuccess;
		}
		gpadminController.disconnect();
		setMsg(text, isSuccess, ResourceHandler.getValue("activateStandby.success"));
		return true;
	}

	/**
	 * 新节点安装gp
	 * 
	 * @param gp
	 * @param segHost
	 * @param isSpread
	 * @param numb
	 * @param text
	 * @param callback
	 * @return
	 */
	private boolean beforExpend(GPManagerEntity gp, List<Host> segHost, boolean isStandby, StyledText text, UICallBack callback) {
		Map<String, String[]> hostMap = new HashMap<String, String[]>();
		List<String> new_host = new ArrayList<String>();
		Map<String, String> passwordMap = new HashMap<String, String>();
		for (Host host : segHost) {
			hostMap.put(host.getIp(), new String[] { host.getName() });
			new_host.add(host.getName());
			passwordMap.put(host.getName(), host.getPassword());
		}
		// 修改hosts文件
		// 修改master节点hosts文件
		String masterName = gp.getMasterHostName();
		String masterIp = gp.getMasterIp();
		String masterRootName = gp.getMasterRootName();
		String masterRootPwd = gp.getMasterRootPwd();
		IGPConnector masterController = new GPConnectorImpl(masterIp, masterRootName, masterRootPwd, -1);
		GPResultSet masterConnectRs = masterController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return isMasterConnect;
		}
		masterController.setCallback(callback);
		String msg = "";
		final GPResultSet masterRs = masterController.getEnvServiceProxy().updateHosts(hostMap);
		boolean isMastrSuccess = masterRs.isSuccessed();
		if (isMastrSuccess) {
			msg = ResourceHandler.getValue("hostConfigure.success.hosts", new String[] { masterName });
			setMsg(text, isMastrSuccess, msg);
		} else {
			msg = ResourceHandler.getValue("hostConfigure.fail.hosts", new String[] { masterName, getErrorMsg(masterRs) });
			setMsg(text, isMastrSuccess, msg);
			masterController.disconnect();
			return isMastrSuccess;
		}

		// masterController.disconnect();
		// 修改新机器节点hosts文件
		hostMap.put(masterIp, new String[] { masterName });
		for (Host host : segHost) {
			IGPConnector gpController = new GPConnectorImpl(host);
			GPResultSet connectRs = gpController.connect();
			boolean isConnect = connectRs.isSuccessed();
			if (!isConnect) {
				setConnectionMsg(text, ResourceHandler.getValue("error", new String[] { (connectRs.getOutputErr() != null ? connectRs.getOutputErr() : connectRs.getOutputEpt()) + "\n" }));
				return isConnect;
			}
			gpController.setCallback(callback);
			// 创建用户
			final GPResultSet createUserRS = gpController.getEnvServiceProxy().createOrReplaceGpUser(gp.getGpUserName(), gp.getGpUserPwd());
			final boolean iscreateUserSuccess = createUserRS.isSuccessed();
			if (iscreateUserSuccess) {
				msg = ResourceHandler.getValue("install.success.user", new String[] { host.getName(), gp.getGpUserName() });
			} else {
				msg = ResourceHandler.getValue("install.fail.user", new String[] { host.getName(), gp.getGpUserName(), getErrorMsg(createUserRS) });
			}
			setMsg(text, iscreateUserSuccess, msg);
			gpController.disconnect();
		}
		boolean isJDBCSuccess = masterController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			masterController.disconnect();
			return isJDBCSuccess;
		}

		// 创建all_host(expand_host)文件
		GPResultSet expandFileRs = masterController.getExpandServiceProxy().createExpandNodeListFile(gp.getInstallPath(), new_host.toArray(new String[new_host.size()]));
		boolean isExpanFileSuccess = expandFileRs.isSuccessed();

		if (isExpanFileSuccess) {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.success"));
		} else {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.error", new String[] { getErrorMsg(expandFileRs) }));
			masterController.disconnect();
			return isExpanFileSuccess;
		}

		// 交换key
		GPResultSet sshExKeysRs = masterController.getExpandServiceProxy().expandSSHExKeys(gp.getInstallPath(), passwordMap);
		boolean isSSHExKeysSuccess = sshExKeysRs.isSuccessed();
		if (isSSHExKeysSuccess) {
			setMsg(text, isSSHExKeysSuccess, ResourceHandler.getValue("ssh.success.exchangKey"));
		} else {
			setMsg(text, isSSHExKeysSuccess, ResourceHandler.getValue("ssh.fail.exchangKey", new String[] { getErrorMsg(sshExKeysRs) }));
			masterController.disconnect();
			return isSSHExKeysSuccess;
		}
		// 同步hosts文件
		GPResultSet hostsFileRs = masterController.getInstallServiceProxy().gpScp(gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, "/etc/hosts");
		GPResultSet hostsAllRs = masterController.getInstallServiceProxy().gpScp(gp.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST, "/etc/hosts");
		boolean isHostsFileSuccess = hostsFileRs.isSuccessed();
		boolean isHostsAllSuccess = hostsAllRs.isSuccessed();
		if (isHostsFileSuccess && isHostsAllSuccess) {
			setMsg(text, isHostsFileSuccess, ResourceHandler.getValue("sync.hosts.success"));
		} else {
			if (!isHostsFileSuccess) {
				setMsg(text, isHostsFileSuccess, ResourceHandler.getValue("sync.hosts.error", new String[] { getErrorMsg(hostsFileRs) }));
				masterController.disconnect();
				return isHostsFileSuccess;
			} else if (!isHostsAllSuccess) {
				setMsg(text, isHostsAllSuccess, ResourceHandler.getValue("sync.hosts.error", new String[] { getErrorMsg(hostsAllRs) }));
				masterController.disconnect();
				return isHostsAllSuccess;
			}
		}
		// 创建数据目录
		// TODO
		if (isStandby) {
			final GPResultSet prepareWorkDirRs = masterController.getInstallServiceProxy().gpMakeDataDir(gp.getMasterDataDir(), gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND,
					gp.getGpUserName());
			boolean isPrepareWorkDirSuccess = prepareWorkDirRs.isSuccessed();
			if (isPrepareWorkDirSuccess) {
				setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.success.createdir"));
			} else {
				setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.fail.createdir", new String[] { getErrorMsg(prepareWorkDirRs) }));
				masterController.disconnect();
				return isPrepareWorkDirSuccess;
			}
		} else {
			String mirrorDir = (gp.getMirrorDataDir() == null || gp.getMirrorDataDir().isEmpty()) ? gp.getDatadir() : gp.getMirrorDataDir();
			final GPResultSet segRs = masterController.getInstallServiceProxy().gpMakeDataDir(gp.getSegmentDataDir(), gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, gp.getGpUserName());
			final GPResultSet mirRs = masterController.getInstallServiceProxy().gpMakeDataDir(mirrorDir, gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, gp.getGpUserName());
			boolean isPrepareWorkDirSuccess = segRs.isSuccessed() && mirRs.isSuccessed();
			if (isPrepareWorkDirSuccess) {
				setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.success.createdir"));
			} else {
				GPResultSet rs = null;
				if (!segRs.isSuccessed())
					rs = segRs;
				else
					rs = mirRs;
				setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.fail.createdir", new String[] { getErrorMsg(rs) }));
				masterController.disconnect();
				return isPrepareWorkDirSuccess;
			}
		}

		// 安装gp
		GPResultSet segInstallRs = masterController.getInstallServiceProxy().gpSegInstall(gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, gp.getGpUserName(), gp.getGpUserPwd());
		boolean isSegInstallSuccess = segInstallRs.isSuccessed();
		if (isSegInstallSuccess) {
			msg = "[INFO:]gp安装成功!";
			setMsg(text, isSegInstallSuccess, ResourceHandler.getValue("install.success.segment"));
		} else {
			setMsg(text, isSegInstallSuccess, ResourceHandler.getValue("install.fail.segment", new String[] { getErrorMsg(segInstallRs) }));
			masterController.disconnect();
			return isSegInstallSuccess;
		}
		masterController.disconnect();
		return true;
	}

	/**
	 * 新节点安装gp
	 * 
	 * @param gp
	 * @param segHost
	 * @param isSpread
	 * @param numb
	 * @param text
	 * @param callback
	 * @return
	 */
	private boolean beforAddStandby(GPManagerEntity gp, List<Host> segHost, StyledText text, UICallBack callback) {
		Map<String, String[]> hostMap = new HashMap<String, String[]>();
		List<String> new_host = new ArrayList<String>();
		Map<String, String> passwordMap = new HashMap<String, String>();
		for (Host host : segHost) {
			hostMap.put(host.getIp(), new String[] { host.getName() });
			new_host.add(host.getName());
			passwordMap.put(host.getName(), host.getPassword());
		}
		String masterIp = gp.getMasterIp();
		String masterRootName = gp.getMasterRootName();
		String masterRootPwd = gp.getMasterRootPwd();
		IGPConnector masterController = new GPConnectorImpl(masterIp, masterRootName, masterRootPwd, -1);
		GPResultSet masterConnectRs = masterController.connect();
		boolean isMasterConnect = masterConnectRs.isSuccessed();
		if (!isMasterConnect) {
			setConnectionMsg(text,
					ResourceHandler.getValue("error", new String[] { (masterConnectRs.getOutputErr() != null ? masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) + "\n" }));
			return isMasterConnect;
		}
		masterController.setCallback(callback);
		boolean isJDBCSuccess = masterController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			masterController.disconnect();
			return isJDBCSuccess;
		}
		// 创建all_host(expand_host)文件
		GPResultSet expandFileRs = masterController.getExpandServiceProxy().createExpandNodeListFile(gp.getInstallPath(), new_host.toArray(new String[new_host.size()]));
		boolean isExpanFileSuccess = expandFileRs.isSuccessed();

		if (isExpanFileSuccess) {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.success"));
		} else {
			setMsg(text, isExpanFileSuccess, ResourceHandler.getValue("mirror.createhostfile.error", new String[] { getErrorMsg(expandFileRs) }));
			masterController.disconnect();
			return isExpanFileSuccess;
		}
		// 同步hosts文件
		GPResultSet hostsFileRs = masterController.getInstallServiceProxy().gpScp(gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND, "/etc/hosts");
		GPResultSet hostsAllRs = masterController.getInstallServiceProxy().gpScp(gp.getInstallPath() + IGpInstallService.DEFAULT_ALL_HOST, "/etc/hosts");
		boolean isHostsFileSuccess = hostsFileRs.isSuccessed();
		boolean isHostsAllSuccess = hostsAllRs.isSuccessed();
		if (isHostsFileSuccess && isHostsAllSuccess) {
			setMsg(text, isHostsFileSuccess, ResourceHandler.getValue("sync.hosts.success"));
		} else {
			if (!isHostsFileSuccess) {
				setMsg(text, isHostsFileSuccess, ResourceHandler.getValue("sync.hosts.error", new String[] { getErrorMsg(hostsFileRs) }));
				masterController.disconnect();
				return isHostsFileSuccess;
			} else if (!isHostsAllSuccess) {
				setMsg(text, isHostsAllSuccess, ResourceHandler.getValue("sync.hosts.error", new String[] { getErrorMsg(hostsAllRs) }));
				masterController.disconnect();
				return isHostsAllSuccess;
			}
		}
		// 创建数据目录
		// TODO
		final GPResultSet prepareWorkDirRs = masterController.getInstallServiceProxy().gpMakeDataDir(gp.getMasterDataDir(), gp.getInstallPath() + IGpExpandService.DEFAULT_HOST_EXPAND,
				gp.getGpUserName());
		boolean isPrepareWorkDirSuccess = prepareWorkDirRs.isSuccessed();

		if (isPrepareWorkDirSuccess) {
			setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.success.createdir"));
		} else {
			setMsg(text, isPrepareWorkDirSuccess, ResourceHandler.getValue("instance.fail.createdir", new String[] { getErrorMsg(prepareWorkDirRs) }));
			masterController.disconnect();
			return isPrepareWorkDirSuccess;
		}

		masterController.disconnect();
		return true;
	}

	/**
	 * 查询所有表
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<GPExpandStatusDetail> queryTables(GPManagerEntity gp, List<DatabaseEntity> condision, long l, int limit, StyledText text, UICallBack callback) {
		List<GPExpandStatusDetail> statusDetailList = new ArrayList<GPExpandStatusDetail>();
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return statusDetailList;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return statusDetailList;
		}
		String sql = "select * from gpexpand.status_detail ";
		String condisionStr = "";
		for (int i = 0; i < condision.size(); i++) {
			DatabaseEntity db = condision.get(i);
			condisionStr = condisionStr + db.toSql() + " or ";
			if (i == condision.size() - 1)
				condisionStr = condisionStr + db.toSql();
		}
		if (!condisionStr.isEmpty())
			sql = sql + "where (" + condisionStr + ")";

		sql = sql + "limit " + limit + " OFFSET " + l;
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery(sql);
		GPExpandStatusDetail statusDetail = null;
		for (Map<String, Object> rowMap : dataList) {
			statusDetail = new GPExpandStatusDetail();
			statusDetail.setDbname((String) rowMap.get("dbname"));
			statusDetail.setFq_name((String) rowMap.get("fq_name"));
			statusDetail.setSchema_oid((long) rowMap.get("schema_oid"));
			statusDetail.setTable_oid((long) rowMap.get("table_oid"));
//			statusDetail.setDistribution_policy((String) rowMap.get("distribution_policy"));
//			statusDetail.setDistribution_policy_names((String) rowMap.get("distribution_policy_names"));
//			statusDetail.setDistribution_policy_coloids((String) rowMap.get("distribution_policy_coloids"));
			// statusDetail.setDistribution_policy_type((String)
			// sqlRs.get("distribution_policy_type"));
//			statusDetail.setStorage_options((String) rowMap.get("storage_options"));
			statusDetail.setRank((int) rowMap.get("rank"));
			statusDetail.setStatus((String) rowMap.get("status"));
//			statusDetail.setExpansion_started((String) rowMap.get("expansion_started"));
//			statusDetail.setExpansion_finished((String) rowMap.get("expansion_finished"));
//			statusDetail.setSource_bytes((int) rowMap.get("source_bytes"));
			statusDetailList.add(statusDetail);
		}
		gpadminController.disconnect();
		return statusDetailList;
	}

	/**
	 * 查询所有表
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public long queryTableItem(GPManagerEntity gp, List<DatabaseEntity> condision, StyledText text, UICallBack callback) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return 0;
		// }
		// gpadminController.setCallback(callback);
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return 0;
		}
		String sql = "select count(*) as count from gpexpand.status_detail ";
		String condisionStr = "";
		for (int i = 0; i < condision.size(); i++) {
			DatabaseEntity db = condision.get(i);
			if (i == condision.size() - 1)
				condisionStr = condisionStr + db.toSql();
			else
				condisionStr = condisionStr + db.toSql() + " or ";
		}
		if (!condisionStr.isEmpty())
			sql = sql + "where " + condisionStr;

		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery(sql);
		long total = 0;
		if(dataList==null){
			return total;
		}
		for (Map<String, Object> rowMap : dataList)
			total = (long) rowMap.get("count");

		gpadminController.disconnect();
		return total;
	}

	/**
	 * 查询所有库
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<DatabaseEntity> queryAllDatabase(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<DatabaseEntity> databaseEntities = new ArrayList<DatabaseEntity>();

		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return databaseEntities;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return databaseEntities;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery("select distinct dbname from gpexpand.status_detail");
		for (Map<String, Object> rowMap : dataList) {
			String dbName = (String) rowMap.get("dbname");
			DatabaseEntity entity = new DatabaseEntity();
			entity.setName(dbName);
			databaseEntities.add(entity);
		}
		gpadminController.disconnect();
		return databaseEntities;
	}

	/**
	 * 查询所有表
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<SchameEntity> queryAllSchemaByDbName(GPManagerEntity gp, String dbName) {
		List<SchameEntity> databaseEntities = new ArrayList<SchameEntity>();

		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// return databaseEntities;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), IGpManageService.DEFAULT_EXPAND_DB);
		if (!isJDBCSuccess) {
			gpadminController.disconnect();
			return databaseEntities;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery(" select distinct schema_oid from gpexpand.status_detail where dbname='" + dbName + "'");
		List<Long> schemaOid = new ArrayList<Long>();
		for (Map<String, Object> rowMap : dataList) {
			long schame_oid = (long) rowMap.get("schema_oid");
			schemaOid.add(schame_oid);
		}
		if (schemaOid.size() < 1) {
			gpadminController.disconnect();
			return databaseEntities;
		}
		boolean isConnectDbSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), dbName);
		if (!isConnectDbSuccess) {
			gpadminController.disconnect();
			return databaseEntities;
		}
		String sql = "select nspname from pg_namespace where oid in(";
		for (Long oid : schemaOid) {
			sql = sql + oid + ",";
		}
		sql = sql.substring(0, sql.lastIndexOf(","));
		sql = sql + ")";
		List<Map<String, Object>> schemaList = gpadminController.getManageServiceProxy().executeQuery(sql);
		for (Map<String, Object> rowMap : schemaList) {
			String schameName = (String) rowMap.get("nspname");
			SchameEntity schema = new SchameEntity();
			schema.setName(schameName);
			databaseEntities.add(schema);
		}
		gpadminController.disconnect();
		return databaseEntities;
	}

	public static void main(String[] args) {
		String a = "aaa,bbb,ccc,";
		System.out.println(a.substring(0, a.length() - 1));
	}
public List<Policy> loadPollicy(GPManagerEntity gp, StyledText text, UICallBack callback){
	List<Policy> policys = new ArrayList<Policy>();
	IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
	gpadminController.connect();
	gpadminController.setCallback(callback);
	boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
	if (!isJDBCSuccess) {
		setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
		return policys;
	}
	List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery("select policy_name,policy_col_name,policy_col_hidden,policy_enable from mac_policy");
	for (Map<String, Object> rowMap : dataList) {
		String name = (String) rowMap.get("policy_name");
		String policy_col_name = (String) rowMap.get("policy_col_name");
		boolean hide = (boolean) rowMap.get("policy_col_hidden");
		boolean enable = (boolean) rowMap.get("policy_enable");
		Policy policy = new Policy();
		policy.setName(name);
		policy.setColumn(policy_col_name);
		policy.setEnable(enable);
		policy.setHide(hide);
		policys.add(policy);
	}
	gpadminController.disconnect();
	return policys;
	
}
	/**
	 * 加载所有用户信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<DbUser> loadUser(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<DbUser> dbUsers = new ArrayList<DbUser>();
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return dbUsers;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return dbUsers;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery("select usename,usesysid,usecreatedb,usesuper,usecatupd,passwd,valuntil,useconfig from pg_user");
		for (Map<String, Object> rowMap : dataList) {
			String userName = (String) rowMap.get("usename");
			int usesysid = Integer.parseInt(rowMap.get("usesysid") + "");
			boolean usecreatedb = (boolean) rowMap.get("usecreatedb");
			String usesuper = (String) rowMap.get("usesuper");
			boolean usecatupd = (boolean) rowMap.get("usecatupd");
			// boolean userepl = rs.getBoolean("userepl");
			String pwd = (String) rowMap.get("passwd");
			String valuntil = (String) rowMap.get("valuntil");
			String useconfig = (String) rowMap.get("useconfig");
			DbUser user = new DbUser();
			user.setUsecatupd(usecatupd);
			user.setUseconfig(useconfig);
			user.setUsecreatedb(usecreatedb);
			// user.setUserepl(userepl);
			user.setUserName(userName);
			user.setUserPwd(pwd);
			user.setUsesuper(usesuper);
			user.setUsesysid(usesysid);
			user.setValuntil(valuntil);
			dbUsers.add(user);
		}
		gpadminController.disconnect();
		return dbUsers;

	}
	/**
	 * 加载所有用户信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<DbUser> loadUser(GPManagerEntity gp) {
		List<DbUser> dbUsers = new ArrayList<DbUser>();
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			return dbUsers;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery("select usename,usesysid,usecreatedb,usesuper,usecatupd,passwd,valuntil,useconfig from pg_user");
		for (Map<String, Object> rowMap : dataList) {
			String userName = (String) rowMap.get("usename");
			int usesysid = Integer.parseInt(rowMap.get("usesysid") + "");
			boolean usecreatedb = (boolean) rowMap.get("usecreatedb");
			String usesuper = (String) rowMap.get("usesuper");
			boolean usecatupd = (boolean) rowMap.get("usecatupd");
			// boolean userepl = rs.getBoolean("userepl");
			String pwd = (String) rowMap.get("passwd");
			String valuntil = (String) rowMap.get("valuntil");
			String useconfig = (String) rowMap.get("useconfig");
			DbUser user = new DbUser();
			user.setUsecatupd(usecatupd);
			user.setUseconfig(useconfig);
			user.setUsecreatedb(usecreatedb);
			// user.setUserepl(userepl);
			user.setUserName(userName);
			user.setUserPwd(pwd);
			user.setUsesuper(usesuper);
			user.setUsesysid(usesysid);
			user.setValuntil(valuntil);
			dbUsers.add(user);
		}
		gpadminController.disconnect();
		return dbUsers;

	}
	/**
	 * 加载用户权限信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<ObjectAuth> loadAuth(GPManagerEntity gp, StyledText text, UICallBack callback) {
		List<ObjectAuth> userAuth = new ArrayList<ObjectAuth>();
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return userAuth;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return userAuth;
		}
		List<Map<String, Object>> tableAuthList = gpadminController.getManageServiceProxy().executeQuery(
				"select grantor,grantee,table_catalog,table_schema,table_name,privilege_type,is_grantable from information_schema.table_privileges where grantee='" + gp.getGpUserName() + "'");
		if (tableAuthList != null) {
			for (Map<String, Object> rowMap : tableAuthList) {
				ObjectAuth auth = new ObjectAuth();
				auth.setGrantor((String) rowMap.get("grantor"));
				auth.setGrantee((String) rowMap.get("grantee"));
				auth.setDbName((String) rowMap.get("table_catalog"));
				auth.setObjectName((String) rowMap.get("table_name"));
				auth.setSchemaName((String) rowMap.get("table_schema"));
				auth.setPrivilege_type((String) rowMap.get("privilege_type"));
				auth.setIs_grantable((String) rowMap.get("is_grantable"));
				auth.setObject_type("TABLE");
				userAuth.add(auth);
			}
		}
		List<Map<String, Object>> otherList = gpadminController.getManageServiceProxy().executeQuery(
				"select grantor,grantee,object_catalog,object_schema,object_name,object_type,privilege_type,is_grantable from information_schema.usage_privileges where grantee='" + gp.getGpUserName()
						+ "'");
		if (otherList != null) {
			for (Map<String, Object> rowMap : otherList) {
				ObjectAuth auth = new ObjectAuth();
				auth.setGrantor((String) rowMap.get("grantor"));
				auth.setGrantee((String) rowMap.get("grantee"));
				auth.setDbName((String) rowMap.get("object_catalog"));
				auth.setObjectName((String) rowMap.get("object_name"));
				auth.setSchemaName((String) rowMap.get("object_schema"));
				auth.setPrivilege_type((String) rowMap.get("privilege_type"));
				auth.setIs_grantable((String) rowMap.get("is_grantable"));
				auth.setObject_type((String) rowMap.get("object_type"));
				userAuth.add(auth);
			}
		}
		List<Map<String, Object>> functionList = gpadminController.getManageServiceProxy().executeQuery(
				"select grantor,grantee,routine_catalog,routine_schema,routine_name,privilege_type,is_grantable from information_schema.routine_privileges where grantee='" + gp.getGpUserName() + "'");
		if (functionList != null) {
			for (Map<String, Object> rowMap : functionList) {
				ObjectAuth auth = new ObjectAuth();
				auth.setGrantor((String) rowMap.get("grantor"));
				auth.setGrantee((String) rowMap.get("grantee"));
				auth.setDbName((String) rowMap.get("routine_catalog"));
				auth.setObjectName((String) rowMap.get("routine_name"));
				auth.setSchemaName((String) rowMap.get("routine_schema"));
				auth.setPrivilege_type((String) rowMap.get("privilege_type"));
				auth.setIs_grantable((String) rowMap.get("is_grantable"));
				auth.setObject_type("FUNCTION");
				userAuth.add(auth);
			}
		}
		gpadminController.disconnect();
		return userAuth;

	}

	/**
	 * 赋权
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public void grantAuth(GPManagerEntity gp, String dbName, SystenAuth sysAuth, List<ObjectAuth> auths, List<Object> users, StyledText text, UICallBack callback) {
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), dbName);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return;
		}
		if (sysAuth.isSuper()) {
			for (Object user : users) {
				String sql = "ALTER USER " + ((DbUser) user).getUserName() + " with SUPERUSER";
				int isSuccess = gpadminController.getManageServiceProxy().executeUpdate(sql);
				if (isSuccess != -1)
					setMsg(text, true, "[INFO:]" + sql + " success\n");
				else
					setMsg(text, false, "[ERROR:]" + sql + " fail\n");
			}
			return;
		}
		String sysAuthStr = "ALTER USER $user" + " with" + (sysAuth.isRolcanlogin() ? " LOGIN" : "") + (sysAuth.isRolcreatedb() ? " CREATEDB" : "") + (sysAuth.isRolcreaterole() ? " CREATEROLE" : "")
				+ (sysAuth.isRolinherit() ? " INHERIT" : "") + (sysAuth.isRolreplication() ? " REPLICATION" : "");
		if (!sysAuthStr.endsWith("with")) {
			for (Object user : users) {
				sysAuthStr = sysAuthStr.replace("$user", ((DbUser) user).getUserName());
				int isSuccess = gpadminController.getManageServiceProxy().executeUpdate(sysAuthStr);
				if (isSuccess != -1)
					setMsg(text, true, "[INFO:]" + sysAuthStr + " success\n");
				else
					setMsg(text, false, "[ERROR:]" + sysAuthStr + " fail\n");
			}
		}
		String userStr = "";
		for (Object user : users) {
			userStr += (((DbUser) user).getUserName() + ",");
		}
		userStr = userStr.substring(0, userStr.length() - 1);
		if (auths.size() > 0) {
			for (ObjectAuth auth : auths) {
				String grantSql = "";
				int isSuccess = -1;
				if (auth.getObject_type().toLowerCase().equals("function")) {
					grantSql = "grant " + auth.getPrivilege_type() + " on ";

					String querySql = "SELECT format('%s %I.%I(%s)', CASE WHEN p.proisagg THEN 'AGGREGATE' ELSE 'FUNCTION' END, n.nspname , p.proname, pg_catalog.pg_get_function_identity_arguments(p.oid)) AS stmt FROM   pg_catalog.pg_proc p JOIN   pg_catalog.pg_namespace n ON n.oid = p.pronamespace WHERE  p.proname = '"
							+ auth.getObjectName() + "'   AND n.nspname = '" + auth.getSchemaName() + "'";
					List<Map<String, Object>> authList = gpadminController.getManageServiceProxy().executeQuery(querySql);
					String stmt = "";
					for (Map<String, Object> rowMap : authList) {
						stmt = (String) rowMap.get("stmt");
					}
					grantSql += stmt;
					grantSql += (" TO " + userStr);
					isSuccess = gpadminController.getManageServiceProxy().executeUpdate(grantSql);
				} else {
					grantSql = "grant " + auth.getPrivilege_type() + " on " + auth.getObject_type() + " " + auth.getSchemaName() + "." + auth.getObjectName() + " TO " + userStr;
					isSuccess = gpadminController.getManageServiceProxy().executeUpdate(grantSql);
				}
				if (isSuccess != -1)
					setMsg(text, true, "[INFO:]" + grantSql + " success\n");
				else
					setMsg(text, false, "[ERROR:]" + grantSql + " fail\n");
			}
		}
		gpadminController.disconnect();
	}

	/**
	 * 加载系统权限信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public SystenAuth loadSystemAuth(GPManagerEntity gp, StyledText text, UICallBack callback) {
		SystenAuth userSystemAuth = new SystenAuth();
		userSystemAuth.setRoleName(gp.getGpUserName());
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return userSystemAuth;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return userSystemAuth;
		}
		List<Map<String, Object>> authList = gpadminController.getManageServiceProxy().executeQuery(
				"select rolsuper,rolinherit,rolcreaterole,rolcreatedb, rolcanlogin,rolreplication from pg_authid where rolname='" + gp.getGpUserName() + "'");
		if (authList != null) {
			for (Map<String, Object> rowMap : authList) {
				userSystemAuth.setRolsuper((String) rowMap.get("rolsuper"));
				userSystemAuth.setRolcreatedb((boolean) rowMap.get("rolcreatedb"));
				userSystemAuth.setRolcreaterole((boolean) rowMap.get("rolcreaterole"));
				userSystemAuth.setRolinherit((boolean) rowMap.get("rolinherit"));
				userSystemAuth.setRolreplication((boolean) rowMap.get("rolreplication"));
				userSystemAuth.setRolcanlogin((boolean) rowMap.get("rolcanlogin"));
			}
		}
		gpadminController.disconnect();
		return userSystemAuth;

	}

	/**
	 * 加载所有数据库名称
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public List<String> loaddatabase(GPManagerEntity gp, String dbName, StyledText text, UICallBack callback) {
		List<String> database = new ArrayList<String>();
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// return database;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), dbName);
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			return database;
		}
		List<Map<String, Object>> dataList = gpadminController.getManageServiceProxy().executeQuery("select datname from pg_database");
		for (Map<String, Object> rowMap : dataList) {
			String name = (String) rowMap.get("datname");
			database.add(name);
		}
		gpadminController.disconnect();
		return database;

	}

	/**
	 * 删除用户信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean deleteUser(GPManagerEntity gp, List<DbUser> users, StyledText text, UICallBack callback) {
		if (users == null || users.size() < 1)
			return true;
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// gpadminController.disconnect();
		// return false;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return false;
		}
		String dropSql = "drop user ";

		for (DbUser user : users) {
			String userName = user.getUserName();
			dropSql += (userName + ",");
		}
		dropSql = dropSql.substring(0, dropSql.lastIndexOf(","));
		int result = gpadminController.getManageServiceProxy().executeUpdate(dropSql);
		if (result == -1) {
			gpadminController.disconnect();
			return false;
		}
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 添加用户信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean addUser(GPManagerEntity gp, DbUser user, StyledText text, UICallBack callback) {
		if (user == null)
			return true;
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// gpadminController.disconnect();
		// return false;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return false;
		}
		String createSql = "create user " + user.getUserName();
		if (user.getUserPwd() != null || !user.getUserPwd().isEmpty()) {
			createSql += (" with password '" + user.getUserPwd() + "'");
		}

		int rs = gpadminController.getManageServiceProxy().executeUpdate(createSql);
		if (rs == -1) {
			gpadminController.disconnect();
			return false;
		}
		gpadminController.disconnect();
		return true;
	}

	/**
	 * 添加用户信息
	 * 
	 * @param gp
	 * @param text
	 * @param callback
	 * @return
	 */
	public boolean modifyUser(GPManagerEntity gp, DbUser user, StyledText text, UICallBack callback) {
		if (user == null)
			return true;
		// IGPConnector gpadminController = new
		// GPConnectorImpl(gp.getMasterIp(), gp.getGpUserName(),
		// gp.getGpUserPwd(), -1);
		// GPResultSet masterConnectRs = gpadminController.connect();
		// boolean isMasterConnect = masterConnectRs.isSuccessed();
		// if (!isMasterConnect) {
		// setConnectionMsg(text,
		// ResourceHandler.getValue("error", new String[] {
		// (masterConnectRs.getOutputErr() != null ?
		// masterConnectRs.getOutputErr() : masterConnectRs.getOutputEpt()) +
		// "\n" }));
		// gpadminController.disconnect();
		// return false;
		// }
		IGPConnector gpadminController = new GPConnectorImpl(gp.getMasterIp());
		gpadminController.connect();
		gpadminController.setCallback(callback);
		boolean isJDBCSuccess = gpadminController.getManageServiceProxy().connectJdbc(gp.getGpUserName(), gp.getGpUserPwd(), Integer.valueOf(gp.getGpPort()), gp.getGpdatabase());
		if (!isJDBCSuccess) {
			setConnectionMsg(text, ResourceHandler.getValue("manage.error.connectiondb"));
			gpadminController.disconnect();
			return false;
		}
		String createSql = "alter user " + user.getUserName();
		if (user.getUserPwd() != null || !user.getUserPwd().isEmpty()) {
			createSql += (" with password '" + user.getUserPwd() + "'");
		}

		int rs = gpadminController.getManageServiceProxy().executeUpdate(createSql);
		if (rs == -1) {
			gpadminController.disconnect();
			return false;
		}
		gpadminController.disconnect();
		return true;
	}
}
