package com.txdb.gpmanage.gpmon.entries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * log_alert, log_alert_history
 * @author xiaom
 */
public class LogAlert {

	private Date logtime;
//	private Date _logtimeFrom;
//	private Date _logtimeTo;
	
	private String loguser;
	private String logdatabase;
	private String logpid;
	private String logthread;
	private String loghost;
	private String logport;
	private Date logsessiontime;
	private int logtransaction;
	private String logsession;
	private String logcmdcount;
	private String logsegment;
	private String logslice;
	private String logdistxact;
	private String loglocalxact;
	private String logsubxact;
	private String logseverity;
	private String logstate;
	private String logmessage;
	private String logdetail;
	private String loghint;
	private String logquery;
	private int logquerypos;
	private String logcontext;
	private String logdebug;
	private int logcursorpos;
	private String logfunction;
	private String logfile;
	private int logline;
	private String logstack;

	public Date getLogtime() {
		return logtime;
	}

	public void setLogtime(Date logtime) {
		this.logtime = logtime;
	}

//	public Date get_logtimeFrom() {
//		return _logtimeFrom;
//	}
//
//	public void set_logtimeFrom(Date _logtimeFrom) {
//		this._logtimeFrom = _logtimeFrom;
//	}
//
//	public Date get_logtimeTo() {
//		return _logtimeTo;
//	}
//
//	public void set_logtimeTo(Date _logtimeTo) {
//		this._logtimeTo = _logtimeTo;
//	}

	public String getLoguser() {
		return loguser;
	}

	public void setLoguser(String loguser) {
		this.loguser = loguser;
	}

	public String getLogdatabase() {
		return logdatabase;
	}

	public void setLogdatabase(String logdatabase) {
		this.logdatabase = logdatabase;
	}

	public String getLogpid() {
		return logpid;
	}

	public void setLogpid(String logpid) {
		this.logpid = logpid;
	}

	public String getLogthread() {
		return logthread;
	}

	public void setLogthread(String logthread) {
		this.logthread = logthread;
	}

	public String getLoghost() {
		return loghost;
	}

	public void setLoghost(String loghost) {
		this.loghost = loghost;
	}

	public String getLogport() {
		return logport;
	}

	public void setLogport(String logport) {
		this.logport = logport;
	}

	public Date getLogsessiontime() {
		return logsessiontime;
	}

	public void setLogsessiontime(Date logsessiontime) {
		this.logsessiontime = logsessiontime;
	}

	public int getLogtransaction() {
		return logtransaction;
	}

	public void setLogtransaction(int logtransaction) {
		this.logtransaction = logtransaction;
	}

	public String getLogsession() {
		return logsession;
	}

	public void setLogsession(String logsession) {
		this.logsession = logsession;
	}

	public String getLogcmdcount() {
		return logcmdcount;
	}

	public void setLogcmdcount(String logcmdcount) {
		this.logcmdcount = logcmdcount;
	}

	public String getLogsegment() {
		return logsegment;
	}

	public void setLogsegment(String logsegment) {
		this.logsegment = logsegment;
	}

	public String getLogslice() {
		return logslice;
	}

	public void setLogslice(String logslice) {
		this.logslice = logslice;
	}

	public String getLogdistxact() {
		return logdistxact;
	}

	public void setLogdistxact(String logdistxact) {
		this.logdistxact = logdistxact;
	}

	public String getLoglocalxact() {
		return loglocalxact;
	}

	public void setLoglocalxact(String loglocalxact) {
		this.loglocalxact = loglocalxact;
	}

	public String getLogsubxact() {
		return logsubxact;
	}

	public void setLogsubxact(String logsubxact) {
		this.logsubxact = logsubxact;
	}

	public String getLogseverity() {
		return logseverity;
	}

	public void setLogseverity(String logseverity) {
		this.logseverity = logseverity;
	}

	public String getLogstate() {
		return logstate;
	}

	public void setLogstate(String logstate) {
		this.logstate = logstate;
	}

	public String getLogmessage() {
		return logmessage;
	}

	public void setLogmessage(String logmessage) {
		this.logmessage = logmessage;
	}

	public String getLogdetail() {
		return logdetail;
	}

	public void setLogdetail(String logdetail) {
		this.logdetail = logdetail;
	}

	public String getLoghint() {
		return loghint;
	}

	public void setLoghint(String loghint) {
		this.loghint = loghint;
	}

	public String getLogquery() {
		return logquery;
	}

	public void setLogquery(String logquery) {
		this.logquery = logquery;
	}

	public int getLogquerypos() {
		return logquerypos;
	}

	public void setLogquerypos(int logquerypos) {
		this.logquerypos = logquerypos;
	}

	public String getLogcontext() {
		return logcontext;
	}

	public void setLogcontext(String logcontext) {
		this.logcontext = logcontext;
	}

