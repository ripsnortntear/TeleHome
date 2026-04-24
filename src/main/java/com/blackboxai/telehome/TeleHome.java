package com.blackboxai.telehome;

import com.blackboxai.telehome.commands.HomeCommand;
import com.blackboxai.telehome.commands.SetHomeCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleHome extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.homeManager = new HomeManager(this);
        this.homeManager.loadHomes();

        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));

        getLogger().info("TeleHome enabled.");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) {
            homeManager.saveHomes();
        }
        getLogger().info("TeleHome disabled.");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }
}