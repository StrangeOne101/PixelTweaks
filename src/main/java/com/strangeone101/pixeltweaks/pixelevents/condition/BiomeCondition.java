package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeCondition extends Condition<ResourceLocation> {
    public BiomeCondition() {

    }

    public transient Set<ResourceLocation> cachedBiomes;
    public List<String> biomes;
    boolean invert = false;

    @Override
    public boolean conditionMet(ResourceLocation item) {
        if (cachedBiomes == null) {
            this.cachedBiomes = BetterSpawnerConfig.INSTANCE.cachedBiomeCategories.entrySet().stream()
                    .filter(entry -> biomes.contains(entry.getKey())) //Filter categories the list contains
                    .flatMap(entry -> entry.getValue().stream()) //Combine the sets together
                    .map(BiomeCondition::getBiome) //Convert to resource locations
                    .collect(Collectors.toSet());

            for (String biome : biomes) {
                if (biome.contains(":")) {
                    cachedBiomes.add(new ResourceLocation(biome.toLowerCase()));
                }
            }
        }

        return cachedBiomes.contains(item) != invert; //Check if the biome is in the list
    }

    @Override
    public ResourceLocation itemFromPixelmon(PixelmonEntity entity) {
        Biome biome = entity.getEntityWorld().getBiome(entity.getPosition());
        return getBiome(biome);
    }

    public static ResourceLocation getBiome(Biome biome) {
        return Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
    }

    @Override
    public String toString() {
        return "BiomeCondition{" +
                "biomes=" + biomes +
                ", invert=" + invert +
                '}';
    }

    // Add getters and setters for the 'biomes' field
}
