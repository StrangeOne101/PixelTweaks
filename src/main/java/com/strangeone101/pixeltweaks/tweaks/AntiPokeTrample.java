package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class AntiPokeTrample {

    public AntiPokeTrample() {
        NeoForge.EVENT_BUS.addListener(this::onTrample);
    }

    public void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof PixelmonEntity && TweaksConfig.antiTrampleCrops.get()) {
            event.setCanceled(true);
        }
    }
}
