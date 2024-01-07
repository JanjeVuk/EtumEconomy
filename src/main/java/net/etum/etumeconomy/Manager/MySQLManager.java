package net.etum.etumeconomy.Manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {

    private final HikariDataSource dataSource;

    // Constantes pour les requêtes SQL
    private static final String INSERT_OR_UPDATE_QUERY =
            "INSERT INTO %s (%s, %s) VALUES (?, ?) ON DUPLICATE KEY UPDATE %s=?";
    private static final String SELECT_QUERY =
            "SELECT %s FROM %s WHERE %s=?";

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
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10); // Adjust as needed

        this.dataSource = new HikariDataSource(config);
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
        String query = String.format(INSERT_OR_UPDATE_QUERY, tableName, keyColumn, valueColumn, valueColumn);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Utiliser une transaction pour garantir l'intégrité des données
            connection.setAutoCommit(false);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, value);
            preparedStatement.executeUpdate();
            connection.commit(); // Valider la transaction
        } catch (SQLException e) {
            handleSQLException(e);
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
        String query = String.format(SELECT_QUERY, valueColumn, tableName, keyColumn);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, key);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(valueColumn);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    /**
     * Handle SQLException and log the error message.
     *
     * @param e SQLException
     */
    private void handleSQLException(SQLException e) {
        // Annuler la transaction en cas d'erreur
        try {
            if (dataSource != null && dataSource.getConnection() != null) {
                dataSource.getConnection().rollback();
            }
        } catch (SQLException ex) {
            Bukkit.getLogger().warning("Error rolling back transaction: " + ex.getMessage());
        }

        Bukkit.getLogger().warning("Error executing SQL query: " + e.getMessage());
    }

    /**
     * Close the HikariCP data source when the plugin is disabled.
     */
    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
