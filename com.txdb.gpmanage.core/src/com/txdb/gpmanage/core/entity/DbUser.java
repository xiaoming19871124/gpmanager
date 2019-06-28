package com.txdb.gpmanage.core.entity;

/**
 * 数据库用户
 * 
 * @author ws
 *
 */
public class DbUser {
	private String userName;
	private String userPwd;
	private int usesysid;
	private boolean usecreatedb;
	private String usesuper;
	private boolean usecatupd;
	private boolean userepl;
	private String valuntil;
	private String useconfig;
	private boolean isVisible = true;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public int getUsesysid() {
		return usesysid;
	}

	public void setUsesysid(int usesysid) {
		this.usesysid = usesysid;
	}

	public boolean isUsecreatedb() {
		return usecreatedb;
	}

	public String getUsecreatedb() {
		return usecreatedb ? "t" : "f";
	}

	public void setUsecreatedb(boolean usecreatedb) {
		this.usecreatedb = usecreatedb;
	}

	public void setUsecreatedb(String usecreatedb) {
		this.usecreatedb = usecreatedb.equals("t");
	}

	public String getUsesuper() {
		return usesuper;
	}

	// public String getUsesuper() {
	// return usesuper ? "t" : "f";
	// }

	public void setUsesuper(String usesuper) {
		this.usesuper = usesuper;
	}

	// public void setUsesuper(String usesuper) {
	// this.usesuper = usesuper.equals("t");
	// }

	public boolean isUsecatupd() {
		return usecatupd;
	}

	public String getUsecatupd() {
		return usecatupd ? "t" : "f";
	}

	public void setUsecatupd(boolean usecatupd) {
		this.usecatupd = usecatupd;
	}

	public void setUsecatupd(String usecatupd) {
		this.usecatupd = usecatupd.equals("t");
	}

	public boolean isUserepl() {
		return userepl;
	}

	public String getUserepl() {
		return userepl ? "t" : "f";
	}

	public void setUserepl(boolean userepl) {
		this.userepl = userepl;
	}

	public void setUserepl(String userepl) {
		this.userepl = userepl.equals("t");
	}

	public String getValuntil() {
		return valuntil;
	}

	public void setValuntil(String valuntil) {
		this.valuntil = valuntil;
	}

	public String getUseconfig() {
		return useconfig;
	}

	public void setUseconfig(String useconfig) {
		this.useconfig = useconfig;
	}

	@Override
	public String toString() {
		return userName;
	}
}
