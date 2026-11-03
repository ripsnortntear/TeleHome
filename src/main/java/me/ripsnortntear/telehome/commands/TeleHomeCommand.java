package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeleHomeCommand implements CommandExecutor, TabCompleter {

    private final TeleHome plugin;
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "version", "help");

    public TeleHomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("telehome.admin")) {
            plugin.getConfigManager().sendMessage(sender, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§6--- TeleHome v" + plugin.getPluginMeta().getVersion() + " ---");
            sender.sendMessage("§e/sethome <name> §7- Set a home at current location");
            sender.sendMessage("§e/home [name] §7- Teleport to a home");
            sender.sendMessage("§e/delhome <name> §7- Delete a home");
            sender.sendMessage("§e/listhome §7- View your homes");
            sender.sendMessage("§e/death §7- Teleport to your last death location");
            sender.sendMessage("§e/back §7- Teleport back to previous location");
            sender.sendMessage("§e/telehome reload §7- Reload configuration and data");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().reload();
            plugin.getHomeManager().loadHomes();
            plugin.getDeathBackManager().loadData();
            plugin.getConfigManager().sendMessage(sender, "plugin-reloaded",
                    "&aTeleHome configuration and data have been reloaded successfully.");
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage("§6TeleHome §ev" + plugin.getPluginMeta().getVersion() + " §7by §f" + String.join(", ", plugin.getPluginMeta().getAuthors()) + " §7(Minecraft 26.3).");
            return true;
        }

        sender.sendMessage("§cUnknown subcommand. Use §f/telehome help§c.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("telehome.admin") || args.length != 1) {
            return Collections.emptyList();
        }

        String partial = args[0].toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String sub : SUBCOMMANDS) {
            if (sub.startsWith(partial)) {
                matches.add(sub);
            }
        }
        return matches;
    }
}
