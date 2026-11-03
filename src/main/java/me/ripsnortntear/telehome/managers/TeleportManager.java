package me.ripsnortntear.telehome.managers;

import me.ripsnortntear.telehome.TeleHome;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final TeleHome plugin;
    private final Map<UUID, ActiveTeleport> activeTeleports = new ConcurrentHashMap<>();

    public static class ActiveTeleport {
        private final Location destination;
        private final Location startLocation;
        private final String targetName;
        private final String successMsgKey;
        private final BukkitTask task;

        public ActiveTeleport(Location destination, Location startLocation, String targetName, String successMsgKey, BukkitTask task) {
            this.destination = destination;
            this.startLocation = startLocation;
            this.targetName = targetName;
            this.successMsgKey = successMsgKey;
            this.task = task;
        }

        public Location getDestination() {
            return destination;
        }

        public Location getStartLocation() {
            return startLocation;
        }

        public String getTargetName() {
            return targetName;
        }

        public String getSuccessMsgKey() {
            return successMsgKey;
        }

        public void cancel() {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
    }

    public TeleportManager(TeleHome plugin) {
        this.plugin = plugin;
    }

    public void startTeleport(Player player, Location destination, String targetName,
                              int delaySeconds, String warmupMsgKey, String successMsgKey) {
        if (!player.isOnline()) {
            return;
        }

        boolean bypass = player.hasPermission("telehome.bypass.delay") || delaySeconds <= 0;
        if (bypass) {
            cancelPending(player.getUniqueId());
            player.teleport(destination);
            plugin.getConfigManager().sendMessage(player, successMsgKey,
                    "&aTeleported to &e%name%&a!",
                    "%name%", targetName != null ? targetName : "");
            return;
        }

        cancelPending(player.getUniqueId());

        Location startLocation = player.getLocation().clone();

        plugin.getConfigManager().sendMessage(player, warmupMsgKey,
                "&aTeleporting to '&e%name%&a' in &e%seconds%&a seconds. Don't move!",
                "%name%", targetName != null ? targetName : "",
                "%seconds%", String.valueOf(delaySeconds));

        BukkitTask task = new BukkitRunnable() {
            int remaining = delaySeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    activeTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (plugin.getConfigManager().isCancelOnMove() && hasMoved(startLocation, player.getLocation())) {
                    plugin.getConfigManager().sendMessage(player, "teleport-cancelled-move",
                            "&cTeleport cancelled because you moved!");
                    activeTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 0) {
                    activeTeleports.remove(player.getUniqueId());
                    cancel();

                    player.teleport(destination);
                    plugin.getConfigManager().sendMessage(player, successMsgKey,
                            "&aTeleported to &e%name%&a!",
                            "%name%", targetName != null ? targetName : "");
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        activeTeleports.put(player.getUniqueId(),
                new ActiveTeleport(destination, startLocation, targetName, successMsgKey, task));
    }

    public static boolean hasMoved(Location start, Location current) {
        if (start.getWorld() == null || current.getWorld() == null) return true;
        if (!start.getWorld().equals(current.getWorld())) return true;
        return start.distanceSquared(current) > 0.25; // More than 0.5 blocks moved
    }

    public void handleDamage(Player player) {
        if (plugin.getConfigManager().isCancelOnDamage()) {
            ActiveTeleport active = activeTeleports.remove(player.getUniqueId());
            if (active != null) {
                active.cancel();
                plugin.getConfigManager().sendMessage(player, "teleport-cancelled-damage",
                        "&cTeleport cancelled because you took damage!");
            }
        }
    }

    public void cancelPending(UUID uuid) {
        ActiveTeleport active = activeTeleports.remove(uuid);
        if (active != null) {
            active.cancel();
        }
    }

    public boolean hasPending(UUID uuid) {
        return activeTeleports.containsKey(uuid);
    }

    public void cancelAll() {
        for (ActiveTeleport active : activeTeleports.values()) {
            active.cancel();
        }
        activeTeleports.clear();
    }
}
