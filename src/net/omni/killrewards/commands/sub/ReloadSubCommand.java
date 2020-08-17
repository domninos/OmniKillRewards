package net.omni.killrewards.commands.sub;

import net.omni.killrewards.KillRewardsPlugin;
import net.omni.killrewards.commands.util.SubCommand;
import org.bukkit.entity.Player;

public class ReloadSubCommand implements SubCommand {
    private final KillRewardsPlugin plugin;

    public ReloadSubCommand(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Player player, String label, String[] args) {
        if (args.length != 1)
            return false;

        plugin.getMessagesHandler().reload();
        plugin.sendMessage(player, "&aSuccessfully reloaded messags.yml.");
        return true;
    }

    @Override
    public String getCommand() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "&cUsageL /killrewards reload";
    }

    @Override
    public String getPermission() {
        return "killrewards.reload";
    }
}
