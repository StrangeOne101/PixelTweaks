package com.strangeone101.pixeltweaks.client.overlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface PixelmonEntityLayerExtension {

    /**
     * Sets the overlay for this pixelmon entity
     * @param overlay The overlay to set
     */
    void setPTOverlay(PokemonOverlay overlay);

    /**
     * Gets the overlay for this pixelmon entity
     * @return The overlay
     */
    PokemonOverlay getPTOverlay();
}
