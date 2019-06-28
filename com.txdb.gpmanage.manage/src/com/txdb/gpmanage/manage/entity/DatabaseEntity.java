package com.txdb.gpmanage.manage.entity;

import java.util.List;

public class DatabaseEntity extends GPData {
	private List<SchameEntity> schame;
	private String tableName = GPData.ALl_NAME;

	public List<SchameEntity> getSchame() {
		return schame;
	}

	public void setSchame(List<SchameEntity> schame) {
		this.schame = schame;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String toSql() {
		String dbSql = "";
		if (!this.name.equals(GPData.ALl_NAME)) {
			dbSql = "dbname = " + "'" + this.name + "'";
		}
		String tableSql = "";
		if (tableName != null && !tableName.isEmpty() && !tableName.equals(GPData.ALl_NAME)) {
			if (schame != null && schame.size() > 0) {
				tableSql = "(";
				for (int i = 0; i < this.schame.size(); i++) {
					SchameEntity schema = this.schame.get(i);
					if (i == this.schame.size() - 1) {
						tableSql = tableSql + "fq_name like '" + schema.getName() + "%" + tableName + "%') ";
					}else{
						tableSql = tableSql + "fq_name like '" + schema.getName() + "%" + tableName + "%' or ";
					}
				}
			} else {
				tableSql = "fq_name like '%" + tableName + "% ";
			}
		} else {
			if (schame != null && schame.size() > 0) {
				tableSql = "(";
				for (int i = 0; i < this.schame.size(); i++) {
					SchameEntity schema = this.schame.get(i);
					if (i == this.schame.size() - 1) {
						tableSql = tableSql + "fq_name like '" + schema.getName() + "%.%') ";
					}else{
						tableSql = tableSql + "fq_name like '" + schema.getName() + "%.%' or ";
					}
				}
			} else {
				tableSql = "fq_name like '%" + tableName + "%' ";
			}
		}

		if (dbSql.isEmpty() && tableSql.isEmpty())
			return "";
		if (dbSql.isEmpty() && !tableSql.isEmpty())
			return tableSql;
		if (!dbSql.isEmpty() && tableSql.isEmpty())
			return dbSql;
		if (!dbSql.isEmpty() && !tableSql.isEmpty())
			return "(" + dbSql + " and " + tableSql + ")";
		return "";
	}
}
