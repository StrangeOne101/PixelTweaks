package com.strangeone101.pixeltweaks.music;

import com.strangeone101.pixeltweaks.struct.Fade;
import net.minecraft.util.ResourceLocation;
import java.util.Objects;

public class Music {

    public ResourceLocation start;
    public ResourceLocation loop;
    public ResourceLocation end;
    public Fade fade = new Fade();

    public float volume = 1.0F;
    public float pitch = 1.0F;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return Float.compare(music.volume, volume) == 0 && Float.compare(music.pitch, pitch) == 0 && Objects.equals(start, music.start) && Objects.equals(loop, music.loop) && Objects.equals(end, music.end) && Objects.equals(fade, music.fade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, loop, end, fade, volume, pitch);
    }
}
