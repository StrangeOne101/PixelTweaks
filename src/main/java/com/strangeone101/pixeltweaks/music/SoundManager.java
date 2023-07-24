package com.strangeone101.pixeltweaks.music;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import net.minecraft.client.audio.ChannelManager;
import net.minecraft.client.audio.ISound;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class SoundManager {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setPriority(5).setDaemon(true).setNameFormat("pixeltweaks_sound_%d").build());

    public static void fadeSoundToStart(ISound sound, long millis) {
        try {
            if (PixelmonMusic.getSoundManager().isPlaying(sound)) return;

            EXECUTOR.submit(() -> {
                PixelmonMusic.resetFade(sound, false);
                PixelmonMusic.getSoundHandler().play(sound);
                AtomicReference<ChannelManager.Entry> channel = new AtomicReference<ChannelManager.Entry>();
                PixelmonMusic.getSoundManager().playingSoundsChannel.forEach((s, c) -> {
                    if (Objects.equals(sound, s)) {
                        channel.set(c);
                    }

                });
                float initialVolume;
                if (channel.get() != null) {
                    initialVolume = PixelmonMusic.getSoundManager().getClampedVolume(sound);
                } else {
                    initialVolume = 1.0F;
                }

                try {
                    for(int i = 0; (long)i < millis; ++i) {
                        float volume = PixelmonMusic.fadeSound(sound, initialVolume, millis, false);
                        if (channel.get() != null) {
                            (channel.get()).runOnSoundExecutor((source) -> {
                                source.setGain(volume);
                            });
                        }

                        Thread.sleep(1L);
                    }
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }

                PixelmonMusic.resetFade(sound, false);
                if (channel.get() != null) {
                    (channel.get()).runOnSoundExecutor((source) -> {
                        source.setGain(initialVolume);
                    });
                }

            });
        } catch (Exception var4) {
            PixelmonMusic.getSoundManager().play(sound);
        }

    }

    public static void fadeSoundToStop(ISound sound, long millis, Runnable runnable) {
        try {
            if (!PixelmonMusic.getSoundManager().isPlaying(sound)) return;
            EXECUTOR.submit(() -> {
                PixelmonMusic.resetFade(sound, true);
                AtomicReference<ChannelManager.Entry> channel = new AtomicReference((Object)null);
                PixelmonMusic.getSoundManager().playingSoundsChannel.forEach((s, c) -> {
                    if (Objects.equals(sound, s)) {
                        channel.set(c);
                    }

                });
                float initialVolume;
                if (channel.get() != null) {
                    initialVolume = PixelmonMusic.getSoundManager().getClampedVolume(sound);
                } else {
                    initialVolume = 1.0F;
                }

                try {
                    for(int i = 0; (long)i < millis; ++i) {
                        float volume = PixelmonMusic.fadeSound(sound, initialVolume, millis, true);
                        if (channel.get() != null) {
                            ((ChannelManager.Entry)channel.get()).runOnSoundExecutor((source) -> {
                                source.setGain(volume);
                            });
                        }

                        Thread.sleep(1L);
                    }
                } catch (InterruptedException var8) {
                    var8.printStackTrace();
                }

                PixelmonMusic.getSoundManager().stop(sound);
                PixelmonMusic.resetFade(sound, true);
                if (channel.get() != null) {
                    ((ChannelManager.Entry)channel.get()).runOnSoundExecutor((source) -> {
                        source.setGain(initialVolume);
                    });
                }

                if (runnable != null) {
                    runnable.run();
                }

            });
        } catch (Exception var5) {
            PixelmonMusic.getSoundManager().stop(sound);
            if (runnable != null) {
                runnable.run();
            }
        }

    }

}
