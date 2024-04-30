package com.strangeone101.pixeltweaks.music;

import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChainedMusic {

    private final Music music;
    private TrackableSound intro, loop, end;
    private boolean finished = false;


    public ChainedMusic(Music music) {
        this.music = music;

        if (music.start != null) {
            this.intro = new TrackableSound(music.start, SoundSource.MUSIC, music.volume, music.pitch, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }
        if (music.loop != null) {
            this.loop = new TrackableSound(music.loop, SoundSource.MUSIC, music.volume, music.pitch, true, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }
        if (music.end != null) {
            this.end = new TrackableSound(music.end, SoundSource.MUSIC, music.volume, music.pitch, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
        }

        //this.introPlayed = intro == null;

        //if (shouldTick())
        tick();
        //else playLoop();

    }

    public void tick() {
        if (this.intro != null && !this.intro.isStarted()) {
            //introPlayed = true;
            if (this.music.fade.start > 0) {
                this.intro.fadeIn(this.music.fade.start);
            } else {
                this.intro.play();
            }
        } else if (this.intro == null || this.intro.isFinished()) {
            if (this.loop != null && !this.loop.isStarted()) {
                this.loop.play();
            }
            //playLoop();
        }
    }

    public boolean shouldTick() {
        return this.intro != null && !finished;
    }

    /*public void playLoop() {
        if (this.intro == null || !Minecraft.getInstance().getSoundHandler().isPlaying(this.intro)) {
            if (this.loop != null) {
                Minecraft.getInstance().getSoundHandler().play(this.loop);
            }
            introFinished = true;
        }
    }*/

    public void finish(Runnable callback) {
        if (finished) return;
        finished = true;
        PixelTweaks.LOGGER.debug("Ending music...");
        if (this.intro != null && this.intro.isPlaying()) {
            long time = this.end == null ? this.music.fade.end : 1000;
            this.intro.fadeOut(time);
            //PixelmonMusic.fadeSoundToStop(this.intro, time);
            PixelmonMusic.EXECUTOR.schedule(callback, time, TimeUnit.MILLISECONDS);
            PixelTweaks.LOGGER.debug("Intro fading out");
        }

        if (this.loop != null && this.loop.isPlaying()) {
            this.loop.fadeOut(this.end == null ? this.music.fade.end : 1000);
            //PixelmonMusic.fadeSoundToStop(this.loop, this.end == null ? this.music.fade.end : 1000);
            PixelTweaks.LOGGER.debug("Loop fading out");
            if (this.end == null) {
                PixelmonMusic.EXECUTOR.schedule(callback, 1000, TimeUnit.MILLISECONDS);
            }
        }
        if (this.end != null && !this.end.isFinished()) {
            this.end.fadeOut(1000);
            //PixelmonMusic.fadeSoundToStart(this.end, 1000);
            PixelTweaks.LOGGER.debug("End fading out");

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
        if (this.intro != null && this.intro.isPlaying()) return true;
        if (this.loop != null && this.loop.isPlaying()) return true;
        if (this.end != null && this.end.isPlaying()) return true;
        //if (!introPlayed) return true;
        //if (introFinished && !finished) return true;
        //if (finished && this.end != null && Minecraft.getInstance().getSoundHandler().isPlaying(this.end)) return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChainedMusic music1 = (ChainedMusic) o;
        return finished == music1.finished && Objects.equals(music, music1.music) && Objects.equals(intro, music1.intro) && Objects.equals(loop, music1.loop) && Objects.equals(end, music1.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(music, intro, loop, end, finished);
    }

    public TrackableSound getPlaying() {
        if (this.intro != null && this.intro.isPlaying()) return this.intro;
        if (this.loop != null && this.loop.isPlaying()) return this.loop;
        if (this.end != null && this.end.isPlaying()) return this.end;
        return null;
    }

}
