package me.ripsnortntear.telehome.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SavedLocation {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public SavedLocation(Location loc) {
        Objects.requireNonNull(loc, "Location cannot be null");
        this.world = loc.getWorld() != null ? loc.getWorld().getName() : "world";
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public SavedLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world != null ? world : "world";
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location toLocation() {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return null;
        }
        return new Location(w, x, y, z, yaw, pitch);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", (double) yaw);
        map.put("pitch", (double) pitch);
        return map;
    }

    public static SavedLocation deserialize(Map<String, Object> map) {
        if (map == null) return null;
        String world = (String) map.getOrDefault("world", "world");
        double x = ((Number) map.getOrDefault("x", 0.0)).doubleValue();
        double y = ((Number) map.getOrDefault("y", 0.0)).doubleValue();
        double z = ((Number) map.getOrDefault("z", 0.0)).doubleValue();
        float yaw = ((Number) map.getOrDefault("yaw", 0.0)).floatValue();
        float pitch = ((Number) map.getOrDefault("pitch", 0.0)).floatValue();
        return new SavedLocation(world, x, y, z, yaw, pitch);
    }
}
