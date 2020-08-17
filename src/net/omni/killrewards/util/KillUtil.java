package net.omni.killrewards.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KillUtil {
    private final Map<Player, Player> PLAYERKILLED = new HashMap<>();

    public Player getKilled(Player killer) {
        return PLAYERKILLED.getOrDefault(killer, null);
    }

    public Player getKiller(Player player) {
        if (!(wasKilled(player)))
            return null;

        Player killer = null;

        for (Player k : PLAYERKILLED.keySet()) {
            if (k == null)
                continue;

            if (PLAYERKILLED.get(k).getName().equals(player.getName())) {
                killer = k;
                break;
            }
        }

        return killer;
    }

    public boolean wasKilled(Player killed) {
        return PLAYERKILLED.containsValue(killed);
    }

    public boolean isKiller(Player killer) {
        return PLAYERKILLED.containsKey(killer);
    }

    public void setKilled(Player player, Player killed) {
        PLAYERKILLED.put(player, killed);
    }

    public void removeKilled(Player player) { // cooldown passed
        PLAYERKILLED.remove(player);
    }
}
