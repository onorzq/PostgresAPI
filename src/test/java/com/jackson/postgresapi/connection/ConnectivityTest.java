package com.jackson.postgresapi.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectivityTest {
	public static final String IP = "127.0.0.1";      // db ip address
	public static final String PORT = "5432";         // db port number
	public static final String DB = "postgres";       // db name
	public static final String USERNAME = "postgres"; // db username
	public static final String PASSWORD = "123456";   // db login password

	public static void main(String args[]) {
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://" + IP + ":" + PORT + "/" + DB, USERNAME,
					PASSWORD);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}

}
