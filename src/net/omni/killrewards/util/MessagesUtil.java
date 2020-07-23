package net.omni.killrewards.util;

import net.omni.killrewards.KillRewardsPlugin;

public class MessagesUtil {
    private final KillRewardsPlugin plugin;
    private String prefix;
    private String playerOnly;
    private String playerNotFound;
    private String typeNotFound;
    private String notNumber;
    private String noPerms;
    private String killed;
    private String broadcastKilled;
    private String cooldownMsg;
    private String cooldownReset;

    private String addedKills;
    private String addedDeaths;
    private String addedKDR;
    private String addedKillStreak;
    private String removedKills;
    private String removedDeaths;
    private String removedKDR;
    private String removedKillStreak;
    private String setKills;
    private String setDeaths;
    private String setKDR;
    private String setKillStreak;

    public MessagesUtil(KillRewardsPlugin plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        this.prefix = getString("prefix");
        this.playerOnly = getString("playerOnly");
        this.playerNotFound = getString("playerNotFound");
        this.typeNotFound = getString("typeNotFound");
        this.notNumber = getString("notNumber");
        this.noPerms = getString("noPerms");
        this.killed = getString("killed");
        this.broadcastKilled = getString("broadcastKilled");
        this.cooldownMsg = getString("cooldownMsg");
        this.cooldownReset = getString("cooldownReset");

        this.addedKills = getString("added.kills");
        this.addedDeaths = getString("added.deaths");
        this.addedKDR = getString("added.kdr");
        this.addedKillStreak = getString("added.killstreak");

        this.removedKills = getString("removed.kills");
        this.removedDeaths = getString("removed.deaths");
        this.removedKDR = getString("removed.kdr");
        this.removedKillStreak = getString("removed.killstreak");

        this.setKills = getString("set.kills");
        this.setDeaths = getString("set.deaths");
        this.setKDR = getString("set.kdr");
        this.setKillStreak = getString("set.killstreak");
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPlayerOnly() {
        return playerOnly;
    }

    public String getPlayerNotFound() {
        return playerNotFound;
    }

    public String getTypeNotFound() {
        return typeNotFound;
    }

    public String getNotNumber() {
        return notNumber;
    }

    public String getNoPerms() {
        return noPerms;
    }

    public String getKilled() {
        return killed;
    }

    public String getBroadcastKilled() {
        return broadcastKilled;
    }

    public String getCooldownMsg() {
        return cooldownMsg;
    }

    public String getCooldownReset() {
        return cooldownReset;
    }

    public String getAddedKills() {
        return addedKills;
    }

    public String getAddedDeaths() {
        return addedDeaths;
    }

    public String getAddedKDR() {
        return addedKDR;
    }

    public String getAddedKillStreak() {
        return addedKillStreak;
    }

    public String getRemovedKills() {
        return removedKills;
    }

    public String getRemovedDeaths() {
        return removedDeaths;
    }

    public String getRemovedKDR() {
        return removedKDR;
    }

    public String getRemovedKillStreak() {
        return removedKillStreak;
    }

    public String getSetKills() {
        return setKills;
    }

    public String getSetDeaths() {
        return setDeaths;
    }

    public String getSetKDR() {
        return setKDR;
    }

    public String getSetKillStreak() {
        return setKillStreak;
    }

    private String getString(String path) {
        return plugin.translate(plugin.getMessagesHandler().getString(path));
    }
}
