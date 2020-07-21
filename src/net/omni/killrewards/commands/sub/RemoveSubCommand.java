package net.omni.killrewards.commands.sub;

import net.omni.killrewards.KillRewardsPlugin;
import net.omni.killrewards.commands.util.StatType;
import net.omni.killrewards.commands.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RemoveSubCommand implements SubCommand {
    private final KillRewardsPlugin plugin;

    public RemoveSubCommand(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Player player, String label, String[] args) {
        if (args.length == 3)
            return false;

        StatType type = StatType.getStatType(args[1]);

        if (type == null) {
            plugin.sendMessage(player, plugin.getMessagesUtil().getTypeNotFound().replace("%type%", args[1]));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);

        if (target == null) {
            plugin.sendMessage(player, plugin.getMessagesUtil().getPlayerNotFound().replace("%player%", args[2]));
            return true;
        }

        double amount;

        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            plugin.sendMessage(player, plugin.getMessagesUtil().getNotNumber().replace("%arg%", args[3]));
            return true;
        }

        if (amount == -1) {
            plugin.sendMessage(player, "&cSomething went wrong parsing number.");
            return true;
        }

        switch (type) {
            default:
                break;

            case KILLS: {
                int before = plugin.getCacheUtil().getKills(target);
                plugin.getCacheUtil().removeKills(target, (int) amount);
                int newKills = plugin.getCacheUtil().getKills(target);

                if (newKills < 0) {
                    newKills = 0;
                    plugin.getCacheUtil().setKills(target, 0);
                }

                String parsed = plugin.getMessagesUtil().getRemovedKills()
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName())
                        .replace("%before%", String.valueOf(before))
                        .replace("%new%", String.valueOf(newKills));

                plugin.sendMessage(player, parsed);
                return true;
            }

            case DEATHS: {
                int before = plugin.getCacheUtil().getDeaths(target);
                plugin.getCacheUtil().removeDeaths(target, (int) amount);
                int newDeaths = plugin.getCacheUtil().getDeaths(target);

                if (newDeaths < 0) {
                    newDeaths = 0;
                    plugin.getCacheUtil().setDeaths(target, 0);
                }

                String parsed = plugin.getMessagesUtil().getRemovedDeaths()
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName())
                        .replace("%before%", String.valueOf(before))
                        .replace("%new%", String.valueOf(newDeaths));

                plugin.sendMessage(player, parsed);
                return true;
            }

            case KDR: {
                double before = plugin.getCacheUtil().getKDR(target);
                plugin.getCacheUtil().removeKDR(target, amount);
                double newKDR = plugin.getCacheUtil().getKDR(target);

                if (newKDR < 0.0) {
                    newKDR = 0.0;
                    plugin.getCacheUtil().setKDR(target, 0.0);
                }

                String parsed = plugin.getMessagesUtil().getRemovedKDR()
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName())
                        .replace("%before%", String.valueOf(before))
                        .replace("%new%", String.valueOf(newKDR));

                plugin.sendMessage(player, parsed);
                return true;
            }

            case KILLSTREAK: {
                int before = plugin.getCacheUtil().getKillStreak(target);
                plugin.getCacheUtil().removeKillStreak(target, (int) amount);
                int newKillStreak = plugin.getCacheUtil().getKillStreak(target);

                if (newKillStreak < 0) {
                    newKillStreak = 0;
                    plugin.getCacheUtil().setKillStreak(target, 0);
                }

                String parsed = plugin.getMessagesUtil().getRemovedKillStreak()
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName())
                        .replace("%before%", String.valueOf(before))
                        .replace("%new%", String.valueOf(newKillStreak));

                plugin.sendMessage(player, parsed);
                return true;
            }
        }
        return true;
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public String getUsage() {
        return "&cUsage: /killrewards remove <type> <player> <amount>";
    }

    @Override
    public String getPermission() {
        return "killrewards.remove";
    }
}
