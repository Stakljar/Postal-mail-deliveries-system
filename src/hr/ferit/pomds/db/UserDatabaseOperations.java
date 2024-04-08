package hr.ferit.pomds.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import hr.ferit.pomds.data.Country;
import hr.ferit.pomds.data.Employee;
import hr.ferit.pomds.data.ServiceUser;
import hr.ferit.pomds.data.Town;

public class UserDatabaseOperations {

	public static String verifyUser(String verificationAttributeName, String verificationAttribute, 
			String password, String table)
			throws SQLException {
		
		String id = null;
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT id FROM " + table + " WHERE " + verificationAttributeName + " = "
				+ "? AND password = SHA2(?, 224)";
		PreparedStatement statement = connection.prepareStatement(sql);
		
		statement.setString(1, verificationAttribute);
		statement.setString(2, password);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			id = resultSet.getString(1);
		}
		connection.close();
		return id;
	}

	public static String registerServiceUser(String username, String password, String firstName, String lastName,
			String address, String townCode, String alpha2CountryCode, String townName)
			throws SQLException {
	
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		String id = UUID.randomUUID().toString();
		
		String sql = "INSERT INTO service_user VALUES(?, ?, SHA2(?, 224), ?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.setString(2, username);
		statement.setString(3, password);
		statement.setString(4, firstName);
		statement.setString(5, lastName);
		statement.setString(6, address);
		statement.setString(7, townCode);
		statement.setString(8, alpha2CountryCode);
		statement.setString(9, townName);
		statement.executeUpdate();
		
		connection.close();
		return id;
	}
	
	private static boolean isUserAlreadyDeleted(Connection connection, String id, String table) throws SQLException {
		
		String sql = "SELECT id FROM " + table + " WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(!resultSet.next()) {
			return true;
		}
		return false;
	}
	
	public static int deleteUserAccount(String id, String table) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "DELETE FROM " + table + " WHERE id = ?";
		if(isUserAlreadyDeleted(connection, id, table)) {
			connection.close();
			return 1;
		}
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.executeUpdate();
		connection.close();
		return 0;
	}
	
	public static String gettownCode(Connection connection, String serviceUserId) throws SQLException {
		
		String sql = "SELECT town_code FROM service_user WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, serviceUserId);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			return resultSet.getString(1);
		}
		return null;
	}
	
	public static int updateUsername(String id, String username, String table) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		if(table == "employee") {
			if(isUserAlreadyDeleted(connection, id, "employee")) {
				connection.close();
				return 1;
			}
		}
		String sql = "UPDATE " + table + " SET username = ? WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		statement.setString(2, id);
		statement.executeUpdate();
		connection.close();
		return 0;
	}
	
	public static void insertEmployee(String username, String password) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		String sql = "INSERT INTO employee VALUES(UUID(), ?, SHA2(?, 224))";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		statement.setString(2, password);
		statement.executeUpdate();
		
		connection.close();
	}
	
	public static void changePassword(String table, String identification, String password) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		String sql = "UPDATE " + table + " SET password = SHA2(?, 224) WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, password);
		statement.setString(2, identification);
		statement.executeUpdate();
		connection.close();
	}
	
	public static void updateServiceUserInformation(String id, String username, String firstName, String lastName, 
			String address, String townCode, String alpha2CountryCode, String townName) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "UPDATE service_user SET username = ?, first_name = ?, last_name = ?, address = ?, "
				+ "town_postal_code = ?, alpha_2_town_country_code = ?, town_name = ? WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		statement.setString(2, firstName);
		statement.setString(3, lastName);
		statement.setString(4, address);
		statement.setString(5, townCode);
		statement.setString(6, alpha2CountryCode);
		statement.setString(7, townName);
		statement.setString(8, id);
		statement.executeUpdate();
		connection.close();
	}
	
	public static ServiceUser getServiceUser(String id) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM service_user LEFT JOIN town ON service_user.town_postal_code = town.postal_code "
				+ "AND service_user.alpha_2_town_country_code = town.alpha_2_country_code AND "
				+ "service_user.town_name = town.name "
				+ "LEFT JOIN country ON town.alpha_2_country_code = country.alpha_2_code WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			ServiceUser serviceUser = new ServiceUser(resultSet.getString(1), resultSet.getString(2), resultSet.getString(4), resultSet.getString(5),
					resultSet.getString(6), new Town(resultSet.getString(10), new Country(resultSet.getString(13), resultSet.getString(14)),
					resultSet.getString(12)));
			connection.close();
			return serviceUser;
		}
		connection.close();
		return null;
	}
	
	public static List<Employee> getAllEmployees() throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM employee";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		List<Employee> employees = new LinkedList<>();
		while(resultSet.next()) {
			employees.add(new Employee(resultSet.getString(1), resultSet.getString(2)));
		}
		connection.close();
		return employees;
	}
	
	public static List<ServiceUser> getServiceUsers(String id, String attribute, String value) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM service_user LEFT JOIN town ON service_user.town_postal_code = town.postal_code "
				+ "AND service_user.alpha_2_town_country_code = town.alpha_2_country_code AND "
				+ "service_user.town_name = town.name "
				+ "LEFT JOIN country ON town.alpha_2_country_code = country.alpha_2_code WHERE id != ? AND LOWER("
				+ attribute + ") LIKE '%" + value + "%'";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		List<ServiceUser> serviceUsers = new LinkedList<>();
		while(resultSet.next()) {
			serviceUsers.add(new ServiceUser(resultSet.getString(1), resultSet.getString(2), resultSet.getString(4), resultSet.getString(5),
					resultSet.getString(6), new Town(resultSet.getString(10), new Country(resultSet.getString(13), resultSet.getString(14)),
					resultSet.getString(12))));
		}
		connection.close();
		return serviceUsers;
	}
	
	public static boolean isUserAlreadyDeleted(String id, String table) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT id FROM " + table + " WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(!resultSet.next()) {
			connection.close();
			return true;
		}
		connection.close();
		return false;
	}
	
	public static List<Town> getAllCities() throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM town LEFT JOIN country ON town.alpha_2_country_code = country.alpha_2_code "
				+ "ORDER BY name LIMIT 200000";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		List<Town> cities = new LinkedList<>();
		while(resultSet.next()) {
			cities.add(new Town(resultSet.getString(1), new Country(resultSet.getString(4), resultSet.getString(5)), resultSet.getString(3)));
		}
		connection.close();
		return cities;
	}
	
	public static List<Country> getAllCountries() throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM country";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		List<Country> countries = new LinkedList<>();
		while(resultSet.next()) {
			countries.add(new Country(resultSet.getString(1), resultSet.getString(2)));
		}
		connection.close();
		return countries;
	}
}
