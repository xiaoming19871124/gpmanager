package com.txdb.gpmanage.core.gp.entry;

/**
 * select * from pg_database order by datname;
 * @author xiaom
 */
public class PGDatabaseInfo {
	
	private String datname;
	private int datdba;
	private int encoding;
	private boolean datistemplate;
	private boolean datallowconn;
	private int datconnlimit;
	private int datlastsysoid;
	private int datfrozenxid;
	private int dattablespace;
	private String datconfig;
	private String datacl;

	public String getDatname() {
		return datname;
	}

	public void setDatname(String datname) {
		this.datname = datname;
	}

	public int getDatdba() {
		return datdba;
	}

	public void setDatdba(int datdba) {
		this.datdba = datdba;
	}

	public int getEncoding() {
		return encoding;
	}

	public void setEncoding(int encoding) {
		this.encoding = encoding;
	}

	public boolean isDatistemplate() {
		return datistemplate;
	}

	public void setDatistemplate(boolean datistemplate) {
		this.datistemplate = datistemplate;
	}

	public boolean isDatallowconn() {
		return datallowconn;
	}

	public void setDatallowconn(boolean datallowconn) {
		this.datallowconn = datallowconn;
	}

	public int getDatconnlimit() {
		return datconnlimit;
	}

	public void setDatconnlimit(int datconnlimit) {
		this.datconnlimit = datconnlimit;
	}

	public int getDatlastsysoid() {
		return datlastsysoid;
	}

	public void setDatlastsysoid(int datlastsysoid) {
		this.datlastsysoid = datlastsysoid;
	}

	public int getDatfrozenxid() {
		return datfrozenxid;
	}

	public void setDatfrozenxid(int datfrozenxid) {
		this.datfrozenxid = datfrozenxid;
	}

	public int getDattablespace() {
		return dattablespace;
	}

	public void setDattablespace(int dattablespace) {
		this.dattablespace = dattablespace;
	}

	public String getDatconfig() {
		return datconfig;
	}

	public void setDatconfig(String datconfig) {
		this.datconfig = datconfig;
	}

	public String getDatacl() {
		return datacl;
	}

	public void setDatacl(String datacl) {
		this.datacl = datacl;
	}
	
	@Override
	public String toString() {
		return "PGDatabaseInfo{" +
				"datname='" + datname + '\'' +
				", datdba='" + datdba + '\'' +
				", encoding='" + encoding + '\'' +
				", datistemplate='" + datistemplate + '\'' +
				", datallowconn='" + datallowconn + '\'' +
				", datconnlimit='" + datconnlimit + '\'' +
				", datlastsysoid='" + datlastsysoid + '\'' +
				", datfrozenxid='" + datfrozenxid + '\'' +
				", dattablespace='" + dattablespace + '\'' +
				", datconfig='" + datconfig + '\'' +
				", datacl='" + datacl + '\'' +
				'}';
	}
}
