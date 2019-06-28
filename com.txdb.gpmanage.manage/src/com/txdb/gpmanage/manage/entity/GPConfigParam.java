package com.txdb.gpmanage.manage.entity;

import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;

public class GPConfigParam {
	private GPConfig gpconfig;
	private GPConfigValue value;
	private String masterValue;
	private String segValue;

	public GPConfig getGpconfig() {
		return gpconfig;
	}

	public void setGpconfig(GPConfig gpconfig) {
		this.gpconfig = gpconfig;
	}

	public GPConfigValue getValue() {
		return value;
	}

	public void setValue(GPConfigValue value) {
		this.value = value;
	}

	public String getMasterValue() {
		return masterValue;
	}

	public void setMasterValue(String masterValue) {
		this.masterValue = masterValue;
	}

	public String getSegValue() {
		return segValue;
	}

	public void setSegValue(String segValue) {
		this.segValue = segValue;
	}
}
