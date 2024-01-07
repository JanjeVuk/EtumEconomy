package net.etum.etumeconomy;

import net.etum.etumeconomy.Listeners.Commands;
import net.etum.etumeconomy.Listeners.Events;
import net.etum.etumeconomy.Manager.ConfigManager;
import net.etum.etumeconomy.Manager.EcoManager;
import net.etum.etumeconomy.Manager.MySQLManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the EtumEconomy plugin.
 */
public class Main extends JavaPlugin {

    private static Economy economy = null;
    private static Main instance = null;
    private static MySQLManager mySQLManager = null;
    private static ConfigManager configManager = null;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Setup default config if not exist
        saveDefaultConfig();

        configManager = new ConfigManager(this);

        if (!setupEconomy()) {
            getLogger().severe("[" + getName() + "] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Events(this);
        new Commands(this);

        if ("MYSQL".equalsIgnoreCase(configManager.getInformationStorageFormat())) {
            mySQLManager = new MySQLManager(
                    configManager.getMysqlHost(),
                    configManager.getMysqlPort(),
                    configManager.getMysqlUser(),
                    configManager.getMysqlPassword(),
                    configManager.getMysqlDatabase()
            );
        }
    }

    /**
     * Set up the Vault economy provider.
     *
     * @return True if successful, false otherwise.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        EcoManager.register();

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            return false;
        }

        economy = economyProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        if (mySQLManager != null) {
            mySQLManager.closeDataSource();
        }
    }

    /**
     * Get the Vault economy provider.
     *
     * @return The economy provider.
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Get the instance of the Main class.
     *
     * @return The instance of the Main class.
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Get the MySQL manager instance.
     *
     * @return The MySQL manager instance.
     */
    public static MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    /**
     * Get the configuration manager instance.
     *
     * @return The configuration manager instance.
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }
}