	public String getLogdebug() {
		return logdebug;
	}

	public void setLogdebug(String logdebug) {
		this.logdebug = logdebug;
	}

	public int getLogcursorpos() {
		return logcursorpos;
	}

	public void setLogcursorpos(int logcursorpos) {
		this.logcursorpos = logcursorpos;
	}

	public String getLogfunction() {
		return logfunction;
	}

	public void setLogfunction(String logfunction) {
		this.logfunction = logfunction;
	}

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public int getLogline() {
		return logline;
	}

	public void setLogline(int logline) {
		this.logline = logline;
	}

	public String getLogstack() {
		return logstack;
	}

	public void setLogstack(String logstack) {
		this.logstack = logstack;
	}
	
	public static List<LogAlert> toList(ResultSet sqlRs) throws SQLException {
		List<LogAlert> logAlertList = new ArrayList<LogAlert>();
		LogAlert logAlert = null;
		while (sqlRs.next()) {
			logAlert = new LogAlert();
			logAlert.setLogtime(new Date(sqlRs.getTimestamp("logtime").getTime()));
			logAlert.setLoguser(sqlRs.getString("loguser"));
			logAlert.setLogdatabase(sqlRs.getString("logdatabase"));
			logAlert.setLogpid(sqlRs.getString("logpid"));
			logAlert.setLogthread(sqlRs.getString("logthread"));
			logAlert.setLoghost(sqlRs.getString("loghost"));
			logAlert.setLogport(sqlRs.getString("logport"));
			
			Timestamp tempLogsessiontime = sqlRs.getTimestamp("logsessiontime");
			logAlert.setLogsessiontime(tempLogsessiontime == null ? null : new Date(tempLogsessiontime.getTime()));
			logAlert.setLogtransaction(sqlRs.getInt("logtransaction"));
			logAlert.setLogsession(sqlRs.getString("logsession"));
			logAlert.setLogcmdcount(sqlRs.getString("logcmdcount"));
			logAlert.setLogsegment(sqlRs.getString("logsegment"));
			logAlert.setLogslice(sqlRs.getString("logslice"));
			logAlert.setLogdistxact(sqlRs.getString("logdistxact"));
			logAlert.setLoglocalxact(sqlRs.getString("loglocalxact"));
			logAlert.setLogsubxact(sqlRs.getString("logsubxact"));
			logAlert.setLogseverity(sqlRs.getString("logseverity"));
			logAlert.setLogstate(sqlRs.getString("logstate"));
			logAlert.setLogmessage(sqlRs.getString("logmessage"));
			logAlert.setLogdetail(sqlRs.getString("logdetail"));
			logAlert.setLoghint(sqlRs.getString("loghint"));
			logAlert.setLogquery(sqlRs.getString("logquery"));
			logAlert.setLogquerypos(sqlRs.getInt("logquerypos"));
			logAlert.setLogcontext(sqlRs.getString("logcontext"));
			logAlert.setLogdebug(sqlRs.getString("logdebug"));
			logAlert.setLogcursorpos(sqlRs.getInt("logcursorpos"));
			logAlert.setLogfunction(sqlRs.getString("logfunction"));
			logAlert.setLogfile(sqlRs.getString("logfile"));
			logAlert.setLogline(sqlRs.getInt("logline"));
			logAlert.setLogstack(sqlRs.getString("logstack"));
			logAlertList.add(logAlert);
		}
		return logAlertList;
	}

	@Override
	public String toString() {
		return "GpmonLogAlert{" +
				  "time='" + logtime + "\'" +
				", user='" + loguser + "\'" +
				", database='" + logdatabase + "\'" +
				", pid='" + logpid + "\'" +
				", thread='" + logthread + "\'" +
				", host='" + loghost + "\'" +
				", port='" + logport + "\'" +
				", sessiontime='" + logsessiontime + "\'" +
				", transaction='" + logtransaction + "\'" +
				", session='" + logsession + "\'" +
				", cmdcount='" + logcmdcount + "\'" +
				", segment='" + logsegment + "\'" +
				", slice='" + logslice + "\'" +
				", distxact='" + logdistxact + "\'" +
				", localxact='" + loglocalxact + "\'" +
				", subxact='" + logsubxact + "\'" +
				", severity='" + logseverity + "\'" +
				", state='" + logstate + "\'" +
				", message='" + logmessage + "\'" +
				", detail='" + logdetail + "\'" +
				", hint='" + loghint + "\'" +
				", query='" + logquery + "\'" +
				", querypos='" + logquerypos + "\'" +
				", context='" + logcontext + "\'" +
				", debug='" + logdebug + "\'" +
				", cursorpos='" + logcursorpos + "\'" +
				", function='" + logfunction + "\'" +
				", file='" + logfile + "\'" +
				", line='" + logline + "\'" +
				", stack='" + logstack + "\'" +
				'}';
	}
}
