package com.strangeone101.pixeltweaks.music;

import com.strangeone101.pixeltweaks.mixin.client.BattleMusicMixin;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Unique;
import vazkii.ambience.Ambience;

public class AmbienceHandler {

    public static float oldGain;


    private static void pauseAmbienceMod() {
        if (ModList.get().isLoaded("ambience") && Ambience.thread != null) {
            oldGain = Ambience.thread.getGain();
            Ambience.thread.setGain(0F);
            Ambience.thread.resetPlayer();
        }
    }

    private static void unpauseAmbienceMod() {
        if (ModList.get().isLoaded("ambience")  && Ambience.thread != null) {
            Ambience.thread.setGain(oldGain);
        }
    }
}
