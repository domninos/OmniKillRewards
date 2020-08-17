package net.omni.killrewards.listener;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {

    private final KillRewardsPlugin plugin;

    public PlayerKillListener(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null) {
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();

                if (entityEvent.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) entityEvent.getDamager();

                    if (arrow.getShooter() instanceof Player)
                        killer = (Player) arrow.getShooter();
                }
            }
        }

        if (killer == null)
            return;

        if (player.getName().equals(killer.getName()))
            return;

        if (plugin.getCooldownUtil().inCooldown(player)) {
            plugin.getCooldownUtil().removeCooldown(player);
            plugin.getKillUtil().removeKilled(player);
            plugin.sendMessage(player, plugin.getMessagesUtil().getCooldownReset());
        }

        if (plugin.getKillUtil().isKiller(killer)) { // in map
            Player killed = plugin.getKillUtil().getKilled(killer); // get the player the killer killed

            if (killed != null && killed.getName().equals(player.getName())) { // if the player the killer killed is the killed player
                if (plugin.getCooldownUtil().inCooldown(killer))
                    plugin.sendMessage(killer, plugin.getMessagesUtil().getCooldownMsg()
                            .replace("%player%", player.getName())
                            .replace("%cooldown%",
                                    "" + plugin.getCooldownUtil().getCooldown(killer)));
                return;
            } else if (plugin.getCooldownUtil().inCooldown(killer)) {
                // the player the killer killed is not the same && killer is in
                // cooldown -> send cooldown reset msg

                plugin.getCooldownUtil().removeCooldown(killer);
                plugin.getKillUtil().removeKilled(killer);
                plugin.sendMessage(killer, plugin.getMessagesUtil().getCooldownReset());
            }
        }

        // not in map -> set killed
        plugin.getKillUtil().setKilled(killer, player);
        plugin.getCooldownUtil().addCooldown(killer, plugin.getConfigHandler().getInt("cooldown-time"));

        plugin.getCacheUtil().addKills(killer, 1);
        plugin.getCacheUtil().updateKDR(killer);

        plugin.getCacheUtil().addDeaths(player, 1);
        plugin.getCacheUtil().updateKDR(player);

        plugin.getCacheUtil().addKillStreak(killer, 1);
        plugin.getCacheUtil().setKillStreak(player, 0);

        Bukkit.broadcastMessage(plugin.getMessagesUtil().getBroadcastKilled().
                replace("%killer%", killer.getName()).
                replace("%killed%", player.getName()));

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                plugin.getConfigHandler().getString("command_money").
                        replace("%killer%", killer.getName()));

        plugin.sendMessage(killer,
                plugin.getMessagesUtil().getKilled().replace("%player%", player.getName()));
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
