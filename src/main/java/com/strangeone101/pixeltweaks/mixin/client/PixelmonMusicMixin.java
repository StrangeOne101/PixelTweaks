package com.strangeone101.pixeltweaks.mixin.client;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.client.music.PixelmonMusic;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PixelmonMusic.class)
public class PixelmonMusicMixin {

    @Accessor(value = "mcMusicTicker", remap = false)
    public static MusicTicker getMCMusicTicker() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }

    @Accessor(value = "mcSoundHandler", remap = false)
    private static SoundHandler getMCSoundHandler() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }

    @Accessor(value = "mcSoundManager", remap = false)
    private static SoundEngine getMCSoundManager() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }
    @Accessor(value = "soundSystem", remap = false)
    private static SoundSystem getSoundSystem() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }
}
