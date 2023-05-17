package com.strangeone101.pixeltweaks.mixin.client;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.util.helpers.WorldHelper;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;

@Mixin(WorldHelper.class)
public class WorldHelperMixin {

    @Accessor(value = "STRUCTURES", remap = false)
    public static Map<Triple<String, Integer, Integer>, List<StructureStart<?>>> getStructures() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }

    @Accessor(value = "CACHED_STRUCTURES", remap = false)
    public static Map<String, Structure<?>> getCachedStructures() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }
}
