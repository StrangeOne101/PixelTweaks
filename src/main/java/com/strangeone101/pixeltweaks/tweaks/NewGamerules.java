package com.strangeone101.pixeltweaks.tweaks;

import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewGamerules {

    public static GameRules.Key<GameRules.BooleanValue> DO_POKEMON_SPAWNING;
    public static GameRules.Key<GameRules.BooleanValue> DO_TRAINER_SPAWNING;
    public static boolean ENABLED;

    public NewGamerules() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
        {
            try
            {
                Method createBoolean = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "m_46250_", boolean.class); //the create(boolean) method
                createBoolean.setAccessible(true);

                Object boolTrue = createBoolean.invoke(GameRules.BooleanValue.class, true);
                DO_POKEMON_SPAWNING = GameRules.register("doPokemonSpawning", GameRules.Category.SPAWNING, (GameRules.Type<GameRules.BooleanValue>) boolTrue);
                DO_TRAINER_SPAWNING = GameRules.register("doTrainerSpawning", GameRules.Category.SPAWNING, (GameRules.Type<GameRules.BooleanValue>) boolTrue);
                ENABLED = true;
                PixelTweaks.LOGGER.info("Registered custom gamerules!");
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                PixelTweaks.LOGGER.error("Failed to register custom gamerules!");
                e.printStackTrace();
            }
        });
    }
}
