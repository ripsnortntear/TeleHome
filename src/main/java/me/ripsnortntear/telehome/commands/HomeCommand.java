package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.Home;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final TeleHome plugin;

    public HomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.home")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        Collection<Home> playerHomes = plugin.getHomeManager().getHomes(player.getUniqueId());

        String homeName;
        if (args.length >= 1) {
            homeName = args[0];
        } else {
            // Default handling
            if (playerHomes.isEmpty()) {
                plugin.getConfigManager().sendMessage(player, "no-homes",
                        "&eYou have no homes set. Use &f/sethome <name>&e to create one.");
                return true;
            }

            Home defaultHome = plugin.getHomeManager().getHome(player.getUniqueId(), "home");
            if (defaultHome != null) {
                homeName = defaultHome.getName();
            } else if (playerHomes.size() == 1) {
                homeName = playerHomes.iterator().next().getName();
            } else {
                plugin.getConfigManager().sendMessage(player, "usage-home", "&cUsage: /home <name>");
                return true;
            }
        }

        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (home == null) {
            plugin.getConfigManager().sendMessage(player, "home-not-found",
                    "&cNo home found named '&e%name%&c'.",
                    "%name%", homeName);
            return true;
        }

        Location destination = home.toLocation();
        if (destination == null) {
            plugin.getConfigManager().sendMessage(player, "world-missing",
                    "&cThe destination world is not currently loaded or does not exist.");
            return true;
        }

        int delay = plugin.getConfigManager().getTeleportDelaySeconds();
        plugin.getTeleportManager().startTeleport(player, destination, home.getName(),
                delay, "teleporting", "teleported");

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
        for (Home home : plugin.getHomeManager().getHomes(player.getUniqueId())) {
            if (home.getName().toLowerCase().startsWith(partial)) {
                matches.add(home.getName());
            }
        }
        Collections.sort(matches);
        return matches;
    }
}
