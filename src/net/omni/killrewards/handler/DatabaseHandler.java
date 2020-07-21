package net.omni.killrewards.handler;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseHandler {

    private static final Logger logger = Bukkit.getLogger();
    private final KillRewardsPlugin plugin;
    private final String connectionString;
    public Connection c = null;

    public DatabaseHandler(String hostname, int port, String database, String username, String password, KillRewardsPlugin plugin) {
        this.connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password="
                + password;
        this.plugin = plugin;
        plugin.sendConsole("&aDatabase: " + database);
        plugin.sendConsole("&aUser: " + username);
        plugin.sendConsole("&aPassword: " + password);
    }

    public Connection open() {
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver);

            this.c = DriverManager.getConnection(connectionString);
            return c;
        } catch (SQLException e) {
            System.out.println("Could not connect to Database! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(driver + " not found!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return this.c;
    }

    public Connection getConn() {
        return this.c;
    }

    public void close() {
        try {
            if (c != null)
                c.close();
        } catch (SQLException ex) {
            System.out.println("ERROR:" + ex.getMessage());
        }
        c = null;
    }

    public boolean isConnected() {
        try {
            return (c != null && !c.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Result query(final String query) {
        if (!isConnected())
            open();
        return query(query, true);
    }

    public Result query(final String query, boolean retry) {
        if (!isConnected())
            open();

        try {
            PreparedStatement statement = null;

            try {
                if (!isConnected())
                    open();
                statement = c.prepareStatement(query);

                if (statement.execute())
                    return new Result(statement, statement.getResultSet());
            } catch (final SQLException e) {
                final String msg = e.getMessage();
                logger.severe("Database query error: " + msg);

                if (retry && msg.contains("_BUSY")) {
                    logger.severe("Retrying query...");
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                            () -> query(query, false), 20);
                }
            }

            if (statement != null)
                statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    protected Statements getStatement(String query) {
        String trimmedQuery = query.trim();

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT"))
            return Statements.SELECT;

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT"))
            return Statements.INSERT;

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE"))
            return Statements.UPDATE;

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE"))
            return Statements.DELETE;

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE"))
            return Statements.CREATE;

        if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER"))
            return Statements.ALTER;
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP"))
            return Statements.DROP;

        if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE"))
            return Statements.TRUNCATE;

        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME"))
            return Statements.RENAME;

        if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO"))
            return Statements.DO;

        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE"))
            return Statements.REPLACE;

        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD"))
            return Statements.LOAD;

        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER"))
            return Statements.HANDLER;

        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL"))
            return Statements.CALL;

        return Statements.SELECT;
    }


    protected enum Statements {
        SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME, START, COMMIT, ROLLBACK, SAVEPOINT, LOCK, UNLOCK, PREPARE, EXECUTE, DEALLOCATE, SET, SHOW, DESCRIBE, EXPLAIN, HELP, USE, ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, RELEASE, VACUUM;
    }

    public class Result {
        private final ResultSet resultSet;
        private final Statement statement;

        public Result(Statement statement, ResultSet resultSet) {
            this.statement = statement;
            this.resultSet = resultSet;
        }

        public ResultSet getResultSet() {
            return this.resultSet;
        }

        public void close() {
            try {
                this.statement.close();
                this.resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
