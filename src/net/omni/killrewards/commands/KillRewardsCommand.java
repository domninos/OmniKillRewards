package net.omni.killrewards.commands;

import net.omni.killrewards.KillRewardsPlugin;
import net.omni.killrewards.commands.sub.*;
import net.omni.killrewards.commands.util.MainCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KillRewardsCommand extends MainCommand {
    public KillRewardsCommand(KillRewardsPlugin plugin) {
        super(plugin, Arrays.asList(
                new AddSubCommand(plugin), new SetSubCommand(plugin), new RemoveSubCommand(plugin),
                new TypesSubCommand(plugin), new ReloadSubCommand(plugin)
        ));
    }

    @Override
    public boolean execute(Player player, String label, String[] args) {
        if (args.length == 0) {
            sendMessage(player, getHelp(player));
            return true;
        }

        return false;
    }

    @Override
    public String getMainCommand() {
        return "killrewards";
    }

    @Override
    public String getHelp(Player player) {
        return "&l&BKillRewards\n"
                + "&b/killrewards add <type> <player> <amount> » Adds the type to the specified player.\n"
                + "&b/killrewards set <type> <player> <amount> » Sets the type to the specified player.\n"
                + "&b/killrewards remove <type> <player> <amount> » Removes the type to the specified player.\n"
                + "&b/killrewards types » Shows the list of types available.\n"
                + "&b/killrewards reload » Reloads messages.yml.\n";
    }

    @Override
    public String getPermission() {
        return "killrewards.use";
    }
}
