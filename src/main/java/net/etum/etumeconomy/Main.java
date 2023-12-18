package net.etum.etumeconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Economy econ = null;
    private static Main instance = null;

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] Disabled Version", getName()));
    }

    @Override
    public void onEnable() {

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        instance = this;


        //setup default config if not exist
        saveDefaultConfig();

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        EcoManager.register();

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }




    public static Economy getEconomy() {
        return econ;
    }

    public static Main getInstance() {
        return instance;
    }
}