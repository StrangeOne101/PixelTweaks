package com.strangeone101.pixeltweaks.arclight.real;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TweaksPlugin extends JavaPlugin {


    public static Plugin PLUGIN;

    @Override
    public void onEnable() {
        PLUGIN = this;

        getLogger().info("PixelTweaks plugin for Arclight enabled!");
    }
}
