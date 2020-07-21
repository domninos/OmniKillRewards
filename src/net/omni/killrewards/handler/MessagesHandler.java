package net.omni.killrewards.handler;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MessagesHandler {
    private final YamlConfiguration config;
    private final File file;
    private final KillRewardsPlugin plugin;

    public MessagesHandler(KillRewardsPlugin plugin) {
        this.plugin = plugin;

        plugin.getDataFolder().mkdir();

        this.file = new File(plugin.getDataFolder(), "messages.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);

        InputStreamReader reader = new InputStreamReader(this.getConfigContent(new InputStreamReader(
                Objects.requireNonNull(plugin.getResource("messages.yml")), StandardCharsets.UTF_8)));

        YamlConfiguration loadConfiguration = YamlConfiguration.loadConfiguration(reader);

        try {
            if (!this.file.exists()) {
                this.config.addDefaults(loadConfiguration);
                this.config.options().copyDefaults(true);
                this.save();
            } else {
                this.config.addDefaults(loadConfiguration);
                this.config.options().copyDefaults(true);
                this.save();
                this.config.load(this.file);
            }
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

    }

    public void reload() {
        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException var2) {
            var2.printStackTrace();
        }

    }

    public InputStream getConfigContent(Reader reader) {
        try {
            String pluginName = this.plugin.getDescription().getName();
            int commentNum = 0;
            StringBuilder whole = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.startsWith("#")) {
                    String addLine = currentLine.replaceFirst("#",
                            pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine).append("\n");
                    ++commentNum;
                } else {
                    whole.append(currentLine).append("\n");
                }
            }

            String config = whole.toString();
            InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            bufferedReader.close();
            return configStream;
        } catch (IOException var10) {
            var10.printStackTrace();
            return null;
        }
    }

    private String prepareConfigString(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith(this.plugin.getDescription().getName() + "_COMMENT")) {
                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
                String normalComment;

                if (comment.startsWith("# ' "))
                    normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# ");
                else
                    normalComment = comment;

                config.append(normalComment).append("\n");
            } else {
                config.append(line).append("\n");
            }
        }

        return config.toString();
    }

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
            writer.write(this.prepareConfigString(this.config.saveToString()));
            writer.flush();
            writer.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public String getStringOrDefault(String path, String def) {
        return config.getString(path, def);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public void set(String s, Object o) {
        this.config.set(s, o);
        save();
    }
}
