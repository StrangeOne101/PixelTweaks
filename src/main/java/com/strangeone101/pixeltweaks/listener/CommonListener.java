package com.strangeone101.pixeltweaks.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ExperienceGainType;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.tweaks.NewGamerules;
import com.strangeone101.pixeltweaks.tweaks.ZygardeCellSpawner;
import com.strangeone101.pixeltweaks.worldgen.ZygardeCellFeature;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class CommonListener {

    public CommonListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPokemonSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);
        //Pixelmon.EVENT_BUS.addListener(this::onPokemonExperienceGain);
        MinecraftForge.EVENT_BUS.addListener(this::onTagReload);

        new ZygardeCellSpawner();
    }

    /**
     * Makes sure pokemon don't spawn when the gamerule is set to false
     * @param event The event
     */
    public void onPokemonSpawn(SpawnEvent event) {
        if (!event.isCanceled() && !Pixelmon.isClient() && NewGamerules.ENABLED) {
            Entity entity = event.action.getOrCreateEntity();
            GameRules rules = event.action.spawnLocation.location.world.getGameRules();
            if (!event.spawner.name.equals("fishing") && entity instanceof PixelmonEntity) {
                if (!rules.getBoolean(NewGamerules.DO_POKEMON_SPAWNING)) {
                    event.setCanceled(true);
                }
            } else if (entity instanceof NPCTrainer) {
                if (!rules.getBoolean(NewGamerules.DO_TRAINER_SPAWNING)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public void onPokemonExperienceGain(ExperienceGainEvent event) {
        if (event.getType() == ExperienceGainType.BATTLE && !Pixelmon.isClient()) {

        }
    }

    public void onBiomeLoad(BiomeLoadingEvent event) {
        ConfiguredFeature<?, ?> configuredFeature = ZygardeCellFeature.CONFIGURED_FEATURE;
        if (configuredFeature == null) {
            PixelTweaks.LOGGER.error("Failed to find the configured feature for Zygarde cells! Cannot spawn!");
            return;
        }
        event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, configuredFeature);
    }

    public void onTagReload(TagsUpdatedEvent event) {
        ZygardeCellSpawner.setTags();
    }
}
