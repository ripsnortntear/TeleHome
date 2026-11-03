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
import java.util.regex.Pattern;

public class SetHomeCommand implements CommandExecutor, TabCompleter {

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,20}$");
    private final TeleHome plugin;

    public SetHomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.sethome")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        String homeName = args.length > 0 ? args[0] : "home";

        if (!VALID_NAME_PATTERN.matcher(homeName).matches()) {
            plugin.getConfigManager().sendMessage(player, "invalid-home-name",
                    "&cInvalid home name. Names must be alphanumeric (1-20 characters).");
            return true;
        }

        Home existing = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (existing == null) {
            int current = plugin.getHomeManager().getHomeCount(player.getUniqueId());
            int max = plugin.getHomeManager().getMaxHomes(player);
            if (current >= max) {
                plugin.getConfigManager().sendMessage(player, "home-limit-reached",
                        "&cYou have reached your limit of &e%limit%&c home(s). Delete one with &f/delhome <name>&c first.",
                        "%limit%", String.valueOf(max));
                return true;
            }
        }

        boolean updated = plugin.getHomeManager().setHome(player.getUniqueId(), homeName, player.getLocation());
        String msgKey = updated ? "home-updated" : "home-set";
        plugin.getConfigManager().sendMessage(player, msgKey,
                "&aHome '&e%name%&a' has been set!",
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
