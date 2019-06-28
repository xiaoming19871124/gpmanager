package com.txdb.gpmanage.core.entity;

import java.util.ArrayList;
import java.util.List;

public class SqlWhere {

	private List<String> fieldNames = new ArrayList<String>();
	private List<String> relations = new ArrayList<String>();
	private List<Object> fieldValues = new ArrayList<Object>();
	
	public void addWhere(String fieldName, String relation, Object value) {
		fieldNames.add(fieldName);
		relations.add(relation);
		fieldValues.add(value);
	}
	
	public void removeAll() {
		fieldNames.clear();
		relations.clear();
		fieldValues.clear();
	}
	
	public String generateSql() {
		if (fieldNames.size() <= 0)
			return "";
		StringBuffer sqlBuffer = new StringBuffer(" where ");
		for (int i = 0; i < fieldNames.size(); i++) {
			String fieldName = fieldNames.get(i);
			String relation = relations.get(i);
			Object fieldValue = fieldValues.get(i);
			
			sqlBuffer.append(fieldName + " " + relation + " ");
			String valueStr = "";
			if (fieldValue instanceof Integer)
				valueStr = String.valueOf(fieldValue);
			else
				valueStr = "'" + fieldValue + "'";
			
			String splitter = " and ";
			if (i == (fieldNames.size() - 1))
				splitter = "";
			valueStr += splitter;
			sqlBuffer.append(valueStr);
		}
		return sqlBuffer.toString();
	}
}
