package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.SavedLocation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BackCommand implements CommandExecutor {

    private final TeleHome plugin;

    public BackCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.back")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        if (!plugin.getConfigManager().isBackCommandEnabled()) {
            player.sendMessage("Back teleportation is disabled.");
            return true;
        }

        SavedLocation lastLoc = plugin.getDeathBackManager().getLastLocation(player.getUniqueId());
        if (lastLoc == null) {
            plugin.getConfigManager().sendMessage(player, "no-last-location",
                    "&cYou do not have a recorded previous location.");
            return true;
        }

        Location destination = lastLoc.toLocation();
        if (destination == null) {
            plugin.getConfigManager().sendMessage(player, "world-missing",
                    "&cThe destination world is not currently loaded or does not exist.");
            return true;
        }

        int delay = plugin.getConfigManager().isDelayDeathAndBack()
                ? plugin.getConfigManager().getTeleportDelaySeconds()
                : 0;

        plugin.getTeleportManager().startTeleport(player, destination, "previous location",
                delay, "back-teleporting", "back-teleported");

        return true;
    }
}
