package com.strangeone101.pixeltweaks.arclight;

import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.mixin.arclight.SimplePluginManagerMixin;
import com.strangeone101.pixeltweaks.tweaks.PokeChat;
import io.izzel.arclight.common.bridge.bukkit.JavaPluginLoaderBridge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.net.URLClassLoader;

public class ArclightUtil {

    public static void registerListeners(PokeChat pokeChat) {
        //Bukkit.getPluginManager().registerEvents(new PokeChatListener(pokeChat), TweaksPlugin.PLUGIN);
    }

    public static void loadPlugin() {
        CraftServer server = (CraftServer) Bukkit.getServer();

        SimplePluginManagerMixin mixedManager = (SimplePluginManagerMixin) server.getPluginManager();

        for (PluginLoader loader : mixedManager.getFileAssociations().values()) {
            if (loader instanceof JavaPluginLoader) {
                JavaPluginLoader javaLoader = (JavaPluginLoader) loader;

                System.out.println("Classloaded of JavaPluginLoader: " + JavaPluginLoader.class.getClassLoader());
                System.out.println("Classloaded of SimplePluginManager: " + SimplePluginManager.class.getClassLoader());
                System.out.println("Classloader of Server: " + CraftServer.class.getClassLoader());
                Plugin plugin = null;
                try {
                    plugin = javaLoader.loadPlugin(PixelTweaks.MOD_FILE);
                } catch (InvalidPluginException e) {
                    throw new RuntimeException(e);
                }
                plugin.onLoad();
                plugin.onEnable();
            }
        };
    }
}
