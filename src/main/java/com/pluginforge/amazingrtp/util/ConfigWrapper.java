package com.pluginforge.amazingrtp.util;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigWrapper {

    private final int radius;
    private final int cooldown;

    public ConfigWrapper(FileConfiguration config) {
        this.radius = config.getInt("rtp-radius", 5000);
        this.cooldown = config.getInt("cooldown-seconds", 30);
    }

    public int getRadius() {
        return radius;
    }

    public int getCooldown() {
        return cooldown;
    }
}
