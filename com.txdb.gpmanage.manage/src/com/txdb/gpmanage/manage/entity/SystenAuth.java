package com.txdb.gpmanage.manage.entity;

public class SystenAuth {
	private String roleName;
	private String rolsuper = "D";
	private boolean rolcreatedb = false;
	private boolean rolcanlogin = false;
	private boolean rolinherit = false;
	private boolean rolcreaterole = false;
	private boolean rolreplication = false;
	private boolean isSuper = false;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRolsuper() {
		return rolsuper;
	}

	public void setRolsuper(String rolsuper) {
		this.rolsuper = rolsuper;
	}

	public boolean isRolcreatedb() {
		return rolcreatedb;
	}

	public void setRolcreatedb(boolean rolcreatedb) {
		this.rolcreatedb = rolcreatedb;
	}

	public boolean isRolcanlogin() {
		return rolcanlogin;
	}

	public void setRolcanlogin(boolean rolcanlogin) {
		this.rolcanlogin = rolcanlogin;
	}

	public boolean isRolinherit() {
		return rolinherit;
	}

	public void setRolinherit(boolean rolinherit) {
		this.rolinherit = rolinherit;
	}

	public boolean isRolcreaterole() {
		return rolcreaterole;
	}

	public void setRolcreaterole(boolean rolcreaterole) {
		this.rolcreaterole = rolcreaterole;
	}

	public boolean isRolreplication() {
		return rolreplication;
	}

	public void setRolreplication(boolean rolreplication) {
		this.rolreplication = rolreplication;
	}

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}

}
