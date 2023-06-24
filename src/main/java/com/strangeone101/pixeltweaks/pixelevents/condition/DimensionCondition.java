package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DimensionCondition extends Condition<ResourceLocation> {
    public List<ResourceLocation> dimensions;
    boolean invert = false;

    @Override
    public boolean conditionMet(ResourceLocation item) {
        return dimensions.contains(item) != invert; //Check if the biome is in the list
    }

    @Override
    public ResourceLocation itemFromPixelmon(PixelmonEntity entity) {
        DimensionType dim = entity.getEntityWorld().getDimensionType();
        return getDimension(dim);
    }

    public static ResourceLocation getDimension(DimensionType biome) {
        return Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.DIMENSION_TYPE_KEY).getKey(biome);
    }

    @Override
    public String toString() {
        return "DimensionCondition{" +
                "dimensions=" + dimensions +
                ", invert=" + invert +
                '}';
    }

    // Add getters and setters for the 'biomes' field
}
