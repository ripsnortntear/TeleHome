package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.SavedLocation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeathCommand implements CommandExecutor {

    private final TeleHome plugin;

    public DeathCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.death")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        if (!plugin.getConfigManager().isDeathCommandEnabled()) {
            player.sendMessage("Death teleportation is disabled.");
            return true;
        }

        SavedLocation deathLoc = plugin.getDeathBackManager().getDeathLocation(player.getUniqueId());
        if (deathLoc == null) {
            plugin.getConfigManager().sendMessage(player, "no-death-location",
                    "&cYou do not have a recorded death location.");
            return true;
        }

        Location destination = deathLoc.toLocation();
        if (destination == null) {
            plugin.getConfigManager().sendMessage(player, "world-missing",
                    "&cThe destination world is not currently loaded or does not exist.");
            return true;
        }

        int delay = plugin.getConfigManager().isDelayDeathAndBack()
                ? plugin.getConfigManager().getTeleportDelaySeconds()
                : 0;

        plugin.getTeleportManager().startTeleport(player, destination, "death location",
                delay, "death-teleporting", "death-teleported");

        return true;
    }
}
