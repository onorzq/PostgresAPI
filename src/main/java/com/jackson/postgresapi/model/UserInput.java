package com.jackson.postgresapi.model;

import java.util.HashMap;
import java.util.Map;

public class UserInput {
	private String tableName;
	private Map<String, Object> cellDataMap = new HashMap<String, Object>();
	private String pkName;
	private Object pkVal;
	
	public UserInput() {
	}

	public UserInput(String tableName, Map<String, Object> cellDataMap, String pkName, Object pkVal) {
		this.tableName = tableName;
		this.cellDataMap = cellDataMap;
		this.pkName = pkName;
		this.pkVal = pkVal;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getCellDataMap() {
		return cellDataMap;
	}

	public void setCellDataMap(Map<String, Object> cellDataMap) {
		this.cellDataMap = cellDataMap;
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	public Object getPkVal() {
		return pkVal;
	}

	public void setPkVal(Object pkVal) {
		this.pkVal = pkVal;
	}

}
