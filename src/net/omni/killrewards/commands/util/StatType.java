package net.omni.killrewards.commands.util;

import java.util.Arrays;

public enum StatType {
    KILLS("kills"), DEATHS("deaths"), KDR("kdr"), KILLSTREAK("killstreak");

    final String name;

    StatType(String name) {
        this.name = name;
    }

    public static StatType getStatType(String name) {
        return Arrays.stream(values()).filter(type -> type.getName().equals(name)).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }
}
