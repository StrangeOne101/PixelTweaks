package com.strangeone101.pixeltweaks.music;

import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;

import java.util.concurrent.TimeUnit;

public class ChainedMusic {

    private final Music music;
    private SimpleSound intro, loop, end;
    private boolean introPlayed = false;
    private boolean introFinished = false;
    private boolean finished = false;


    public ChainedMusic(Music music) {
        this.music = music;

        if (music.start != null) {
            this.intro = new SimpleSound(music.start, SoundCategory.MUSIC, music.volume, music.pitch, false, 0, SimpleSound.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
        }
        if (music.loop != null) {
            this.loop = new SimpleSound(music.loop, SoundCategory.MUSIC, music.volume, music.pitch, true, 0, SimpleSound.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
        }
        if (music.end != null) {
            this.end = new SimpleSound(music.end, SoundCategory.MUSIC, music.volume, music.pitch, false, 0, SimpleSound.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
        }

        this.introPlayed = intro == null;

        if (shouldTick()) tick();
        else playLoop();

    }

    public void tick() {
        if (!introPlayed && this.intro != null) {
            introPlayed = true;
            if (this.music.fade.start > 0) {
                PixelmonMusic.fadeSoundToStart(this.intro, this.music.fade.start);
            } else {
                Minecraft.getInstance().getSoundHandler().play(this.intro);
            }
        } else if (introPlayed && !introFinished) {
            playLoop();
        }
    }

    public boolean shouldTick() {
        return this.intro != null && !finished;
    }

    public void playLoop() {
        if (this.intro == null || !Minecraft.getInstance().getSoundHandler().isPlaying(this.intro)) {
            if (this.loop != null) {
                Minecraft.getInstance().getSoundHandler().play(this.loop);
            }
            introFinished = true;
        }
    }

    public void finish(Runnable callback) {
        if (finished) return;
        finished = true;
        if (this.loop != null) {
            PixelmonMusic.fadeSoundToStop(this.loop, this.end == null ? this.music.fade.end : 1000);
            if (this.end == null) {
                PixelmonMusic.EXECUTOR.schedule(callback, 1000, TimeUnit.MILLISECONDS);
            }
        }
        if (this.end != null) {
            PixelmonMusic.fadeSoundToStart(this.end, 1000);

            PixelmonMusic.EXECUTOR.submit(() -> {
                try {
                    Thread.sleep(20);

                    while (true) {
                        if (!isPlaying()) {
                            callback.run();
                            return;
                        }
                        Thread.sleep(20);
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public boolean isPlaying() {
        if (!introPlayed) return true;
        if (introFinished && !finished) return true;
        if (finished && Minecraft.getInstance().getSoundHandler().isPlaying(this.end)) return true;
        return false;
    }
}
