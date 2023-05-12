package com.strangeone101.pixeltweaks.tweaks;

import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewGamerules {

    public static GameRules.RuleKey<GameRules.BooleanValue> DO_POKEMON_SPAWNING;
    public static GameRules.RuleKey<GameRules.BooleanValue> DO_TRAINER_SPAWNING;
    public static boolean ENABLED;

    public NewGamerules() {
        try{
            Method createBoolean = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "create", boolean.class);
            createBoolean.setAccessible(true);
            DeferredWorkQueue.runLater( () ->
            {
                try
                {
                    Object boolTrue = createBoolean.invoke(GameRules.BooleanValue.class, true);
                    DO_POKEMON_SPAWNING = GameRules.register("doPokemonSpawning", GameRules.Category.SPAWNING, (GameRules.RuleType<GameRules.BooleanValue>) boolTrue);
                    DO_TRAINER_SPAWNING = GameRules.register("doTrainerSpawning", GameRules.Category.SPAWNING, (GameRules.RuleType<GameRules.BooleanValue>) boolTrue);
                    ENABLED = true;
                    PixelTweaks.LOGGER.info("Registered custom gamerules!");
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    PixelTweaks.LOGGER.error("Failed to register custom gamerules!");
                    e.printStackTrace();
                }
            });
        }
        catch (IllegalArgumentException e) {
            PixelTweaks.LOGGER.error("Failed to register custom gamerules!");
            e.printStackTrace();
            throw e;
        }
    }
}
