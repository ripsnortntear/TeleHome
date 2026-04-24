package com.blackboxai.telehome.commands;

import com.blackboxai.telehome.TeleHome;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor {

    private final TeleHome plugin;

    public SetHomeCommand(TeleHome plugin) {
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
            player.sendMessage(color(plugin.getConfig().getString("messages.usage-sethome",
                    "&cUsage: /sethome <name>")));
            return true;
        }

        String homeName = args[0];

        // Basic name validation — alphanumeric + underscore/dash, 1–32 chars
        if (!homeName.matches("[a-zA-Z0-9_-]{1,32}")) {
            player.sendMessage(color("&cInvalid home name. Use letters, numbers, _ or - (max 32 chars)."));
            return true;
        }

        plugin.getHomeManager().setHome(player.getUniqueId(), homeName, player.getLocation());

        String msg = plugin.getConfig().getString("messages.home-set",
                "&aHome '&e%name%&a' has been set!").replace("%name%", homeName);
        player.sendMessage(color(msg));

        return true;
    }

    @SuppressWarnings("deprecation")
    private String color(String input) {
        if (input == null) return "";
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}