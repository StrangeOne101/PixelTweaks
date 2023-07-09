package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "jei");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(PixelmonItems.poke_ball);
        registration.useNbtForSubtypes(PixelmonItems.poke_ball_lid);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen1);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen2);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen3);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen4);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen5);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen6);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen7);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen8);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen9);
        registration.useNbtForSubtypes(PixelmonItems.tr_gen8);

    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
