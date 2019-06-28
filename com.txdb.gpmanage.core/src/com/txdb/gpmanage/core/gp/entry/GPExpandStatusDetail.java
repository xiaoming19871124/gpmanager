package com.txdb.gpmanage.core.gp.entry;

public class GPExpandStatusDetail {

	private String dbname;
	private String fq_name;
	private long schema_oid;
	private String schema_name;
	private long table_oid;
	private String distribution_policy;
	private String distribution_policy_names;
	private String distribution_policy_coloids;
	private String storage_options;
	private int rank;
	private String status;
	private String expansion_started;
	private String expansion_finished;
	private int source_bytes;
	private boolean isModify = false;
	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getFq_name() {
		return fq_name;
	}

	public void setFq_name(String fq_name) {
		if (fq_name != null && !fq_name.isEmpty() && fq_name.contains(".")) {
			String[] schema_table = fq_name.split("\\.");
			this.schema_name = schema_table[0];
			this.fq_name = schema_table[1];
		} else {
			this.fq_name = fq_name;
		}
	}

	public long getSchema_oid() {
		return schema_oid;
	}

	public void setSchema_oid(long schema_oid) {
		this.schema_oid = schema_oid;
	}

	public long getTable_oid() {
		return table_oid;
	}

	public void setTable_oid(long table_oid) {
		this.table_oid = table_oid;
	}

	public String getDistribution_policy() {
		return distribution_policy;
	}

	public void setDistribution_policy(String distribution_policy) {
		this.distribution_policy = distribution_policy;
	}

	public String getDistribution_policy_names() {
		return distribution_policy_names;
	}

	public void setDistribution_policy_names(String distribution_policy_names) {
		this.distribution_policy_names = distribution_policy_names;
	}

	public String getDistribution_policy_coloids() {
		return distribution_policy_coloids;
	}

	public void setDistribution_policy_coloids(String distribution_policy_coloids) {
		this.distribution_policy_coloids = distribution_policy_coloids;
	}

	public String getStorage_options() {
		return storage_options;
	}

	public void setStorage_options(String storage_options) {
		this.storage_options = storage_options;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExpansion_started() {
		return expansion_started;
	}

	public void setExpansion_started(String expansion_started) {
		this.expansion_started = expansion_started;
	}

	public String getExpansion_finished() {
		return expansion_finished;
	}

	public void setExpansion_finished(String expansion_finished) {
		this.expansion_finished = expansion_finished;
	}

	public int getSource_bytes() {
		return source_bytes;
	}

	public void setSource_bytes(int source_bytes) {
		this.source_bytes = source_bytes;
	}

	@Override
	public String toString() {
		return "GPExpandStatusDetail{" + "table_oid='" + table_oid + '\'' + ", fq_name='" + fq_name + '\'' + ", dbname='" + dbname + '\'' + ", rank='" + rank + '\'' + ", status='" + status + '\'' + '}';
	}

	public String getSchema_name() {
		return schema_name;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}
}
