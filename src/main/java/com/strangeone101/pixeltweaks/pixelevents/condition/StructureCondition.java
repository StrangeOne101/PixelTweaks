package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.mixin.client.WorldHelperMixin;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class StructureCondition extends Condition<PixelmonEntity> {
    public List<ResourceLocation> structures;
    public transient Set<Structure> internalStructures;
    boolean invert = false;

    @Override
    public boolean conditionMet(PixelmonEntity item) {
        /*if (internalStructures == null) {
            internalStructures = new HashSet<>();

            structures.stream().map(Registries.STRUCTURE::)
                    .filter(Optional::isPresent).map(Optional::get)
                    .forEach(internalStructures::add);
        }

        for (Structure structure : internalStructures) {
            if (insideStructure(item.level(), structure.getStructureName(), item.getPosition())) {
                return !invert;
            } else if (item.getOwner() != null && insideStructure(item.world, structure.getStructureName(), item.getOwner().getPosition())) {
                return !invert;
            }
        }*/

        return invert;
    }

    @Override
    public PixelmonEntity itemFromPixelmon(PixelmonEntity entity) {
        return entity;
    }

    @Override
    public String toString() {
        return "StructureCondition{" +
                "structures=" + structures +
                '}';
    }

    /*public static boolean insideStructure(Level world, String structure, BlockPos pos) {
        Structure value = WorldHelperMixin.getCachedStructures().computeIfAbsent(structure.toLowerCase(Locale.ROOT), (s) -> {
            return Structure.NAME_STRUCTURE_BIMAP.get(structure);
        });
        if (value == null) {
            return false;
        } else {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            List<StructureStart<?>> structureStarts = WorldHelperMixin.getStructures().get(Triple.of(world.getDimensionKey().getLocation().toString(), chunkX, chunkZ));
            if (structureStarts != null && !structureStarts.isEmpty()) {
                Iterator<StructureStart<?>> var7 = structureStarts.iterator();

                StructureStart<?> structureStart;
                do {
                    if (!var7.hasNext()) {
                        return false;
                    }

                    structureStart = var7.next();
                } while(!Objects.equals(structureStart.getStructure(), value) || !structureStart.getBoundingBox().isVecInside(pos));

                return true;
            } else {
                return false;
            }
        }
    }*/
}
