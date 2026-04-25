package com.blackboxai.telehome.commands;

import com.blackboxai.telehome.TeleHome;
import org.bukkit.ChatColor;
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
            sender.sendMessage(color(plugin.getConfig().getString("messages.players-only",
                    "&cOnly players can use this command.")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(color(plugin.getConfig().getString("messages.usage-delhome",
                    "&cUsage: /delhome <name>")));
            return true;
        }

        String homeName = args[0];

        boolean removed = plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);

        if (!removed) {
            String msg = plugin.getConfig().getString("messages.home-not-found",
                    "&cNo home found with the name '&e%name%&c'.").replace("%name%", homeName);
            player.sendMessage(color(msg));
            return true;
        }

        String msg = plugin.getConfig().getString("messages.home-deleted",
                "&aHome '&e%name%&a' has been deleted.").replace("%name%", homeName);
        player.sendMessage(color(msg));
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

    @SuppressWarnings("deprecation")
    private String color(String input) {
        if (input == null) return "";
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}