package hr.ferit.pomds.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import hr.ferit.pomds.data.Country;
import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.data.Mail;
import hr.ferit.pomds.data.ServiceUser;
import hr.ferit.pomds.data.Town;
import hr.ferit.pomds.utils.DeliveryState;

public class DeliveryDatabaseOperations {
	
	public static void insertDelivery(String senderId, String recipientId, String mailType, String mailName,
			boolean isFragile) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		UUID mailId = UUID.randomUUID();
		
		insertMail(connection, mailId.toString(), mailType, mailName, isFragile);
		
		String sql = "INSERT INTO delivery VALUES(UUID(), ?, ?, ?, null, null, false)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, mailId.toString());
		statement.setString(2, senderId);
		statement.setString(3, recipientId);
		statement.executeUpdate();
		
		connection.close();
	}
	
	public static int changeDeliveryDate(DeliveryState deliveryState, String id, Date date) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		if(isDeliveryAlreadyChanged(connection, id, deliveryState)) {
			connection.close();
			return 1;
		}
		String sql = "UPDATE delivery SET ";
		if(deliveryState == DeliveryState.PENDING) {
			sql += "takeover_date = ? ";
		}
		else if(deliveryState == DeliveryState.ACTIVE) {
			sql += "completion_date = ? ";
		}
		else {
			connection.close();
			return 2;
		}
		sql += "WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setDate(1, date);
		statement.setString(2, id);
		statement.executeUpdate();
		
		connection.close();
		return 0;
	}
	
	public static int failDelivery(String id) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		if(isDeliveryAlreadyChanged(connection, id, DeliveryState.ACTIVE)) {
			connection.close();
			return 1;
		}
		String sql = "UPDATE delivery SET is_unsuccessful = true WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.executeUpdate();
		
		connection.close();
		return 0;
	}
	
	private static boolean isDeliveryAlreadyChanged(Connection connection, String id, DeliveryState deliveryState) throws SQLException {
	
		String sql = "SELECT * FROM delivery WHERE id = ? AND ";
		if(deliveryState == DeliveryState.PENDING) {
			sql += "takeover_date IS NULL AND completion_date IS NULL AND is_unsuccessful = false";
		}
		else if(deliveryState == DeliveryState.ACTIVE) {
			sql += "takeover_date IS NOT NULL AND completion_date IS NULL AND is_unsuccessful = false";
		}
		else {
			sql += "takeover_date IS NOT NULL AND (completion_date IS NOT NULL OR is_unsuccessful = true)";
		}
		
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			return false;
		}
		return true;
	}
	
	public static boolean isDeliveryAlreadyChanged(String id, DeliveryState deliveryState) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		
		String sql = "SELECT * FROM delivery WHERE id = ? AND ";
		if(deliveryState == DeliveryState.PENDING) {
			sql += "takeover_date IS NULL AND completion_date IS NULL AND is_unsuccessful = false";
		}
		else if(deliveryState == DeliveryState.ACTIVE) {
			sql += "takeover_date IS NOT NULL AND completion_date IS NULL AND is_unsuccessful = false";
		}
		else {
			sql += "takeover_date IS NOT NULL AND (completion_date IS NOT NULL OR is_unsuccessful = true)";
		}
		
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			connection.close();
			return false;
		}
		connection.close();
		return true;
	}
	
	public static List<Delivery> getAllDeliveries(DeliveryState state) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "SELECT * FROM delivery LEFT JOIN mail ON delivery.mail_id = mail.id "
				+ "LEFT JOIN service_user sender ON delivery.sender_id = sender.id "
				+ "LEFT JOIN town source_town ON sender.town_postal_code = source_town.postal_code AND sender.alpha_2_town_country_code "
				+ "= source_town.alpha_2_country_code AND sender.town_name = source_town.name "
				+ "LEFT JOIN country source_country ON source_town.alpha_2_country_code = source_country.alpha_2_code "
				+ "LEFT JOIN service_user recipient ON delivery.recipient_id = recipient.id "
				+ "LEFT JOIN town destination_town ON recipient.town_postal_code = destination_town.postal_code AND "
				+ "recipient.alpha_2_town_country_code = destination_town.alpha_2_country_code AND recipient.town_name = destination_town.name "
				+ "LEFT JOIN country destination_country ON destination_town.alpha_2_country_code = destination_country.alpha_2_code ";
		if(state == DeliveryState.PENDING) {
			sql += "WHERE takeover_date IS NULL AND completion_date IS NULL "
					+ "AND is_unsuccessful = false";
		}
		else if(state == DeliveryState.ACTIVE) {
			sql += "WHERE takeover_date IS NOT NULL AND completion_date IS NULL "
					+ "AND is_unsuccessful = false ";
		}
		else {
			sql += "WHERE takeover_date IS NOT NULL AND (completion_date IS NOT NULL "
					+ "OR is_unsuccessful = true)";
		}
		sql += " ORDER BY takeover_date DESC";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		List<Delivery> deliveries = new LinkedList<>();
		while(resultSet.next()) {
			deliveries.add(new Delivery(resultSet.getString(1), new Mail(resultSet.getString(8), resultSet.getString(9), resultSet.getString(10), 
					resultSet.getBoolean(11)), new ServiceUser(resultSet.getString(12), resultSet.getString(13), resultSet.getString(15),
					resultSet.getString(16), resultSet.getString(17), new Town(resultSet.getString(21), new Country(resultSet.getString(24), resultSet.getString(25)),
					resultSet.getString(23))), new ServiceUser(resultSet.getString(26), resultSet.getString(27), resultSet.getString(29),
					resultSet.getString(30), resultSet.getString(31), new Town(resultSet.getString(35), new Country(resultSet.getString(38), resultSet.getString(39)),
					resultSet.getString(37))), resultSet.getDate(5), resultSet.getDate(6), resultSet.getBoolean(7)));
		}
		
		connection.close();
		return deliveries;
	}
	
	public static List<Delivery> getUserDeliveries(String id) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql =  "SELECT * FROM delivery LEFT JOIN mail ON delivery.mail_id = mail.id "
				+ "LEFT JOIN service_user sender ON delivery.sender_id = sender.id "
				+ "LEFT JOIN town source_town ON sender.town_postal_code = source_town.postal_code AND sender.alpha_2_town_country_code "
				+ "= source_town.alpha_2_country_code AND sender.town_name = source_town.name "
				+ "LEFT JOIN country source_country ON source_town.alpha_2_country_code = source_country.alpha_2_code "
				+ "LEFT JOIN service_user recipient ON delivery.recipient_id = recipient.id "
				+ "LEFT JOIN town destination_town ON recipient.town_postal_code = destination_town.postal_code AND "
				+ "recipient.alpha_2_town_country_code = destination_town.alpha_2_country_code AND recipient.town_name = destination_town.name "
				+ "LEFT JOIN country destination_country ON destination_town.alpha_2_country_code = destination_country.alpha_2_code "
				+ "WHERE sender_id = ? OR recipient_id = ? ORDER BY "
				+ "takeover_date is NULL DESC, is_unsuccessful = false AND completion_date is NULL DESC, "
				+ "takeover_date DESC";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.setString(2, id);
		ResultSet resultSet = statement.executeQuery();
		List<Delivery> deliveries = new LinkedList<>();
		while(resultSet.next()) {
			deliveries.add(new Delivery(resultSet.getString(1), new Mail(resultSet.getString(8), resultSet.getString(9), resultSet.getString(10), 
					resultSet.getBoolean(11)), new ServiceUser(resultSet.getString(12), resultSet.getString(13), resultSet.getString(15),
					resultSet.getString(16), resultSet.getString(17), new Town(resultSet.getString(21), new Country(resultSet.getString(24), resultSet.getString(25)),
					resultSet.getString(23))), new ServiceUser(resultSet.getString(26), resultSet.getString(27), resultSet.getString(29),
					resultSet.getString(30), resultSet.getString(31), new Town(resultSet.getString(35), new Country(resultSet.getString(38), resultSet.getString(39)),
					resultSet.getString(37))), resultSet.getDate(5), resultSet.getDate(6), resultSet.getBoolean(7)));
		}
		connection.close();
		return deliveries;
	}
	
	public static void deleteDelivery(String id) throws SQLException {
		
		Connection connection = DatabaseConnectionSetup.getConnection();
		String sql = "DELETE FROM delivery WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.executeUpdate();
	}
	
	private static void insertMail(Connection connection, String id, String type, String name, boolean isFragile) throws SQLException {
		
		String sql = "INSERT INTO mail VALUES(?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.setString(2, type);
		statement.setString(3, name);
		statement.setBoolean(4, isFragile);
		statement.executeUpdate();
	}
}