package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.listener.ZygardeCellsListener;
import net.minecraft.block.Block;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(ZygardeCellsListener.class)
public interface ZygardeListenerMixin {

    @Accessor(value = "hasCube", remap = false)
    public static Set<UUID> getHasCube() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }

    @Accessor(value = "SPAWNABLE_TAG", remap = false)
    public static Tags.IOptionalNamedTag<Block> getSpawnableBlocks() {
        throw new UnsupportedOperationException("Mixin accessor called");
    }
}
