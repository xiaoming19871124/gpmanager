package com.txdb.gpmanage.manage.entity;

public class ObjectAuth {
	/**
	 * 权限来源
	 */
	private String grantor;
	/**
	 * 被授权者
	 */
	private String grantee;
	/**
	 * 对象所在数据库
	 */
	private String dbName;
	/**
	 * 对象所在模式
	 */
	private String schemaName;
	/**
	 * 对象名称
	 */
	private String objectName;
	/**
	 * 对象类型
	 */
	private String object_type;
	/**
	 * 权限类型
	 */
	private String privilege_type;
	/**
	 * 是否可以赋权
	 */
	private String is_grantable;

	public String getGrantor() {
		return grantor;
	}

	public void setGrantor(String grantor) {
		this.grantor = grantor;
	}

	public String getGrantee() {
		return grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getPrivilege_type() {
		return privilege_type;
	}

	public void setPrivilege_type(String privilege_type) {
		this.privilege_type = privilege_type;
	}

	public String getIs_grantable() {
		return is_grantable;
	}

	public void setIs_grantable(String is_grantable) {
		this.is_grantable = is_grantable;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
}
