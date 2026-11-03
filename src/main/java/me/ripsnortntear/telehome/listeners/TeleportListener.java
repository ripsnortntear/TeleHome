package me.ripsnortntear.telehome.listeners;

import me.ripsnortntear.telehome.TeleHome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    private final TeleHome plugin;

    public TeleportListener(TeleHome plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getTeleportManager().cancelPending(player.getUniqueId());

        if (plugin.getConfigManager().isDeathCommandEnabled()) {
            plugin.getDeathBackManager().setDeathLocation(player.getUniqueId(), player.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!plugin.getConfigManager().isBackCommandEnabled()) {
            return;
        }

        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.COMMAND ||
            cause == PlayerTeleportEvent.TeleportCause.PLUGIN ||
            cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
            cause == PlayerTeleportEvent.TeleportCause.SPECTATE ||
            cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) {

            plugin.getDeathBackManager().setLastLocation(event.getPlayer().getUniqueId(), event.getFrom());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            plugin.getTeleportManager().handleDamage(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTeleportManager().cancelPending(event.getPlayer().getUniqueId());
    }
}
