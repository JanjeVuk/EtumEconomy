package net.etum.etumeconomy.Manager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

/**
 * Cette classe gère les opérations sur un serveur Redis pour stocker les soldes des utilisateurs.
 */
public class RedisManager {

    // Instance de la connexion Jedis pour communiquer avec le serveur Redis
    private final JedisPool jedisPool;

    /**
     * Constructeur de la classe RedisManager avec mot de passe.
     *
     * @param host     Adresse IP ou nom d'hôte du serveur Redis
     * @param port     Port du serveur Redis
     * @param password Mot de passe du serveur Redis
     */
    public RedisManager(String host, int port, String password) {
        // Configuration du pool Jedis
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // Vous pouvez ajuster la configuration du pool selon vos besoins

        // Initialisation du pool Jedis avec mot de passe
        this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
    }

    /**
     * Ferme la connexion avec le serveur Redis.
     */
    public void close() {
        // Fermeture du pool Jedis
        jedisPool.close();
    }

    /**
     * Ajoute ou met à jour le solde d'un utilisateur.
     *
     * @param uuid   UUID de l'utilisateur
     * @param amount Montant à définir pour l'utilisateur
     */
    public void setBalance(String uuid, String amount) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Utilisation de la méthode hset de Jedis pour ajouter ou mettre à jour le solde dans l'objet balance
            jedis.hset("balances", uuid, amount);
        }
    }

    /**
     * Récupère le solde d'un utilisateur.
     *
     * @param uuid UUID de l'utilisateur
     * @return Le solde de l'utilisateur, ou null si l'utilisateur n'a pas de solde défini
     */
    public String getBalance(String uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Utilisation de la méthode hget de Jedis pour récupérer le solde de l'utilisateur depuis l'objet balance
            return jedis.hget("balances", uuid);
        }
    }

    /**
     * Récupère tous les soldes d'utilisateurs.
     *
     * @return Une carte (Map) des soldes avec les UUID comme clés et les montants comme valeurs
     */
    public Map<String, String> getAllBalances() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Utilisation de la méthode hgetAll de Jedis pour récupérer tous les soldes d'utilisateurs depuis l'objet balance
            return jedis.hgetAll("balances");
        }
    }
}
