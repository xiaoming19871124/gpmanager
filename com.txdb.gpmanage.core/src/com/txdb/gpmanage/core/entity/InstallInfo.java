package com.txdb.gpmanage.core.entity;

import java.util.ArrayList;
import java.util.List;

public class InstallInfo {
	private List<Host> allHost = new ArrayList<Host>();
	private List<Host> confHost = new ArrayList<Host>();
	private List<Host> installHost = new ArrayList<Host>();
	private Host masterHost;
	private String installPath;
	private String masterDataDir;
	private String segmentDataDir;
	private String mirrorDataDir;
	private String port;
	private int primaryNumb;
	private String databaseName;
	private String superUserName;
	private String superUserPassword;
	boolean isAddMirror = false;
	boolean isSpread = false;
	private String mirrorPort;
	private String replicationPort;
	private String mirrorReplicationPort;

	public List<Host> getAllHost() {
		return allHost;
	}

	public void setAllHost(List<Host> allHost) {
		this.allHost = allHost;
	}

	public List<Host> getConfHost() {
		return confHost;
	}

	public void setConfHost(List<Host> confHost) {
		this.confHost = confHost;
	}

	public List<Host> getInstallHost() {
		return installHost;
	}

	public void setInstallHost(List<Host> installHost) {
		this.installHost = installHost;
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

	public String getSuperUserName() {
		return superUserName;
	}

	public void setSuperUserName(String superUserName) {
		this.superUserName = superUserName;
	}

	public String getSuperUserPassword() {
		return superUserPassword;
	}

	public void setSuperUserPassword(String superUserPassword) {
		this.superUserPassword = superUserPassword;
	}

	public String getMasterDataDir() {
		return masterDataDir;
	}

	public void setMasterDataDir(String masterDataDir) {
		if (masterDataDir != null && !masterDataDir.isEmpty()) {
			if (!masterDataDir.endsWith("/"))
				masterDataDir += "/";
		} else {
			masterDataDir = "/opt/data/";
		}

		this.masterDataDir = masterDataDir;
	}

	public String getSegmentDataDir() {
		return segmentDataDir;
	}

	public void setSegmentDataDir(String segmentDataDir) {
		if (segmentDataDir != null && !segmentDataDir.isEmpty()) {
			if (!segmentDataDir.endsWith("/"))
				segmentDataDir += "/";
		}  else {
			segmentDataDir = "/opt/data/";
		}
		this.segmentDataDir = segmentDataDir;
	}

	public String getMirrorDataDir() {
		return mirrorDataDir;
	}

	public void setMirrorDataDir(String mirrorDataDir) {
		if (mirrorDataDir != null && !mirrorDataDir.isEmpty()) {
			if (!mirrorDataDir.endsWith("/"))
				mirrorDataDir += "/";
		}else {
			mirrorDataDir = "/opt/data/";
		}
		this.mirrorDataDir = mirrorDataDir;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getPrimaryNumb() {
		return primaryNumb;
	}

	public void setPrimaryNumb(int primaryNumb) {
		this.primaryNumb = primaryNumb;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public boolean isAddMirror() {
		return isAddMirror;
	}

	public void setAddMirror(boolean isAddMirror) {
		this.isAddMirror = isAddMirror;
	}

	public boolean isSpread() {
		return isSpread;
	}

	public void setSpread(boolean isSpread) {
		this.isSpread = isSpread;
	}

	public String getMirrorPort() {
		return mirrorPort;
	}

	public void setMirrorPort(String mirrorPort) {
		this.mirrorPort = mirrorPort;
	}

	public String getReplicationPort() {
		return replicationPort;
	}

	public void setReplicationPort(String replicationPort) {
		this.replicationPort = replicationPort;
	}

	public String getMirrorReplicationPort() {
		return mirrorReplicationPort;
	}

	public void setMirrorReplicationPort(String mirrorReplicationPort) {
		this.mirrorReplicationPort = mirrorReplicationPort;
	}

	public Host getMasterHost() {
		return masterHost;
	}

	public void setMasterHost(Host masterHost) {
		this.masterHost = masterHost;
	}
}
