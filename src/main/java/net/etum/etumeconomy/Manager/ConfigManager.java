package net.etum.etumeconomy.Manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * This class manages the configuration of the plugin.
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private ConfigurationSection redisConfig;
    private ConfigurationSection storageConfig;
    private ConfigurationSection informationStorageConfig;
    private ConfigurationSection mysqlConfig;

    /**
     * Constructor for the ConfigManager class.
     *
     * @param plugin The plugin instance
     */
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }

    /**
     * Loads the configuration from the configuration file.
     */
    private void loadConfig() {
        // Check if the plugin folder exists, create it if not
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            // Handle the folder creation error
            return;
        }

        // Create the configuration file if it does not exist
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // Load the configuration from the YAML file
        this.redisConfig = plugin.getConfig().getConfigurationSection("redis");
        this.storageConfig = plugin.getConfig().getConfigurationSection("storage");
        this.informationStorageConfig = plugin.getConfig().getConfigurationSection("information_storage");
        this.mysqlConfig = plugin.getConfig().getConfigurationSection("mysql");
    }

    /**
     * Gets the Redis host from the configuration.
     *
     * @return The Redis host
     */
    public String getRedisHost() {
        return redisConfig.getString("host");
    }

    /**
     * Gets the Redis port from the configuration.
     *
     * @return The Redis port
     */
    public int getRedisPort() {
        return redisConfig.getInt("port", 6379);
    }

    /**
     * Gets the Redis password from the configuration.
     *
     * @return The Redis password
     */
    public String getRedisPassword() {
        return redisConfig.getString("password");
    }

    /**
     * Gets the Redis timeout from the configuration.
     *
     * @return The Redis timeout
     */
    public int getRedisTimeout() {
        return redisConfig.getInt("timeout", 2000);
    }

    /**
     * Gets the storage type from the configuration.
     *
     * @return The storage type
     */
    public String getStorageType() {
        return storageConfig.getString("type");
    }

    /**
     * Gets the information storage format from the configuration.
     *
     * @return The information storage format
     */
    public String getInformationStorageFormat() {
        return informationStorageConfig.getString("format");
    }

    /**
     * Gets the MySQL host from the configuration.
     *
     * @return The MySQL host
     */
    public String getMysqlHost() {
        return mysqlConfig.getString("host");
    }

    /**
     * Gets the MySQL port from the configuration.
     *
     * @return The MySQL port
     */
    public int getMysqlPort() {
        return mysqlConfig.getInt("port", 3306);
    }

    /**
     * Gets the MySQL username from the configuration.
     *
     * @return The MySQL username
     */
    public String getMysqlUser() {
        return mysqlConfig.getString("user");
    }

    /**
     * Gets the MySQL password from the configuration.
     *
     * @return The MySQL password
     */
    public String getMysqlPassword() {
        return mysqlConfig.getString("password");
    }

    /**
     * Gets the MySQL database name from the configuration.
     *
     * @return The MySQL database name
     */
    public String getMysqlDatabase() {
        return mysqlConfig.getString("database");
    }
}
