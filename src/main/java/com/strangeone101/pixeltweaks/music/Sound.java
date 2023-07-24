package com.strangeone101.pixeltweaks.music;

import com.strangeone101.pixeltweaks.struct.Fade;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class Sound {

    public ResourceLocation sound;

    public Fade fade = new Fade(0, 0);

    public float volume = 1.0F;
    public float pitch = 1.0F;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sound sound1 = (Sound) o;
        return Float.compare(sound1.volume, volume) == 0 && Float.compare(sound1.pitch, pitch) == 0 && Objects.equals(sound, sound1.sound) && Objects.equals(fade, sound1.fade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sound, fade, volume, pitch);
    }
}
