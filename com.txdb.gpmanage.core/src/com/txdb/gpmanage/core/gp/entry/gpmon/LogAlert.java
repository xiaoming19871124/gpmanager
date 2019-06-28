package com.txdb.gpmanage.core.gp.entry.gpmon;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * log_alert, log_alert_history
 * @author xiaom
 */
public class LogAlert {
	
	public static final String LOG_PANIC = "PANIC";
	public static final String LOG_FATAL = "FATAL";
	public static final String LOG_ERROR = "ERROR";
	public static final String LOG_WARNING = "WARNING";
	
	private int limit = 0;
	private int offset = 0;
	private int totalCount;

	private Date logtime;
	private Date _logtimeFrom;
	private Date _logtimeTo;
	
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
	private boolean _logseverity_panic = true;
	private boolean _logseverity_fatal = true;
	private boolean _logseverity_error = true;
	private boolean _logseverity_warning = true;
	
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Date getLogtime() {
		return logtime;
	}

	public void setLogtime(Date logtime) {
		this.logtime = logtime;
	}

	public Date get_logtimeFrom() {
		return _logtimeFrom;
	}

	public void set_logtimeFrom(Date _logtimeFrom) {
		this._logtimeFrom = _logtimeFrom;
	}

	public Date get_logtimeTo() {
		return _logtimeTo;
	}

	public void set_logtimeTo(Date _logtimeTo) {
		this._logtimeTo = _logtimeTo;
	}

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

	public boolean is_logseverity_panic() {
		return _logseverity_panic;
	}

	public void set_logseverity_panic(boolean _logseverity_panic) {
		this._logseverity_panic = _logseverity_panic;
	}

	public boolean is_logseverity_fatal() {
		return _logseverity_fatal;
	}

	public void set_logseverity_fatal(boolean _logseverity_fatal) {
		this._logseverity_fatal = _logseverity_fatal;
	}

	public boolean is_logseverity_error() {
		return _logseverity_error;
	}

	public void set_logseverity_error(boolean _logseverity_error) {
		this._logseverity_error = _logseverity_error;
	}

	public boolean is_logseverity_warning() {
		return _logseverity_warning;
	}

	public void set_logseverity_warning(boolean _logseverity_warning) {
		this._logseverity_warning = _logseverity_warning;
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
	
	public static List<LogAlert> toList(List<Map<String, Object>> dataList) throws SQLException {
		List<LogAlert> logAlertList = new ArrayList<LogAlert>();
		LogAlert logAlert = null;
		for (Map<String,Object> rowMap : dataList) {
			logAlert = new LogAlert();
//			logAlert.setLogtime(new Date(sqlRs.getTimestamp("logtime").getTime()));
			logAlert.setLogtime(new Date(((Timestamp) rowMap.get("logtime")).getTime()));
			logAlert.setLoguser((String) rowMap.get("loguser"));
			logAlert.setLogdatabase((String) rowMap.get("logdatabase"));
			logAlert.setLogpid((String) rowMap.get("logpid"));
			logAlert.setLogthread((String) rowMap.get("logthread"));
			logAlert.setLoghost((String) rowMap.get("loghost"));
			logAlert.setLogport((String) rowMap.get("logport"));
			
//			Timestamp tempLogsessiontime = sqlRs.getTimestamp("logsessiontime");
			Timestamp tempLogsessiontime = (Timestamp) rowMap.get("logsessiontime");
			logAlert.setLogsessiontime(tempLogsessiontime == null ? null : new Date(tempLogsessiontime.getTime()));
			logAlert.setLogtransaction((int) rowMap.get("logtransaction"));
			logAlert.setLogsession((String) rowMap.get("logsession"));
			logAlert.setLogcmdcount((String) rowMap.get("logcmdcount"));
			logAlert.setLogsegment((String) rowMap.get("logsegment"));
			logAlert.setLogslice((String) rowMap.get("logslice"));
			logAlert.setLogdistxact((String) rowMap.get("logdistxact"));
			logAlert.setLoglocalxact((String) rowMap.get("loglocalxact"));
			logAlert.setLogsubxact((String) rowMap.get("logsubxact"));
			logAlert.setLogseverity((String) rowMap.get("logseverity"));
			logAlert.setLogstate((String) rowMap.get("logstate"));
			logAlert.setLogmessage((String) rowMap.get("logmessage"));
			logAlert.setLogdetail((String) rowMap.get("logdetail"));
			logAlert.setLoghint((String) rowMap.get("loghint"));
			logAlert.setLogquery((String) rowMap.get("logquery"));
			
			int logquerypos = 0;
			try {
				logquerypos = (int) rowMap.get("logquerypos");
			} catch (Exception e) {}
			logAlert.setLogquerypos(logquerypos);
			logAlert.setLogcontext((String) rowMap.get("logcontext"));
			logAlert.setLogdebug((String) rowMap.get("logdebug"));
			logAlert.setLogcursorpos((int) rowMap.get("logcursorpos"));
			logAlert.setLogfunction((String) rowMap.get("logfunction"));
			logAlert.setLogfile((String) rowMap.get("logfile"));
			logAlert.setLogline((int) rowMap.get("logline"));
			logAlert.setLogstack((String) rowMap.get("logstack"));
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
