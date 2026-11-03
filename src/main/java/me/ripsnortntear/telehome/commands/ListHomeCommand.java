package me.ripsnortntear.telehome.commands;

import me.ripsnortntear.telehome.TeleHome;
import me.ripsnortntear.telehome.models.Home;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ListHomeCommand implements CommandExecutor {

    private final TeleHome plugin;
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    public ListHomeCommand(TeleHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getConfigManager().sendMessage(sender, "players-only", "&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("telehome.listhome")) {
            plugin.getConfigManager().sendMessage(player, "no-permission", "&cYou do not have permission to use this command.");
            return true;
        }

        Collection<Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            plugin.getConfigManager().sendMessage(player, "no-homes",
                    "&eYou have no homes set. Use &f/sethome <name>&e to create one.");
            return true;
        }

        List<Home> sorted = new ArrayList<>(homes);
        sorted.sort(Comparator.comparing(Home::getName, String.CASE_INSENSITIVE_ORDER));

        int max = plugin.getHomeManager().getMaxHomes(player);
        String limitStr = max == Integer.MAX_VALUE ? "∞" : String.valueOf(max);

        plugin.getConfigManager().sendMessage(player, "list-header",
                "&6--- Your Homes (&e%count%&6/%limit%) ---",
                "%count%", String.valueOf(sorted.size()),
                "%limit%", limitStr);

        String rawEntry = plugin.getConfigManager().getRawMessage("list-entry",
                "&7- &e%name% &7(&f%world%&7: &f%x%&7, &f%y%&7, &f%z%&7)");

        for (Home home : sorted) {
            String formattedText = rawEntry
                    .replace("%name%", home.getName())
                    .replace("%world%", home.getWorld())
                    .replace("%x%", String.valueOf((int) Math.round(home.getX())))
                    .replace("%y%", String.valueOf((int) Math.round(home.getY())))
                    .replace("%z%", String.valueOf((int) Math.round(home.getZ())));

            Component entryComp = SERIALIZER.deserialize(formattedText)
                    .hoverEvent(HoverEvent.showText(SERIALIZER.deserialize("&aClick to teleport to &e" + home.getName())))
                    .clickEvent(ClickEvent.runCommand("/home " + home.getName()));

            player.sendMessage(entryComp);
        }

        plugin.getConfigManager().sendMessage(player, "list-footer",
                "&7Click or type &f/home <name>&7 to teleport.");

        return true;
    }
}
