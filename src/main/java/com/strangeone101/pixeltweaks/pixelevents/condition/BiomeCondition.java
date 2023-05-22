package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class BiomeCondition extends Condition<Biome> {
    public List<ResourceLocation> biomes;
    boolean invert = false;

    @Override
    public boolean conditionMet(Biome item) {
        return biomes.contains(item.getRegistryName()) != invert;
    }

    @Override
    public Biome itemFromPixelmon(PixelmonEntity entity) {
        return entity.getEntityWorld().getBiome(entity.getPosition());
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
