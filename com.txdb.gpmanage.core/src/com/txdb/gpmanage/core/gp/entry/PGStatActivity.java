package com.txdb.gpmanage.core.gp.entry;

public class PGStatActivity {

	private int datid;
	private String datname;
	private int sess_id;
	private int usesysid;
	private String usename;

	public int getDatid() {
		return datid;
	}

	public void setDatid(int datid) {
		this.datid = datid;
	}

	public String getDatname() {
		return datname;
	}

	public void setDatname(String datname) {
		this.datname = datname;
	}

	public int getSess_id() {
		return sess_id;
	}

	public void setSess_id(int sess_id) {
		this.sess_id = sess_id;
	}

	public int getUsesysid() {
		return usesysid;
	}

	public void setUsesysid(int usesysid) {
		this.usesysid = usesysid;
	}

	public String getUsename() {
		return usename;
	}

	public void setUsename(String usename) {
		this.usename = usename;
	}
}
