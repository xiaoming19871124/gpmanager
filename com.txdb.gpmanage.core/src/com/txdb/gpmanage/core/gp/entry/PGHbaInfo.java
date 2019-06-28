package com.txdb.gpmanage.core.gp.entry;

public class PGHbaInfo {
	
	/** 匹配通过 Unix 域套接字进行的联接企图 */
	public static final String TYPE_LOCAL = "local";
	/** 匹配通过TCP/IP网络进行的联接尝试（匹配ssl及非ssl方式的连接） */
	public static final String TYPE_HOST = "host";
	/** 匹配通过在TCP/IP上进行的SSL联接企图 */
	public static final String TYPE_HOSTSSL = "hostssl";
	/** 只匹配通过在TCP/IP上进行的非SSL联接企图 */
	public static final String TYPE_HOSTNOSSL = "hostnossl";
	
	
	/** 匹配所有数据库 */
	public static final String DATABASE_ALL = "all";
	/** 匹配请求的数据库与请求的用户同名的数据库 */
	public static final String DATABASE_SAMEUSER = "sameuser";
	/** 请求的用户必须是与数据库同名的组成员 */
	public static final String DATABASE_SAMEGROUP = "samegroup";
	/** 匹配一条replication连接 */
	public static final String DATABASE_REPLICATION = "replication";
	
	
	/** 无条件地允许联接 */
	public static final String METHOD_TRUST = "trust";
	/** 联接无条件拒绝 */
	public static final String METHOD_REJECT = "reject";
	/** MD5加密认证（允许口令存储在pg_shadow中） */
	public static final String METHOD_MD5 = "md5";
	/** 明文密码认证 */
	public static final String METHOD_PASSWORD = "password";
	/** GSSAPI认证（只适用TCP/IP连接） */
	public static final String METHOD_GSS = "gss";
	/** SSPI认证（只适用Windows连接） */
	public static final String METHOD_SSPI = "sspi";
	/** 获取客户端的操作系统用户名并判断他是否匹配请求的数据库名（只适用于本地连接） */
	public static final String METHOD_PEER = "peer";
	/** LDAP服务验证 */
	public static final String METHOD_LDAP = "ldap";
	/** RADIUS服务验证 */
	public static final String METHOD_RADIUS = "radius";
	/** SSL服务验证 */
	public static final String METHOD_CERT = "cert";
	/** 使用操作系统提供的可插入的认证模块服务 （Pluggable Authentication Modules）（PAM）认证 */
	public static final String METHOD_PAM = "pam";
	
	
	/** 追加权限 */
	public static final int MODIFY_ADD = 0;
	/** 删除权限 */
	public static final int MODIFY_RM = 1;
	
	
	/** 连接方式 */
	private String type;
	
	/** 数据库 */
	private String[] databaseArray;
	
	/** 用户 */
	private String[] userArray;
	
	/** 主机地址 */
	private String address;
	
	/** 认证方法 */
	private String method;
	
	/** 权限修改方法（默认-添加） */
	private int modifyType = MODIFY_ADD;
	
	public PGHbaInfo() {}
	
	public PGHbaInfo(String pgHbaRow) {
		// local all gpadmin ident
		// host all gpadmin 127.0.0.1/28 trust
		String[] rowFragments = pgHbaRow.split(" ");
		
		setType(rowFragments[0]);
		setDatabaseArray(rowFragments[1].split(","));
		setUserArray(rowFragments[2].split(","));
		
		if (rowFragments.length == 4)
			setMethod(rowFragments[3]);
		else {
			setAddress(rowFragments[3]);
			setMethod(rowFragments[4]);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getDatabaseArray() {
		return databaseArray;
	}
	
	public String getDatabaseArrayString() {
		String databaseCollect = "";
		for (String database : databaseArray)
			databaseCollect += "," + database;
		if (databaseCollect.length() > 0)
			databaseCollect = databaseCollect.substring(1);
		return databaseCollect;
	}

	public void setDatabaseArray(String[] databaseArray) {
		this.databaseArray = databaseArray;
	}
	
	public void addDatabase(String database) {
		String tempDatabase = database.trim();
		if (databaseArray == null)
			databaseArray = new String[] {};
		
		String[] tempDatabaseArray = new String[databaseArray.length + 1];
		boolean isExist = false;
		for (int i = 0; i < databaseArray.length; i++) {
			tempDatabaseArray[i] = databaseArray[i];
			if (databaseArray[i].trim().equals(tempDatabase))
				isExist = true;
		}
		tempDatabaseArray[databaseArray.length] = tempDatabase;
		if (!isExist)
			setDatabaseArray(tempDatabaseArray);
	}

	public String[] getUserArray() {
		return userArray;
	}
	
	public String getUserArrayString() {
		String userCollect = "";
		for (String user : userArray)
			userCollect += "," + user;
		if (userCollect.length() > 0)
			userCollect = userCollect.substring(1);
		return userCollect;
	}
	
	public void addUser(String user) {
		String tempUser = user.trim();
		if (userArray == null)
			userArray = new String[] {};
		
		String[] tempUserArray = new String[userArray.length + 1];
		boolean isExist = false;
		for (int i = 0; i < userArray.length; i++) {
			tempUserArray[i] = userArray[i];
			if (userArray[i].trim().equals(tempUser))
				isExist = true;
		}
		tempUserArray[userArray.length] = tempUser;
		if (!isExist)
			setUserArray(tempUserArray);
	}

	public void setUserArray(String[] userArray) {
		this.userArray = userArray;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public int getModifyType() {
		return modifyType;
	}

	public void setModifyType(int modifyType) {
		this.modifyType = modifyType;
	}

	@Override
	public String toString() {
		return "PGHbaInfo{" +
				"type='" + type + '\'' +
				", databaseArray='" + getDatabaseArrayString() + '\'' +
				", userArray='" + getUserArrayString() + '\'' +
				", address='" + address + '\'' +
				", method='" + method + '\'' +
				'}';
	}
}
