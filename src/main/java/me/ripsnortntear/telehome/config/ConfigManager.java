package me.ripsnortntear.telehome.config;

import me.ripsnortntear.telehome.TeleHome;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final TeleHome plugin;
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    public ConfigManager(TeleHome plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public int getTeleportDelaySeconds() {
        return getConfig().getInt("teleport-delay-seconds", 3);
    }

    public boolean isDelayDeathAndBack() {
        return getConfig().getBoolean("delay-death-and-back", true);
    }

    public boolean isCancelOnMove() {
        return getConfig().getBoolean("cancel-on-move", true);
    }

    public boolean isCancelOnDamage() {
        return getConfig().getBoolean("cancel-on-damage", true);
    }

    public int getMaxHomes() {
        return getConfig().getInt("max-homes", 10);
    }

    public boolean isDeathCommandEnabled() {
        return getConfig().getBoolean("features.death-command", true);
    }

    public boolean isBackCommandEnabled() {
        return getConfig().getBoolean("features.back-command", true);
    }

    public String getRawMessage(String key, String def) {
        return getConfig().getString("messages." + key, def);
    }

    public String getPrefix() {
        return getConfig().getString("messages.prefix", "&6[TeleHome]&r ");
    }

    public Component getComponent(String key, String def, String... replacements) {
        String msg = getRawMessage(key, def);
        if (msg == null || msg.isEmpty()) {
            return null;
        }

        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }

        return SERIALIZER.deserialize(msg);
    }

    public void sendMessage(CommandSender sender, String key, String def, String... replacements) {
        Component comp = getComponent(key, def, replacements);
        if (comp != null) {
            sender.sendMessage(comp);
        }
    }
}
