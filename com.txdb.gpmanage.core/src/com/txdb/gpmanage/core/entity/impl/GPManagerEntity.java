package com.txdb.gpmanage.core.entity.impl;

import com.txdb.gpmanage.core.entity.BaseGPEntity;
import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.TableTag;

/**
 * GP Manager配置
 * 
 * @author ws
 */
@TableTag(tableName = "gpmanage")
public class GPManagerEntity extends BaseGPEntity {

	@FieldTag(constraint = "PRIMARY KEY")
	private String gpName;
	private String masterHostName;
	private String masterIp;
	private String masterRootName;
	private String masterRootPwd;
	private String role;
	private String standbyHostName;
	private String standbyIp;
	private String standbyRootName;
	private String standbyRootPwd;

	private String gpPort;
	private String gpUserName;
	private String gpUserPwd;
	private String gpdatabase;
	private String installPath;
	private String datadir;
	private String masterDataDir;
	private String mirrorDataDir;
	private String segmentDataDir;

	private int hasMirror = 0;
	private int hasStandby = 0;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMasterHostName() {
		return masterHostName;
	}

	public void setMasterHostName(String masterHostName) {
		this.masterHostName = masterHostName;
	}

	public String getGpPort() {
		return gpPort;
	}

	public void setGpPort(String gpPort) {
		this.gpPort = gpPort;
	}

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		if (installPath != null && !installPath.isEmpty()) {
			if (!installPath.endsWith("/"))
				installPath += "/";
		} else {
			installPath = "/user/local/";
		}
		this.installPath = installPath;
	}

	public String getMasterRootName() {
		return masterRootName;
	}

	public void setMasterRootName(String masterRootName) {
		this.masterRootName = masterRootName;
	}

	public String getMasterRootPwd() {
		return masterRootPwd;
	}

	public void setMasterRootPwd(String masterRootPwd) {
		this.masterRootPwd = masterRootPwd;
	}

	public String getGpUserName() {
		return gpUserName;
	}

	public void setGpUserName(String gpUserName) {
		this.gpUserName = gpUserName;
	}

	public String getGpUserPwd() {
		return gpUserPwd;
	}

	public void setGpUserPwd(String gpUserPwd) {
		this.gpUserPwd = gpUserPwd;
	}

	public String getGpName() {
		return gpName;
	}

	public void setGpName(String gpName) {
		this.gpName = gpName;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public void setMasterIp(String masterIp) {
		this.masterIp = masterIp;
	}

	public String getDatadir() {
		return datadir;
	}

	public void setDatadir(String datadir) {
		if (datadir != null && !datadir.isEmpty()) {
			if (!datadir.endsWith("/"))
				datadir += "/";
		} else {
			datadir = "/opt/gpdata/";
		}
		this.datadir = datadir;
	}

	public String getStandbyHostName() {
		return standbyHostName;
	}

	public void setStandbyHostName(String standbyHostName) {
		this.standbyHostName = standbyHostName;
	}

	public String getStandbyIp() {
		return standbyIp;
	}

	public void setStandbyIp(String standbyIp) {
		this.standbyIp = standbyIp;
	}

	public String getStandbyRootName() {
		return standbyRootName;
	}

	public void setStandbyRootName(String standbyRootName) {
		this.standbyRootName = standbyRootName;
	}

	public String getStandbyRootPwd() {
		return standbyRootPwd;
	}

	public void setStandbyRootPwd(String standbyRootPwd) {
		this.standbyRootPwd = standbyRootPwd;
	}

	public int getHasMirror() {
		return hasMirror;
	}

	public void setHasMirror(int hasMirror) {
		this.hasMirror = hasMirror;
	}

	public int getHasStandby() {
		return hasStandby;
	}

	public void setHasStandby(int hasStandby) {
		this.hasStandby = hasStandby;
	}

	public String getMirrorDataDir() {
		return mirrorDataDir;
	}

	public void setMirrorDataDir(String mirrorDataDir) {
		this.mirrorDataDir = mirrorDataDir;
	}

	public String getSegmentDataDir() {
		return segmentDataDir;
	}

	public void setSegmentDataDir(String segmentDataDir) {
		this.segmentDataDir = segmentDataDir;
	}

	public String getMasterDataDir() {
		return masterDataDir;
	}

	public void setMasterDataDir(String masterDataDir) {
		if (masterDataDir != null && !masterDataDir.isEmpty()) {
			if (!masterDataDir.endsWith("/"))
				masterDataDir += "/";
		} else {
			masterDataDir = "/opt/gpdata/";
		}
		this.masterDataDir = masterDataDir;
	}

	public String getGpdatabase() {
		return gpdatabase;
	}

	public void setGpdatabase(String gpdatabase) {
		this.gpdatabase = gpdatabase;
	}
}
