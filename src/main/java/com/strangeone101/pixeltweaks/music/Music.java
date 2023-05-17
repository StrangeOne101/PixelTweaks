package com.strangeone101.pixeltweaks.music;

import net.minecraft.util.ResourceLocation;

public class Music {

    public ResourceLocation start;
    public ResourceLocation loop;
    public ResourceLocation end;
    public Fade fade = new Fade();

    public float volume = 1.0F;
    public float pitch = 1.0F;

    public static class Fade {
        public long start = 1000;
        public long end = 1000;
    }

    public static class MusicInstance {

        public MusicEvent event;

        public MusicInstance(MusicEvent event) {
            this.event = event;

        }
    }

}
