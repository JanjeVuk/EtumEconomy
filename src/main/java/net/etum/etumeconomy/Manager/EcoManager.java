package net.etum.etumeconomy.Manager;

import net.etum.etumeconomy.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.HashMap;
import java.util.List;

public class EcoManager implements Economy {

    private final String currencyNameSingular = "Helok";
    private final HashMap<String, Double> balances = new HashMap<>();

    ConfigManager config = new ConfigManager(Main.getInstance());
    RedisManager redis = new RedisManager(config);


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return Main.getInstance().getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double value) {
        return value + " " + currencyNamePlural();
    }

    @Override
    public String currencyNamePlural() {
        return currencyNameSingular + "s";
    }

    @Override
    public String currencyNameSingular() {
        return currencyNameSingular;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return switch (config.getStorageType()) {
            case "LOCAL" -> balances.containsKey(playerName);
            case "REDIS" -> redis.hasAccount(playerName);
            default -> false;
        };
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer.getName());
    }

    @Override
    public boolean hasAccount(String playerName, String world) {
        // Implement if needed
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer playerName, String world) {
        // Implement if needed
        return false;
    }

    @Override
    public double getBalance(String playerName) {
        return switch (config.getStorageType()) {
            case "LOCAL" -> balances.get(playerName);
            case "REDIS" -> redis.getBalance(playerName);
            default -> 0.0;
        };
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getName());
    }

    @Override
    public double getBalance(String playerName, String world) {
        // Implement if needed
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String world) {
        // Implement if needed
        return 0;
    }

    @Override
    public boolean has(String playerName, double value) {
        return getBalance(playerName) >= value;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double value) {
        return has(offlinePlayer.getName(), value);
    }

    @Override
    public boolean has(String s, String s1, double value) {
        // Implement if needed
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        // Implement if needed
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double value) {
        switch (config.getStorageType()) {
            case "LOCAL":
                double playerBalance = getBalance(playerName);

                if (playerBalance < value) {
                    // The player does not have enough money
                    return new EconomyResponse(0, playerBalance, EconomyResponse.ResponseType.FAILURE, "Player does not have enough money");
                }

                // Withdrawal successful
                balances.put(playerName, playerBalance - value);
                return new EconomyResponse(value, balances.get(playerName), EconomyResponse.ResponseType.SUCCESS, null);

            case "REDIS":
                // Use the RedisManager class to perform the withdrawal
                if (redis.hasAccount(playerName)) {
                    double playerRedisBalance = redis.getBalance(playerName);

                    if (playerRedisBalance < value) {
                        // The player does not have enough money in Redis
                        return new EconomyResponse(0, playerRedisBalance, EconomyResponse.ResponseType.FAILURE, "Player does not have enough money");
                    }

                    // Withdrawal successful from Redis
                    redis.setBalance(playerName, playerRedisBalance - value);
                    return new EconomyResponse(value, redis.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    // Player account not found in Redis
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player account not found in Redis");
                }

            default:
                // Unsupported storage type
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unsupported storage type");
        }
    }


    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double value) {
        return withdrawPlayer(offlinePlayer.getPlayer(), value);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double value) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double value) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, double value) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double value) {
        return depositPlayer(offlinePlayer.getName(), value);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double value) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double value) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double value) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        // Implement if needed
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        // Implement if needed
        return null;
    }

    @Override
    public List<String> getBanks() {
        // Implement if needed
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        // Implement if needed
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        // Implement if needed
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        // Implement if needed
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        // Implement if needed
        return false;
    }

    public static void register() {
        Bukkit.getServicesManager().register(Economy.class, new EcoManager(), Main.getInstance(), ServicePriority.Normal);
    }
}
