package com.txdb.gpmanage.core.gp.entry.gpmon;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * queries_now, queries_history
 * @author xiaom
 */
public class Queries {

	private Date ctime;
	private int tmid;
	private int ssid;
	private int ccnt;
	private String username;
	private String db;
	private int cost;
	private Date tsubmit;
	private Date tstart;
	private Date tfinish;
	private String status;
	private long rows_out;
	private long cpu_elapsed;
	private double cpu_currpct;
	private double skew_cpu;
	private double skew_rows;
	private long query_hash;
	private String query_text;
	private String query_plan;
	private String application_name;
	private String rsqname;
	private String rqppriority;

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public int getTmid() {
		return tmid;
	}

	public void setTmid(int tmid) {
		this.tmid = tmid;
	}

	public int getSsid() {
		return ssid;
	}

	public void setSsid(int ssid) {
		this.ssid = ssid;
	}

	public int getCcnt() {
		return ccnt;
	}

	public void setCcnt(int ccnt) {
		this.ccnt = ccnt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Date getTsubmit() {
		return tsubmit;
	}

	public void setTsubmit(Date tsubmit) {
		this.tsubmit = tsubmit;
	}

	public Date getTstart() {
		return tstart;
	}

	public void setTstart(Date tstart) {
		this.tstart = tstart;
	}

	public Date getTfinish() {
		return tfinish;
	}

	public void setTfinish(Date tfinish) {
		this.tfinish = tfinish;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getRows_out() {
		return rows_out;
	}

	public void setRows_out(long rows_out) {
		this.rows_out = rows_out;
	}

	public long getCpu_elapsed() {
		return cpu_elapsed;
	}

	public void setCpu_elapsed(long cpu_elapsed) {
		this.cpu_elapsed = cpu_elapsed;
	}

	public double getCpu_currpct() {
		return cpu_currpct;
	}

	public void setCpu_currpct(double cpu_currpct) {
		this.cpu_currpct = cpu_currpct;
	}

	public double getSkew_cpu() {
		return skew_cpu;
	}

	public void setSkew_cpu(double skew_cpu) {
		this.skew_cpu = skew_cpu;
	}

	public double getSkew_rows() {
		return skew_rows;
	}

	public void setSkew_rows(double skew_rows) {
		this.skew_rows = skew_rows;
	}

	public long getQuery_hash() {
		return query_hash;
	}

	public void setQuery_hash(long query_hash) {
		this.query_hash = query_hash;
	}

	public String getQuery_text() {
		return query_text;
	}

	public void setQuery_text(String query_text) {
		this.query_text = query_text;
	}

	public String getQuery_plan() {
		return query_plan;
	}

	public void setQuery_plan(String query_plan) {
		this.query_plan = query_plan;
	}

	public String getApplication_name() {
		return application_name;
	}

	public void setApplication_name(String application_name) {
		this.application_name = application_name;
	}

	public String getRsqname() {
		return rsqname;
	}

	public void setRsqname(String rsqname) {
		this.rsqname = rsqname;
	}

	public String getRqppriority() {
		return rqppriority;
	}

	public void setRqppriority(String rqppriority) {
		this.rqppriority = rqppriority;
	}
	
	public static List<Queries> toList(List<Map<String, Object>> dataList) throws SQLException {
		List<Queries> queriesList = new ArrayList<Queries>();
		Queries queries = null;
		for (Map<String, Object> rowMap : dataList) {
			queries = new Queries();
			queries.setCtime(new Date(((Timestamp) rowMap.get("ctime")).getTime()));
			queries.setTmid((int) rowMap.get("tmid"));
			queries.setSsid((int) rowMap.get("ssid"));
			queries.setCcnt((int) rowMap.get("ccnt"));
			queries.setUsername((String) rowMap.get("username"));
			queries.setDb((String) rowMap.get("db"));
			queries.setCost((int) rowMap.get("cost"));
			
			Timestamp tsubmit = (Timestamp) rowMap.get("tsubmit");
			queries.setTsubmit(tsubmit == null ? new Date() : new Date(tsubmit.getTime()));
			
			Timestamp tstart = (Timestamp) rowMap.get("tstart");
			queries.setTstart(tstart == null ? new Date() : new Date(tstart.getTime()));
			
			Timestamp tfinish = (Timestamp) rowMap.get("tfinish");
			queries.setTfinish(tfinish == null ? new Date() : new Date(tfinish.getTime()));
			
			queries.setStatus((String) rowMap.get("status"));
			queries.setRows_out((long) rowMap.get("rows_out"));
			queries.setCpu_elapsed((long) rowMap.get("cpu_elapsed"));
			queries.setCpu_currpct((double) rowMap.get("cpu_currpct"));
			queries.setSkew_cpu((double) rowMap.get("skew_cpu"));
			queries.setSkew_rows((double) rowMap.get("skew_rows"));
			queries.setQuery_hash((long) rowMap.get("query_hash"));
			queries.setQuery_text((String) rowMap.get("query_text"));
			queries.setQuery_plan((String) rowMap.get("query_plan"));
			queries.setApplication_name((String) rowMap.get("application_name"));
			queries.setRsqname((String) rowMap.get("rsqname"));
			queries.setRqppriority((String) rowMap.get("rqppriority"));
			queriesList.add(queries);
		}
		return queriesList;
	}
}
