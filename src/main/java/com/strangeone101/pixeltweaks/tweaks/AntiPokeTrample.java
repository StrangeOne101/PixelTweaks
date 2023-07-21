package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class AntiPokeTrample {

    public AntiPokeTrample() {
        MinecraftForge.EVENT_BUS.addListener(this::onTrample);
    }

    public void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof PixelmonEntity && TweaksConfig.antiTrampleCrops.get()) {
            event.setCanceled(true);
        }
    }
}
