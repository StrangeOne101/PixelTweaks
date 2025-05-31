package com.strangeone101.pixeltweaks.listener;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.ClientScheduler;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.ShinyTracker;
import com.strangeone101.pixeltweaks.TweaksConfig;
import com.strangeone101.pixeltweaks.particle.FakeParticle;
import com.strangeone101.pixeltweaks.particle.StarParticle;
import com.strangeone101.pixeltweaks.pixelevents.EventRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ClientListener {

    private boolean enableSparkle;

    public ClientListener(ModContainer container) {
        container.getEventBus().addListener(this::clientSetup);
        container.getEventBus().addListener(this::onTextureStitch);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onPokemonSpawn);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLeaveWorld);
        NeoForge.EVENT_BUS.addListener(this::onRenderWorldLastEvent);

        new EventRegistry();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        if (TweaksConfig.shinySparkleRange.get() > 0) {
            enableSparkle = true;
        }
    }

    public void onPokemonSpawn(EntityJoinLevelEvent event) {
        if (enableSparkle && event.getEntity() instanceof PixelmonEntity && !event.isCanceled() && event.getLevel().isClientSide) {
            ClientScheduler.schedule(1, () -> { //Wait a tick so the entity is fully loaded, so it isn't a bulbasaur
                PixelmonEntity entity = (PixelmonEntity) event.getEntity();
                if (entity.getPokemon().isShiny() && !entity.isBossPokemon()) {
                    ShinyTracker tracker = ShinyTracker.INSTANCE;
                    if (tracker.shouldTrackShiny(entity)) {
                        tracker.track(entity);
                    }
                }
            });
        }
    }

    public void onClientTick(ClientTickEvent.Post event) {

        if (enableSparkle && !Minecraft.getInstance().isPaused()
                && (Minecraft.getInstance().screen == null || Minecraft.getInstance().screen instanceof ChatScreen)) {
            ShinyTracker.INSTANCE.tick();
            ClientScheduler.tick();
        }
    }

    public void onPlayerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        ShinyTracker.INSTANCE.untrackAll();
    }

    public void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        if (enableSparkle) {
            ShinyTracker.INSTANCE.camera = event.getFrustum();
        }
    }

    public void onTextureStitch(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
            PixelTweaks.LOGGER.debug("Stitching shiny particles");

            TextureAtlasSprite star0 = event.getAtlas().getSprite(ResourceLocation.fromNamespaceAndPath(PixelTweaks.MODID, "stars_0"));
            TextureAtlasSprite star1 = event.getAtlas().getSprite(ResourceLocation.fromNamespaceAndPath(PixelTweaks.MODID, "stars_1"));

            StarParticle.SPRITES = new FakeParticle.FakeParticleTexture(Arrays.asList(star0, star1));
        }
    }


}
