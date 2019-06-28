package com.txdb.gpmanage.core.entity.impl;

import com.txdb.gpmanage.core.entity.BaseGPEntity;
import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.TableTag;

/**
 * GP Audit配置
 * @author xiaom
 */
@TableTag(tableName = "gpaudit")
public class GPAuditEntity extends BaseGPEntity {

	@FieldTag(constraint = "PRIMARY KEY")
	private String auditName;
	private String hostname;

	private int sshPort;
	private String gpUsername;
	private String gpUserpwd;
	
	private int gpPort;
	private String dbUsername;
	private String dbUserpwd;

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public String getGpUsername() {
		return gpUsername;
	}

	public void setGpUsername(String gpUsername) {
		this.gpUsername = gpUsername;
	}

	public String getGpUserpwd() {
		return gpUserpwd;
	}

	public void setGpUserpwd(String gpUserpwd) {
		this.gpUserpwd = gpUserpwd;
	}

	public int getGpPort() {
		return gpPort;
	}

	public void setGpPort(int gpPort) {
		this.gpPort = gpPort;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbUserpwd() {
		return dbUserpwd;
	}

	public void setDbUserpwd(String dbUserpwd) {
		this.dbUserpwd = dbUserpwd;
	}
}
