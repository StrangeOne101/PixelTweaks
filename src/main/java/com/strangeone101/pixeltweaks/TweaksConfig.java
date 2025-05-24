package com.strangeone101.pixeltweaks;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TweaksConfig {

    public static final TweaksConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    //Server
    public static ModConfigSpec.ConfigValue<Boolean> healersDropThemselves;
    public static ModConfigSpec.ConfigValue<Integer> hypertrainLevel;
    public static ModConfigSpec.ConfigValue<List<String>> hypertrainBlacklist;
    public static ModConfigSpec.ConfigValue<Boolean> randomlyDropRipeApricorns;
    public static ModConfigSpec.ConfigValue<Boolean> foxesLoveBerries;
    public static ModConfigSpec.ConfigValue<Boolean> antiTrampleCrops;
    public static ModConfigSpec.ConfigValue<Boolean> backpacksIntegration;
    public static ModConfigSpec.ConfigValue<Boolean> enablePokemonChat;
    public static ModConfigSpec.ConfigValue<Double> catchMultiplier;
    public static ModConfigSpec.ConfigValue<Double> legendaryCatchMultiplier;
    public static ModConfigSpec.ConfigValue<Boolean> includeMythicals;

    //Client
    public static ModConfigSpec.ConfigValue<Double> shinySparkleRange;
    public static ModConfigSpec.ConfigValue<Double> shinySparkleVolume;
    public static ModConfigSpec.ConfigValue<Integer> autoWrapLoreLength;

    static {
        Pair<TweaksConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(TweaksConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public TweaksConfig(ModConfigSpec.Builder BUILDER) {
        BUILDER.comment("All features that require being on the client").push("Client Features");;

        shinySparkleRange = BUILDER.comment("The range in which shinies will sparkle. Set to -1 to disable. Default: 25.0")
                .defineInRange("shinySparkleRange", 25.0, -1.0F, 500.0F);

        shinySparkleVolume = BUILDER.comment("The volume of the shiny sparkle sound. Set to 0 to disable. Default and maximum: 1.0")
                .defineInRange("shinySparkleVolume", 1.0, 0F, 1.0F);

        autoWrapLoreLength = BUILDER.comment("The length of each line of lore in the item tooltip. Set to 0 to disable. Default: 60")
                .define("autoWrapLoreLength", 60);

        BUILDER.pop().comment("All features that require being on the server").push("Server Features");

        healersDropThemselves = BUILDER.comment("If Pixelmon Healers should drop themselves when broken. Default: true")
                .define("healersDropThemselves", true);

        hypertrainLevel = BUILDER.comment("The level a pokemon can begin to be hypertrained. Default: 50 (PixelTweaks), 100 (Pixelmon)")
                .defineInRange("hypertrainLevel", 50, 1, 100);

        randomlyDropRipeApricorns = BUILDER.comment("If apricorns should randomly drop when they are ripe. Default: true")
                .define("randomlyDropRipeApricorns", true);

        foxesLoveBerries = BUILDER.comment("If fox pokemon should become immune to vanilla berry bushes & heal when right clicked with berries. Default: true")
                .define("foxesLoveBerries", true);

        antiTrampleCrops = BUILDER.comment("If pokemon should be prevented from trampling crops. Default: true")
                .define("antiTrampleCrops", true);

        backpacksIntegration = BUILDER.comment("If Backpack integration should be enabled (supports 4 backpack mods). Default: true")
                .define("backpacksIntegration", true);

        enablePokemonChat = BUILDER.comment("If Pokemon chat replacement should be enabled. Allows players to use [Pokemon] and [Party] to show their pokemon in chat. Default: true")
                .define("enablePokemonChat", true);

        BUILDER.pop();
        /*BUILDER.comment("All catch related features").push("Server Features - Catching");

        catchMultiplier = BUILDER.comment("The multiplier for the catch rate of all pokemon. Default: 1.0")
                .define("catchRateMultiplier", 1.0);

        legendaryCatchMultiplier = BUILDER.comment("The multiplier for the catch rate of all legendary pokemon. Default: 1.1")
                .define("legendaryCatchRateMultiplier", 1.1);

        includeMythicals = BUILDER.comment("If mythicals should be included in the catch rate multiplier. Default: true")
                .define("includeMythicals", true);

        BUILDER.pop();*/
    }
}
