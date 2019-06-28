package com.txdb.gpmanage.core.entity;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.core.entity.annotation.NoToDbTag;

public class AuditEntity {
	
	@NoToDbTag
	private int limit = 0;
	@NoToDbTag
	private int offset = 0;
	@NoToDbTag
	private int totalCount;

	private Date event_time;
	private String user_name;
	private String database_name;
	private String process_id;
	private String thread_id;
	private String remote_host;
	private String remote_port;
	private Date session_start_time;
	private int transaction_id;
	private String gp_session_id;
	private String gp_command_count;
	private String gp_segment;
	private String slice_id;
	private String distr_tranx_id;
	private String local_tranx_id;
	private String sub_tranx_id;
	private String event_severity;
	private String sql_state_code;
	private String event_message;
	private String event_detail;
	private String event_hint;
	private String internal_query;
	private int internal_query_pos;
	private String event_context;
	private String debug_query_string;
	private int error_cursor_pos;
	private String func_name;
	private String file_name;
	private int file_line;
	private String stack_trace;

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

	public Date getEvent_time() {
		return event_time;
	}

	public void setEvent_time(Date event_time) {
		this.event_time = event_time;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getDatabase_name() {
		return database_name;
	}

	public void setDatabase_name(String database_name) {
		this.database_name = database_name;
	}

	public String getProcess_id() {
		return process_id;
	}

	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}

