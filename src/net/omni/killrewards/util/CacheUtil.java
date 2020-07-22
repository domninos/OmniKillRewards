package net.omni.killrewards.util;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheUtil {
    private final Map<UUID, Integer> playerKills;
    private final Map<UUID, Integer> playerDeaths;
    private final Map<UUID, Double> playerKDR;
    private final Map<UUID, Integer> playerKillStreak;
    private final KillRewardsPlugin plugin;

    public CacheUtil(KillRewardsPlugin plugin) {
        // TODO save every 10 mins

        this.playerKills = new HashMap<>();
        this.playerDeaths = new HashMap<>();
        this.playerKDR = new HashMap<>();
        this.playerKillStreak = new HashMap<>();
        this.plugin = plugin;
    }

    public void load(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getDatabaseUtil().getUUID(player.getName()) == null)
                plugin.getDatabaseUtil().createDatabase(player);

            loadKills(player);
            loadDeaths(player);
            loadKDR(player);
            loadKillStreak(player);
        });
    }

    public void shutdownSave() {
        for (Map.Entry<UUID, Integer> entry : playerKills.entrySet())
            plugin.getDatabaseUtil().setKills(entry.getKey(), entry.getValue());

        for (Map.Entry<UUID, Integer> entry : playerDeaths.entrySet())
            plugin.getDatabaseUtil().setDeaths(entry.getKey(), entry.getValue());

        for (Map.Entry<UUID, Double> entry : playerKDR.entrySet())
            plugin.getDatabaseUtil().setKDR(entry.getKey(), entry.getValue());

        for (Map.Entry<UUID, Integer> entry : playerKillStreak.entrySet())
            plugin.getDatabaseUtil().setKillStreak(entry.getKey(), entry.getValue());

        flush();
    }

    public void save(Player player) {
        saveKills(player);
        saveDeaths(player);
        saveKDR(player);
        saveKillStreak(player);
    }

    public void loadKills(Player player) {
        int kills = plugin.getDatabaseUtil().getKills(player.getName());
        playerKills.put(player.getUniqueId(), kills);
    }

    public int getKills(Player player) {
        return playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public void addKills(Player player, int kills) {
        setKills(player, playerKills.get(player.getUniqueId()) + kills);
    }

    public void setKills(Player player, int kills) {
        playerKills.put(player.getUniqueId(), kills);

        String fromPlayer = "none";
        int top = 0;

        for (Map.Entry<UUID, Integer> entry : playerKills.entrySet()) {
            if (top < entry.getValue()) {
                top = entry.getValue();
                fromPlayer = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            }
        }

        if (plugin.getTopHandler().getTopKills() != top) {
            final String from = fromPlayer;
            final int finalTop = top;
            Bukkit.getScheduler().runTaskAsynchronously(plugin,
                    () -> plugin.getTopHandler().setTopKills(from, finalTop));
        }
    }

    public void removeKills(Player player, int kills) {
        setKills(player, playerKills.get(player.getUniqueId()) - kills);
    }

    public void saveKills(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setKills(player.getName(), playerKills.remove(player.getUniqueId())));
    }

    public void loadDeaths(Player player) {
        int deaths = plugin.getDatabaseUtil().getDeaths(player.getName());
        playerDeaths.put(player.getUniqueId(), deaths);
    }

    public int getDeaths(Player player) {
        return playerDeaths.getOrDefault(player.getUniqueId(), 0);
    }

    public void addDeaths(Player player, int deaths) {
        setDeaths(player, playerDeaths.get(player.getUniqueId()) + deaths);
    }

    public void setDeaths(Player player, int deaths) {
        playerDeaths.put(player.getUniqueId(), deaths);

        String fromPlayer = "none";
        int top = 0;

        for (Map.Entry<UUID, Integer> entry : playerDeaths.entrySet()) {
            if (top < entry.getValue()) {
                top = entry.getValue();
                fromPlayer = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            }
        }

        if (plugin.getTopHandler().getTopKills() != top) {
            final String from = fromPlayer;
            final int finalTop = top;
            Bukkit.getScheduler().runTaskAsynchronously(plugin,
                    () -> plugin.getTopHandler().setTopDeaths(from, finalTop));
        }
    }

    public void removeDeaths(Player player, int deaths) {
        setDeaths(player, playerDeaths.get(player.getUniqueId()) - deaths);
    }

    public void saveDeaths(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setDeaths(player.getName(), playerDeaths.remove(player.getUniqueId())));
    }

    public void loadKDR(Player player) {
        double KDR = plugin.getDatabaseUtil().getKDR(player.getName());
        playerKDR.put(player.getUniqueId(), KDR);
    }

    public double getKDR(Player player) {
        return playerKDR.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void addKDR(Player player, double KDR) {
        setKDR(player, getKDR(player) + KDR);
    }

    public void setKDR(Player player, double KDR) {
        playerKDR.put(player.getUniqueId(), KDR);
    }

    public void removeKDR(Player player, double KDR) {
        setKDR(player, getKDR(player) - KDR);
    }

    public void saveKDR(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setKDR(player.getName(), playerKDR.remove(player.getUniqueId())));
    }

    public void updateKDR(Player player) {
        double kills = getKills(player);
        double deaths = getDeaths(player);

        if (kills == 0) {
            if (deaths == 0)
                setKDR(player, 0.0);
            else
                setKDR(player, -deaths);
        } else if (deaths == 0) {
            if (kills == 0)
                setKDR(player, 0.0);
            else
                setKDR(player, kills);
        } else {
            double kdr = kills / deaths;
            setKDR(player, kdr);
        }

        String fromPlayer = "none";
        double top = 0;

        for (Map.Entry<UUID, Double> entry : playerKDR.entrySet()) {
            if (top < entry.getValue()) {
                top = entry.getValue();
                fromPlayer = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            }
        }

        if (plugin.getTopHandler().getTopKills() != top) {
            final String from = fromPlayer;
            final double finalTop = top;
            Bukkit.getScheduler().runTaskAsynchronously(plugin,
                    () -> plugin.getTopHandler().setTopKDR(from, finalTop));
        }
    }

    public void loadKillStreak(Player player) {
        int killStreak = plugin.getDatabaseUtil().getKillStreak(player.getName());
        playerKillStreak.put(player.getUniqueId(), killStreak);
    }

    public int getKillStreak(Player player) {
        return playerKillStreak.getOrDefault(player.getUniqueId(), 0);
    }

    public void addKillStreak(Player player, int killStreak) {
        setKillStreak(player, getKillStreak(player) + killStreak);
    }

    public void setKillStreak(Player player, int killStreak) {
        playerKillStreak.put(player.getUniqueId(), killStreak);

        String fromPlayer = "none";
        int top = 0;

        for (Map.Entry<UUID, Integer> entry : playerKillStreak.entrySet()) {
            if (top < entry.getValue()) {
                top = entry.getValue();
                fromPlayer = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            }
        }

        if (plugin.getTopHandler().getTopKills() != top) {
            final String from = fromPlayer;
            final int finalTop = top;
            Bukkit.getScheduler().runTaskAsynchronously(plugin,
                    () -> plugin.getTopHandler().setTopKS(from, finalTop));
        }
    }

    public void removeKillStreak(Player player, int killStreak) {
        setKillStreak(player, getKillStreak(player) - killStreak);
    }

    public void saveKillStreak(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setKillStreak(player.getName(), playerKillStreak.remove(player.getUniqueId())));
    }

    public void flush() {
        playerKills.clear();
        playerDeaths.clear();
        playerKillStreak.clear();
        playerKDR.clear();
    }
}