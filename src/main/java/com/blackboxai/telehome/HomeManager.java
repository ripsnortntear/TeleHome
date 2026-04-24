package com.blackboxai.telehome;

import com.blackboxai.telehome.models.Home;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private final TeleHome plugin;
    private final File homesFile;
    private final FileConfiguration homesConfig;

    // UUID -> (homeName(lower) -> Home)
    private final Map<UUID, Map<String, Home>> homes = new HashMap<>();

    public HomeManager(TeleHome plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml: " + e.getMessage());
            }
        }
        this.homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void loadHomes() {
        homes.clear();
        for (String uuidKey : homesConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidKey);
                ConfigurationSection playerSection = homesConfig.getConfigurationSection(uuidKey);
                if (playerSection == null) continue;

                Map<String, Home> playerHomes = new HashMap<>();
                for (String homeName : playerSection.getKeys(false)) {
                    ConfigurationSection hs = playerSection.getConfigurationSection(homeName);
                    if (hs == null) continue;

                    Home home = new Home(
                            homeName,
                            hs.getString("world"),
                            hs.getDouble("x"),
                            hs.getDouble("y"),
                            hs.getDouble("z"),
                            (float) hs.getDouble("yaw"),
                            (float) hs.getDouble("pitch")
                    );
                    playerHomes.put(homeName.toLowerCase(), home);
                }
                homes.put(uuid, playerHomes);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Invalid UUID key in homes.yml: " + uuidKey);
            }
        }
        plugin.getLogger().info("Loaded homes for " + homes.size() + " player(s).");
    }

    public void saveHomes() {
        // Wipe root keys, then rewrite.
        for (String key : new HashSet<>(homesConfig.getKeys(false))) {
            homesConfig.set(key, null);
        }

        for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
            String uuid = entry.getKey().toString();
            for (Home home : entry.getValue().values()) {
                String path = uuid + "." + home.getName();
                homesConfig.set(path + ".world", home.getWorld());
                homesConfig.set(path + ".x", home.getX());
                homesConfig.set(path + ".y", home.getY());
                homesConfig.set(path + ".z", home.getZ());
                homesConfig.set(path + ".yaw", home.getYaw());
                homesConfig.set(path + ".pitch", home.getPitch());
            }
        }

        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml: " + e.getMessage());
        }
    }

    public void setHome(UUID uuid, String name, Location location) {
        String key = name.toLowerCase();
        homes.computeIfAbsent(uuid, k -> new HashMap<>())
             .put(key, new Home(key, location));
        saveHomes();
    }

    public Home getHome(UUID uuid, String name) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase());
    }
}