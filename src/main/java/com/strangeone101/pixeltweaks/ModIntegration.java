package com.strangeone101.pixeltweaks;

import net.minecraftforge.fml.ModList;

public class ModIntegration {

    public static boolean backpacks() {
        return isLoaded("usefulbackpacks") && TweaksConfig.usefulBackpacksIntegration.get();
    }

    static boolean isLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
