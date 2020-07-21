package net.omni.killrewards.commands.sub;

import net.omni.killrewards.KillRewardsPlugin;
import net.omni.killrewards.commands.util.StatType;
import net.omni.killrewards.commands.util.SubCommand;
import org.bukkit.entity.Player;

public class TypesSubCommand implements SubCommand {
    private final KillRewardsPlugin plugin;

    public TypesSubCommand(KillRewardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Player player, String label, String[] args) {
        if (args.length != 1)
            return false;

        plugin.sendMessage(player, "&aTypes:");
        for (StatType types : StatType.values())
            plugin.sendMessage(player, "&b- " + types.getName());
        return true;
    }

    @Override
    public String getCommand() {
        return "types";
    }

    @Override
    public String getUsage() {
        return "&cUsage: /killrewards types";
    }

    @Override
    public String getPermission() {
        return "killrewards.types";
    }
}
