package net.omni.killrewards.listener;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        if (killer == null)
            return;

        if (plugin.getKillUtil().wasKilled(player)) { // player is in map
            Player killedKiller = plugin.getKillUtil().getKiller(player); // get the killer of the player who died

            if (killedKiller == null) // just a check incase the killer leaves
                return;

            if (killedKiller.getName().equals(killer.getName())) { // same person was killed by the killer
                if (plugin.getCooldownUtil().inCooldown(killer)) { // player is in cooldown = no reward;
                    plugin.sendMessage(killer,
                            plugin.getMessagesUtil().getCooldownMsg().
                                    replace("%player%", player.getName()).
                                    replace("%cooldown%",
                                            "" + plugin.getCooldownUtil().getCooldown(killer)));
                    return;
                }
            }
        }

        plugin.getKillUtil().setKilled(killer, player);
        plugin.getCooldownUtil().addCooldown(killer, plugin.getConfigHandler().getInt("cooldown-time"));

        // TODO cache
        plugin.getCacheUtil().addKills(killer, 1);
        plugin.getCacheUtil().updateKDR(killer);

        plugin.getCacheUtil().addDeaths(player, 1);
        plugin.getCacheUtil().updateKDR(player);

        plugin.getCacheUtil().addKillStreak(killer, 1);
        plugin.getCacheUtil().setKillStreak(player, 0);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                plugin.getConfigHandler().getString("command_money").
                        replace("%killer%", killer.getName()));
        plugin.sendMessage(killer,
                plugin.getMessagesUtil().getKilled().replace("%player%", player.getName()));
        Bukkit.broadcastMessage(plugin.getMessagesUtil().getBroadcastKilled().
                replace("%killer%", killer.getName()).
                replace("%killed%", player.getName()));
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
