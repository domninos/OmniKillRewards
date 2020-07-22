package net.omni.killrewards.util;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUtil {
    private final KillRewardsPlugin plugin;
    private final String table_name;
    private final String statsTopTable;

    public DatabaseUtil(KillRewardsPlugin plugin) {
        this.plugin = plugin;
        this.table_name = "killrewards";
        this.statsTopTable = "killrewards_stats_top";

        loadDatabase();
    }

    // TODO
    private void loadDatabase() {
        plugin.getDatabaseHandler().query("CREATE TABLE IF NOT EXISTS `" + table_name + "` ( " +
                "`uuid` varchar(512) NOT NULL, " +
                "`name` varchar(16) NOT NULL, " +
                "`kills` varchar(8) NOT NULL, " +
                "`deaths` varchar(8) NOT NULL, " +
                "`killstreak` varchar(8) NOT NULL, " +
                "`kdr` varchar(16) NOT NULL, " +
                "UNIQUE KEY `uuid` (`uuid`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        plugin.sendConsole("&aSuccessfully loaded " + table_name + " table.");

        plugin.getDatabaseHandler().query("CREATE TABLE IF NOT EXISTS `" + statsTopTable + "` (" +
                "`kills` varchar(512) NOT NULL, " +
                "`deaths` varchar(16) NOT NULL, " +
                "`kdr` varchar(16) NOT NULL, " +
                "`killstreak` varchar(16) NOT NULL " +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        plugin.sendConsole("&aSuccessfully loaded " + statsTopTable + " table.");
    }

    public void setTop(String name, String stat, int value) {
        if (getTop(stat).equals("none"))
            plugin.getDatabaseHandler().query("INSERT IGNORE INTO `" + statsTopTable
                    + "` (`kills`,`deaths`,`kdr`,`killstreak`) "
                    + "VALUES ('none:0','none:0','none:0','none:0');");

        plugin.getDatabaseHandler().
                query("UPDATE `" + statsTopTable + "` SET `" + stat + "`='" + name + ":" + value + "';");
        plugin.sendConsole("&aSuccessfully set top " + stat + "  to " + name + ":" + value);
    }

    public void setTopKDR(String name, double value) {
        if (getTop("kdr").equals("none"))
            plugin.getDatabaseHandler().query("INSERT IGNORE INTO `" + statsTopTable
                    + "` (`kills`,`deaths`,`kdr`,`killstreak`) "
                    + "VALUES ('none:0','none:0','none:0','none:0');");

        plugin.getDatabaseHandler().
                query("UPDATE `" + statsTopTable + "` SET `kdr`='" + name + ":" + value + "';");
        plugin.sendConsole("&aSuccessfully set top KDR to " + name + ":" + value);
    }

    public String getTop(String stat) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `" + stat + "` FROM `" + statsTopTable + "`;").getResultSet();

            if (rs.next())
                return rs.getString(stat);
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
            return "none";
        }

        return "none";
    }

    public void createDatabase(Player player) {
        plugin.getDatabaseHandler().query("INSERT IGNORE INTO `" + table_name +
                "` (`uuid`,`name`,`kills`,`deaths`,`killstreak`,`kdr`) " +
                "VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "','0','0','0','0');");
        plugin.sendConsole("&aSuccessfully created " + player.getName() + "'s database.");
    }

    public void deleteDatabase(String name) {
        plugin.getDatabaseHandler()
                .query("DELETE FROM `" + table_name + "` WHERE `name`='" + name + "';");
        plugin.sendConsole("&aSuccessfully deleted " + name + "'s database.");
    }

    public UUID getUUID(String name) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `uuid` FROM `" + table_name + "` WHERE `name`='" + name + "';").getResultSet();

            if (rs.next())
                return UUID.fromString(rs.getString("uuid"));
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String getName(String uuid) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `name` FROM `" + table_name + "` WHERE `uuid`='" + uuid + "';").getResultSet();

            if (rs.next())
                return rs.getString("name");
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public int getKills(String name) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `kills` FROM `" + table_name + "` WHERE `name`='" + name + "';").getResultSet();

            if (rs.next())
                return rs.getInt("kills");
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public void setKills(String name, int kills) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `kills`='" + kills + "' WHERE `name`='" + name + "';");
        plugin.sendConsole("&aSuccessfully set kills of " + name + " to " + kills);
    }

    public void setKills(UUID uuid, int kills) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `kills`='" + kills + "' WHERE `uuid`='" + uuid.toString() + "';");
    }

    public int getDeaths(String name) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `deaths` FROM `" + table_name + "` WHERE `name`='" + name + "';").getResultSet();

            if (rs.next())
                return rs.getInt("deaths");
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public void setDeaths(String name, int deaths) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `deaths`='" + deaths + "' WHERE `name`='" + name + "';");
        plugin.sendConsole("&aSuccessfully set deaths of " + name + " to " + deaths);
    }

    public void setDeaths(UUID uuid, int deaths) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `deaths`='" + deaths + "' WHERE `uuid`='" + uuid.toString() + "';");
    }

    public double getKDR(String name) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `kdr` FROM `" + table_name + "` WHERE `name`='" + name + "';").getResultSet();

            if (rs.next())
                return rs.getDouble("kdr");
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public void setKDR(String name, double kdr) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `kdr`='" + kdr + "' WHERE `name`='" + name + "';");
        plugin.sendConsole("&ASuccessfully set " + name + "'s KDR to " + kdr);
    }

    public void setKDR(UUID uuid, double kdr) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `kdr`='" + kdr + "' WHERE `uuid`='" + uuid.toString() + "';");
    }

    public int getKillStreak(String name) {
        try {
            ResultSet rs = plugin.getDatabaseHandler().
                    query("SELECT `killstreak` FROM `" + table_name + "` WHERE `name`='" + name + "';").getResultSet();

            if (rs.next())
                return rs.getInt("killstreak");
        } catch (SQLException e) {
            plugin.sendConsole("&ACouldn't fetch from database. " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public void setKillStreak(String name, int killStreak) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `killstreak`='" + killStreak + "' WHERE `name`='" + name + "';");
        plugin.sendConsole("&aSuccessfully set " + name + "'s killstreak to " + killStreak);
    }

    public void setKillStreak(UUID uuid, int killStreak) {
        plugin.getDatabaseHandler().
                query("UPDATE `" + table_name + "` SET `killstreak`='" + killStreak + "' WHERE `uuid`='" + uuid.toString() + "';");
    }

    public String getTable() {
        return table_name;
    }
}
