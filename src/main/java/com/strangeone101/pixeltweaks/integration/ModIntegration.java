package com.strangeone101.pixeltweaks.integration;

import com.strangeone101.pixeltweaks.TweaksConfig;
import com.strangeone101.pixeltweaks.integration.backpack.TravelersBackpackIntegration;
import com.strangeone101.pixeltweaks.integration.backpack.UsefulBackpacksIntegration;

import net.minecraftforge.fml.ModList;

public class ModIntegration {

    public static boolean usefulBackpacks() {
        return isLoaded("usefulbackpacks") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean simplyBackpacks() {
        return isLoaded("simplybackpacks") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean sophisticatedBackpacks() {
        return isLoaded("sophisticatedbackpacks") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean curiosApi() {
        return isLoaded("curios");
    }

    public static boolean travelersBackpack() {
        return isLoaded("travelersbackpack") && TweaksConfig.backpacksIntegration.get();
    }

    public static boolean jei() {
        return isLoaded("jei");
    }

    public static boolean ftbQuests() {
        return isLoaded("ftbquests");
    }

    static boolean isLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static void registerBackpackIntegrations() {
        //new SophisticatedBackpacksIntegration();
        //new SimplyBackpacksIntegration();
        new UsefulBackpacksIntegration();
        new TravelersBackpackIntegration();
    }

    public static void registerFTBQuestsIntegration() {
        if (!ftbQuests()) return;

        //PokemonTaskTypes.register();
        //PokemonRewardTypes.register();
        //new TaskListener();
    }
}
