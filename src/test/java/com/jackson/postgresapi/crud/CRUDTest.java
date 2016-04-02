package com.jackson.postgresapi.crud;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.util.PSQLException;

// experiment code here for development purpose only, most of the code here can be discard
public class CRUDTest {
	public static final String IP = "127.0.0.1"; // postgres server ip address
	public static final String PORT = "5432"; //  port number
	public static final String DB = "testdb1"; // database name
	public static final String USERNAME = "postgres";
	public static final String PASSWORD = "123456";
	
	public static final int ADD_COLUMN = 1;
	public static final int ALTER_COLUMN_TYPE = 2;
	public static final int RENAME_COLUMN = 3;
	public static final int DROP_COLUMN = 4;
	
	
	public void executeQuery(String sql) {
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://" + IP + ":" + PORT + "/" + DB,
				USERNAME, PASSWORD)) {
			connection.setAutoCommit(false);
			
			try (Statement statement = connection.createStatement()) {	
				
				boolean hasResultSet = statement.execute(sql);
				connection.commit();
				
				if (hasResultSet) {
					try (ResultSet resultSet = statement.getResultSet()) {
						int numOfCols = resultSet.getMetaData().getColumnCount();

						for (int i = 1; i <= numOfCols; i++) {
							System.out.printf("%-12s ", resultSet.getMetaData().getColumnName(i));
						}
						System.out.println();

						while (resultSet.next()) {
							for (int i = 1; i <= numOfCols; i++) {
								System.out.printf("%-12.12s ", resultSet.getObject(i));
							}
							System.out.println();
						}
					}
				}
			} catch (PSQLException e) {
				e.printStackTrace();
				connection.rollback();
			}
			
			connection.setAutoCommit(true);
		}  catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// if tableName does not exist, the query would executed, if colName DNE, exception will be thrown
	// str can be data type or new column name depends on mode selected
	// ideally, user can only select provided tableName and colName
	public String colQuery(String tableName, String colName, String str, int colAlterMode) {
		StringBuilder query = new StringBuilder();
		
		query.append("ALTER TABLE IF EXISTS ");
		query.append(tableName);

		switch (colAlterMode) {
		case 1:
			query.append(" ADD ");
			query.append(colName);
			query.append(" ");
			query.append(str); // column type
			break;
		case 2:
			query.append(" ALTER COLUMN ");
			query.append(colName);
			query.append(" TYPE ");
			query.append(str); // new column type
			break;
		case 3:
			query.append(" RENAME COLUMN ");
			query.append(colName);
			query.append(" TO ");
			query.append(str); // new column name
			break;
		case 4:
			query.append(" DROP COLUMN ");
			query.append(colName);
			break;

		default:
			query.append(" RENAME COLUMN ");
			query.append(colName);
			query.append(" TO ");
			query.append(colName);
			break;
		}
		
		return query.toString();
	}
	
	public String createQuery(String tableName, Map<String, Object> record) {
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
	
	public String readQuery(String tableName, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		boolean isSelectSpecificRow = primaryKey != null && pkValue != null ? true : false;
		
		query.append("SELECT ");
		query.append("*");
		query.append(" FROM ");
		query.append(tableName);
		
		if(isSelectSpecificRow) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}

	public String updateQuery(String tableName, Map<String, Object> record, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		boolean isUpdateSpecificRow = primaryKey != null && pkValue != null ? true : false;
		
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
		
		if(isUpdateSpecificRow) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}
	
	public String deleteQuery(String tableName, String primaryKey, Object pkValue) {
		StringBuilder query = new StringBuilder();
		boolean isDeleteSpecificRow = primaryKey != null && pkValue != null ? true : false;
		
		query.append("DELETE FROM ");
		query.append(tableName);

		if (isDeleteSpecificRow) {
			query.append(" WHERE ");
			query.append(primaryKey);
			query.append(" = ");
			query.append(pkValue);
		}
		
		return query.toString();
	}
	
	public Map<String, Object> inputData() {
		Map<String, Object> record = new HashMap<String, Object>();
		
		record.put("id", 4);
		record.put("name", "'test name'");
		record.put("age", 12);
		record.put("address", "'test address'");
		record.put("salary", 123456);
		
		return record;
	}
	
	public Map<String, Object> updateData() {
		Map<String, Object> record = new HashMap<String, Object>();
		
//		record.put("id", 4);
		record.put("name", "'updated name'");
		record.put("age", 52);
		record.put("address", "'updated addr'");
		record.put("salary", 654321);
		
		return record;
	}

	@Test
	public void test() {
// sql query reference:
//		"select d.datname from pg_catalog.pg_database d"; // list all db
//		"SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema'"; // list all table
//		"SELECT * FROM pg_catalog.pg_tables WHERE schemaname = 'public'"; // list all public table
//		"select * from pg_catalog.pg_tables"; // list all table
//		"table test1"; // list all row		
//		"CREATE DATABASE testdb1"
//		"CREATE TABLE TEST1 " +
//        "(ID INT PRIMARY KEY     NOT NULL," +
//        " NAME           TEXT    NOT NULL, " +
//        " AGE            INT     NOT NULL, " +
//        " ADDRESS        CHAR(50), " +
//        " SALARY         REAL)";
//		"INSERT INTO TEST1 (ID,NAME,AGE,ADDRESS,SALARY) "
//        + "VALUES (3, 'Paul2', 33, 'California2', 20001.00 )";
		
//		executeQuery("SELECT EXISTS(SELECT * FROM pg_catalog.pg_tables WHERE tablename = 'test1')");
//		executeQuery("ALTER TABLE IF EXISTS test1 RENAME TO newtest1");
//		executeQuery("CREATE TABLE test2 (id int PRIMARY KEY NOT NULL)");
//		executeQuery("SELECT * FROM pg_catalog.pg_tables WHERE schemaname = 'public'");
//		executeQuery("CREATE DATABASE testdb2"); // create, rename and drop database is more complicate than I expected
//		executeQuery("ALTER DATABASE testdb11 RENAME TO testdb1");
		executeQuery("SELECT * FROM pg_catalog.pg_database");

//		System.out.println("add row");
//		executeQuery(createQuery("test1", inputData()));
//		executeQuery(readQuery("test1", "id", null));
//		System.out.println("update row");
//		executeQuery(updateQuery("test1", updateData(), "id", 4));
//		executeQuery(readQuery("test1", null, 1));
//		System.out.println("delete row");
//		executeQuery(deleteQuery("test1", "id", 4));
//		executeQuery(readQuery("test1", null, null));
		
	}
	

}
