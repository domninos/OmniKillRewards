package net.omni.killrewards.commands.util;

import net.omni.killrewards.KillRewardsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public abstract class MainCommand implements CommandExecutor {
    private final KillRewardsPlugin plugin;
    private final List<SubCommand> subCommands;
    private final List<String> subs;

    public MainCommand(KillRewardsPlugin plugin, List<SubCommand> subCommands) {
        this.plugin = plugin;
        this.subCommands = subCommands;
        subs = subCommands.stream().map(SubCommand::getCommand).map(String::toLowerCase).collect(Collectors.toList());
    }

    public abstract boolean execute(Player player, String label, String[] args);

    public abstract String getMainCommand();

    public abstract String getHelp(Player player);

    public abstract String getPermission();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            sendMessage(commandSender, plugin.getMessagesUtil().getPlayerOnly());
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission(getPermission())) {
            sendMessage(player,
                    plugin.getMessagesUtil().getNoPerms().replace("%permissionnode%", getPermission()));
            return true;
        }

        execute(player, s, strings);

        if (strings.length > 0) {
            if (!subs.contains(strings[0].toLowerCase())) {
                plugin.sendMessage(player, getHelp(player));
                return true;
            }

            for (SubCommand sub : getSubCommands()) {
                if (!strings[0].equalsIgnoreCase(sub.getCommand())) continue;

                if (!player.hasPermission(sub.getPermission())) {
                    sendMessage(player,
                            plugin.getMessagesUtil().getNoPerms().replace("%permissionnode%", getPermission()));
                    continue;
                }

                try {
                    if (!sub.execute(player, s, strings))
                        sendMessage(player, sub.getUsage());
                } catch (Exception exc) {
                    sendMessage(player, "Could not execute " + sub.getUsage());
                    exc.printStackTrace();
                }
            }
        }

        return true;
    }

    public void register() {
        plugin.getCommand(getMainCommand()).setExecutor(this);
    }

    public void sendMessage(CommandSender sender, String text) {
        plugin.sendMessage(sender, text);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}