package com.txdb.gpmanage.core.entity;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.txdb.gpmanage.core.entity.annotation.TableTag;

@TableTag(tableName = "")
public interface IGPEntity {
	
	String getTableName();
	
	String getDescription();
	
	boolean create(Statement statement) throws SQLException;
	
	List<IGPEntity> query(Statement statement, SqlWhere where) throws SQLException;

	void insert(Statement statement) throws SQLException;

	void delete(Statement statement, SqlWhere where) throws SQLException;

	void update(Statement statement, SqlWhere where) throws SQLException;
}
