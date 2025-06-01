package com.strangeone101.pixeltweaks;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.strangeone101.pixeltweaks.listener.ClientListener;
import com.strangeone101.pixeltweaks.listener.CommonListener;
import com.strangeone101.pixeltweaks.tweaks.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PixelTweaks.MODID)
public class PixelTweaks {

    public static final String MODID = "pixeltweaks";

    public static final int SHINY_COLOR = 0xe8aa00;

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger("PixelTweaks");

    public static Set<String> UNKNOWN_MOVES = new HashSet<>();


    public PixelTweaks(ModContainer container) {

        //Make the server tell clients it is fine to join without it
        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        container.registerConfig(ModConfig.Type.COMMON, TweaksConfig.SERVER_SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.registerConfig(ModConfig.Type.CLIENT, TweaksConfig.CLIENT_SPEC);

            new ClientListener(container);
        }

        new CommonListener();

        LOGGER.info("Enabling Tweaks");
        container.getEventBus().addListener(this::initializeTweaks);
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
        LOGGER.debug("Initializing tweaks");
        new Healer();
        new NewGamerules(event);
        new TridentDrops();
        new FoxImmunity();
        LOGGER.debug("Anti Trample");
        new AntiPokeTrample();
        LOGGER.debug("Pokechat");
        new PokeChat();
        LOGGER.debug("Mod integration");
        ModIntegration.registerBackpackIntegrations();
        ModIntegration.registerFTBQuestsIntegration();
        LOGGER.debug("Done");
        new BetterTypeColors();
    }

    public static int getPixelmonVersion() {
        String version = Pixelmon.getVersion();

        String[] split = version.split("\\.", 3);

        int major = Integer.parseInt(split[0]);
        int minor = 0;
        int fix = 0;

        if (split.length > 1) {
            minor = Integer.parseInt(split[1]);

            if (split.length > 2) {
                fix = Integer.parseInt(split[2]);
            }
        }
        return major * 1000 + minor * 100 + fix; //9.1.5 -> 9105
    }
}
