package net.etum.etumeconomy.Manager;

import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * This class manages reading and writing data in YAML format.
 * It uses the SnakeYAML library to handle YAML files.
 */
public class YamlManager {

    // Path to the YAML file
    private final Path yamlFilePath;

    // Instance of the SnakeYAML parser
    private final Yaml yaml;

    /**
     * Constructor for the YamlManager class.
     *
     * @param yamlFilePath Path to the YAML file to manage.
     */
    public YamlManager(Path yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
        this.yaml = new Yaml();
    }

    /**
     * Saves the provided data to the specified YAML file.
     *
     * @param data Data to save to the YAML file.
     */
    public void save(Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(yamlFilePath.toFile())) {
            // Use SnakeYAML to convert data to YAML format and write it to the file
            yaml.dump(data, writer);
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Loads data from the specified YAML file.
     *
     * @return The data loaded from the YAML file, or null if the file does not exist.
     */
    public Map<String, Object> load() {
        try {
            // Check if the YAML file exists
            if (Files.exists(yamlFilePath)) {
                // Use SnakeYAML to load data from the YAML file
                return yaml.load(Files.newBufferedReader(yamlFilePath));
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the YAML file exists.
     *
     * @return true if the YAML file exists, otherwise false.
     */
    public boolean exists() {
        return Files.exists(yamlFilePath);
    }

    /**
     * Deletes the YAML file.
     *
     * @return true if the YAML file was successfully deleted, otherwise false.
     */
    public boolean delete() {
        try {
            // Delete the YAML file if it exists
            Files.deleteIfExists(yamlFilePath);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }
}
