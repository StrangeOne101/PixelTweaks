package com.strangeone101.pixeltweaks.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ExperienceGainType;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixellang.LangRegistry;
import com.strangeone101.pixeltweaks.tweaks.NewGamerules;
import com.strangeone101.pixeltweaks.worldgen.ZygardeCellFeature;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashMap;
import java.util.Map;

public class CommonListener {

    public CommonListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPokemonSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);
        //Pixelmon.EVENT_BUS.addListener(this::onPokemonExperienceGain);
        MinecraftForge.EVENT_BUS.addListener(this::onTagReload);

        new ZygardeCellSpawner();
        new LangRegistry();

        TYPE_COLORS.put(Element.BUG, 0x9DC130);
        TYPE_COLORS.put(Element.DARK, 0x5F606D);
        TYPE_COLORS.put(Element.DRAGON, 0x0773C7);
        TYPE_COLORS.put(Element.ELECTRIC, 0xF9CF30);
        TYPE_COLORS.put(Element.FAIRY, 0xEF97E6);
        TYPE_COLORS.put(Element.FIGHTING, 0xD94256);
        TYPE_COLORS.put(Element.FIRE, 0xF8A54F);
        TYPE_COLORS.put(Element.FLYING, 0x9BB4E8);
        TYPE_COLORS.put(Element.GHOST, 0x6970C5);
        TYPE_COLORS.put(Element.GRASS, 0x7AC74C);
        TYPE_COLORS.put(Element.GROUND, 0xD78555);
        TYPE_COLORS.put(Element.ICE, 0x7ED4C9);
        TYPE_COLORS.put(Element.NORMAL, 0xC6C6A7);
        TYPE_COLORS.put(Element.POISON, 0xB563CE);
        TYPE_COLORS.put(Element.PSYCHIC, 0xF87C7A);
        TYPE_COLORS.put(Element.ROCK, 0xC9BB8A);
        TYPE_COLORS.put(Element.STEEL, 0x5695A3);
        TYPE_COLORS.put(Element.WATER, 0x539DDF);

        for (Element e : Element.getAllTypes()) {
            if (!TYPE_COLORS.containsKey(e)) {
                TYPE_COLORS.put(e, 0xFFFFFF);
            }
        }
    }

    public Map<Element, Integer> TYPE_COLORS = new HashMap<>();

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
        //Disable Zygarde cell feature because it is causing timeouts for some reason
        /*ConfiguredFeature<?, ?> configuredFeature = ZygardeCellFeature.CONFIGURED_FEATURE;
        if (configuredFeature == null) {
            PixelTweaks.LOGGER.error("Failed to find the configured feature for Zygarde cells! Cannot spawn!");
            return;
        }
        event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, configuredFeature);*/
    }

    public void onTagReload(TagsUpdatedEvent event) {
        ZygardeCellSpawner.setTags();
    }
}
