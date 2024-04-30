package com.strangeone101.pixeltweaks.mixin.client;

import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.music.BattleMusic;
import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import com.pixelmonmod.pixelmon.client.music.VoidMusicTicker;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.init.registry.SoundRegistration;
import com.pixelmonmod.pixelmon.sounds.BattleMusicType;
import com.pixelmonmod.pixelmon.sounds.PixelmonSounds;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.music.ChainedMusic;
import com.strangeone101.pixeltweaks.music.SoundManager;
import com.strangeone101.pixeltweaks.pixelevents.EventRegistry;
import com.strangeone101.pixeltweaks.music.MusicEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(BattleMusic.class)
public abstract class BattleMusicMixin {

    @Shadow(remap = false)
    private static SoundInstance song;

    /**
     * @author StrangeOne101
     * @reason Rewrites the battle music logic, so we can use music for specific battles
     */
    @Overwrite(remap = false)
    public static void startBattleMusic(BattleMusicType type, int index, long playtime, boolean repeat) {
        PixelTweaks.LOGGER.debug("Starting battle music for " + type.name() + " at index " + index + " with playtime " + playtime + " and repeat " + repeat);

        if (playtime == -1) {
            endBattleMusic();
            return;
        }

        List<PixelmonEntity> opponentEntities = new ArrayList<>(1);

        if (!Minecraft.getInstance().isLocalServer()) { //On a server
            //Define list of displayed opponents
            PixelTweaks.LOGGER.debug("Multi player battle music");
            List<PixelmonClientData> opponents = new ArrayList<>(Arrays.asList(ClientProxy.battleManager.displayedEnemyPokemon));
            //Loop through all entities in the world
            for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
                if (entity instanceof PixelmonEntity) {
                    PixelmonEntity pokemon = (PixelmonEntity)entity;

                    //Loop through all opponents and check if the UUID matches
                    for (Iterator<PixelmonClientData> it = opponents.iterator(); it.hasNext(); ) {
                        PixelmonClientData data = it.next();
                        if (data.pokemonUUID.equals(pokemon.getUUID())) {
                            opponentEntities.add(pokemon);
                            it.remove();
                            break;
                        }
                    }

                    if (opponents.isEmpty()) break;
                }
            }
        } else { //Single player. This method is faster
            PixelTweaks.LOGGER.debug("Single player battle music");
            BattleController controller = BattleRegistry.getBattle(Minecraft.getInstance().player);
            if (controller == null) {
                PixelTweaks.LOGGER.error("Battle controller is null! Can not load battle music for events!");
            } else {
                PixelTweaks.LOGGER.debug("Controller is " + controller);
                BattleParticipant player = controller.getPlayer(Minecraft.getInstance().player.getDisplayName().getString());
                controller.getOpponentPokemon(player).stream().map(wrapper -> wrapper.entity).forEach(opponentEntities::add);
            }
        }

        if (!opponentEntities.isEmpty()) {
            // BattleParticipant player = controller.getPlayer(Minecraft.getInstance().player.getDisplayName().getString());
            //List<PixelmonWrapper> opponents = controller.getOpponentPokemon(player);
            Collection<MusicEvent.Battle> events = EventRegistry.getEvents(MusicEvent.Battle.class);
            PixelTweaks.LOGGER.debug("Size is " + events.size());

            Optional<MusicEvent.Battle> optional = events.stream().filter(event -> {
                return event.conditions.stream().allMatch(condition -> {
                    for (PixelmonEntity opponent : opponentEntities) {
                        if (condition.conditionMet(opponent)) {
                            PixelTweaks.LOGGER.debug("Condition " + condition.toString() + " met!");
                            return true;
                        } else {
                            PixelTweaks.LOGGER.debug("Condition " + condition.toString() + " failed!");
                        }
                    }
                    return false;
                });
            }).findFirst();

            if (optional.isPresent() && !isPlaying()) {
                MusicEvent.Battle event = optional.get();
                PixelTweaks.LOGGER.debug("Playing sound event " + event.getFile());
                pixelTweaks$pause();

                SoundManager.playEvent(event);
                return;
            }
        }


        Minecraft mc = Minecraft.getInstance();
        pixelTweaks$pause();
        if (isPlaying()) {
            mc.getSoundManager().stop(song);
            song = null;
        }

        SoundEvent soundEvent = (SoundEvent) PixelmonSounds.battleMusics.get(type);
        SimpleSoundInstance record = new BattleMusic.FixedTrackSound(soundEvent, SoundSource.MUSIC, PixelmonConfigProxy.getBattle().getBattleMusicVolume(), 1.0F, RandomSource.create(), new BlockPos(0, 0, 0), index);
        song = record;
        PixelmonMusic.fadeSoundToStart(record, 2000L);
    }

    /**
     * @author StrangeOne101
     * @reason Rewrites the battle music logic, so we can use music for specific battles
     */
    @Overwrite(remap = false)
    public static void endBattleMusic() {
        Minecraft mc = Minecraft.getInstance();
        if (isPlaying()) {
            if (pixelTweaks$isPixelmonSongPlaying()) {
                PixelmonMusic.fadeSoundToStop(song, 2000L, BattleMusicMixin::pixelTweaks$unpause);
            } else {
                CompletableFuture[] futures = new CompletableFuture[SoundManager.BATTLE_MUSIC.size()];
                int num = 0;

                for (ChainedMusic music : SoundManager.BATTLE_MUSIC) {
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    music.finish(() -> {
                        future.complete(null);
                        SoundManager.ALL_MUSIC.remove(music);
                    });
                    futures[num++] = future;
                }
                SoundManager.BATTLE_MUSIC.clear();
                CompletableFuture.allOf(futures).thenAccept((v) -> {
                    pixelTweaks$unpause();
                });
            }
        } else if (mc.getMusicManager() instanceof VoidMusicTicker) {
            pixelTweaks$unpause();
        }

        song = null;
    }

    @Unique
    private static void pixelTweaks$unpause() {
        VoidMusicTicker.restoreMusicTicker();
        //pixelTweaks$unpauseAmbienceMod();
        SoundManager.resumeAllMusic();
    }

    @Unique
    private static void pixelTweaks$pause() {
        VoidMusicTicker.replaceMusicTicker();
        //pixelTweaks$pauseAmbienceMod();
        SoundManager.pauseAllMusic();
    }

    /**
     * @author StrangeOne101
     * @reason See above
     */
    @Overwrite(remap = false)
    public static boolean isPlaying() {
        return (SoundManager.BATTLE_MUSIC.size() > 0 && SoundManager.BATTLE_MUSIC.stream().anyMatch(ChainedMusic::isPlaying)) || (pixelTweaks$isPixelmonSongPlaying());
    }

    @Unique
    private static boolean pixelTweaks$isPixelmonSongPlaying() {
        return song != null && PixelmonMusic.getSoundHandler().isActive(song);
    }

}
