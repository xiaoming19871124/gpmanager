package com.txdb.gpmanage.gpmon.entries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * diskspace_now, diskspace_history
 * @author xiaom
 */
public class Diskspace {

	private Date ctime;
	private String hostname;
	private String filesystem;
	private long total_bytes;
	private long bytes_used;
	private long bytes_available;

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getFilesystem() {
		return filesystem;
	}

	public void setFilesystem(String filesystem) {
		this.filesystem = filesystem;
	}

	public long getTotal_bytes() {
		return total_bytes;
	}

	public void setTotal_bytes(long total_bytes) {
		this.total_bytes = total_bytes;
	}

	public long getBytes_used() {
		return bytes_used;
	}

	public void setBytes_used(long bytes_used) {
		this.bytes_used = bytes_used;
	}

	public long getBytes_available() {
		return bytes_available;
	}

	public void setBytes_available(long bytes_available) {
		this.bytes_available = bytes_available;
	}
	
	public static List<Diskspace> toList(ResultSet sqlRs) throws SQLException {
		List<Diskspace> diskspaceList = new ArrayList<Diskspace>();
		Diskspace diskspace = null;
		while (sqlRs.next()) {
			diskspace = new Diskspace();
			diskspace.setCtime(new Date(sqlRs.getTimestamp("ctime").getTime()));
			try {
				diskspace.setHostname(sqlRs.getString("hostname"));
			} catch (Exception e) {}
			diskspace.setFilesystem(sqlRs.getString("filesystem"));
			diskspace.setTotal_bytes(sqlRs.getLong("total_bytes"));
			diskspace.setBytes_used(sqlRs.getLong("bytes_used"));
			diskspace.setBytes_available(sqlRs.getLong("bytes_available"));
			diskspaceList.add(diskspace);
		}
		return diskspaceList;
	}
}
