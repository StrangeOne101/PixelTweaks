package com.strangeone101.pixeltweaks.tweaks;

import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.world.level.GameRules;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NewGamerules {

    public static GameRules.Key<GameRules.BooleanValue> DO_POKEMON_SPAWNING;
    public static GameRules.Key<GameRules.BooleanValue> DO_TRAINER_SPAWNING;
    public static boolean ENABLED;

    public NewGamerules(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
        {
            GameRules.Type<GameRules.BooleanValue> boolTrue = GameRules.BooleanValue.create(true);
            DO_POKEMON_SPAWNING = GameRules.register("doPokemonSpawning", GameRules.Category.SPAWNING, boolTrue);
            DO_TRAINER_SPAWNING = GameRules.register("doTrainerSpawning", GameRules.Category.SPAWNING, boolTrue);
            ENABLED = true;
            PixelTweaks.LOGGER.info("Registered custom gamerules!");
        });
    }
}
