package com.strangeone101.pixeltweaks.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

public class CommonListener {

    public CommonListener() {
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPokemonSpawn);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPokemonSpawn);
    }

    /**
     * Makes sure pokemon don't spawn when the gamerule is set to false
     * @param event The event
     */
    public void onPokemonSpawn(SpawnEvent event) {
        if (!event.isCanceled() && !Pixelmon.isClient()) {
            if (!event.action.spawnLocation.location.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                event.setCanceled(true);
            }
        }
    }

    public void onPokemonExperienceGain(ExperienceGainEvent event) {

    }
}
