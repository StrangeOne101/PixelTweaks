package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.init.registry.BlockRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.block.AbstractBlock;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class Healer {

    public Healer() {
        if (TweaksConfig.healersDropThemselves.get()) {
            //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onBlockRegistry);
            PixelTweaks.LOGGER.info("Registered healer tweak");
            try {
                Field field = ObfuscationReflectionHelper.findField(AbstractBlock.class, "lootTable");
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(BlockRegistration.HEALER.get(), new ResourceLocation("pixeltweaks", "blocks/healer"));
                field.setAccessible(accessible);
            } catch (Exception e) {
                PixelTweaks.LOGGER.error("Failed to set healer loot table!");
                e.printStackTrace();
            }
        }
    }

    /*public void onBlockRegistry(RegistryEvent.Register<Block> event) {
        // Iterate through all blocks being registered
        for (Block block : event.getRegistry()) {
            // Check if the block is from another mod
            if (block.getRegistryName().toString().equals("pixelmon:healer")) {

            }
        }
    }*/
}
