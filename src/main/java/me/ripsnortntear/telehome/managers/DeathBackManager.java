package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.SavedLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeathBackManager {

    private final TeleHome plugin;
    private final File dataFolder;
    private final Logger logger;
    private final File file;
    private FileConfiguration config;

    // UUID -> DeathLocation
    private final Map<UUID, SavedLocation> deathLocations = new ConcurrentHashMap<>();
    // UUID -> LastLocation
    private final Map<UUID, SavedLocation> lastLocations = new ConcurrentHashMap<>();

    private volatile boolean dirty = false;

    public DeathBackManager(TeleHome plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getLogger();
        this.file = new File(dataFolder, "deathback.yml");
    }

    public DeathBackManager(File dataFolder, Logger logger) {
        this.plugin = null;
        this.dataFolder = dataFolder;
        this.logger = logger != null ? logger : Logger.getLogger("DeathBackManager");
        this.file = new File(dataFolder, "deathback.yml");
    }

    public synchronized void loadData() {
        deathLocations.clear();
        lastLocations.clear();

        if (!file.exists()) {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not create deathback.yml", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        // Load death locations
        ConfigurationSection deaths = config.getConfigurationSection("deaths");
        if (deaths != null) {
            for (String uuidKey : deaths.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    ConfigurationSection sec = deaths.getConfigurationSection(uuidKey);
                    if (sec == null) continue;
                    deathLocations.put(uuid, new SavedLocation(
                            sec.getString("world", "world"),
                            sec.getDouble("x"),
                            sec.getDouble("y"),
                            sec.getDouble("z"),
                            (float) sec.getDouble("yaw"),
                            (float) sec.getDouble("pitch")
                    ));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // Load last locations
        ConfigurationSection backs = config.getConfigurationSection("backs");
        if (backs != null) {
            for (String uuidKey : backs.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    ConfigurationSection sec = backs.getConfigurationSection(uuidKey);
                    if (sec == null) continue;
                    lastLocations.put(uuid, new SavedLocation(
                            sec.getString("world", "world"),
                            sec.getDouble("x"),
                            sec.getDouble("y"),
                            sec.getDouble("z"),
                            (float) sec.getDouble("yaw"),
                            (float) sec.getDouble("pitch")
                    ));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        dirty = false;
        logger.info("Loaded " + deathLocations.size() + " death location(s) and " + lastLocations.size() + " back location(s).");
    }

    public synchronized void saveData() {
        if (config == null) {
            config = new YamlConfiguration();
        }

        config.set("deaths", null);
        config.set("backs", null);

        for (Map.Entry<UUID, SavedLocation> entry : deathLocations.entrySet()) {
            String path = "deaths." + entry.getKey();
            SavedLocation loc = entry.getValue();
            config.set(path + ".world", loc.getWorld());
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            config.set(path + ".yaw", (double) loc.getYaw());
            config.set(path + ".pitch", (double) loc.getPitch());
        }

        for (Map.Entry<UUID, SavedLocation> entry : lastLocations.entrySet()) {
            String path = "backs." + entry.getKey();
            SavedLocation loc = entry.getValue();
            config.set(path + ".world", loc.getWorld());
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            config.set(path + ".yaw", (double) loc.getYaw());
            config.set(path + ".pitch", (double) loc.getPitch());
        }

        try {
            config.save(file);
            dirty = false;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save deathback.yml", e);
        }
    }

    public void setDeathLocation(UUID uuid, Location loc) {
        if (loc == null) return;
        deathLocations.put(uuid, new SavedLocation(loc));
        markDirty();
    }

    public SavedLocation getDeathLocation(UUID uuid) {
        return deathLocations.get(uuid);
    }

    public void clearDeathLocation(UUID uuid) {
        deathLocations.remove(uuid);
        markDirty();
    }

    public void setLastLocation(UUID uuid, Location loc) {
        if (loc == null) return;
        lastLocations.put(uuid, new SavedLocation(loc));
        markDirty();
    }

    public SavedLocation getLastLocation(UUID uuid) {
        return lastLocations.get(uuid);
    }

    public void clearLastLocation(UUID uuid) {
        lastLocations.remove(uuid);
        markDirty();
    }

    private void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }
}
