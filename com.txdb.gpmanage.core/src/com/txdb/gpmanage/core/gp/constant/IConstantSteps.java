package com.txdb.gpmanage.core.gp.constant;

public interface IConstantSteps {

	public static final String AGREEMENT_YN_S1 = "Yy/Nn";
	public static final String AGREEMENT_YN_S2 = "Yy|Nn";
	public static final String AGREEMENT_YN_S1_F = "yes/no";
	public static final String AGREEMENT_YN_S2_F = "yes|no";
	public static final String AGREEMENT_INSTALL_LICENSE = "Do you accept the Pivotal Database license agreement";
	public static final String AGREEMENT_INSTALL_CONFIRM = "Install Greenplum Database into";
	public static final String AGREEMENT_INSTALL_MIRRORS = "Continue with add mirrors procedure";
	public static final String AGREEMENT_EXPAND_GENERATE = "Would you like to initiate a new System Expansion";
	public static final String AGREEMENT_EXPAND_STANDBY = "Do you want to continue with standby master initialization";
	public static final String AGREEMENT_STARTUP_GP = "Continue with Greenplum instance startup";
	public static final String AGREEMENT_SHUTDOWN_GP = "Continue with Greenplum instance shutdown";
	
	public static final String ABNORMAL_FATAL = "[FATAL]";
	public static final String ABNORMAL_ERROR = "[ERROR]";
	public static final String ABNORMAL_CRITICAL = "[CRITICAL]";
	public static final String ABNORMAL_INSTALLER = "Installer will only install on RedHat/CentOS x86_64";
	public static final String ABNORMAL_KILL = "Failed to kill processes for segment";

	public static final String INPUT_PASSWORD_EXKEYS = "Enter password for";
	public static final String INPUT_PASSWORD_SEG_INSTALL = "'s password";
	public static final String INPUT_PASSWORD_STANDBY = "'s password";
	public static final String INPUT_INSTALL_PATH = "Provide the installation path for Greenplum Database";
	public static final String INPUT_INSTALL_PATH_MIRROR = "Enter mirror segment data directory location";
	public static final String INPUT_EXPAND_SEGMENT_NUMBER = "How many new primary segments per host do you want to add";
	public static final String INPUT_EXPAND_SEGMENT_DIR = "Enter new primary data directory";
	public static final String INPUT_EXPAND_MIRROR_DIR = "Enter new mirror data directory";
	public static final String INPUT_EXPAND_SEGMENT_BLANK = "Enter a blank line to only add segments to existing hosts";
	public static final String INPUT_EXPAND_STRATEGY = "What type of mirroring strategy would you like";

	public static final String FINAL_SUCCESS_INSTALL = "Installation complete";
	public static final String FINAL_PASSWORD_BAD = "bad password";
	
	public static final String FINAL_GPSSH_MKDIR = "] mkdir -p";
	public static final String FINAL_GPSSH_CHOWN = "] chown";
}
