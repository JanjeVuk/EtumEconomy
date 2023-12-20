package net.etum.etumeconomy.Manager;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class manages basic operations on a MySQL database.
 */
public class MySQLManager {

    // Connection URL to the MySQL database
    private final String url;

    // Username for the MySQL database connection
    private final String user;

    // Password for the MySQL database connection
    private final String password;

    /**
     * Constructor for the MySQLManager class.
     *
     * @param host     IP address or hostname of the MySQL server
     * @param port     Port of the MySQL server
     * @param user     MySQL username
     * @param password MySQL password
     * @param database MySQL database name
     */
    public MySQLManager(String host, int port, String user, String password, String database) {
        // Constructing the URL for the MySQL database connection
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.user = user;
        this.password = password;
    }

    /**
     * Inserts or updates an entry in the specified table.
     *
     * @param tableName   Name of the MySQL table
     * @param keyColumn   Name of the column for the key
     * @param valueColumn Name of the column for the value
     * @param key         Key to insert or update
     * @param value       Value associated with the key
     */
    public void insertOrUpdate(String tableName, String keyColumn, String valueColumn, String key, String value) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // SQL query to insert or update an entry in the table
            String query = "INSERT INTO " + tableName + " (" + keyColumn + ", " + valueColumn + ") VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE " + valueColumn + "=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, value);
                preparedStatement.setString(3, value);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            // In case of an error, display a warning in the Bukkit console
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Retrieves the value associated with a key in the specified table.
     *
     * @param tableName   Name of the MySQL table
     * @param keyColumn   Name of the column for the key
     * @param valueColumn Name of the column for the value
     * @param key         Key to search for
     * @return The value associated with the key, or null if the key is not found
     */
    public String get(String tableName, String keyColumn, String valueColumn, String key) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // SQL query to select the value associated with a key
            String query = "SELECT " + valueColumn + " FROM " + tableName + " WHERE " + keyColumn + "=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, key);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(valueColumn);
                    }
                }
            }
        } catch (SQLException e) {
            // In case of an error, display a warning in the Bukkit console
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }
}
