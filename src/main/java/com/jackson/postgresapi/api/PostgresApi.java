package com.jackson.postgresapi.api;

import java.util.ArrayList;

import com.jackson.postgresapi.model.UserInput;

public interface PostgresApi {
	public ArrayList<Object[]> listAllDatabases();

	public ArrayList<Object[]> listAllTables();

	public Object executeQuery(String psql);

	public void createTable(UserInput input);

	public void renameTable(UserInput input);

	public void deleteTable(UserInput input);

	public void createRow(UserInput input);

	public Object readRow(UserInput input);

	public void updateRow(UserInput input);

	public void deleteRow(UserInput input);

	public void createCol(UserInput input);

	public void alterColType(UserInput input);

	public void renameCol(UserInput input);

	public void deleteCol(UserInput input);

}
