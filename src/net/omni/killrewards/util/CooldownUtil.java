package net.omni.killrewards.util;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownUtil {
    private final Map<Player, Integer> COOLDOWNS = new HashMap<>();

    public CooldownUtil(KillRewardsPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Player, Integer> cd : COOLDOWNS.entrySet()) {
                if (cd.getValue() <= 0) {
                    removeCooldown(cd.getKey());
                    plugin.getKillUtil().removeKilled(cd.getKey());
                    continue;
                }

                cd.setValue(cd.getValue() - 1);
            }
        }, 20, 20);
    }

    public int getCooldown(Player player) {
        return inCooldown(player) ? COOLDOWNS.get(player) : -1;
    }

    public void addCooldown(Player player, int seconds) {
        if (inCooldown(player)) {
            int cooldown = COOLDOWNS.get(player);
            int added = cooldown + seconds;
            COOLDOWNS.put(player, added);
        } else
            COOLDOWNS.put(player, seconds);
    }

    public void removeCooldown(Player player) {
        if (!inCooldown(player))
            return;

        COOLDOWNS.remove(player);
    }

    public void subtractSeconds(Player player, int seconds) {
        if (!inCooldown(player))
            return;

        int cooldown = COOLDOWNS.get(player);
        int subtracted = cooldown - seconds;
        COOLDOWNS.put(player, subtracted);
    }

    public boolean inCooldown(Player player) {
        return COOLDOWNS.containsKey(player);
    }
}
