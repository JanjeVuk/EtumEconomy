package net.etum.etumeconomy.Manager;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Cette classe gère les opérations de base sur une base de données MySQL.
 */
public class MySQLManager {

    // URL de connexion à la base de données MySQL
    private final String url;

    // Nom d'utilisateur pour la connexion à la base de données MySQL
    private final String user;

    // Mot de passe pour la connexion à la base de données MySQL
    private final String password;

    /**
     * Constructeur de la classe MySQLManager.
     *
     * @param host     Adresse IP ou nom d'hôte du serveur MySQL
     * @param port     Port du serveur MySQL
     * @param user     Nom d'utilisateur MySQL
     * @param password Mot de passe MySQL
     * @param database Nom de la base de données MySQL
     */
    public MySQLManager(String host, int port, String user, String password, String database) {
        // Construction de l'URL de connexion à la base de données MySQL
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.user = user;
        this.password = password;
    }

    /**
     * Insère ou met à jour une entrée dans la table spécifiée.
     *
     * @param tableName    Nom de la table MySQL
     * @param keyColumn    Nom de la colonne pour la clé
     * @param valueColumn  Nom de la colonne pour la valeur
     * @param key          Clé à insérer ou mettre à jour
     * @param value        Valeur associée à la clé
     */
    public void insertOrUpdate(String tableName, String keyColumn, String valueColumn, String key, String value) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Requête SQL pour insérer ou mettre à jour une entrée dans la table
            String query = "INSERT INTO " + tableName + " (" + keyColumn + ", " + valueColumn + ") VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE " + valueColumn + "=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, value);
                preparedStatement.setString(3, value);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            // En cas d'erreur, afficher un avertissement dans la console Bukkit
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Récupère la valeur associée à une clé dans la table spécifiée.
     *
     * @param tableName   Nom de la table MySQL
     * @param keyColumn   Nom de la colonne pour la clé
     * @param valueColumn Nom de la colonne pour la valeur
     * @param key         Clé à rechercher
     * @return La valeur associée à la clé, ou null si la clé n'est pas trouvée
     */
    public String get(String tableName, String keyColumn, String valueColumn, String key) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Requête SQL pour sélectionner la valeur associée à une clé
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
            // En cas d'erreur, afficher un avertissement dans la console Bukkit
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }
}
