package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.Home;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeManager {

    private final TeleHome plugin;
    private final File dataFolder;
    private final Logger logger;
    private final int defaultMaxHomes;
    private final File file;
    private FileConfiguration config;

    // UUID -> (lowercaseName -> Home)
    private final Map<UUID, Map<String, Home>> homes = new ConcurrentHashMap<>();

    public HomeManager(TeleHome plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getLogger();
        this.defaultMaxHomes = plugin.getConfigManager() != null ? plugin.getConfigManager().getMaxHomes() : 10;
        this.file = new File(dataFolder, "homes.yml");
    }

    public HomeManager(File dataFolder, Logger logger, int defaultMaxHomes) {
        this.plugin = null;
        this.dataFolder = dataFolder;
        this.logger = logger != null ? logger : Logger.getLogger("HomeManager");
        this.defaultMaxHomes = defaultMaxHomes;
        this.file = new File(dataFolder, "homes.yml");
    }

    public synchronized void loadHomes() {
        homes.clear();
        if (!file.exists()) {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not create homes.yml", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String uuidStr : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection sec = config.getConfigurationSection(uuidStr);
                if (sec == null) continue;

                Map<String, Home> playerHomes = new HashMap<>();
                for (String homeKey : sec.getKeys(false)) {
                    ConfigurationSection hs = sec.getConfigurationSection(homeKey);
                    if (hs == null) continue;

                    String originalName = hs.getString("name", homeKey);
                    String world = hs.getString("world", "world");
                    double x = hs.getDouble("x");
                    double y = hs.getDouble("y");
                    double z = hs.getDouble("z");
                    float yaw = (float) hs.getDouble("yaw");
                    float pitch = (float) hs.getDouble("pitch");
                    long createdAt = hs.getLong("createdAt", 0L);

                    Home home = new Home(originalName, world, x, y, z, yaw, pitch, createdAt);
                    playerHomes.put(originalName.toLowerCase(Locale.ROOT), home);
                }
                homes.put(uuid, playerHomes);
            } catch (IllegalArgumentException e) {
                logger.warning("Skipping invalid UUID in homes.yml: " + uuidStr);
            }
        }
        logger.info("Loaded homes for " + homes.size() + " player(s).");
    }

    public synchronized void saveHomes() {
        if (config == null) {
            config = new YamlConfiguration();
        }

        for (String key : new HashSet<>(config.getKeys(false))) {
            config.set(key, null);
        }

        for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Home home : entry.getValue().values()) {
                String path = uuidStr + "." + home.getName().toLowerCase(Locale.ROOT);
                config.set(path + ".name", home.getName());
                config.set(path + ".world", home.getWorld());
                config.set(path + ".x", home.getX());
                config.set(path + ".y", home.getY());
                config.set(path + ".z", home.getZ());
                config.set(path + ".yaw", (double) home.getYaw());
                config.set(path + ".pitch", (double) home.getPitch());
                config.set(path + ".createdAt", home.getCreatedAt());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save homes.yml", e);
        }
    }

    public boolean setHome(UUID uuid, String name, Location location) {
        String key = name.toLowerCase(Locale.ROOT);
        Map<String, Home> playerHomes = homes.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        boolean existed = playerHomes.containsKey(key);
        playerHomes.put(key, new Home(name, location));
        saveHomes();
        return existed;
    }

    public Home getHome(UUID uuid, String name) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return false;

        Home removed = playerHomes.remove(name.toLowerCase(Locale.ROOT));
        if (removed == null) return false;

        if (playerHomes.isEmpty()) {
            homes.remove(uuid);
        }
        saveHomes();
        return true;
    }

    public Collection<Home> getHomes(UUID uuid) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) return Collections.emptyList();
        return Collections.unmodifiableCollection(playerHomes.values());
    }

    public int getHomeCount(UUID uuid) {
        Map<String, Home> playerHomes = homes.get(uuid);
        return playerHomes == null ? 0 : playerHomes.size();
    }

    public int getMaxHomes(Player player) {
        if (player.hasPermission("telehome.bypass.limit") || player.hasPermission("telehome.admin")) {
            return Integer.MAX_VALUE;
        }

        int max = (plugin != null && plugin.getConfigManager() != null)
                ? plugin.getConfigManager().getMaxHomes()
                : defaultMaxHomes;

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            if (pai.getValue() && pai.getPermission().toLowerCase(Locale.ROOT).startsWith("telehome.max.")) {
                try {
                    int val = Integer.parseInt(pai.getPermission().substring("telehome.max.".length()));
                    if (val > max) {
                        max = val;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return max;
    }
}
