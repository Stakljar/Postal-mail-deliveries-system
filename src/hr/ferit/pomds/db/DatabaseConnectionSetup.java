package hr.ferit.pomds.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionSetup {

	private static String username;
	private static String password;
	private static String serverName;
	private static String port;
	private static String databaseName;
	
	public static void setUpConnectionParameters(String username, String password, String serverName, String port, String databaseName)
			throws ClassNotFoundException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		DatabaseConnectionSetup.username = username;
		DatabaseConnectionSetup.password = password;
		DatabaseConnectionSetup.serverName = serverName;
		DatabaseConnectionSetup.port = port;
		DatabaseConnectionSetup.databaseName = databaseName;
	}
	
	public static Connection getConnection() throws SQLException {
		
		return DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + databaseName, username, password);
	}
}
