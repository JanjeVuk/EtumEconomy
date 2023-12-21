package net.etum.etumeconomy.Manager;

import net.etum.etumeconomy.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages operations on a Redis server to store user balances.
 */
public class RedisManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);

    // Jedis pool instance to communicate with the Redis server
    private JedisPool jedisPool;

    // Flag to indicate whether to use Redis storage or not
    private boolean useRedis;

    /**
     * Constructor for the RedisManager class.
     */
    public RedisManager(ConfigManager configManager) {
        // Check the storage type
        String storageType = configManager.getStorageType();
        useRedis = storageType.equalsIgnoreCase("REDIS");

        // If using Redis, initialize the Jedis pool
        if (useRedis) {
            initializeJedisPool(configManager);
        }
    }

    /**
     * Initializes the Jedis pool if using Redis storage.
     */
    private void initializeJedisPool(ConfigManager configManager) {
        // Jedis pool configuration
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // Initialize the Jedis pool with a password
        this.jedisPool = new JedisPool(poolConfig, configManager.getRedisHost(), configManager.getRedisPort(), configManager.getRedisTimeout(), configManager.getRedisPassword());

        // Check and reconnect if the connection is closed during the creation of the RedisManager object
        checkAndReconnect(configManager);
    }

    /**
     * Checks if the Jedis connection is closed and reconnects if necessary.
     */
    private void checkAndReconnect(ConfigManager config) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Check if the connection is closed or if the ping to the server fails
            if (!jedis.isConnected() || !pingServer(jedis)) {
                // Log a warning message
                logger.warn("Connection to Redis is closed or ping to the server failed. Reconnecting...");

                // Close the existing pool
                close();

                // Reconnect the connection
                initializeJedisPool(config);

                // Log an info message
                logger.info("Reconnection to Redis successful.");
            }
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during Redis connection check and reconnect.", e);
        }
    }

    /**
     * Checks if the Redis server is accessible by sending a ping.
     *
     * @param jedis Jedis connection
     * @return true if the ping succeeds, false otherwise
     */
    private boolean pingServer(Jedis jedis) {
        try {
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during Redis ping.", e);
            return false;
        }
    }

    /**
     * Closes the connection with the Redis server.
     */
    public void close() {
        try {
            // Close the Jedis pool
            jedisPool.close();
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during Redis connection close.", e);
        }
    }

    /**
     * Adds or updates a user's balance.
     *
     * @param playerName User Name
     * @param amount     Amount to set for the user
     */
    public void setBalance(String playerName, double amount) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Use the hset method of Jedis to add or update the balance in the balances object
            jedis.hset("balances", playerName, String.valueOf(amount));
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during setBalance operation.", e);
        }
    }

    /**
     * Retrieves the balance of a user.
     *
     * @param playerName User Name
     * @return The user's balance, or 0.0 if the user has no defined balance
     */
    public double getBalance(String playerName) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Use the hget method of Jedis to retrieve the user's balance from the balances object
            String balance = jedis.hget("balances", playerName);
            return balance != null ? Double.parseDouble(balance) : 0.0;
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during getBalance operation.", e);
            return 0.0;
        }
    }

    /**
     * Vérifie si un compte avec un solde est associé au nom d'un joueur.
     *
     * @param playerName Nom du joueur
     * @return true si un compte existe, false sinon
     */
    public boolean hasAccount(String playerName) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Utilise la méthode hexists de Jedis pour vérifier si le joueur a un compte dans l'objet "balances"
            return jedis.hexists("balances", playerName);
        } catch (Exception e) {
            // Journalise un message d'erreur en cas d'exception
            logger.error("Erreur lors de la vérification de l'existence du compte.", e);
            return false;
        }
    }

    /**
     * Retrieves all user balances.
     *
     * @return A map of balances with player names as keys and amounts as values
     */
    public Map<String, Double> getAllBalances() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Use the hgetAll method of Jedis to retrieve all user balances from the balances object
            Map<String, String> redisBalances = jedis.hgetAll("balances");
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<String, String> entry : redisBalances.entrySet()) {
                result.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            }
            return result;
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("Error during getAllBalances operation.", e);
            return Collections.emptyMap();
        }
    }
}

