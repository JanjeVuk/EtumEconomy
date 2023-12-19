package net.etum.etumeconomy.Manager;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Cette classe gère la lecture et l'écriture de données au format YAML.
 * Elle utilise la bibliothèque SnakeYAML pour traiter les fichiers YAML.
 */
public class YamlManager {

    // Chemin du fichier YAML
    private final Path filePath;

    // Instance de l'analyseur YAML de SnakeYAML
    private final Yaml yaml;

    /**
     * Constructeur de la classe YamlManager.
     *
     * @param filePath Chemin du fichier YAML à gérer.
     */
    public YamlManager(Path filePath) {
        this.filePath = filePath;
        this.yaml = new Yaml();
    }

    /**
     * Enregistre les données fournies dans le fichier YAML spécifié.
     *
     * @param data Données à enregistrer dans le fichier YAML.
     */
    public void save(Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            // Utilisation de SnakeYAML pour convertir les données en format YAML et les écrire dans le fichier
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge les données à partir du fichier YAML spécifié.
     *
     * @return Les données chargées depuis le fichier YAML, ou null si le fichier n'existe pas.
     */
    public Map<String, Object> load() {
        try {
            // Vérifie si le fichier YAML existe
            if (Files.exists(filePath)) {
                // Utilisation de SnakeYAML pour charger les données à partir du fichier YAML
                return yaml.load(Files.newBufferedReader(filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Vérifie si le fichier YAML existe.
     *
     * @return true si le fichier YAML existe, sinon false.
     */
    public boolean exists() {
        return Files.exists(filePath);
    }

    /**
     * Supprime le fichier YAML.
     *
     * @return true si le fichier YAML a été supprimé avec succès, sinon false.
     */
    public boolean delete() {
        try {
            // Supprime le fichier YAML s'il existe
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
