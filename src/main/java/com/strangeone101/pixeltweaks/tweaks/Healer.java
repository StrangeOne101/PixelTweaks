package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.init.registry.BlockRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.lang.reflect.Field;

public class Healer {

    public Healer() {
        if (TweaksConfig.healersDropThemselves.get()) {
            //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onBlockRegistry);
            PixelTweaks.LOGGER.info("Registered healer tweak");
            try {
                Field field = BlockBehaviour.class.getDeclaredField("drops");
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                for (DeferredHolder<Block, Block> healerBlock : BlockRegistration.HEALERS) {
                    field.set(healerBlock.get(), ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("pixeltweaks", "blocks/" + healerBlock.getId().getPath())));
                }
                field.setAccessible(accessible);
            } catch (Exception e) {
                PixelTweaks.LOGGER.error("Failed to set healer loot table!");
                e.printStackTrace();
            }
        }
    }
}
