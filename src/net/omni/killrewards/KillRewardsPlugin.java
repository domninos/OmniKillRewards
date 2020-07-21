package net.omni.killrewards;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import net.omni.killrewards.commands.KillRewardsCommand;
import net.omni.killrewards.handler.ConfigHandler;
import net.omni.killrewards.handler.DatabaseHandler;
import net.omni.killrewards.handler.MessagesHandler;
import net.omni.killrewards.handler.TopHandler;
import net.omni.killrewards.listener.PlayerKillListener;
import net.omni.killrewards.listener.PlayerListener;
import net.omni.killrewards.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class KillRewardsPlugin extends JavaPlugin {

    public Thread hook;
    private MessagesHandler messagesHandler;
    private MessagesUtil messagesUtil;
    private ConfigHandler configHandler;
    private CooldownUtil cooldownUtil;
    private DatabaseHandler databaseHandler;
    private DatabaseUtil databaseUtil;
    private KillUtil killUtil;
    private CacheUtil cacheUtil;
    private TopHandler topHandler;

    @Override
    public void onEnable() {
        this.messagesHandler = new MessagesHandler(this);
        this.messagesUtil = new MessagesUtil(this);
        this.configHandler = new ConfigHandler(this);

        sendConsole("&aConnecting to MySQL database...");
        String host = configHandler.getConfig().getString("database.host");
        int port = configHandler.getConfig().getInt("database.port");
        String database = configHandler.getConfig().getString("database.database");
        String username = configHandler.getConfig().getString("database.username");
        String password = configHandler.getConfig().getString("database.password");

        this.databaseHandler = new DatabaseHandler(host, port, database, username, password, this);
        databaseHandler.open();
        this.databaseUtil = new DatabaseUtil(this);

        sendConsole("&aSuccessfully connected to database.");

        this.cooldownUtil = new CooldownUtil(this);
        this.killUtil = new KillUtil();
        this.cacheUtil = new CacheUtil(this);

        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI"))
            registerPlaceHolders();
        else {
            sendConsole("&cMVdWPlaceholderAPI not found.");
            return;
        }

        this.topHandler = new TopHandler(this);

        topHandler.updateTop();

        registerListeners();
        registerCommands();
        sendConsole("&aSuccessfully enabled OmniKillRewards v" + this.getDescription().getVersion());

        Runtime.getRuntime().addShutdownHook(hook = new Thread(() -> cacheUtil.shutdownSave()));
        sendConsole("&aAdded shutdown hook.");
    }

    @Override
    public void onDisable() {

        databaseHandler.close();
        sendConsole("&aSuccessfully disconnected from database.");

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        Runtime.getRuntime().removeShutdownHook(hook);
        sendConsole("&aSuccessfully removed shutdown hook.");

        cacheUtil.flush();
        sendConsole("&aSuccessfully disabled OmniKillRewards");
    }

    public void sendConsole(String msg) {
        sendMessage(Bukkit.getConsoleSender(), msg);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate(getMessagesUtil().getPrefix() + " " + message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void registerPlaceHolders() {
        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_kills", event -> {
            if (event.isOnline()) {
                Player player = event.getPlayer();

                if (player != null)
                    return String.valueOf(getCacheUtil().getKills(player));
            } else {
                OfflinePlayer offlinePlayer = event.getOfflinePlayer();

                return String.valueOf(getDatabaseUtil().getKills(offlinePlayer.getName()));
            }

            return "0";
        });

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_deaths", event -> {
            if (event.isOnline()) {
                Player player = event.getPlayer();

                if (player != null)
                    return String.valueOf(getCacheUtil().getDeaths(player));
            } else {
                OfflinePlayer offlinePlayer = event.getOfflinePlayer();

                return String.valueOf(getDatabaseUtil().getDeaths(offlinePlayer.getName()));
            }

            return "0";
        });

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_kdr", event -> {
            if (event.isOnline()) {
                Player player = event.getPlayer();

                if (player != null)
                    return String.valueOf(getCacheUtil().getKDR(player));
            } else {
                OfflinePlayer offlinePlayer = event.getOfflinePlayer();

                return String.valueOf(getDatabaseUtil().getKDR(offlinePlayer.getName()));
            }

            return "0";
        });

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_kstreak", event -> {
            if (event.isOnline()) {
                Player player = event.getPlayer();

                if (player != null)
                    return String.valueOf(getCacheUtil().getKillStreak(player));
            } else {
                OfflinePlayer offlinePlayer = event.getOfflinePlayer();

                return String.valueOf(getDatabaseUtil().getKillStreak(offlinePlayer.getName()));
            }

            return "0";
        });

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestkills_name",
                event -> getTopHandler().getTopKillsName());

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestkills_value",
                event -> String.valueOf(getTopHandler().getTopKills()));

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestdeaths_name",
                event -> getTopHandler().getTopDeathsName());

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestdeaths_value",
                event -> String.valueOf(getTopHandler().getTopDeaths()));

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestkdr_name",
                event -> getTopHandler().getTopKDRName());

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestkdr_value",
                event -> String.valueOf(getTopHandler().getTopKDR()));

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestks_name",
                event -> getTopHandler().getTopKillStreakName());

        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_bestks_value",
                event -> String.valueOf(getTopHandler().getTopKillStreak()));

        //%leaderheads_name_<statistic>_<time>_<rank>%

        // TODO get killslist
        /*
        The components of the placeholders:
<statistic>: the name of your statistic, but without any %, { or } characters. For example, %vault_eco_balance% becomes vault_eco_balance and {stat_walk_cm} becomes stat_walk_cm.
<time>: which timetype of the leaderboard to show. Options: alltime, daily, weekly, monthly, yearly.
<rank>: which rank in the leaderboard to show. Must be a number.

MVdWPlaceholderAPI placeholders
{leaderheads_name_<statistic>_<time>_<rank>} shows the name of the player in the leaderboard. {leaderheads_value_<statistic>_<time>_<rank>} shows the value of the player in the leaderboard

Example: {leaderheads_name_statistic_player_kills_daily_5} shows the 5th player in the daily kills leaderboard based on the %statistic_player_kills% placeholder of the Statistic expansion.
         */
        sendConsole("&aSuccessfully registered placeholders.");
    }

    public void registerListeners() {
        new PlayerListener(this).register();
        new PlayerKillListener(this).register();
    }

    public void registerCommands() {
        new KillRewardsCommand(this).register(); // /killrewards
    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    public MessagesUtil getMessagesUtil() {
        return messagesUtil;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public CooldownUtil getCooldownUtil() {
        return cooldownUtil;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public DatabaseUtil getDatabaseUtil() {
        return databaseUtil;
    }

    public KillUtil getKillUtil() {
        return killUtil;
    }

    public CacheUtil getCacheUtil() {
        return cacheUtil;
    }

    public TopHandler getTopHandler() {
        return topHandler;
    }
}
