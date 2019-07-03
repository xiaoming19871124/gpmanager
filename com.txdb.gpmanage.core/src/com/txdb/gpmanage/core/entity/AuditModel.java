package com.txdb.gpmanage.core.entity;

import java.lang.reflect.Field;
import java.util.Date;

import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.NoToDbTag;

public class AuditModel {

	private Date event_time;
	private String user_name;
	private String database_name;
	private String process_id;
	private String thread_id;
	private String remote_host;
	private String remote_port;
	private Date session_start_time;
	private int transaction_id;
	private Text gp_session_id;
	private Text gp_command_count;
	private Text gp_segment;
	private Text slice_id;
	private Text distr_tranx_id;
	private Text local_tranx_id;
	private Text sub_tranx_id;
	private String event_severity;
	private String sql_state_code;
	private Text event_message;
	private Text event_detail;
	private Text event_hint;
	private Text internal_query;
	private int internal_query_pos;
	private Text event_context;
	private Text debug_query_string;
	private int error_cursor_pos;
	private Text func_name;
	private Text file_name;
	private int file_line;
	private Text stack_trace;

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

	public Text getGp_session_id() {
		return gp_session_id;
	}

	public void setGp_session_id(Text gp_session_id) {
		this.gp_session_id = gp_session_id;
	}

	public Text getGp_command_count() {
		return gp_command_count;
	}

	public void setGp_command_count(Text gp_command_count) {
		this.gp_command_count = gp_command_count;
	}

	public Text getGp_segment() {
		return gp_segment;
	}

	public void setGp_segment(Text gp_segment) {
		this.gp_segment = gp_segment;
	}

	public Text getSlice_id() {
		return slice_id;
	}

	public void setSlice_id(Text slice_id) {
		this.slice_id = slice_id;
	}

	public Text getDistr_tranx_id() {
		return distr_tranx_id;
	}

	public void setDistr_tranx_id(Text distr_tranx_id) {
		this.distr_tranx_id = distr_tranx_id;
	}

	public Text getLocal_tranx_id() {
		return local_tranx_id;
	}

	public void setLocal_tranx_id(Text local_tranx_id) {
		this.local_tranx_id = local_tranx_id;
	}

	public Text getSub_tranx_id() {
		return sub_tranx_id;
	}

	public void setSub_tranx_id(Text sub_tranx_id) {
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

	public Text getEvent_message() {
		return event_message;
	}

	public void setEvent_message(Text event_message) {
		this.event_message = event_message;
	}

	public Text getEvent_detail() {
		return event_detail;
	}

	public void setEvent_detail(Text event_detail) {
		this.event_detail = event_detail;
	}

	public Text getEvent_hint() {
		return event_hint;
	}

	public void setEvent_hint(Text event_hint) {
		this.event_hint = event_hint;
	}

	public Text getInternal_query() {
		return internal_query;
	}

	public void setInternal_query(Text internal_query) {
		this.internal_query = internal_query;
	}

	public int getInternal_query_pos() {
		return internal_query_pos;
	}

	public void setInternal_query_pos(int internal_query_pos) {
		this.internal_query_pos = internal_query_pos;
	}

	public Text getEvent_context() {
		return event_context;
	}

	public void setEvent_context(Text event_context) {
		this.event_context = event_context;
	}

	public Text getDebug_query_string() {
		return debug_query_string;
	}

	public void setDebug_query_string(Text debug_query_string) {
		this.debug_query_string = debug_query_string;
	}

	public int getError_cursor_pos() {
		return error_cursor_pos;
	}

	public void setError_cursor_pos(int error_cursor_pos) {
		this.error_cursor_pos = error_cursor_pos;
	}

	public Text getFunc_name() {
		return func_name;
	}

	public void setFunc_name(Text func_name) {
		this.func_name = func_name;
	}

	public Text getFile_name() {
		return file_name;
	}

	public void setFile_name(Text file_name) {
		this.file_name = file_name;
	}

	public int getFile_line() {
		return file_line;
	}

	public void setFile_line(int file_line) {
		this.file_line = file_line;
	}

	public Text getStack_trace() {
		return stack_trace;
	}

	public void setStack_trace(Text stack_trace) {
		this.stack_trace = stack_trace;
	}
	
	public final String tableName(String serialNo) {
		return "audit_" + serialNo;
	}
	
	public final String generateCreateSql(String serialNo) {
		StringBuffer sqlBuffer = new StringBuffer("create table audit_" + serialNo + " (");

		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			NoToDbTag noToDb = field.getAnnotation(NoToDbTag.class);
			if (noToDb != null)
				continue;
			
			String columnStr = field.getName();
			String type = field.getType().getName();
			if (type.contains("int"))
				columnStr += " int";
			else if (type.contains("Date"))
				columnStr += " timestamp with time zone";
			else if (type.contains("Text"))
				columnStr += " text default ''";
			else
				columnStr += " varchar(100) default ''";

			String splitter = (field == fields[fields.length - 1] ? ")" : ", ");
			FieldTag fieldTag = field.getAnnotation(FieldTag.class);
			if (fieldTag != null && fieldTag.constraint().equals("FOREIGN KEY")) {
				columnStr += (", " + fieldTag.constraint() + "(" + field.getName() + ") REFERENCES " + fieldTag.constraintTable() + "(" + field.getName() + ")");
				columnStr += (" ON DELETE CASCADE ON UPDATE CASCADE" + splitter);
			} else
				columnStr += (fieldTag == null ? splitter : " " + fieldTag.constraint() + splitter);
			
			sqlBuffer.append(columnStr);
		}
		return sqlBuffer.toString();
	}
}
