package net.etum.etumeconomy.Manager;

import net.etum.etumeconomy.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * This class manages player balances stored in individual YAML files.
 */
public class BalanceManager {

    // Dossier de données
    private final File dataFolder;

    /**
     * Constructor for the BalanceManager class.
     * Initializes the data folder and creates it if it does not exist.
     */
    public BalanceManager() {
        this.dataFolder = new File(Main.getInstance().getDataFolder(), "data");
        if (!this.dataFolder.mkdirs()) {
            Bukkit.getLogger().warning("Failed to create data folder for player balances.");
        }
    }


    /**
     * Gets the balance of a player by UUID.
     *
     * @param playerUUID UUID of the player.
     * @return The balance of the player, or 0.0 if not found or an error occurs.
     */
    public double getBalance(UUID playerUUID) {
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        if (playerFile.exists()) {
            try {
                YamlConfiguration config = new YamlConfiguration();
                config.load(playerFile);
                return config.getDouble("balance", 0.0);
            } catch (IOException | InvalidConfigurationException e) {
                Bukkit.getLogger().warning("Error loading player balance: " + e.getMessage());
            }
        }
        return 0.0;
    }

    /**
     * Sets the balance of a player by UUID.
     *
     * @param playerUUID UUID of the player.
     * @param balance    New balance to set.
     */
    public void setBalance(UUID playerUUID, double balance) {
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("balance", balance);
            config.save(playerFile);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Error saving player balance: " + e.getMessage());
        }
    }
}
