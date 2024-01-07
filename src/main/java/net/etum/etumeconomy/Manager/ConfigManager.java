package net.etum.etumeconomy.Manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;

/**
 * This class manages the configuration of the plugin (improved version).
 */
public class ConfigManager {

    private static final int REDIS_DEFAULT_PORT = 6379;
    private static final int REDIS_DEFAULT_TIMEOUT = 2000;
    private static final int MYSQL_DEFAULT_PORT = 3306;
    private static final boolean BUNGEE_DEFAULT_ENABLED = false;

    private final JavaPlugin plugin;
    private ConfigurationSection redisConfig;
    private ConfigurationSection storageConfig;
    private ConfigurationSection informationStorageConfig;
    private ConfigurationSection mysqlConfig;
    private ConfigurationSection bungeeConfig;

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
     * Loads the configuration from the default configuration file.
     */
    private void loadConfig() {
        // Load the default configuration from the plugin
        FileConfiguration config = plugin.getConfig();
        Optional.ofNullable(config.getConfigurationSection("redis")).ifPresent(conf -> this.redisConfig = conf);
        Optional.ofNullable(config.getConfigurationSection("storage")).ifPresent(conf -> this.storageConfig = conf);
        Optional.ofNullable(config.getConfigurationSection("information_storage")).ifPresent(conf -> this.informationStorageConfig = conf);
        Optional.ofNullable(config.getConfigurationSection("mysql")).ifPresent(conf -> this.mysqlConfig = conf);
        Optional.ofNullable(config.getConfigurationSection("bungee")).ifPresent(conf -> this.bungeeConfig = conf);
    }

    public String getRedisHost() {
        return Optional.ofNullable(redisConfig.getString("host")).orElse("");
    }

    public int getRedisPort() {
        return redisConfig.getInt("port", REDIS_DEFAULT_PORT);
    }

    public String getRedisPassword() {
        return Optional.ofNullable(redisConfig.getString("password")).orElse("");
    }

    public int getRedisTimeout() {
        return redisConfig.getInt("timeout", REDIS_DEFAULT_TIMEOUT);
    }

    public String getStorageType() {
        return Optional.ofNullable(storageConfig.getString("type")).orElse("").toUpperCase();
    }

    public String getInformationStorageFormat() {
        return Optional.ofNullable(informationStorageConfig.getString("format")).orElse("").toUpperCase();
    }

    public String getMysqlHost() {
        return Optional.ofNullable(mysqlConfig.getString("host")).orElse("");
    }

    public int getMysqlPort() {
        return mysqlConfig.getInt("port", MYSQL_DEFAULT_PORT);
    }

    public String getMysqlUser() {
        return Optional.ofNullable(mysqlConfig.getString("user")).orElse("");
    }

    public String getMysqlPassword() {
        return Optional.ofNullable(mysqlConfig.getString("password")).orElse("");
    }

    public String getMysqlDatabase() {
        return Optional.ofNullable(mysqlConfig.getString("database")).orElse("");
    }

    public boolean isBungeeEnabled() {
        return bungeeConfig.getBoolean("enabled", BUNGEE_DEFAULT_ENABLED);
    }
}