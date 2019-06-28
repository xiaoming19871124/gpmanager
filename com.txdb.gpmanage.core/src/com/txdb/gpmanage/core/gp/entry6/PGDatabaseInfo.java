package com.txdb.gpmanage.core.gp.entry6;

/**
 * select * from pg_database order by datname;
 * @author xiaom
 */
public class PGDatabaseInfo {

	private String datname;
	private int datdba;
	private int encoding;
	private String datcollate;
	private String datctype;
	private String datistemplate;
	private String datallowconn;
	private int datconnlimit;
	private int datlastsysoid;
	private int datfrozenxid;
	private String dattablespace;
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

	public String getDatcollate() {
		return datcollate;
	}

	public void setDatcollate(String datcollate) {
		this.datcollate = datcollate;
	}

	public String getDatctype() {
		return datctype;
	}

	public void setDatctype(String datctype) {
		this.datctype = datctype;
	}

	public String getDatistemplate() {
		return datistemplate;
	}

	public void setDatistemplate(String datistemplate) {
		this.datistemplate = datistemplate;
	}

	public String getDatallowconn() {
		return datallowconn;
	}

	public void setDatallowconn(String datallowconn) {
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

	public String getDattablespace() {
		return dattablespace;
	}

	public void setDattablespace(String dattablespace) {
		this.dattablespace = dattablespace;
	}

	public String getDatacl() {
		return datacl;
	}

	public void setDatacl(String datacl) {
		this.datacl = datacl;
	}
	
	@Override
	public String toString() {
		return "GPSegmentInfo{" +
				"datname='" + datname + '\'' +
				", datdba='" + datdba + '\'' +
				", encoding='" + encoding + '\'' +
				", datcollate='" + datcollate + '\'' +
				", datctype='" + datctype + '\'' +
				", datistemplate='" + datistemplate + '\'' +
				", datallowconn='" + datallowconn + '\'' +
				", datconnlimit='" + datconnlimit + '\'' +
				", datlastsysoid='" + datlastsysoid + '\'' +
				", datfrozenxid='" + datfrozenxid + '\'' +
				", dattablespace='" + dattablespace + '\'' +
				", datacl='" + datacl + '\'' +
				'}';
	}
}
