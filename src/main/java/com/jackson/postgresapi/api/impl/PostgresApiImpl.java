package com.jackson.postgresapi.api.impl;

import java.util.ArrayList;

import com.jackson.postgresapi.api.PostgresApi;
import com.jackson.postgresapi.model.BaseQuery;
import com.jackson.postgresapi.model.Psql;
import com.jackson.postgresapi.model.UserInput;

public class PostgresApiImpl implements PostgresApi {
	private BaseQuery query;
	private Psql psql;

	public PostgresApiImpl(BaseQuery query, Psql psql) {
		this.query = query;
		this.psql = psql;
	}
	
	@Override
	public ArrayList<Object[]> listAllDatabases() {
		query.execute("SELECT * FROM pg_catalog.pg_database");
		return query.getResult();
	}
	
	@Override
	public ArrayList<Object[]> listAllTables() {
		query.execute("SELECT * FROM pg_catalog.pg_tables WHERE schemaname = 'public'");
		return query.getResult();
	}
	
	@Override
	public Object executeQuery(String psql) {
		query.execute(psql);
		return query.getResult();
	}

	@Override
	public void createTable(UserInput input) {
		query.execute(psql.createTable(input.getTableName(), input.getCellDataMap()));
	}

	@Override
	public void renameTable(UserInput input) {
		query.execute(psql.renameTable(input.getCellDataMap()));
	}

	@Override
	public void deleteTable(UserInput input) {
		query.execute(psql.deleteTable(input.getCellDataMap().keySet()));
	}

	@Override
	public void createRow(UserInput input) {
		query.execute(psql.createRow(input.getTableName(), input.getCellDataMap()));
	}

	@Override
	public Object readRow(UserInput input) {
		query.execute(psql.readRow(input.getTableName(), input.getPkName(), input.getPkVal()));
		return query.getResult();
	}

	@Override
	public void updateRow(UserInput input) {
		query.execute(psql.updateRow(input.getTableName(), input.getCellDataMap(), input.getPkName(), input.getPkVal()));
	}

	@Override
	public void deleteRow(UserInput input) {
		query.execute(psql.deleteRow(input.getTableName(), input.getPkName(), input.getPkVal()));
	}

	@Override
	public void createCol(UserInput input) {
		query.execute(psql.addCol(input.getTableName(), input.getCellDataMap()));		
	}

	@Override
	public void alterColType(UserInput input) {
		query.execute(psql.alterColType(input.getTableName(), input.getCellDataMap()));
	}

	@Override
	public void renameCol(UserInput input) {
		query.execute(psql.renameCol(input.getTableName(), input.getCellDataMap()));
	}

	@Override
	public void deleteCol(UserInput input) {
		query.execute(psql.deleteCol(input.getTableName(), input.getCellDataMap().keySet()));
	}
}
