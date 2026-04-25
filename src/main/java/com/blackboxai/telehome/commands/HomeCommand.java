package com.blackboxai.telehome.commands;

import com.blackboxai.telehome.TeleHome;
import com.blackboxai.telehome.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final TeleHome plugin;
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();

    public HomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.players-only",
                    "&cOnly players can use this command.")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(color(plugin.getConfig().getString("messages.usage-home",
                    "&cUsage: /home <name>")));
            return true;
        }

        String homeName = args[0];
        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);

        if (home == null) {
            String msg = plugin.getConfig().getString("messages.home-not-found",
                    "&cNo home found with the name '&e%name%&c'.").replace("%name%", homeName);
            player.sendMessage(color(msg));
            return true;
        }

        Location destination = home.toLocation();
        if (destination == null) {
            player.sendMessage(color(plugin.getConfig().getString("messages.world-missing",
                    "&cThe world for that home no longer exists.")));
            return true;
        }

        int delay = plugin.getConfig().getInt("teleport-delay-seconds", 3);
        boolean cancelOnMove = plugin.getConfig().getBoolean("cancel-on-move", true);

        if (delay <= 0) {
            player.teleport(destination);
            String msg = plugin.getConfig().getString("messages.teleported",
                    "&aTeleported to '&e%name%&a'!").replace("%name%", home.getName());
            player.sendMessage(color(msg));
            return true;
        }

        BukkitRunnable existing = pendingTeleports.remove(player.getUniqueId());
        if (existing != null && !existing.isCancelled()) {
            existing.cancel();
        }

        String tpMsg = plugin.getConfig().getString("messages.teleporting",
                "&aTeleporting to '&e%name%&a' in &e%seconds% &aseconds. Don't move!")
                .replace("%name%", home.getName())
                .replace("%seconds%", String.valueOf(delay));
        player.sendMessage(color(tpMsg));

        Location startLoc = player.getLocation().clone();

        BukkitRunnable task = new BukkitRunnable() {
            int remaining = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    pendingTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (cancelOnMove && hasMoved(startLoc, player.getLocation())) {
                    player.sendMessage(color(plugin.getConfig().getString("messages.teleport-cancelled",
                            "&cTeleport cancelled because you moved!")));
                    pendingTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 0) {
                    player.teleport(destination);
                    String done = plugin.getConfig().getString("messages.teleported",
                            "&aTeleported to '&e%name%&a'!").replace("%name%", home.getName());
                    player.sendMessage(color(done));
                    pendingTeleports.remove(player.getUniqueId());
                    cancel();
                }
            }
        };

        pendingTeleports.put(player.getUniqueId(), task);
        task.runTaskTimer(plugin, 20L, 20L);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) {
            return Collections.emptyList();
        }

        String partial = args[0].toLowerCase();
        List<String> matches = new ArrayList<>();
        plugin.getHomeManager().getHomes(player.getUniqueId()).forEach(home -> {
            if (home.getName().toLowerCase().startsWith(partial)) {
                matches.add(home.getName());
            }
        });
        Collections.sort(matches);
        return matches;
    }

    private boolean hasMoved(Location a, Location b) {
        if (a.getWorld() == null || b.getWorld() == null) return true;
        if (!a.getWorld().equals(b.getWorld())) return true;
        return a.getBlockX() != b.getBlockX()
                || a.getBlockY() != b.getBlockY()
                || a.getBlockZ() != b.getBlockZ();
    }

    @SuppressWarnings("deprecation")
    private String color(String input) {
        if (input == null) return "";
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}