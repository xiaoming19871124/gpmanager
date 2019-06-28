package com.txdb.gpmanage.core.gp.entry;

/**
 * select * from gp_segment_configuration order by dbid;
 * 
 * @author xiaom
 */
public class GPSegmentInfo {

	/** Primary */
	public static final String ROLE_PRIMARY = "p";
	/** Mirror */
	public static final String ROLE_MIRROR = "m";

	/** 同步状态（未同步-Not Syncing） */
	public static final String MODE_NOTSYNC = "n";
	/** 同步状态（同步-Synced） */
	public static final String MODE_SYNCED = "s";
	/** 同步状态（变化跟踪-Change Tracking） */
	public static final String MODE_TRACKING = "c";
	/** 同步状态（重新同步中-Resyncing） */
	public static final String MODE_RESYNC = "r";

	/** 故障状态（启动） */
	public static final String STATUS_UP = "u";
	/** 故障状态（停止） */
	public static final String STATUS_DOWN = "d";

	/** 实例标志 */
	private int dbid;
	/** 数据库标识 */
	private int content;
	/** 角色 */
	private String role;
	/** 初始角色 */
	private String preferred_role;
	/** 同步状态 */
	private String mode;
	/** 故障状态 */
	private String status;
	/** 监听端口 */
	private int port;
	/** 机器名称 */
	private String hostname;
	/** 实例地址 */
	private String address;
	/** 数据目录 */
	private String datadir;

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPreferred_role() {
		return preferred_role;
	}

	public void setPreferred_role(String preferred_role) {
		this.preferred_role = preferred_role;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDatadir() {
		return datadir;
	}

	public void setDatadir(String datadir) {
		this.datadir = datadir;
	}

	@Override
	public String toString() {
		return "GPSegmentInfo{" + "dbid='" + dbid + '\'' + ", content='"
				+ content + '\'' + ", role='" + role + '\''
				+ ", preferred_role='" + preferred_role + '\'' + ", mode='"
				+ mode + '\'' + ", status='" + status + '\'' + ", port='"
				+ port + '\'' + ", hostname='" + hostname + '\''
				+ ", address='" + address + '\'' + ", datadir='" + datadir
				+ '\'' + '}';
	}
}
