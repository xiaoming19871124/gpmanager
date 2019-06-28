package com.txdb.gpmanage.core.entity;

import com.txdb.gpmanage.core.exception.HostRole;

public class Host {
	private String name;
	private String ip;
	private String userName;
	private String password;
	private String port;
	private String directory;
	private String dbId;
	private String instanceDir;
	private HostRole role = HostRole.STANDBY;
	private boolean isMaster = false;
	private boolean isSegment = false;
	private boolean isSSHExchange = false;
	private boolean isInstall = false;
	private boolean isInGP = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public boolean isSSHExchange() {
		return isSSHExchange;
	}

	public void setSSHExchange(boolean isSSHExchange) {
		this.isSSHExchange = isSSHExchange;
	}

	public boolean isSegment() {
		return isSegment;
	}

	public void setSegment(boolean isSegment) {
		this.isSegment = isSegment;
	}

	public boolean isInstall() {
		return isInstall;
	}

	public void setInstall(boolean isInstall) {
		this.isInstall = isInstall;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getInstanceDir() {
		return instanceDir;
	}

	public void setInstanceDir(String instanceDir) {
		this.instanceDir = instanceDir;
	}

	public HostRole getRole() {
		return role;
	}

	public void setRole(HostRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isInGP() {
		return isInGP;
	}

	public void setInGP(boolean isInGP) {
		this.isInGP = isInGP;
	}
}
