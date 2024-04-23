package com.strangeone101.pixeltweaks.mixin.arclight;

import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(SimplePluginManager.class)
public interface SimplePluginManagerMixin extends PluginManager {

    @Accessor(value = "fileAssociations", remap = false)
    public Map<Pattern, PluginLoader>  getFileAssociations();
}
