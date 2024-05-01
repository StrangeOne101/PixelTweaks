package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.api.tags.TagsHelper;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

import java.util.Collections;
import java.util.HashSet;
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
            Set<ResourceLocation> cache = new HashSet<>();



            /*this.cachedBiomes = BetterSpawnerConfig.INSTANCE.cachedBiomeCategories.entrySet().stream()
                    .filter(entry -> biomes.contains(entry.getKey())) //Filter categories the list contains
                    .flatMap(entry -> entry.getValue().stream()) //Combine the sets together
                    .map(BiomeCondition::getBiome) //Convert to resource locations
                    .collect(Collectors.toSet());*/

            for (String biome : biomes) {
                if (biome.startsWith("#")) {

                   // TODO FIGURE THIS OUT
                } else if (biome.contains(":")) {
                    cachedBiomes.add(new ResourceLocation(biome.toLowerCase()));
                } else {
                    cachedBiomes.add(new ResourceLocation("minecraft", biome.toLowerCase()));
                }
            }
        }

        return cachedBiomes.contains(item) != invert; //Check if the biome is in the list
    }

    @Override
    public ResourceLocation itemFromPixelmon(PixelmonEntity entity) {
        Biome biome = Minecraft.getInstance().level.getBiome(entity.getOnPos()).get();
        return getBiome(biome);
    }

    public static ResourceLocation getBiome(Biome biome) {
        if (biome == null) return null;
        ResourceLocation l = Minecraft.getInstance().level.registryAccess().registry(Registries.BIOME).get().getKey(biome);
        if (l == null) return null;
        return l;
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
