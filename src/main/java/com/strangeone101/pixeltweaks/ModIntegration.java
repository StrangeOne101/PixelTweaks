package com.strangeone101.pixeltweaks;

import net.minecraftforge.fml.ModList;

public class ModIntegration {

    public static boolean usefulBackpacks() {
        return isLoaded("usefulbackpacks") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean simplyBackpacks() {
        return isLoaded("simplybackpacks") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean jei() {
        return isLoaded("jei");
    }

    static boolean isLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
