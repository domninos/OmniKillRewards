package net.omni.killrewards.util;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CooldownUtil {
    private final Map<Player, Instant> COOLDOWNS = new HashMap<>();

    public CooldownUtil(KillRewardsPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Player, Instant> cd : COOLDOWNS.entrySet()) {
                if (cd.getValue().isBefore(Instant.now())) {
                    removeCooldown(cd.getKey());
                    plugin.getKillUtil().removeKilled(cd.getKey());
                }
            }
        }, 20, 20);
    }

    public int getCooldown(Player player) {
        return inCooldown(player) ? COOLDOWNS.get(player).compareTo(Instant.now()) : -1;
    }

    public void addCooldown(Player player, int seconds) {
        if (inCooldown(player)) {
            Instant cooldown = COOLDOWNS.get(player);
            Instant added = cooldown.plusSeconds(seconds);
            COOLDOWNS.put(player, added);
        } else
            COOLDOWNS.put(player, Instant.now());
    }

    public void removeCooldown(Player player) {
        if (!inCooldown(player))
            return;

        COOLDOWNS.remove(player);
    }

    public void subtractSeconds(Player player, int seconds) {
        if (!inCooldown(player))
            return;

        COOLDOWNS.put(player, COOLDOWNS.get(player).minusSeconds(seconds));
    }

    public boolean inCooldown(Player player) {
        return COOLDOWNS.containsKey(player);
    }
}
