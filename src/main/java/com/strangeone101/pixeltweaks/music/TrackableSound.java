package com.strangeone101.pixeltweaks.music;

import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class TrackableSound extends SimpleSoundInstance implements AutoCloseable, SoundEventListener {
    private boolean called;
    private boolean started;


    public TrackableSound(ResourceLocation sound, SoundSource category, float volume, float pitch, boolean repeat, int repeatDelay, SoundInstance.Attenuation attenuationType, double x, double y, double z, boolean global) {
        super(sound, category, volume, pitch, RandomSource.create(), repeat, repeatDelay, attenuationType, x, y, z, global);

        Minecraft.getInstance().getSoundManager().addListener(this);
    }

    public void fadeIn(long time) {
        this.called = true;
        PixelmonMusic.fadeSoundToStart(this, time);
    }

    public void fadeOut(long time) {
        SoundManager.fadeSoundToStop(this, time, null);
    }

    public void play() {
        Minecraft.getInstance().getSoundManager().play(this);
        this.started = true;
    }

    public void stop() {
        Minecraft.getInstance().getSoundManager().stop(this);
    }

    public boolean isPlaying() {
        return called || Minecraft.getInstance().getSoundManager().isActive(this);
    }

    public boolean isFinished() {
        return started && !isPlaying();
    }

    public boolean isStarted() {
        return called || started;
    }

    @Override
    public void close() throws Exception {
        Minecraft.getInstance().getSoundManager().removeListener(this);
    }

    @Override
    public void onPlaySound(SoundInstance soundIn, WeighedSoundEvents accessor) {
        if (soundIn == this) {
            this.called = false;
            this.started = true;
        }
    }
}
