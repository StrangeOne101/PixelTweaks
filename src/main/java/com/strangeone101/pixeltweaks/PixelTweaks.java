package com.strangeone101.pixeltweaks;

import com.strangeone101.pixeltweaks.listener.ClientListener;
import com.strangeone101.pixeltweaks.listener.CommonListener;
import com.strangeone101.pixeltweaks.tweaks.FoxImmunity;
import com.strangeone101.pixeltweaks.tweaks.Healer;
import com.strangeone101.pixeltweaks.tweaks.NewGamerules;
import com.strangeone101.pixeltweaks.tweaks.TridentDrops;
import com.strangeone101.pixeltweaks.worldgen.ZygardeCellFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.util.ConstraintParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PixelTweaks.MODID)
public class PixelTweaks {

    public static final String MODID = "pixeltweaks";

    public static final int SHINY_COLOR = 0xe8aa00;

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger("PixelTweaks");

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<Feature<?>> FEATURE_OBJECT = PixelTweaks.FEATURES.register("zygarde_cell", ZygardeCellFeature::new);;

    public PixelTweaks() {

        //Make the server tell clients it is fine to join without it
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        new TweaksConfig();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ClientListener();
        }

        new CommonListener();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initializeTweaks);

        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static InputStream getResource(String filename) throws IOException {
        URL url = PixelTweaks.class.getClassLoader().getResource("assets/" + MODID + "/" + filename);

        if (url == null) {
            return null;
        }

        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

    public void initializeTweaks(FMLCommonSetupEvent event) {
        LOGGER.info("Initializing tweaks");
        new Healer();
        new NewGamerules();
        new TridentDrops();
        new FoxImmunity();

        Lazy<ZygardeCellFeature> lazyFeature = Lazy.of(() -> ZygardeCellFeature.FEATURE);
        event.enqueueWork(() -> {
            ZygardeCellFeature feature = lazyFeature.get();
            ZygardeCellFeature.CONFIGURED_FEATURE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                    new ResourceLocation(MODID, "zygarde_cell"), feature.withConfiguration(new NoFeatureConfig()));
        });
    }
}
