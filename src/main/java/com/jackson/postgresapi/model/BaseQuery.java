package com.jackson.postgresapi.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

public class BaseQuery {
	private DatabaseInfo dbInfo;
	private ArrayList<Object[]> result;
	
	public BaseQuery(DatabaseInfo dbInfo) {
		this.dbInfo = dbInfo;
	}
	
	public void execute(String sql) {
		try (Connection connection = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(),
				dbInfo.getPassword())) {
			
			connection.setAutoCommit(false);
			
			try (Statement statement = connection.createStatement()) {	
				
				boolean hasResultSet = statement.execute(sql);
				connection.commit();
				
				if (hasResultSet) {
					try (ResultSet resultSet = statement.getResultSet()) {
						int numOfCols = resultSet.getMetaData().getColumnCount();

						Object[] currentRow = new Object[numOfCols];
						
						for (int i = 1; i <= numOfCols; i++) {
							currentRow[i - 1] = resultSet.getMetaData().getColumnName(i);
						}
						
						result = new ArrayList<Object[]>();
						result.add(currentRow);

						while (resultSet.next()) {
							currentRow = new Object[numOfCols]; // TODO: questionable design, using more memory here?
							for (int i = 1; i <= numOfCols; i++) {
								currentRow[i - 1] = resultSet.getObject(i);
							}
							result.add(currentRow);
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

	public void setDbInfo(DatabaseInfo dbInfo) {
		this.dbInfo = dbInfo;
	}

	public ArrayList<Object[]> getResult() {
		return result;
	}
	
}
