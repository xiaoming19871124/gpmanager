package com.txdb.gpmanage.core.gp.entry.gpmon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Connection information for host
 * @author xiaom
 */
public class RequireConnection {
	
	private final String DATE_PATTERN = "yyyy-MM-dd H:m:s";

	private String monitorName;
	private String host;

	private String sshUsername;
	private String sshPassword;
	private int sshPort;

	private String jdbcUsername;
	private String jdbcPassword;
	private int jdbcPort;

	private String database;

	private String dateFromStr;
	private String dateToStr;

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSshUsername() {
		return sshUsername;
	}

	public void setSshUsername(String sshUsername) {
		this.sshUsername = sshUsername;
	}

	public String getSshPassword() {
		return sshPassword;
	}

	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public int getJdbcPort() {
		return jdbcPort;
	}

	public void setJdbcPort(int jdbcPort) {
		this.jdbcPort = jdbcPort;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDateFromStr() {
		return dateFromStr;
	}

	public void setDateFromStr(String dateFromStr) {
		this.dateFromStr = dateFromStr;
	}
	
	public Date getDateFrom() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		return sdf.parse(dateFromStr);
	}
	
	public void setDateFrom(Calendar calendar) {
		dateFromStr = getCalendarStr(calendar);
	}

	public String getDateToStr() {
		return dateToStr;
	}

	public void setDateToStr(String dateToStr) {
		this.dateToStr = dateToStr;
	}
	
	public Date getDateTo() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		return sdf.parse(dateToStr);
	
	}

	public void setDateTo(Calendar calendar) {
		dateToStr = getCalendarStr(calendar);
	}
	
	public String getCalendarStr(Calendar calendar) {
		StringBuffer dateBuff = new StringBuffer("");
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		dateBuff.append(year + "-");
		dateBuff.append((month >= 10 ? month : "0" + month) + "-");
		dateBuff.append((day >= 10 ? day : "0" + day) + " ");
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		
		dateBuff.append((hour >= 10 ? hour : "0" + hour) + ":");
		dateBuff.append((minute >= 10 ? minute : "0" + minute) + ":");
		dateBuff.append(second >= 10 ? second : "0" + second);
		
		return dateBuff.toString();
	}
}
