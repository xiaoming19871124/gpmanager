package com.txdb.gpmanage.core.exception;

/**
 * 主机角色
 * 
 * @author ws
 *
 */
public enum HostRole {
	STANDBY, MIRROR, SEGEMENT;
	public String getName() {
		if (this == HostRole.STANDBY)
			return "standby";
		if (this == HostRole.MIRROR)
			return "mirror";
		if (this == HostRole.SEGEMENT)
			return "segment";
		return null;
	}
}
