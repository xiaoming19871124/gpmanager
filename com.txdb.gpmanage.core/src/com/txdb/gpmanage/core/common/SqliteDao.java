package com.txdb.gpmanage.core.common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.txdb.gpmanage.core.entity.IGPEntity;
import com.txdb.gpmanage.core.entity.SqlWhere;
import com.txdb.gpmanage.core.entity.impl.GPAuditEntity;
import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.entity.impl.GPMonitorEntity;
import com.txdb.gpmanage.core.entity.impl.MailEntity;
import com.txdb.gpmanage.core.entity.impl.SystemWarningEntity;
import com.txdb.gpmanage.core.log.LogUtil;

/**
 * sqlLite工具类
 * @author ws
 */
public class SqliteDao {
	
	private static SqliteDao instance;
	private Connection connection;

	public static SqliteDao getInstance() {
		if (instance == null)
			instance = new SqliteDao();
		return instance;
	}
	
	private SqliteDao() {
		Location location = Platform.getInstallLocation();
		File fileUrl = new File(location.getURL().getPath() + "gp");
		if (!fileUrl.exists())
			fileUrl.mkdir();
		String url = fileUrl.getPath() + "\\gpdb";
		
		initConnection(url);
		initTable();
	}

	private void initConnection(String url) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + url);
			LogUtil.info("Make connection successed.");

		} catch (ClassNotFoundException | SQLException e) {
			LogUtil.error("Make connection failed!", e);
		}
	}

	private void initTable() {
		// Prepare Statement
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			LogUtil.error("Make statement failed, skipped table creating!", e);
		}
		if (statement == null)
			return;
		
		// 1.0 Create table for Manager
		IGPEntity managerEntity = new GPManagerEntity();
		try {
			managerEntity.create(statement);
			LogUtil.info("Table \"" + managerEntity.getTableName() + "\" has created/skipped.");
			
		} catch (SQLException e) {
			LogUtil.error("Create table \"" + managerEntity.getTableName() + "\" error!", e);
		}
		
		// 2.0 Create table for Monitor
		IGPEntity monitorEntity = new GPMonitorEntity();
		try {
			monitorEntity.create(statement);
			LogUtil.info("Table \"" + monitorEntity.getTableName() + "\" has created/skipped.");
			
		} catch (SQLException e) {
			LogUtil.error("Create table \"" + monitorEntity.getTableName() + "\" error!", e);
		}
		
		// 2.1 Create table for Audit
		IGPEntity auditEntity = new GPAuditEntity();
		try {
			auditEntity.create(statement);
			LogUtil.info("Table \"" + auditEntity.getTableName() + "\" has created/skipped.");
			
		} catch (SQLException e) {
			LogUtil.error("Create table \"" + auditEntity.getTableName() + "\" error!", e);
		}
		
		// 3.0 Create table for Warning
		IGPEntity warningEntity = new SystemWarningEntity();
		try {
			warningEntity.create(statement);
			LogUtil.info("Table \"" + monitorEntity.getTableName() + "\" has created/skipped.");

		} catch (SQLException e) {
			LogUtil.error("Create table \"" + monitorEntity.getTableName() + "\" error!", e);
		}
		
		// 4.0 Create table for mail settings of gp warning
		IGPEntity mailEntity = new MailEntity();
		try {
			mailEntity.create(statement);
			LogUtil.info("Table \"" + mailEntity.getTableName() + "\" has created/skipped.");

		} catch (SQLException e) {
			LogUtil.error("Create table \"" + mailEntity.getTableName() + "\" error!", e);
		}
		
		// Dispose Statement
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			LogUtil.error("Statement close error!", e);
		}
	}
	
	/**
	 * 添加 GPEntity
	 * @param entity
	 */
	public boolean insertGPEntity(IGPEntity entity) {
		// Prepare Statement
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			LogUtil.error("Make statement failed!", e);
		}
		if (statement == null)
			return false;
		
		// 1.0 Insert row into table
		boolean resultFlag = false;
		try {
			entity.insert(statement);
			resultFlag = true;
			LogUtil.info("Insert entity into \"" + entity.getTableName() + "\" successed.");
			
		} catch (SQLException e) {
			LogUtil.error("Insert entity into \"" + entity.getTableName() + "\" error!", e);
		}
		
		// Dispose Statement
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			LogUtil.error("Statement close error!", e);
		}
		return resultFlag;
	}
	
	public boolean updateGPEntity(IGPEntity entity, SqlWhere where) {
		// Prepare Statement
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			LogUtil.error("Make statement failed!", e);
		}
		if (statement == null)
			return false;
		
		// 1.0 
		boolean resultFlag = false;
		try {
			entity.update(statement, where);
			resultFlag = true;
			LogUtil.info("Data has updated in table \"" + entity.getTableName() + "\".");
			
		} catch (SQLException e) {
			LogUtil.error("Data update failed in table \"" + entity.getTableName() + "\"!", e);
		}
		
		// Dispose Statement
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			LogUtil.error("Statement close error!", e);
		}
		return resultFlag;
	}

	public boolean deleteGPEntity(IGPEntity entity, SqlWhere where) {
		// Prepare Statement
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			LogUtil.error("Make statement failed!", e);
		}
		if (statement == null)
			return false;
		
		// 1.0 Delete Row
		boolean resultFlag = false;
		try {
			entity.delete(statement, where);
			resultFlag = true;
			LogUtil.info("Delete Data successed in table \"" + entity.getTableName() + "\".");
			
		} catch (SQLException e) {
			LogUtil.error("Delete Data failed in table \"" + entity.getTableName() + "\"!", e);
		}
		
		// Dispose Statement
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			LogUtil.error("Statement close error!", e);
		}
		return resultFlag;
	}

	public List<IGPEntity> queryGPEntity(IGPEntity entity, SqlWhere where) {
		// Prepare Statement
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			LogUtil.error("Make statement failed!", e);
		}
		if (statement == null)
			return null;
		
		// 1.0 Query Data
		List<IGPEntity> entityList = null;
		try {
			entityList = entity.query(statement, where);
			LogUtil.info("Query Data successed in table \"" + entity.getTableName() + "\".");
			
		} catch (SQLException e) {
			LogUtil.error("Query Data failed in table \"" + entity.getTableName() + "\"!", e);
		}
		
		// Dispose Statement
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			LogUtil.error("Statement close error!", e);
		}
		return entityList;
	}
}