	public String getThread_id() {
		return thread_id;
	}

	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}

	public String getRemote_host() {
		return remote_host;
	}

	public void setRemote_host(String remote_host) {
		this.remote_host = remote_host;
	}

	public String getRemote_port() {
		return remote_port;
	}

	public void setRemote_port(String remote_port) {
		this.remote_port = remote_port;
	}

	public Date getSession_start_time() {
		return session_start_time;
	}

	public void setSession_start_time(Date session_start_time) {
		this.session_start_time = session_start_time;
	}

	public int getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(int transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getGp_session_id() {
		return gp_session_id;
	}

	public void setGp_session_id(String gp_session_id) {
		this.gp_session_id = gp_session_id;
	}

	public String getGp_command_count() {
		return gp_command_count;
	}

	public void setGp_command_count(String gp_command_count) {
		this.gp_command_count = gp_command_count;
	}

	public String getGp_segment() {
		return gp_segment;
	}

	public void setGp_segment(String gp_segment) {
		this.gp_segment = gp_segment;
	}

	public String getSlice_id() {
		return slice_id;
	}

	public void setSlice_id(String slice_id) {
		this.slice_id = slice_id;
	}

	public String getDistr_tranx_id() {
		return distr_tranx_id;
	}

	public void setDistr_tranx_id(String distr_tranx_id) {
		this.distr_tranx_id = distr_tranx_id;
	}

	public String getLocal_tranx_id() {
		return local_tranx_id;
	}

	public void setLocal_tranx_id(String local_tranx_id) {
		this.local_tranx_id = local_tranx_id;
	}

	public String getSub_tranx_id() {
		return sub_tranx_id;
	}

	public void setSub_tranx_id(String sub_tranx_id) {
		this.sub_tranx_id = sub_tranx_id;
	}

	public String getEvent_severity() {
		return event_severity;
	}

	public void setEvent_severity(String event_severity) {
		this.event_severity = event_severity;
	}

	public String getSql_state_code() {
		return sql_state_code;
	}

	public void setSql_state_code(String sql_state_code) {
		this.sql_state_code = sql_state_code;
	}

	public String getEvent_message() {
		return event_message;
	}

	public void setEvent_message(String event_message) {
		this.event_message = event_message;
	}

	public String getEvent_detail() {
		return event_detail;
	}

	public void setEvent_detail(String event_detail) {
		this.event_detail = event_detail;
	}

	public String getEvent_hint() {
		return event_hint;
	}

	public void setEvent_hint(String event_hint) {
		this.event_hint = event_hint;
	}

	public String getInternal_query() {
		return internal_query;
	}

	public void setInternal_query(String internal_query) {
		this.internal_query = internal_query;
	}

	public int getInternal_query_pos() {
		return internal_query_pos;
	}

	public void setInternal_query_pos(int internal_query_pos) {
		this.internal_query_pos = internal_query_pos;
	}

	public String getEvent_context() {
		return event_context;
	}

	public void setEvent_context(String event_context) {
		this.event_context = event_context;
	}

	public String getDebug_query_string() {
		return debug_query_string;
	}

	public void setDebug_query_string(String debug_query_string) {
		this.debug_query_string = debug_query_string;
	}

	public int getError_cursor_pos() {
		return error_cursor_pos;
	}

	public void setError_cursor_pos(int error_cursor_pos) {
		this.error_cursor_pos = error_cursor_pos;
	}

	public String getFunc_name() {
		return func_name;
	}

	public void setFunc_name(String func_name) {
		this.func_name = func_name;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public int getFile_line() {
		return file_line;
	}

	public void setFile_line(int file_line) {
		this.file_line = file_line;
	}

	public String getStack_trace() {
		return stack_trace;
	}

	public void setStack_trace(String stack_trace) {
		this.stack_trace = stack_trace;
	}
	
	public final String tableName(String serialNo) {
		return "audit_" + serialNo;
	}
	
	public static List<AuditEntity> toList(List<Map<String, Object>> dataList) throws SQLException {
		List<AuditEntity> auditInfoList = new ArrayList<AuditEntity>();
		AuditEntity auditInfo = null;
		for (Map<String,Object> rowMap : dataList) {
			auditInfo = new AuditEntity();
			
			auditInfo.setEvent_time(new Date(((Timestamp) rowMap.get("event_time")).getTime()));
			auditInfo.setUser_name((String) rowMap.get("user_name"));
			auditInfo.setDatabase_name((String) rowMap.get("database_name"));
			auditInfo.setProcess_id((String) rowMap.get("process_id"));
			auditInfo.setThread_id((String) rowMap.get("thread_id"));
			auditInfo.setRemote_host((String) rowMap.get("remote_host"));
			auditInfo.setRemote_port((String) rowMap.get("remote_port"));
			
			Object session_start_time = rowMap.get("session_start_time");
			auditInfo.setSession_start_time(session_start_time == null ? null : new Date(((Timestamp) session_start_time).getTime()));
			auditInfo.setTransaction_id((int) rowMap.get("transaction_id"));
			auditInfo.setGp_session_id((String) rowMap.get("gp_session_id"));
			auditInfo.setGp_command_count((String) rowMap.get("gp_command_count"));
			auditInfo.setGp_segment((String) rowMap.get("gp_segment"));
			auditInfo.setSlice_id((String) rowMap.get("slice_id"));
			auditInfo.setDistr_tranx_id((String) rowMap.get("distr_tranx_id"));
			auditInfo.setLocal_tranx_id((String) rowMap.get("local_tranx_id"));
			auditInfo.setSub_tranx_id((String) rowMap.get("sub_tranx_id"));
			auditInfo.setEvent_severity((String) rowMap.get("event_severity"));
			auditInfo.setSql_state_code((String) rowMap.get("sql_state_code"));
			auditInfo.setEvent_message((String) rowMap.get("event_message"));
			auditInfo.setEvent_detail((String) rowMap.get("event_detail"));
			auditInfo.setEvent_hint((String) rowMap.get("event_hint"));
			auditInfo.setInternal_query((String) rowMap.get("internal_query"));
			
			Object internal_query_pos = rowMap.get("internal_query_pos");
			auditInfo.setInternal_query_pos(internal_query_pos == null ? 0 : (int) internal_query_pos);
			auditInfo.setEvent_context((String) rowMap.get("event_context"));
			auditInfo.setDebug_query_string((String) rowMap.get("debug_query_string"));
			
			Object error_cursor_pos = rowMap.get("error_cursor_pos");
			auditInfo.setError_cursor_pos(error_cursor_pos == null ? 0 : (int) error_cursor_pos);
			auditInfo.setFunc_name((String) rowMap.get("func_name"));
			auditInfo.setFile_name((String) rowMap.get("file_name"));
			
			Object file_line = rowMap.get("file_line");
			auditInfo.setFile_line(file_line == null ? 0 : (int) file_line);
			auditInfo.setStack_trace((String) rowMap.get("stack_trace"));
			auditInfoList.add(auditInfo);
		}
		return auditInfoList;
	}
}
