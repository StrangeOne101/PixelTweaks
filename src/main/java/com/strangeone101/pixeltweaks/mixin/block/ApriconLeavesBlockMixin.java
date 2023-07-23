package com.strangeone101.pixeltweaks.mixin.block;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.blocks.ApricornLeavesBlock;
import com.pixelmonmod.pixelmon.enums.items.ApricornType;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(ApricornLeavesBlock.class)
public class ApriconLeavesBlockMixin extends LeavesBlock {

    @Final
    @Shadow(remap = false)
    private ApricornType apricorn;

    public ApriconLeavesBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author StrangeOne101
     * @reason Make apricorn trees drop their berries randomly
     */
    @Overwrite
    public void randomTick(BlockState state, ServerWorld level, BlockPos pos, Random rand) {
        if ((Boolean)state.get(PERSISTENT)) {
            super.randomTick(state, level, pos, rand);
        } else {
            int i = (Integer)state.get(BlockStateProperties.AGE_0_2);
            if (i < 2 && ForgeHooks.onCropsGrowPre(level, pos, state, rand.nextInt(this.apricorn.getGrowthTime()) == 0)) {
                state = (BlockState)state.with(BlockStateProperties.AGE_0_2, i + 1);
                level.setBlockState(pos, state, 2);
                ForgeHooks.onCropsGrowPost(level, pos, state);
            } else if (TweaksConfig.randomlyDropRipeApricorns.get() && rand.nextInt(this.apricorn.getGrowthTime() * 20) == 0) {
                level.setBlockState(pos, (BlockState)state.with(ApricornLeavesBlock.AGE, 0));
                ItemStack stack = new ItemStack(PixelmonItems.getApricorn(this.apricorn));
                List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
                Collections.shuffle(directions);
                for (Direction dir : directions) {
                    BlockPos offset = pos.offset(dir);
                    if (!level.getBlockState(offset).isSolid()) {
                        ItemEntity item = new ItemEntity(level, (double)pos.getX() + 0.5 + (double)dir.getXOffset() * 0.5,
                                (double)pos.getY() + 0.5 + (double)dir.getYOffset() * 0.5, (double)pos.getZ() + 0.5 + (double)dir.getZOffset() * 0.5, stack);
                        item.lifespan = 60 * 20;
                        level.addEntity(item);
                        return;
                    }
                }
            }

            super.randomTick(state, level, pos, rand);
        }
    }
}
