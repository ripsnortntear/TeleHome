package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final TeleHome plugin;

    public DelHomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.delhome")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getConfigManager().sendMessage(player, "usage-delhome", "&cUsage: /delhome <name>");
            return true;
        }

        String homeName = args[0];
        boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);

        if (!deleted) {
            plugin.getConfigManager().sendMessage(player, "home-not-found",
                    "&cNo home found named '&e%name%&c'.",
                    "%name%", homeName);
            return true;
        }

        plugin.getConfigManager().sendMessage(player, "home-deleted",
                "&aHome '&e%name%&a' has been deleted.",
                "%name%", homeName);

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
