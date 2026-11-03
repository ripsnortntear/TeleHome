package me.ripsnortntear.telehome.models;

import org.bukkit.Location;

import java.util.Map;
import java.util.Objects;

public class Home extends SavedLocation {

    private final String name;
    private final long createdAt;

    public Home(String name, Location loc) {
        super(loc);
        this.name = Objects.requireNonNull(name, "Home name cannot be null");
        this.createdAt = System.currentTimeMillis();
    }

    public Home(String name, String world, double x, double y, double z, float yaw, float pitch, long createdAt) {
        super(world, x, y, z, yaw, pitch);
        this.name = Objects.requireNonNull(name, "Home name cannot be null");
        this.createdAt = createdAt > 0 ? createdAt : System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("name", name);
        map.put("createdAt", createdAt);
        return map;
    }

    public static Home fromMap(String name, Map<String, Object> map) {
        if (map == null) return null;
        String actualName = (String) map.getOrDefault("name", name);
        String world = (String) map.getOrDefault("world", "world");
        double x = ((Number) map.getOrDefault("x", 0.0)).doubleValue();
        double y = ((Number) map.getOrDefault("y", 0.0)).doubleValue();
        double z = ((Number) map.getOrDefault("z", 0.0)).doubleValue();
        float yaw = ((Number) map.getOrDefault("yaw", 0.0)).floatValue();
        float pitch = ((Number) map.getOrDefault("pitch", 0.0)).floatValue();
        long createdAt = ((Number) map.getOrDefault("createdAt", 0L)).longValue();
        return new Home(actualName, world, x, y, z, yaw, pitch, createdAt);
    }
}
