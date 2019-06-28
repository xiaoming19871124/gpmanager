package com.txdb.gpmanage.core.gp.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.JSchException;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.gp.entry.CmdLsbInfo;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;

public interface IExecuteDao {
	
	public static final int DEFAULT_SSH_PORT = 22;
	public static final int MSG_RECEIVE_TIME_MAX = 5000;
	
	public static final int DEFAULT_GP_PORT = 5432;
	public static final String DRIVER_CLASS = "org.postgresql.Driver";
	
	CmdLsbInfo getCmdLsbInfo();
	
	void setCallback(UICallBack callback);
	void callback(String callbackMsg);
	
	String getExecutorName();
	
	String getHost();
	String getHostname();
	
	String getSshUserName();
	String getSshPassword();
	int getSshPort();
	
	GPResultSet login();
	void logout();
	
	String getJdbcUsername();
	String getJdbcPassword();
	int getJdbcPort();
	String getJdbcDatabase();
	String getJdbcUrl();
	
	boolean isSshConnected();
	boolean isJdbcConnected();
	
	
	
	/**
	 * 执行简单命令
	 * @param command
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	GPResultSet executeCommand(String command) throws JSchException, IOException;
	
	/**
	 * 执行交互命令
	 * @param command
	 * @param stepStr
	 * @param readMode 阅读模式（true 自动浏览需要翻页的内容, false 常规浏览模式<默认>）
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	GPResultSet executeInteractiveCommand(String command, String[] stepsStr, boolean readMode) throws JSchException, IOException, InterruptedException;

	/**
	 * 执行过程命令
	 * @param command
	 * @param stepStr
	 * @param readMode 阅读模式（true 自动浏览需要翻页的内容, false 常规浏览模式<默认>）
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	GPResultSet executeProcessCommand(String command, String[] stepsStr, boolean readMode) throws JSchException, IOException, InterruptedException;
	
	/**
	 * 执行交互命令
	 * @param command
	 * @param stepsStr
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	GPResultSet executeInteractiveCommand(String command, String[] stepsStr) throws JSchException, IOException, InterruptedException;
	
	/**
	 * 执行过程命令
	 * @param command
	 * @param stepsStr
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	GPResultSet executeProcessCommand(String command, String[] stepsStr) throws JSchException, IOException, InterruptedException;
	
	/**
	 * 执行交互命令
	 * @param command
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	GPResultSet executeInteractiveCommand(String command) throws JSchException, IOException, InterruptedException;
	
	/**
	 * 获取命令行结束标志
	 * @return
	 */
	String getCmdHeader();
	
	/**
	 * 判断远程文件路径是否存在
	 * @param pathType (eg:d)
	 * @param filePath (eg:/home/gpadmin/)
	 * @param message (eg:filePath exists)
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	boolean isDirExists(String pathType, String filePath) throws JSchException, IOException;
	
	/**
	 * 将输入流的数据上传到sftp作为文件
	 * @param directory 上传到该目录
	 * @param sftpFileName sftp端文件名
	 * @param input 输入流
	 * @return
	 * @throws JSchException
	 */
	boolean upload(String directory, String sftpFileName, InputStream input) throws JSchException;
	
	/**
	 * 上传单个文件
	 * @param directory 上传到sftp目录 (eg:/home/test/)
	 * @param uploadFile 要上传的文件,包括路径(eg:/home/123.txt)
	 * @return
	 * @throws FileNotFoundException
	 * @throws JSchException
	 */
	boolean upload(String directory, String uploadFile) throws FileNotFoundException, JSchException;
	
	/**
	 * 下载单个文件
	 * @param src linux服务器文件地址
	 * @param dst 本地存放地址
	 * @return
	 * @throws JSchException
	 */
	boolean download(String src, String dst) throws JSchException;
	
	/**
	 * 创建PSQL（创建数据库连接）
	 * @param username
	 * @param password
	 * @param port
	 * @param database
	 * @return
	 */
	void createPSQL(String username, String password, int port, String database) throws ClassNotFoundException, SQLException;
	void recreatePSQL() throws ClassNotFoundException, SQLException;
	
	/**
	 * 执行SQL查询
	 * @param sql
	 * @return
	 */
	List<Map<String, Object>> executeQuery(String sql) throws SQLException;
	
	/**
	 * 执行SQL更新
	 * @param sql
	 * @return
	 */
	int executeUpdate(String sql) throws SQLException;
	
	/**
	 * 执行SQL（对象级）
	 * @param sql
	 * @return
	 */
	boolean execute(String sql) throws SQLException;
}
