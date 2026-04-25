package com.blackboxai.telehome.commands;

import com.blackboxai.telehome.TeleHome;
import com.blackboxai.telehome.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListHomeCommand implements CommandExecutor {

    private final TeleHome plugin;

    public ListHomeCommand(TeleHome plugin) {
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

        Collection<Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());

        if (homes.isEmpty()) {
            player.sendMessage(color(plugin.getConfig().getString("messages.no-homes",
                    "&eYou have no homes set. Use &f/sethome <name>&e to set one.")));
            return true;
        }

        List<Home> sorted = new ArrayList<>(homes);
        sorted.sort(Comparator.comparing(Home::getName));

        String header = plugin.getConfig().getString("messages.list-header",
                "&6--- Your Homes (&e%count%&6) ---").replace("%count%", String.valueOf(sorted.size()));
        player.sendMessage(color(header));

        String entryFormat = plugin.getConfig().getString("messages.list-entry",
                "&7- &e%name% &7(&f%world%&7 @ &f%x%&7, &f%y%&7, &f%z%&7)");

        for (Home home : sorted) {
            String line = entryFormat
                    .replace("%name%", home.getName())
                    .replace("%world%", home.getWorld())
                    .replace("%x%", String.valueOf((int) home.getX()))
                    .replace("%y%", String.valueOf((int) home.getY()))
                    .replace("%z%", String.valueOf((int) home.getZ()));
            player.sendMessage(color(line));
        }

        // Optional footer with join hint
        String names = sorted.stream().map(Home::getName).collect(Collectors.joining(", "));
        String footer = plugin.getConfig().getString("messages.list-footer",
                "&7Use &f/home <name>&7 to teleport.");
        if (footer != null && !footer.isBlank()) {
            player.sendMessage(color(footer));
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    private String color(String input) {
        if (input == null) return "";
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}