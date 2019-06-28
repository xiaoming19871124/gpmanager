package com.txdb.gpmanage.audit.constant;

public interface AuditConstant {

	public static final String fieldFlag = "#";
	public static final String[] logEntryFields = { 
		"event_time#1", 
		"user_name#2", 
		"database_name#3", 
		"process_id#4", 
		"thread_id#5", 
		"remote_host#6", 
		"remote_port#7", 
		"session_start_time#8",
		"transaction_id#9", 
		"gp_session_id#10", 
		"gp_command_count#11", 
		"gp_segment#12", 
		"slice_id#13", 
		"distr_tranx_id#14", 
		"local_tranx_id#15", 
		"sub_tranx_id#16", 
		"event_severity#17",
		"sql_state_code#18", 
		"event_message#19", 
		"event_detail#20", 
		"event_hint#21", 
		"internal_query#22", 
		"internal_query_pos#23", 
		"event_context#24", 
		"debug_query_string#25",
		"error_cursor_pos#26", 
		"func_name#27", 
		"file_name#28", 
		"file_line#29", 
		"stack_trace#30" };
}
