package me.ripsnortntear.telehome;

import me.ripsnortntear.telehome.commands.*;
import me.ripsnortntear.telehome.config.ConfigManager;
import me.ripsnortntear.telehome.listeners.TeleportListener;
import me.ripsnortntear.telehome.managers.DeathBackManager;
import me.ripsnortntear.telehome.managers.HomeManager;
import me.ripsnortntear.telehome.managers.TeleportManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleHome extends JavaPlugin {

    private ConfigManager configManager;
    private HomeManager homeManager;
    private DeathBackManager deathBackManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.homeManager = new HomeManager(this);
        this.homeManager.loadHomes();

        this.deathBackManager = new DeathBackManager(this);
        this.deathBackManager.loadData();

        this.teleportManager = new TeleportManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);

        // Register commands & completers
        registerCommand("sethome", new SetHomeCommand(this));
        registerCommand("home", new HomeCommand(this));
        registerCommand("delhome", new DelHomeCommand(this));
        registerCommand("listhome", new ListHomeCommand(this));
        registerCommand("death", new DeathCommand(this));
        registerCommand("back", new BackCommand(this));
        registerCommand("telehome", new TeleHomeCommand(this));

        getLogger().info("TeleHome v" + getPluginMeta().getVersion() + " for Minecraft 26.3 enabled!");
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) {
            teleportManager.cancelAll();
        }
        if (homeManager != null) {
            homeManager.saveHomes();
        }
        if (deathBackManager != null && deathBackManager.isDirty()) {
            deathBackManager.saveData();
        }
        getLogger().info("TeleHome disabled.");
    }

    private void registerCommand(String name, Object executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Command /" + name + " is not defined in plugin.yml!");
            return;
        }

        if (executor instanceof org.bukkit.command.CommandExecutor ce) {
            cmd.setExecutor(ce);
        }
        if (executor instanceof org.bukkit.command.TabCompleter tc) {
            cmd.setTabCompleter(tc);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public DeathBackManager getDeathBackManager() {
        return deathBackManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
