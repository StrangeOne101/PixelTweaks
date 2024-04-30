package com.strangeone101.pixeltweaks.mixin.block;

import com.pixelmonmod.pixelmon.api.util.helpers.BlockHelper;
import com.pixelmonmod.pixelmon.blocks.GenericModelBlock;
import com.pixelmonmod.pixelmon.blocks.ZygardeCellBlock;
import com.pixelmonmod.pixelmon.blocks.tileentity.ZygardeCellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ZygardeCellBlock.class)
public abstract class ZygardeCellBlockMixin extends GenericModelBlock {

    @Final
    @Shadow(remap = false)
    public static DirectionProperty ORIENTATION_PROPERTY;

    protected ZygardeCellBlockMixin(Properties builder) {
        super(builder);
    }

    /**
     * @author StrangeOne101
     * @reason Allow Zygarde Cells to be placed on leaves, wood, and other organic blocks
     */
    @Overwrite(remap = false)
    public boolean canBlockStay(World worldIn, BlockState state, BlockPos pos, BlockPos newBlockPos) {
        ZygardeCellTileEntity cell = (ZygardeCellTileEntity) BlockHelper.getTileEntity(ZygardeCellTileEntity.class, worldIn, pos);
        if (cell == null) {
            return false;
        } else {
            TileEntity neighbor = BlockHelper.getTileEntity(TileEntity.class, worldIn, newBlockPos);
            if (!cell.isPermanent() && !(neighbor instanceof ZygardeCellTileEntity)) {
                Direction dir = state.get(ORIENTATION_PROPERTY);
                pos = pos.offset(dir);
                return Block.hasEnoughSolidSide(worldIn, pos, dir.getOpposite());
            } else {
                return true;
            }
        }
    }
}
