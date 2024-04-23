package com.strangeone101.pixeltweaks.mixin.arclight;

import com.strangeone101.pixeltweaks.arclight.ArclightUtil;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftServer.class)
public abstract class CraftServerMixin implements Server {

    @Inject(remap = false, method = "enablePlugins", at = @At("TAIL"))
    private void onEnablePlugins(PluginLoadOrder type, CallbackInfo ci) {
        System.out.println("Classloader of Mixin: " + getClass().getClassLoader());
        ArclightUtil.loadPlugin();


    }
}
