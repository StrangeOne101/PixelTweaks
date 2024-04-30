package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.init.registry.BlockRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;

public class Healer {

    public Healer() {
        if (TweaksConfig.healersDropThemselves.get()) {
            //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onBlockRegistry);
            PixelTweaks.LOGGER.info("Registered healer tweak");
            try {
                Field field = ObfuscationReflectionHelper.findField(BlockBehaviour.class, "f_60440_"); // drops field
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
