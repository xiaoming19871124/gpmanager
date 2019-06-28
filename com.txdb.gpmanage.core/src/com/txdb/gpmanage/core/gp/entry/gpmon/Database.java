package com.txdb.gpmanage.core.gp.entry.gpmon;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * database_now, database_history
 * @author xiaom
 */
public class Database {

	private Date ctime;
	private int queries_total;
	private int queries_running;
	private int queries_queued;

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public int getQueries_total() {
		return queries_total;
	}

	public void setQueries_total(int queries_total) {
		this.queries_total = queries_total;
	}

	public int getQueries_running() {
		return queries_running;
	}

	public void setQueries_running(int queries_running) {
		this.queries_running = queries_running;
	}

	public int getQueries_queued() {
		return queries_queued;
	}

	public void setQueries_queued(int queries_queued) {
		this.queries_queued = queries_queued;
	}
	
	public static List<Database> toList(List<Map<String, Object>> dataList) throws SQLException {
		List<Database> databaseList = new ArrayList<Database>();
		Database database = null;
		for (Map<String, Object> rowMap : dataList) {
			database = new Database();
			database.setCtime(new Date(((Timestamp) rowMap.get("ctime")).getTime()));
			database.setQueries_total((int) rowMap.get("queries_total"));
			database.setQueries_running((int) rowMap.get("queries_running"));
			database.setQueries_queued((int) rowMap.get("queries_queued"));
			databaseList.add(database);
		}
		return databaseList;
	}
}
