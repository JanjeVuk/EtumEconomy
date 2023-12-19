package net.etum.etumeconomy.Manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Cette classe gère la configuration du plugin.
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private ConfigurationSection redisConfig;
    private ConfigurationSection storageConfig;
    private ConfigurationSection informationStorageConfig;
    private ConfigurationSection mysqlConfig;

    /**
     * Constructeur de la classe ConfigManager.
     *
     * @param plugin Instance du plugin
     */
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }

    /**
     * Charge la configuration depuis le fichier de configuration.
     */
    private void loadConfig() {
        // Vérifie si le dossier du plugin existe, sinon le crée
        if (!plugin.getDataFolder().exists()) {
            if(!plugin.getDataFolder().mkdirs()){
                Bukkit.getLogger().warning("Le dossier ne peut être créé");
                return;
            }
        }

        // Crée le fichier de configuration s'il n'existe pas
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // Charge la configuration depuis le fichier YAML
        this.redisConfig = plugin.getConfig().getConfigurationSection("redis");
        this.storageConfig = plugin.getConfig().getConfigurationSection("storage");
        this.informationStorageConfig = plugin.getConfig().getConfigurationSection("information_storage");
        this.mysqlConfig = plugin.getConfig().getConfigurationSection("mysql");
    }

    /**
     * Obtient l'hôte Redis depuis la configuration.
     *
     * @return L'hôte Redis
     */
    public String getRedisHost() {
        return redisConfig.getString("host");
    }

    /**
     * Obtient le port Redis depuis la configuration.
     *
     * @return Le port Redis
     */
    public int getRedisPort() {
        return redisConfig.getInt("port", 6379);
    }

    /**
     * Obtient le type de stockage depuis la configuration.
     *
     * @return Le type de stockage
     */
    public String getStorageType() {
        return storageConfig.getString("type");
    }

    /**
     * Obtient le format de stockage d'informations depuis la configuration.
     *
     * @return Le format de stockage d'informations
     */
    public String getInformationStorageFormat() {
        return informationStorageConfig.getString("format");
    }

    /**
     * Obtient l'hôte MySQL depuis la configuration.
     *
     * @return L'hôte MySQL
     */
    public String getMysqlHost() {
        return mysqlConfig.getString("host");
    }

    /**
     * Obtient le port MySQL depuis la configuration.
     *
     * @return Le port MySQL
     */
    public int getMysqlPort() {
        return mysqlConfig.getInt("port", 3306);
    }

    /**
     * Obtient le nom d'utilisateur MySQL depuis la configuration.
     *
     * @return Le nom d'utilisateur MySQL
     */
    public String getMysqlUser() {
        return mysqlConfig.getString("user");
    }

    /**
     * Obtient le mot de passe MySQL depuis la configuration.
     *
     * @return Le mot de passe MySQL
     */
    public String getMysqlPassword() {
        return mysqlConfig.getString("password");
    }

    /**
     * Obtient le nom de la base de données MySQL depuis la configuration.
     *
     * @return Le nom de la base de données MySQL
     */
    public String getMysqlDatabase() {
        return mysqlConfig.getString("database");
    }
}
