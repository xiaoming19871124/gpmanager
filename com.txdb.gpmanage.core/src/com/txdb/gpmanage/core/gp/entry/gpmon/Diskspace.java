package com.txdb.gpmanage.core.gp.entry.gpmon;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
	public static List<Diskspace> toList(List<Map<String, Object>> dataList) throws SQLException {
		List<Diskspace> diskspaceList = new ArrayList<Diskspace>();
		Diskspace diskspace = null;
		for (Map<String, Object> rowMap : dataList) {
			diskspace = new Diskspace();
			diskspace.setCtime(new Date(((Timestamp) rowMap.get("ctime")).getTime()));
			diskspace.setHostname((String) rowMap.get("hostname"));
			diskspace.setFilesystem((String) rowMap.get("filesystem"));
			diskspace.setTotal_bytes((long) rowMap.get("total_bytes"));
			diskspace.setBytes_used((long) rowMap.get("bytes_used"));
			diskspace.setBytes_available((long) rowMap.get("bytes_available"));
			diskspaceList.add(diskspace);
		}
		return diskspaceList;
	}
}
