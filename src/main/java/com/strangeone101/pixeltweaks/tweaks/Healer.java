package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.init.registry.BlockRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.block.AbstractBlock;

import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class Healer {

    public Healer() {
        if (TweaksConfig.healersDropThemselves.get()) {
            //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onBlockRegistry);
            PixelTweaks.LOGGER.info("Registered healer tweak");
            try {
                Field field = ObfuscationReflectionHelper.findField(AbstractBlock.class, "field_220085_g"); // lootTable field
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                for (RegistryObject<Block> healerBlock : BlockRegistration.HEALERS) {
                    field.set(healerBlock.get(), new ResourceLocation("pixeltweaks", "blocks/" + healerBlock.getId().getPath()));
                }
                field.setAccessible(accessible);
            } catch (Exception e) {
                PixelTweaks.LOGGER.error("Failed to set healer loot table!");
                e.printStackTrace();
            }
        }
    }
}
