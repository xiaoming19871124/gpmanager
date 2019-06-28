package com.txdb.gpmanage.gpmon.entries;

import java.util.Date;

/**
 * segment_now, segment_history
 * @author xiaom
 */
public class Segment {

	private Date ctime;
	private int dbid;
	private String hostname;
	private long dynamic_memory_used;
	private long dynamic_memory_available;

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public long getDynamic_memory_used() {
		return dynamic_memory_used;
	}

	public void setDynamic_memory_used(long dynamic_memory_used) {
		this.dynamic_memory_used = dynamic_memory_used;
	}

	public long getDynamic_memory_available() {
		return dynamic_memory_available;
	}

	public void setDynamic_memory_available(long dynamic_memory_available) {
		this.dynamic_memory_available = dynamic_memory_available;
	}
}
