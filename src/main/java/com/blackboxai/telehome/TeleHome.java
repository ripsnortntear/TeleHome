package com.blackboxai.telehome;

import com.blackboxai.telehome.commands.DelHomeCommand;
import com.blackboxai.telehome.commands.HomeCommand;
import com.blackboxai.telehome.commands.ListHomeCommand;
import com.blackboxai.telehome.commands.SetHomeCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleHome extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.homeManager = new HomeManager(this);
        this.homeManager.loadHomes();

        // /sethome
        getCommand("sethome").setExecutor(new SetHomeCommand(this));

        // /home (with tab-complete)
        HomeCommand homeCmd = new HomeCommand(this);
        PluginCommand home = getCommand("home");
        home.setExecutor(homeCmd);
        home.setTabCompleter(homeCmd);

        // /delhome (with tab-complete)
        DelHomeCommand delHomeCmd = new DelHomeCommand(this);
        PluginCommand delhome = getCommand("delhome");
        delhome.setExecutor(delHomeCmd);
        delhome.setTabCompleter(delHomeCmd);

        // /listhome
        getCommand("listhome").setExecutor(new ListHomeCommand(this));

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