package com.txdb.gpmanage.core.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.txdb.gpmanage.core.entity.annotation.FieldTag;
import com.txdb.gpmanage.core.entity.annotation.NoToDbTag;
import com.txdb.gpmanage.core.entity.annotation.TableTag;
import com.txdb.gpmanage.core.log.LogUtil;

public abstract class BaseGPEntity implements IGPEntity {

	public final String getTableName() {
		TableTag tag = getClass().getAnnotation(TableTag.class);
		return tag.tableName();
	}

	public final String getDescription() {
		TableTag tag = getClass().getAnnotation(TableTag.class);
		return tag.description();
	}

	protected final String generateCreateSql() {
		StringBuffer sqlBuffer = new StringBuffer("create table " + getTableName() + " (");

		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			NoToDbTag noToDb = field.getAnnotation(NoToDbTag.class);
			if (noToDb != null)
				continue;
			
			String columnStr = field.getName();
			String type = field.getType().getName();
			if (type.contains("int"))
				columnStr += " INTEGER";
			else
				columnStr += " varchar(50)";

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

	@Override
	public boolean create(Statement statement) throws SQLException {
		if (isExist(statement)) {
			LogUtil.info("Table \"" + getTableName() + "\" is already exist, create operation was skipped.");
			return true;
		}
		String sql = generateCreateSql();
		return statement.executeUpdate(sql) > 0;
	}

	protected final boolean isExist(Statement statement) throws SQLException {
		String sql = "select name from sqlite_master where type = 'table' and name = '" + getTableName() + "' order by name";
		ResultSet rs = statement.executeQuery(sql);
		boolean exist = rs.next();
		rs.close();
		return exist;
	}

	protected final String generateInsertSql() {
		StringBuffer sqlBuffer = new StringBuffer("insert into " + getTableName() + "(%s) values (%s)");
		Field[] fields = getClass().getDeclaredFields();

		String keys = "";
		String values = "";
		for (Field field : fields) {
			field.setAccessible(true);
			NoToDbTag noToDb = field.getAnnotation(NoToDbTag.class);
			if (noToDb != null)
				continue;
			
			String key = field.getName();
			Object value = null;
			try {
				value = field.get(this);
				if (value == null)
					value = "";
				if (field.getType().getName().contains("String"))
					value = "'" + value + "'";

			} catch (Exception e) {
				value = "''";
			}
			String splitter = ", ";
			if (field == fields[fields.length - 1])
				splitter = "";

			keys += key + splitter;
			values += value + splitter;
		}
		return String.format(sqlBuffer.toString(), keys, values);
	}

	@Override
	public void insert(Statement statement) throws SQLException {
		String sql = generateInsertSql();
		statement.executeUpdate(sql);
	}

	protected final String generateUpdateSql() {
		return generateUpdateSql(null);
	}

	protected final String generateUpdateSql(SqlWhere where) {
		StringBuffer sqlBuffer = new StringBuffer("update " + getTableName() + " set ");
		Field[] fields = getClass().getDeclaredFields();

		String setterStr = "";
		for (Field field : fields) {
			field.setAccessible(true);
			NoToDbTag noToDb = field.getAnnotation(NoToDbTag.class);
			if (noToDb != null)
				continue;
			
			String key = field.getName();
			Object value = null;
			try {
				value = field.get(this);
				if (value == null)
					value = "";
				if (field.getType().getName().contains("String"))
					value = "'" + value + "'";

			} catch (Exception e) {
				value = "''";
			}
			String splitter = ", ";
			if (field == fields[fields.length - 1])
				splitter = "";
			setterStr += key + " = " + value + splitter;
		}
		sqlBuffer.append(setterStr);
		if (where != null)
			sqlBuffer.append(where.generateSql());

		return sqlBuffer.toString();
	}

	@Override
	public void update(Statement statement, SqlWhere where) throws SQLException {
		String sql = generateUpdateSql(where);
		statement.executeUpdate(sql);
	}

	protected final String generateDeleteSql(SqlWhere where) {
		StringBuffer sqlBuffer = new StringBuffer("delete from " + getTableName());

		String setterStr = "";
		sqlBuffer.append(setterStr);
		if (where != null)
			sqlBuffer.append(where.generateSql());

		return sqlBuffer.toString();
	}

	@Override
	public void delete(Statement statement, SqlWhere where) throws SQLException {
		String sql = generateDeleteSql(where);
		statement.executeUpdate(sql);
	}

	protected final String generateQuerySql(SqlWhere where) {
		StringBuffer sqlBuffer = new StringBuffer("select * from " + getTableName());

		String setterStr = "";
		sqlBuffer.append(setterStr);
		if (where != null)
			sqlBuffer.append(where.generateSql());

		return sqlBuffer.toString();
	}

	@Override
	public List<IGPEntity> query(Statement statement, SqlWhere where) throws SQLException {
		String sql = generateQuerySql(where);
		ResultSet rs = statement.executeQuery(sql);

		List<IGPEntity> entityList = new ArrayList<IGPEntity>();
		try {
			Field[] fields = getClass().getDeclaredFields();
			while (rs.next()) {
				IGPEntity newEntity = getClass().newInstance();
				for (Field field : fields) {
					field.setAccessible(true);
					NoToDbTag noToDb = field.getAnnotation(NoToDbTag.class);
					if (noToDb != null)
						continue;
					String name = field.getName();
					String type = field.getType().getName();
					if (type.contains("int"))
						field.set(newEntity, rs.getInt(name));
					else
						field.set(newEntity, rs.getString(name));
				}
				entityList.add(newEntity);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LogUtil.error("Query Sqlite Data failed in table \"" + getTableName() + "\"!", e);

		} finally {
			rs.close();
		}
		return entityList;
	}
}
