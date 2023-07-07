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
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
