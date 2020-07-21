package net.omni.killrewards.commands.util;

import org.bukkit.entity.Player;

public interface SubCommand {

    boolean execute(Player player, String label, String[] args);

    String getCommand();

    String getUsage();

    String getPermission();
}