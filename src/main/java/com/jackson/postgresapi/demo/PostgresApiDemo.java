package com.jackson.postgresapi.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jackson.postgresapi.api.PostgresApi;
import com.jackson.postgresapi.api.impl.PostgresApiImpl;
import com.jackson.postgresapi.model.BaseQuery;
import com.jackson.postgresapi.model.DatabaseInfo;
import com.jackson.postgresapi.model.Psql;
import com.jackson.postgresapi.model.UserInput;

public class PostgresApiDemo {
	public static final String URL_PREFIX = "jdbc:postgresql:"; // postgres server ip address
	public static final String IP = "127.0.0.1"; // postgres server ip address
	public static final String PORT = "5432"; //  port number
	public static final String DB = "testdb1"; // database name
	public static final String USERNAME = "postgres";
	public static final String PASSWORD = "123456";

	public static void main(String[] args) {
		DatabaseInfo dbInfo = new DatabaseInfo(URL_PREFIX, IP, PORT, DB, USERNAME, PASSWORD);
		BaseQuery query = new BaseQuery(dbInfo);
		Psql psql = new Psql();
		PostgresApi api = new PostgresApiImpl(query, psql);
		Object[] rawInput;
		
		System.out.println("list all databases within current server:");
		printResult(api.listAllDatabases());
		
		System.out.println("show current database:"); // execute a query directly
		printResult((ArrayList<Object[]>) api.executeQuery("SELECT current_database()"));
		
		System.out.println("list all tables within current database:");
		printResult(api.listAllTables());
		
		System.out.println("create a new table:");
		rawInput = new Object[] { "test2", null, null, "id", "int primary key not null" };
		api.createTable(parseUserInput(rawInput));
		printResult(api.listAllTables());
		
		System.out.println("rename a table:");
		rawInput = new Object[] { null, null, null, "test2", "newtest2" };
		api.renameTable(parseUserInput(rawInput));
		printResult(api.listAllTables());
		
		System.out.println("create another table:");
		rawInput = new Object[] { "test3", null, null, "id", "int primary key not null" };
		api.createTable(parseUserInput(rawInput));
		printResult(api.listAllTables());
		
		System.out.println("show table test3:");
		printTable(api, "test3");
		
		System.out.println("add new row for test3:");
		rawInput = new Object[] { "test3", null, null, "id", 1 };	
		api.createRow(parseUserInput(rawInput));
		printTable(api, "test3");

		System.out.println("show table test1:");
		printTable(api, "test1");
		
		System.out.println("add new row:");
		// attention! add '' inside "" for string value
		rawInput = new Object[] { "test1", null, null, "id", 4, "name", "'test name'", "age", 12, "address",
				"'test addr'", "salary", 123456 };
		api.createRow(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("update row with pk = 4:");
		rawInput = new Object[] { "test1", "id", 4, "id", 5, "name", "'new name'", "age", 24, "address", "'new addr'",
				"salary", 654321 };	
		api.updateRow(parseUserInput(rawInput));
		printTable(api, "test1");

		System.out.println("update row with pk = 5:");
		rawInput = new Object[] { "test1", "id", 5, "name", "'new name2'", "age", 25, "address", "'new addr2'",
				"salary", 11111 };	
		api.updateRow(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("delete row with pk = 4, nothing happened!");
		rawInput = new Object[] { "test1", "id", 4 };
		api.deleteRow(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("delete row with pk = 5:");
		rawInput = new Object[] { "test1", "id", 5 };
		api.deleteRow(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("add columns:");
		rawInput = new Object[] { "test1", null, null, "col1", "text", "col2", "int", "col3", "varchar(5)" };
		api.createCol(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("update rows with new added columns:");
		rawInput = new Object[] { "test1", "id", 1, "col1", "'foo'", "col2", 1 };	
		api.updateRow(parseUserInput(rawInput));
		rawInput = new Object[] { "test1", "id", 2, "col1", "'bar'", "col2", 2 };	
		api.updateRow(parseUserInput(rawInput));
		rawInput = new Object[] { "test1", "id", 3, "col1", "'que'", "col2", 3 };	
		api.updateRow(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("update columns type:");
		rawInput = new Object[] { "test1", null, null, "col1", "char(50)", "col2", "real" };
		api.alterColType(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("rename a column:");
		rawInput = new Object[] { "test1", null, null, "col3", "newcol3" };
		api.renameCol(parseUserInput(rawInput));
		printTable(api, "test1");

		System.out.println("delete columns:");
		rawInput = new Object[] { "test1", null, null, "col1", null, "col2", null };
		api.deleteCol(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("delete column:");
		rawInput = new Object[] { "test1", null, null, "newcol3", null };
		api.deleteCol(parseUserInput(rawInput));
		printTable(api, "test1");
		
		System.out.println("delete tables:");
		rawInput = new Object[] { null, null, null, "newtest2", null, "test3", null };
		api.deleteTable(parseUserInput(rawInput));
		printResult(api.listAllTables());
	}
	
	// rawInput should come from the UI in the future, Json is better than Object[] if develop webapp
	public static UserInput parseUserInput(Object[] rawInput) {
		UserInput result = new UserInput();

		result.setTableName((String) rawInput[0]);
		result.setPkName((String) rawInput[1]);
		result.setPkVal(rawInput[2]);

		Map<String, Object> record = new HashMap<String, Object>();

		for (int i = 3; i < rawInput.length - 1; i += 2) {
			record.put((String) rawInput[i], rawInput[i + 1]);
		}

		result.setCellDataMap(record);

		return result;
	}
	
	// this should be output to the UI in the future
	public static void printTable(PostgresApi api, String tableName) {
		printResult((ArrayList<Object[]>) api.readRow(new UserInput(tableName, null, null, null)));
	}
	
	public static void printResult(ArrayList<Object[]> result) {	
		for (int j = 0; j < result.size(); j++) {
			for (int i = 0; i < result.get(j).length; i++) {
				System.out.printf("%-12.12s ", result.get(j)[i]);
			}
			System.out.println();
		}
	}

}
