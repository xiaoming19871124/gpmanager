package com.txdb.gpmanage.core.entity.impl;

import com.txdb.gpmanage.core.entity.BaseGPEntity;
import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.TableTag;

/**
 * GP 预警配置
 * @author ws
 */
@TableTag(tableName = "gpwarning")
public class SystemWarningEntity extends BaseGPEntity {

	@FieldTag(constraint = "PRIMARY KEY")
	private String warningName;
	private String mail;
	private String cpuWarning;
	private String IoWarning;
	private String netWorkWarning;
	private String diskWarning;
	private String memoryWarning;
	private int faultWarning=0;
	private String queryWarning;
	private String skewWarning;
	
	@FieldTag(constraint = "FOREIGN KEY", constraintTable = "gpmonitor")
	private String monitorName;

	public String getWarningName() {
		return warningName;
	}

	public void setWarningName(String warningName) {
		this.warningName = warningName;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCpuWarning() {
		return cpuWarning;
	}

	public void setCpuWarning(String cpuWarning) {
		this.cpuWarning = cpuWarning;
	}

	public String getIoWarning() {
		return IoWarning;
	}

	public void setIoWarning(String ioWarning) {
		IoWarning = ioWarning;
	}

	public String getMemoryWarning() {
		return memoryWarning;
	}

	public void setMemoryWarning(String memoryWarning) {
		this.memoryWarning = memoryWarning;
	}

	public int getFaultWarning() {
		return faultWarning;
	}

	public void setFaultWarning(int faultWarning) {
		this.faultWarning = faultWarning;
	}

	public String getQueryWarning() {
		return queryWarning;
	}

	public void setQueryWarning(String queryWarning) {
		this.queryWarning = queryWarning;
	}

	@Override
	public String toString() {
		return warningName;
	}

	public String getSkewWarning() {
		return skewWarning;
	}

	public void setSkewWarning(String skewWarning) {
		this.skewWarning = skewWarning;
	}

	public String getDiskWarning() {
		return diskWarning;
	}

	public void setDiskWarning(String diskWarning) {
		this.diskWarning = diskWarning;
	}

	public String getNetWorkWarning() {
		return netWorkWarning;
	}

	public void setNetWorkWarning(String netWorkWarning) {
		this.netWorkWarning = netWorkWarning;
	}

//	public String getIsWarnCpu() {
//		return isWarnCpu;
//	}
//
//	public void setIsWarnCpu(String isWarnCpu) {
//		this.isWarnCpu = isWarnCpu;
//	}
//
//	public String getIsWarnIO() {
//		return isWarnIO;
//	}
//
//	public void setIsWarnIO(String isWarnIO) {
//		this.isWarnIO = isWarnIO;
//	}
//
//	public String getIsWarnMemory() {
//		return isWarnMemory;
//	}
//
//	public void setIsWarnMemory(String isWarnMemory) {
//		this.isWarnMemory = isWarnMemory;
//	}
//
//	public String getIsWarnFault() {
//		return isWarnFault;
//	}
//
//	public void setIsWarnFault(String isWarnFault) {
//		this.isWarnFault = isWarnFault;
//	}
//
//	public String getIsWarnQuery() {
//		return isWarnQuery;
//	}
//
//	public void setIsWarnQuery(String isWarnQuery) {
//		this.isWarnQuery = isWarnQuery;
//	}
//
//	public String getIsWarnSkew() {
//		return isWarnSkew;
//	}
//
//	public void setIsWarnSkew(String isWarnSkew) {
//		this.isWarnSkew = isWarnSkew;
//	}
}
