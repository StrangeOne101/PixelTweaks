package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

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
        DimensionType dim = entity.level().dimensionType();
        return getDimension(dim);
    }

    public static ResourceLocation getDimension(DimensionType dim) {
        return Minecraft.getInstance().level.registryAccess().registry(Registries.DIMENSION_TYPE).get().getKey(dim);
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
