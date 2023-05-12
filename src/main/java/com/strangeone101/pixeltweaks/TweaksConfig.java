package com.strangeone101.pixeltweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TweaksConfig {

    protected static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    //Server
    public static ForgeConfigSpec.ConfigValue<Boolean> healersDropThemselves;
    public static ForgeConfigSpec.ConfigValue<Integer> hypertrainLevel;

    //Client
    public static ForgeConfigSpec.ConfigValue<Double> shinySparkleRange;
    public static ForgeConfigSpec.ConfigValue<Double> shinySparkleVolume;

    public TweaksConfig() {
        BUILDER.comment("All features that require being on the client").push("Client Features");;

        shinySparkleRange = BUILDER.comment("The range in which shinies will sparkle. Set to -1 to disable. Default: 20.0")
                .define("shinySparkleRange", 20.0);

        shinySparkleVolume = BUILDER.comment("The volume of the shiny sparkle sound. Set to 0 to disable. Default and maximum: 1.0")
                .define("shinySparkleVolume", 1.0);

        BUILDER.pop().comment("All features that require being on the server").push("Server Features");

        healersDropThemselves = BUILDER.comment("If Pixelmon Healers should drop themselves when broken. Default: true")
                .define("healersDropThemselves", true);

        hypertrainLevel = BUILDER.comment("The level a pokemon can begin to be hypertrained. Default: 50 (PixelTweaks), 100 (Pixelmon)")
                .define("hypertrainLevel", 50);

        BUILDER.pop();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BUILDER.build(), PixelTweaks.MODID + ".toml");
    }



}
