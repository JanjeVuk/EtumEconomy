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
    private final ConfigManager config;
    private final RedisManager redis;

    public EcoManager() {
        this.config = Main.getConfigManager();
        this.redis = new RedisManager(config);
    }

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
        switch (config.getStorageType()) {
            case "LOCAL":
                return balances.containsKey(playerName);
            case "REDIS":
                return redis.hasAccount(playerName);
            default:
                return false;
        }
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
        switch (config.getStorageType()) {
            case "LOCAL":
                return balances.getOrDefault(playerName, 0.0);
            case "REDIS":
                return redis.getBalance(playerName);
            default:
                return 0.0;
        }
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
                return withdrawLocal(playerName, value);

            case "REDIS":
                return withdrawRedis(playerName, value);

            default:
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unsupported storage type");
        }
    }

    private EconomyResponse withdrawLocal(String playerName, double value) {
        double playerBalance = getBalance(playerName);

        if (playerBalance < value) {
            return new EconomyResponse(0, playerBalance, EconomyResponse.ResponseType.FAILURE, "Player does not have enough money");
        }

        // Vérification pour éviter un solde négatif
        if (playerBalance - value < 0) {
            return new EconomyResponse(0, playerBalance, EconomyResponse.ResponseType.FAILURE, "Withdrawal would result in negative balance");
        }

        // Retrait réussi
        balances.put(playerName, playerBalance - value);
        return new EconomyResponse(value, balances.get(playerName), EconomyResponse.ResponseType.SUCCESS, null);
    }

    private EconomyResponse withdrawRedis(String playerName, double value) {
        if (redis.hasAccount(playerName)) {
            double playerRedisBalance = redis.getBalance(playerName);

            if (playerRedisBalance < value) {
                return new EconomyResponse(0, playerRedisBalance, EconomyResponse.ResponseType.FAILURE, "Player does not have enough money");
            }

            // Vérification pour éviter un solde négatif
            if (playerRedisBalance - value < 0) {
                return new EconomyResponse(0, playerRedisBalance, EconomyResponse.ResponseType.FAILURE, "Withdrawal would result in negative balance");
            }

            // Retrait réussi de Redis
            redis.setBalance(playerName, playerRedisBalance - value);
            return new EconomyResponse(value, redis.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player account not found in Redis");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double value) {
        return withdrawPlayer(offlinePlayer.getName(), value);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String world, double value) {
        return withdrawPlayer(playerName, value);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double value) {
        return withdrawPlayer(offlinePlayer.getName(), value);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double value) {
        switch (config.getStorageType()) {
            case "LOCAL":
                return depositLocal(playerName, value);

            case "REDIS":
                return depositRedis(playerName, value);

            default:
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unsupported storage type");
        }
    }

    private EconomyResponse depositLocal(String playerName, double value) {
        double playerBalance = getBalance(playerName);

        // Deposit successful
        balances.put(playerName, playerBalance + value);
        return new EconomyResponse(value, balances.get(playerName), EconomyResponse.ResponseType.SUCCESS, null);
    }

    private EconomyResponse depositRedis(String playerName, double value) {
        if (redis.hasAccount(playerName)) {
            double playerRedisBalance = redis.getBalance(playerName);

            // Deposit successful to Redis
            redis.setBalance(playerName, playerRedisBalance + value);
            return new EconomyResponse(value, redis.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player account not found in Redis");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double value) {
        return depositPlayer(offlinePlayer.getName(), value);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String world, double value) {
        return depositPlayer(playerName, value);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double value) {
        return depositPlayer(offlinePlayer.getName(), value);
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

    public HashMap<String, Double> getBalances() {
        return balances;
    }
}
