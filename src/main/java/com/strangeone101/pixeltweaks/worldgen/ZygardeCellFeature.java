package com.strangeone101.pixeltweaks.worldgen;

import com.mojang.serialization.Codec;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.tweaks.ZygardeCellSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class ZygardeCellFeature extends Feature<NoFeatureConfig> {

    public static ZygardeCellFeature FEATURE;
    public static ConfiguredFeature CONFIGURED_FEATURE;

    private static final long UNIQUE_SEED = -2257395837L;

    public ZygardeCellFeature() {
        super(NoFeatureConfig.CODEC);

        FEATURE = this;
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {

        int x = pos.getX() / 16;
        int z = pos.getZ() / 16;

        x += getXOffset(reader.getSeed() + UNIQUE_SEED);
        z += getZOffset(reader.getSeed() + UNIQUE_SEED);

        if ((x + z) % 6 == 0 && z % 3 == 0) {
            IChunk chunk = reader.getChunk(pos.getX() >> 4,pos.getZ() >> 4);
            PixelTweaks.LOGGER.debug("Trying to spawn Zygarde Cell in chunk via feature " + chunk.getPos().toString());
            return ZygardeCellSpawner.trySpawnInChunk(chunk, false);
        }

        return false;
    }

    public static int getXOffset(long seed) {
        return (int)(seed & 513L) % 10;
    }

    public static int getZOffset(long seed) {
        return (int)(seed & 719L) % 10;
    }
}
