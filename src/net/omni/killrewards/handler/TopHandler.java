package net.omni.killrewards.handler;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;

public class TopHandler {

    private final KillRewardsPlugin plugin;
    private String topKillsName;
    private int topKills;
    private String topDeathsName;
    private int topDeaths;
    private String topKDRName;
    private double topKDR;
    private String topKillStreakName;
    private int topKillStreak;

    public TopHandler(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateTop() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            updateTopKills();
            updateTopDeaths();
            updateTopKDR();
            updateTopKillStreak();
        });
    }

    public void setTopKills(String name, int top) {
        this.topKillsName = name;
        this.topKills = top;

        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getDatabaseUtil().setTop(name, "kills", topKills));
    }

    public void updateTopKills() {
        String string = plugin.getDatabaseUtil().getTop("kills");
        String[] split = string.split(":");

        this.topKillsName = split[0];
        this.topKills = Integer.parseInt(split[1]);
    }

    public String getTopKillsName() {
        return topKillsName != null ? topKillsName : "";
    }

    public int getTopKills() {
        return topKills;
    }

    public void setTopDeaths(String name, int top) {
        this.topDeathsName = name;
        this.topDeaths = top;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setTop(name, "deaths", topDeaths));
    }

    public void updateTopDeaths() {
        String string = plugin.getDatabaseUtil().getTop("deaths");
        String[] split = string.split(":");

        this.topDeathsName = split[0];
        this.topKills = Integer.parseInt(split[1]);
    }

    public String getTopDeathsName() {
        return topDeathsName != null ? topDeathsName : "";
    }

    public int getTopDeaths() {
        return topDeaths;
    }

    public void setTopKDR(String name, double top) {
        this.topKDRName = name;
        this.topKDR = top;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setTop(name, "kdr", topKDR));
    }

    public void updateTopKDR() {
        String string = plugin.getDatabaseUtil().getTop("kdr");
        String[] split = string.split(":");

        this.topKDRName = split[0];
        this.topKDR = Integer.parseInt(split[1]);
    }

    public String getTopKDRName() {
        return topKDRName != null ? topKDRName : "";
    }

    public double getTopKDR() {
        return topKDR;
    }

    public void setTopKS(String name, int top) {
        this.topKillStreakName = name;
        this.topKillStreak = top;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getDatabaseUtil().setTop(name, "killstreak", topKillStreak));
    }

    public void updateTopKillStreak() {
        String string = plugin.getDatabaseUtil().getTop("killstreak");

        String[] split = string.split(":");

        this.topKillStreakName = split[0];
        this.topKillStreak = Integer.parseInt(split[1]);
    }

    public String getTopKillStreakName() {
        return topKillStreakName != null ? topKillStreakName : "";
    }

    public int getTopKillStreak() {
        return topKillStreak;
    }
}
