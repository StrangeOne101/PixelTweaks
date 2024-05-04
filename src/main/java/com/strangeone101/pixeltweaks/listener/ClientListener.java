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
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ClientListener {

    private boolean enableSparkle;

    public ClientListener() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onPokemonSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLeaveWorld);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLastEvent);

        new EventRegistry();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        if (TweaksConfig.shinySparkleRange.get() > 0) {
            enableSparkle = true;
        }
    }

    public void onPokemonSpawn(EntityJoinLevelEvent event) {
        if (enableSparkle && event.getEntity() instanceof PixelmonEntity && !event.isCanceled() && event.getResult() != Event.Result.DENY && event.getLevel().isClientSide) {
            //PixelTweaks.LOGGER.info("Pixelmon spawned on client: " + event.getWorld().isRemote);
            ClientScheduler.schedule(1, () -> { //Wait a tick so the entity is fully loaded, so it isn't a bulbasaur
                PixelmonEntity entity = (PixelmonEntity) event.getEntity();
                if (entity.getPokemon().isShiny() && !entity.isBossPokemon()) {
                    //PixelTweaks.LOGGER.info("Pixelmon spawned on client2: " + event.getWorld().isRemote);
                    ShinyTracker tracker = ShinyTracker.INSTANCE;
                    if (tracker.shouldTrackShiny(entity)) {
                        //PixelTweaks.LOGGER.info("Pixelmon spawned on client3: " + event.getWorld().isRemote);
                        tracker.track(entity);
                    }
                }
            });
        }
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (enableSparkle && event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isPaused()
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

    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
            PixelTweaks.LOGGER.debug("Stitching particles");

            TextureAtlasSprite star0 = event.getAtlas().getSprite(new ResourceLocation(PixelTweaks.MODID, "stars_0"));
            TextureAtlasSprite star1 = event.getAtlas().getSprite(new ResourceLocation(PixelTweaks.MODID, "stars_1"));

            StarParticle.SPRITES = new FakeParticle.FakeParticleTexture(Arrays.asList(star0, star1));
        } else {
            PixelTweaks.LOGGER.debug("Stitching " + event.getAtlas().location().toString());
        }
    }


}
