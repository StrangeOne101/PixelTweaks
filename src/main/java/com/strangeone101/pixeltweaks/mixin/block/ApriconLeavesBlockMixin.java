package com.strangeone101.pixeltweaks.mixin.block;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.blocks.ApricornLeavesBlock;
import com.pixelmonmod.pixelmon.enums.items.ApricornType;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
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

import static com.pixelmonmod.pixelmon.blocks.BerryLeavesBlock.AGE;

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
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if ((Boolean)state.getValue(PERSISTENT)) {
            super.randomTick(state, level, pos, rand);
        } else {
            int i = (Integer)state.getValue(AGE);
            if (i < 2 && ForgeHooks.onCropsGrowPre(level, pos, state, rand.nextInt(this.apricorn.getGrowthTime()) == 0)) {
                state = (BlockState)state.setValue(AGE, i + 1);
                level.setBlock(pos, state, 2);
                ForgeHooks.onCropsGrowPost(level, pos, state);
            }

            super.randomTick(state, level, pos, rand);
        }

        if ((Boolean)state.getValue(PERSISTENT)) {
            super.randomTick(state, level, pos, rand);
        } else if (TweaksConfig.randomlyDropRipeApricorns.get() && rand.nextInt(this.apricorn.getGrowthTime() * 20) == 0) {
            level.setBlock(pos, (BlockState)state.setValue(AGE, 0), 2);
            ItemStack stack = new ItemStack(this.apricorn.apricorn());
            List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
            Collections.shuffle(directions);
            for (Direction dir : directions) {
                BlockPos offset = pos.offset(dir.getNormal());
                if (!level.getBlockState(offset).isSolid()) {
                    ItemEntity item = new ItemEntity(level, (double)pos.getX() + 0.5 + (double)dir.getStepX() * 0.5,
                            (double)pos.getY() + 0.5 + (double)dir.getStepY() * 0.5, (double)pos.getZ() + 0.5 + (double)dir.getStepZ() * 0.5, stack);
                    item.lifespan = 60 * 20;
                    level.addFreshEntity(item);
                    return;
                }
            }
        }

        super.randomTick(state, level, pos, rand);
    }
}
