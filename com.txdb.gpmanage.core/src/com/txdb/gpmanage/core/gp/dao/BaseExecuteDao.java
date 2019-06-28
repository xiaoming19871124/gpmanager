package com.txdb.gpmanage.core.gp.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.entry.CmdLsbInfo;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpManageService;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseExecuteDao implements IExecuteDao, IConstantsCmds {

	protected String host;
	protected String hostname;
	protected String username;
	protected String password;
	protected int sshPort = DEFAULT_SSH_PORT;
	
	protected String jdbc_username;
	protected String jdbc_password;
	protected int jdbc_port = DEFAULT_GP_PORT;
	protected String jdbc_database;
	
	private Session session;
	private ChannelSftp sftp;
	private ChannelExec exec;
	private ChannelShell shell;
	
	private Connection connection;
	private Statement statement;
	
	private CmdLsbInfo lsbInfo;
	private UICallBack callback;
	
	public BaseExecuteDao(String host) {
		this(host, null, null, -1);
	}
	
	public BaseExecuteDao(String host, String username, String password) {
		this(host, username, password, -1);
	}
	
	public BaseExecuteDao(String host, String username, String password, int sshPort) {
		this.host = host;
		this.username = username;
		this.password = password;
		if (sshPort != -1)
			this.sshPort = sshPort;
	}
	
	@Override
	public CmdLsbInfo getCmdLsbInfo() {
		return lsbInfo;
	}
	
	@Override
	public void setCallback(UICallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void callback(String callbackMsg) {
		if (callback != null)
			callback.refreshUI(callbackMsg);
	}
	
	private String executorName = "Initializing_" + hashCode();
	
	@Override
	public String getExecutorName() {
		return executorName;
	}
	
	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}
	
	@Override
	public String getHost() {
		return host;
	}
	
	@Override
	public String getHostname() {
		return hostname;
	}
	
	@Override
	public int getSshPort() {
		return sshPort;
	}
	
	@Override
	public String getSshUserName() {
		return username;
	}
	
	@Override
	public String getSshPassword() {
		return password;
	}
	
	@Override
	public String getJdbcUsername() {
		return jdbc_username;
	}
	
	@Override
	public String getJdbcPassword() {
		return jdbc_password;
	}
	
	@Override
	public int getJdbcPort() {
		return jdbc_port;
	}
	
	@Override
	public String getJdbcDatabase() {
		return jdbc_database;
	}
	
	@Override
	public String getJdbcUrl() {
		return String.format(JDBC_URL, host, jdbc_port, jdbc_database);
	}
	
	@Override
	public GPResultSet login() {
		GPResultSet rs = new GPResultSet(this);
		if (this.username == null) {
			LogUtil.info("(" + getExecutorName() + ") Skipped the Operation of SSH Connection.");
			return rs;
		}
		
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(username, host, sshPort);
			session.setConfig("PreferredAuthentications", "password");
			session.setConfig("StrictHostKeyChecking", "no");
			
			if (password != null)
				session.setPassword(password);
			session.connect();
			
			LogUtil.info("(" + getExecutorName() + ")Session is connected");
			rs = fillLsbInfo();
			if (rs.isSuccessed()) 
				LogUtil.info("(" + getExecutorName() + ") Load LsbInfo Successed.");
			else {
				LogUtil.error("(" + getExecutorName() + ") Load LsbInfo failed!");
				return rs;
			}
			shell = (ChannelShell) session.openChannel("shell");
			shell.connect(3 * 1000);
			LogUtil.info("(" + getExecutorName() + ")Shell is connected");
			
			return rs;
			
		} catch (JSchException e) {
			LogUtil.error("(" + getExecutorName() + ") login exception: " + e.getMessage());
			rs.setOutputEpt(e.getMessage());
			return rs;
		}
	}
	
	private GPResultSet fillLsbInfo() {
		GPResultSet rs = new GPResultSet(this);
		String osName = "Unknow_" + hashCode();
		try {
			// LsbInfo
			String cmd = String.format(LSB_RELEASE, "-a");
			GPResultSet rsVersion = executeCommand(cmd);
			lsbInfo = new CmdLsbInfo(rsVersion.getOutputMsg());
			if (lsbInfo.getDistributorID() != null)
				osName = lsbInfo.getDistributorID();
			osName += "_" + hashCode();
			
			// Hostname
			GPResultSet hostnameRs = executeCommand("hostname");
			if (hostnameRs.isSuccessed())
				hostname = hostnameRs.getOutputMsg();
			
		} catch (Exception e) {
			LogUtil.error("(" + getExecutorName() + ") initExecutor exception: " + e.getMessage());
			rs.setOutputEpt(e.getMessage());
			setExecutorName(osName);
			return rs;
		}
		setExecutorName(osName);
		rs.setSuccessed(true);
		return rs;
	}
	
	@Override
	public void logout() {
		if (shell != null && shell.isConnected()) {
			shell.disconnect();
			LogUtil.info("(" + getExecutorName() + ")SSH Shell is disconnected");
		}
		if (session != null && session.isConnected()) {
			session.disconnect();
			LogUtil.info("(" + getExecutorName() + ")SSH Session is disconnected");
		} else {
			LogUtil.info("(" + getExecutorName() + ")SSH Session is already closed");
		}
		try {
			closePSQL();
		} catch (SQLException e) {
			LogUtil.error("(" + getExecutorName() + ")Close psql failed: " + e.getMessage());
		}
		setExecutorName("unknow");
	}
	
	@Override
	public boolean isSshConnected() {
		return (session != null && session.isConnected());
	}
	
	@Override
	public boolean isJdbcConnected() {
		try {
			return (connection != null && !connection.isClosed());
		} catch (SQLException e) {
			LogUtil.error("(" + getExecutorName() + ")Check Jdbc Connection failed: " + e.getMessage());
			return false;
		}
	}
	
	@Override
	public GPResultSet executeCommand(String command) throws JSchException, IOException {
		String outputMsg = "";
		String outputErr = "";
		Long startTimestamp = System.currentTimeMillis();
		GPResultSet result = new GPResultSet(this);
		if (session != null && session.isConnected() && command != null && !"".equals(command)) {
			exec = (ChannelExec) session.openChannel("exec");
			exec.setCommand(command);
			exec.setInputStream(null);
			exec.setErrStream(null);

			InputStream in = exec.getInputStream();
			InputStream errorIn = exec.getErrStream();

			exec.connect();
			result.setExecutedCmd(command);
			LogUtil.info("(" + getExecutorName() + ")Execute command: " + command);
			
			while (true) {
				outputMsg += readInputStream(in, 1024);
				outputErr += readInputStream(errorIn, 1024, true);
				
				if (exec.isClosed()) {
					if (in.available() > 0 || errorIn.available() > 0)
						continue;
					break;
				}
			}
			
			// TODO 在此添加控制台输出回执
//			callback(outputMsg);
//			callback(outputErr);
			
			result.setOutputMsg(outputMsg.trim());
			result.setOutputErr(outputErr.trim());
			result.setSuccessed(outputErr.length() <= 0);
			
			exec.disconnect();
		} else {
			LogUtil.warn("(" + getExecutorName() + ")Need connect SSH Session first.");
			result.setOutputErr("Need connect SSH Session first.");
		}
		result.setTimeCost(System.currentTimeMillis() - startTimestamp);
		return result;
	}
	
	public String getCmdHeader() {
		try {
			GPResultSet headerRs = executeShellCommand("\n", new String[] { "@", "~", "#", "$", "=>" }, false);
			return headerRs.getOutputMsg() == null ? null : headerRs.getOutputMsg().trim();
		} catch (Exception e) {
			return null;
		}
	}
	
	private String[] CURR_SHELL_CONDITIONS = {};
	
	@Override
	public GPResultSet executeInteractiveCommand(String command) throws JSchException, IOException, InterruptedException {
		return executeInteractiveCommand(command, null, false);
	}
	
	@Override
	public GPResultSet executeInteractiveCommand(String command, String[] stepsStr) throws JSchException, IOException, InterruptedException {
		return executeInteractiveCommand(command, stepsStr, false);
	}
	
	@Override
	public GPResultSet executeProcessCommand(String command, String[] stepsStr) throws JSchException, IOException, InterruptedException {
		return executeInteractiveCommand(command, stepsStr, false);
	}
	
	@Override
	public GPResultSet executeInteractiveCommand(String command, String[] stepsStr, boolean readMode) throws JSchException, IOException, InterruptedException {
		// 【null】-使用上一次条件列表，【length <= 0】-获取CmdHeader信息
		if (stepsStr != null) {
			String cmdHeader = getCmdHeader();
//			cmdHeader = cmdHeader.trim().split("\n")[0].trim();
			String[] headerFragments = cmdHeader.trim().split("\n");
			cmdHeader = headerFragments[headerFragments.length - 1].trim();
			
			CURR_SHELL_CONDITIONS = stepsStr;
			if (cmdHeader != null) {
				CURR_SHELL_CONDITIONS = new String[stepsStr.length + 1];
				System.arraycopy(stepsStr, 0, CURR_SHELL_CONDITIONS, 0, stepsStr.length);
				CURR_SHELL_CONDITIONS[stepsStr.length] = cmdHeader;
			}
		}
		return executeShellCommand(command, CURR_SHELL_CONDITIONS, readMode);
	}
	
	@Override
	public GPResultSet executeProcessCommand(String command, String[] stepsStr, boolean readMode) throws JSchException, IOException, InterruptedException {
		return executeShellCommand(command, stepsStr, readMode);
	}
	
	public GPResultSet executeShellCommand(String command, String[] stepsStr, boolean readMode) throws JSchException, IOException, InterruptedException {
		StringBuffer outputMsg = new StringBuffer("");
		Long startTimestamp = System.currentTimeMillis();
		GPResultSet result = new GPResultSet(this);
		if (stepsStr == null || stepsStr.length <= 0) {
			LogUtil.error("(" + getExecutorName() + ") Cannot get find command in stepsStr, executor will exit.");
			return result;
		}
		if (session != null && session.isConnected() && command != null && !command.equals("")) {
			OutputStream outputStream = shell.getOutputStream();
			InputStream inputStream = shell.getInputStream();
			
			LogUtil.info("(" + getExecutorName() + ") Execute Active command: " + command + ", Steps Count: " + stepsStr.length);
			result.setExecutedCmd(command);
			
			if (!command.endsWith("\n"))
				command += "\n";
			outputStream.write(command.getBytes());
			outputStream.flush();
			
			byte[] temp = new byte[1024];
			while (inputStream.available() < 0)
				Thread.sleep(5 * 1000);
			
			int receiveTime = 0;
			boolean stepOverFlag = false;
			boolean procOverFlag = false;
			while (!stepOverFlag) {
				if (inputStream.available() <= 0)
					continue;
				
				receiveTime++;
				int i = inputStream.read(temp, 0, 1024);
				String segmentStr = new String(temp, 0, i);
				if ("".equals(segmentStr.trim()))
					continue;
				
				// TODO 在此添加控制台输出回执
				String tempStr = segmentStr.trim().replaceAll("\r\n", "\n");
				if (!IConstantsCmds.ECHO_CHK.equals(command) && !"\n".equals(command)) {
					System.err.println(tempStr);
//					LogUtil.info("(" + getExecutorName() + ") Active Message: " + tempStr);
					callback(segmentStr);
				}
				
				outputMsg.append(segmentStr);
				stepOverFlag = (receiveTime >= MSG_RECEIVE_TIME_MAX);
				for (String stepStr : stepsStr) {
					int lastIndex = outputMsg.lastIndexOf(stepStr);
					if (lastIndex != -1) {
						result.setMsgKeyword(stepStr);
						if (stepStr.equals(stepsStr[stepsStr.length - 1]) && stepStr.length() > 1 && !stepStr.equals("=>")) {
							
							// 过滤掉 [gpadmin@mdw ~]$ 在起始位置的情况
							String tempMsg = outputMsg.toString().trim();
							int idx = tempMsg.indexOf(stepStr);
							int lastIdx = tempMsg.lastIndexOf(stepStr);
							if (idx == lastIdx && idx == 0)
								continue;
							
							// 移除输出信息最后的命令头 [gpadmin@mdw ~]$ 
							if (outputMsg.length() == (lastIndex + stepStr.length() + 1)) {
								outputMsg = new StringBuffer(outputMsg.substring(0, lastIndex));
								procOverFlag = true;
							}
						}
						stepOverFlag = true;
						break;
					}
				}
				// ReadMode need refresh outputMessage
				if (readMode) {
					outputStream.write("\n".getBytes());
					outputStream.flush();
				}
			}
			// 主要针对读取license内容，对一些换行符乱码做处理
			if (readMode) {
				String c1 = "\\[7m--More--\\[m\\[K";
				String c2 = "\\[7m--More--\\[m";
				String c3 = "\\[K";
				
				String outputMsgStr = outputMsg.toString();
				outputMsgStr = outputMsgStr.replaceAll(c1 + "\r", "");
				outputMsgStr = outputMsgStr.replaceAll(c1, "");
				outputMsgStr = outputMsgStr.replaceAll(c2 + "\r", "");
				outputMsgStr = outputMsgStr.replaceAll(c2, "");
				outputMsgStr = outputMsgStr.replaceAll(c3 + "\r", "");
				outputMsgStr = outputMsgStr.replaceAll(c3, "");
				outputMsg = new StringBuffer(outputMsgStr);
			}
//			result.setOutputMsg(outputMsg.toString());
			result.setOutputMsg(outputMsg.toString().replaceAll("\r\n", "\n"));
			
			boolean isSuccessed = true;
			if (procOverFlag && !IConstantsCmds.ECHO_CHK.equals(command)) {
				GPResultSet finalRs = executeInteractiveCommand(IConstantsCmds.ECHO_CHK, null);
				String[] codeFragments = finalRs.getOutputMsg().trim().split("\n");
				isSuccessed = (codeFragments.length >= 2 && "0".equals(codeFragments[1]));
				callback("\n");
				
				if (!isSuccessed) {
					result.setOutputErr(result.getOutputMsg());
					result.setOutputMsg("");
				}
			}
			result.setSuccessed(isSuccessed);
			
		} else {
			LogUtil.warn("(" + getExecutorName() + ")Need connect SSH Session first.");
			result.setOutputErr("Need connect SSH Session first.");
		}
		result.setTimeCost(System.currentTimeMillis() - startTimestamp);
		return result;
	}

	@Override
	public boolean isDirExists(String pathType, String filePath) throws JSchException, IOException {
		boolean flag = false;
		if (session != null && session.isConnected() && filePath != null && !"".equals(filePath)) {
			Channel channel = session.openChannel("exec");
			exec = (ChannelExec) channel;
			exec.setCommand("test -" + pathType + " " + filePath + " && echo filePath exists || echo filePath not exists");
			exec.setInputStream(null);
			exec.setErrStream(System.err);
			
			InputStream inputStream = exec.getInputStream();
			exec.connect();
			LogUtil.info("(" + getExecutorName() + ")exec channel is connected");
			
			while (true) {
				String outputMsg = readInputStream(inputStream, 1024);
				if (outputMsg.trim().equals("filePath exists"))
					flag = true;
				
				if (exec.isClosed()) {
					if (inputStream.available() > 0)
						continue;
					LogUtil.error("(" + getExecutorName() + ")exit-status:" + exec.getExitStatus());
					break;
				}
			}
			exec.disconnect();
		}
		return flag;
	}
	
	@Override
	public boolean upload(String directory, String sftpFileName, InputStream input) throws JSchException {
		if (session != null && session.isConnected()) {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			LogUtil.info("(" + getExecutorName() + ")sftp channel is connected");

			sftp = (ChannelSftp) channel;
			LogUtil.info("(" + getExecutorName() + ")sftp connected host " + host + " successed");
			try {
				sftp.mkdir(directory);
			} catch (SftpException e) {}
			try {
				sftp.cd(directory);
				sftp.put(input, sftpFileName);
				LogUtil.info("(" + getExecutorName() + ")sftp upload successed, fileName: " + sftpFileName);
				return true;
				
			} catch (SftpException e) {
				LogUtil.error("(" + getExecutorName() + ") upload exception: " + e.getMessage());
			}
			sftp.disconnect();
		}
		return false;
	}
	
	@Override
	public boolean upload(String directory, String uploadFile) throws FileNotFoundException, JSchException {
		File file = new File(uploadFile);
		return upload(directory, file.getName(), new FileInputStream(file));
	}
	
	@Override
	public boolean download(String src, String dst) throws JSchException {
		if (session != null && session.isConnected()) {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			LogUtil.info("(" + getExecutorName() + ")sftp channel is connected");

			sftp = (ChannelSftp) channel;
			LogUtil.info("(" + getExecutorName() + ")sftp connected host " + host + " successed");
			try {
				sftp.get(src, dst);
				
				LogUtil.info("(" + getExecutorName() + ")sftp download successed, src: " + src);
				return true;
				
			} catch (SftpException e) {
				LogUtil.error("(" + getExecutorName() + ") download exception: " + e.getMessage());
			}
			sftp.disconnect();
		}
		return false;
	}

	protected String readInputStream(InputStream inputStream, int readLen) throws IOException {
		return readInputStream(inputStream, readLen, false);
	}
	
	protected String readInputStream(InputStream inputStream, int readLen, boolean print) throws IOException {
		String outputMsg = "";
		byte[] temp = new byte[readLen];
		while (inputStream.available() > 0) {
			int i = inputStream.read(temp, 0, 1024);
			if (i < 0)
				break;
			outputMsg += new String(temp, 0, i);
			if (print)
				LogUtil.error(">>> (" + getExecutorName() + ") readInputStream outputMsg: " + outputMsg.trim() + " <<<");
		}
		return outputMsg;
	}
	
	@Override
	public void createPSQL(String username, String password, int port, String database) throws ClassNotFoundException, SQLException {
		if (!isSshConnected() && !login().isSuccessed() && this.username != null) {
			throw new SQLException("Auto connect host by SSH failed.");
		}
		closePSQL();
		Class.forName(DRIVER_CLASS);
		port = (port < 0 ? DEFAULT_GP_PORT : port);
		String url = String.format(JDBC_URL, host, port, database);
		connection = DriverManager.getConnection(url, username, password);
		statement = connection.createStatement();
		
		jdbc_username = username;
		jdbc_password = password;
		jdbc_port = port;
		jdbc_database = database;
	}
	
	@Override
	public void recreatePSQL() throws ClassNotFoundException, SQLException {
		createPSQL(jdbc_username, jdbc_password, jdbc_port, jdbc_database);
	}
	
	public Statement getStatement() throws SQLException {
		return connection.createStatement();
	}
	
	private void closePSQL() throws SQLException {
		if (statement != null && !statement.isClosed())
			statement.close();
		if (connection != null && !connection.isClosed())
			connection.close();
	}
	
	@Override
	public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
		if (connection == null || connection.isClosed()) {
			LogUtil.warn("(" + getExecutorName() + ")Cannot execute query, need execute method: <createPsql> first");
			try {
				recreatePSQL();
				LogUtil.info("(" + getExecutorName() + ")Reconnect JDBC complete.");
			} catch (ClassNotFoundException e) {
				LogUtil.error("(" + getExecutorName() + ")Reconnect JDBC failed: " + e.getMessage());
				return null;
			}
		}
		System.err.println("\nQuery Sql: " + sql);
		
		Statement statement = getStatement();
		ResultSet rs = statement.executeQuery(sql);
		ResultSetMetaData md = rs.getMetaData();
		
		int columnCount = md.getColumnCount();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++)
				rowData.put(md.getColumnName(i), rs.getObject(i));
			dataList.add(rowData);
		}
		statement.close();
		rs.close();
		return dataList;
	}
	
	@Override
	public int executeUpdate(String sql) throws SQLException {
		if (connection == null || connection.isClosed()) {
			LogUtil.warn("(" + getExecutorName() + ")Cannot execute update, need execute method: <createPsql> first");
			try {
				recreatePSQL();
				LogUtil.info("(" + getExecutorName() + ")Reconnect JDBC complete.");
			} catch (ClassNotFoundException e) {
				LogUtil.error("(" + getExecutorName() + ")Reconnect JDBC failed: " + e.getMessage());
				return -1;
			}
		}
		return statement.executeUpdate(sql);
	}
	
	@Override
	public boolean execute(String sql) throws SQLException {
		if (connection == null || connection.isClosed()) {
			LogUtil.warn("(" + getExecutorName() + ")Cannot execute, need execute method: <createPsql> first");
			try {
				recreatePSQL();
				LogUtil.info("(" + getExecutorName() + ")Reconnect JDBC complete.");
			} catch (ClassNotFoundException e) {
				LogUtil.error("(" + getExecutorName() + ")Reconnect JDBC failed: " + e.getMessage());
				return false;
			}
		}
		return statement.execute(sql);
	}
	
	public static void main(String[] args) throws Exception {
		Class.forName(DRIVER_CLASS);
		String url = String.format(JDBC_URL, "192.168.73.120", DEFAULT_GP_PORT, "postgres");
		Connection conn = DriverManager.getConnection(url, "gpadmin", "");
		Statement stat = conn.createStatement();
		boolean createRs = stat.execute("create database " + IGpManageService.DEFAULT_EXPAND_DB);
		System.err.println("createRs: " + createRs);
		
//		ResultSet rs = stat.executeQuery("select * from gp_segment_configuration");
//		System.err.println(rs);
	}
}
