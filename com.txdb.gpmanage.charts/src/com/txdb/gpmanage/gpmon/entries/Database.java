package com.txdb.gpmanage.gpmon.entries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	public static List<Database> toList(ResultSet sqlRs) throws SQLException {
		List<Database> databaseList = new ArrayList<Database>();
		Database database = null;
		while (sqlRs.next()) {
			database = new Database();
			database.setCtime(new Date(sqlRs.getTimestamp("ctime").getTime()));
			database.setQueries_total(sqlRs.getInt("queries_total"));
			database.setQueries_running(sqlRs.getInt("queries_running"));
			database.setQueries_queued(sqlRs.getInt("queries_queued"));
			databaseList.add(database);
		}
		return databaseList;
	}
}
