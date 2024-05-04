package com.strangeone101.pixeltweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class TweaksConfig {

    protected static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    //Server
    public static ForgeConfigSpec.ConfigValue<Boolean> healersDropThemselves;
    public static ForgeConfigSpec.ConfigValue<Integer> hypertrainLevel;
    public static ForgeConfigSpec.ConfigValue<List<String>> hypertrainBlacklist;
    public static ForgeConfigSpec.ConfigValue<Boolean> randomlyDropRipeApricorns;
    public static ForgeConfigSpec.ConfigValue<Boolean> foxesLoveBerries;
    public static ForgeConfigSpec.ConfigValue<Boolean> antiTrampleCrops;
    public static ForgeConfigSpec.ConfigValue<Boolean> backpacksIntegration;
    public static ForgeConfigSpec.ConfigValue<Boolean> enablePokemonChat;
    public static ForgeConfigSpec.ConfigValue<Double> catchMultiplier;
    public static ForgeConfigSpec.ConfigValue<Double> legendaryCatchMultiplier;
    public static ForgeConfigSpec.ConfigValue<Boolean> includeMythicals;

    //Client
    public static ForgeConfigSpec.ConfigValue<Double> shinySparkleRange;
    public static ForgeConfigSpec.ConfigValue<Double> shinySparkleVolume;
    public static ForgeConfigSpec.ConfigValue<Integer> autoWrapLoreLength;

    public TweaksConfig() {
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BUILDER.build(), PixelTweaks.MODID + ".toml");
    }



}
