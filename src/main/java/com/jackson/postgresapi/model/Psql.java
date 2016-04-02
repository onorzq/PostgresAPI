package com.jackson.postgresapi.model;

import java.util.Map;
import java.util.Set;

public class Psql {
	
	public String createTable(String tableName, Map<String, Object> record) {
		StringBuilder query = new StringBuilder();
		
		query.append("CREATE TABLE ");
		query.append(tableName);
		
		query.append(" (");
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getKey()); // column name
			query.append(" ");
			query.append(entry.getValue()); // data type and constraints
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);	
		query.append(" )");
		
		return query.toString();
	}
	
	public String renameTable(Map<String, Object> record) {
		StringBuilder query = new StringBuilder();

		query.append("ALTER TABLE IF EXISTS ");
		
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getKey()); // current table name
			query.append(" RENAME TO ");
			query.append(entry.getValue()); // new table name
		}

		return query.toString();
	}
	
	public String deleteTable(Set<String> tableName) {
		StringBuilder query = new StringBuilder();
		
		query.append("DROP TABLE IF EXISTS ");
		
		for (String s : tableName) {
			query.append(s); // table name
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);	
		
		return query.toString();
	}
	
	public String addCol(String tableName, Map<String, Object> record) {
		StringBuilder query = new StringBuilder();
		
		query.append("ALTER TABLE IF EXISTS ");
		query.append(tableName);
		
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(" ADD ");
			query.append(entry.getKey()); // column name
			query.append(" ");
			query.append(entry.getValue()); // column type
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);
		
		return query.toString();
	}
	
	public String alterColType(String tableName, Map<String, Object> record) {
		StringBuilder query = new StringBuilder();
		
		query.append("ALTER TABLE IF EXISTS ");
		query.append(tableName);
		
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(" ALTER COLUMN ");
			query.append(entry.getKey()); // column name
			query.append(" TYPE ");
			query.append(entry.getValue()); // column type
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);

		return query.toString();
	}

	// column have to be renamed one by one, psql does not support multiple columns rename
	public String renameCol(String tableName, Map<String, Object> record) {
		StringBuilder query = new StringBuilder();

		query.append("ALTER TABLE IF EXISTS ");
		query.append(tableName);
		query.append(" RENAME COLUMN ");
		
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getKey()); // current column name
			query.append(" TO ");
			query.append(entry.getValue()); // new column name
		}

		return query.toString();
	}
	
	public String deleteCol(String tableName, Set<String> colName) {
		StringBuilder query = new StringBuilder();
		
		query.append("ALTER TABLE IF EXISTS ");
		query.append(tableName);
		
		for (String s : colName) {
			query.append(" DROP COLUMN IF EXISTS ");
			query.append(s); // column name
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);	
		
		return query.toString();
	}
	
	public String createRow(String tableName, Map<String, Object> record) {
		StringBuilder query = new StringBuilder();

		query.append("INSERT INTO ");
		query.append(tableName);
		
		query.append(" (");
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getKey());
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);	
		query.append(" )");
		
		query.append(" VALUES (");
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getValue());
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);	
		query.append(" )");
			
		return query.toString();
	}
	
	public String readRow(String tableName, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ");
		query.append("*");
		query.append(" FROM ");
		query.append(tableName);
		
		if(primaryKey != null && pkValue != null) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}

	public String updateRow(String tableName, Map<String, Object> record, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE ");
		query.append(tableName);
		query.append(" SET ");
		
		for (Map.Entry<String, Object> entry : record.entrySet()) {
			query.append(entry.getKey());
			query.append(" = ");
			query.append(entry.getValue());
			query.append(",");
		}
		query.deleteCharAt(query.length() - 1);
		
		if(primaryKey != null && pkValue != null) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}
	
	public String deleteRow(String tableName, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ");
		query.append(tableName);

		if (primaryKey != null && pkValue != null) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}
}
