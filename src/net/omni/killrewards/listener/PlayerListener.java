package net.omni.killrewards.listener;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final KillRewardsPlugin plugin;

    public PlayerListener(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> plugin.getCacheUtil().load(event.getPlayer()), 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getCacheUtil().save(player);
        plugin.getKillUtil().removeKilled(player);
        plugin.getCooldownUtil().removeCooldown(player);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
