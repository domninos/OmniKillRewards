package net.omni.killrewards;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.gmail.filoghost.holographicdisplays.placeholder.RelativePlaceholder;
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

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
            registerHoloPlaceholders();

        this.topHandler = new TopHandler(this);

        topHandler.updateTop();

        registerListeners();
        registerCommands();
        sendConsole("&aSuccessfully enabled ValpsKillRewards v" + this.getDescription().getVersion());

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
        sendConsole("&aSuccessfully disabled ValpsKillRewards");
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
        PlaceholderAPI.registerPlaceholder(this, "valpsnetwork_kills", (event) -> {
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

        sendConsole("&aSuccessfully registered placeholders.");
    }

    public void registerHoloPlaceholders() {
        RelativePlaceholder.register(new RelativePlaceholder("{valpsnetwork_kills}") {
            @Override
            public String getReplacement(Player player) {
                return player != null ? String.valueOf(getCacheUtil().getKills(player)) : "0";
            }
        });

        RelativePlaceholder.register(new RelativePlaceholder("{valpsnetwork_deaths}") {
            @Override
            public String getReplacement(Player player) {
                return player != null ? String.valueOf(getCacheUtil().getDeaths(player)) : "0";
            }
        });

        RelativePlaceholder.register(new RelativePlaceholder("{valpsnetwork_kdr}") {
            @Override
            public String getReplacement(Player player) {
                return player != null ? String.valueOf(getCacheUtil().getKDR(player)) : "0";
            }
        });

        RelativePlaceholder.register(new RelativePlaceholder("{valpsnetwork_kstreak}") {
            @Override
            public String getReplacement(Player player) {
                return player != null ? String.valueOf(getCacheUtil().getKillStreak(player)) : "0";
            }
        });

        sendConsole("&aSuccessfully registered placeholders to HolographicDisplays.");
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
