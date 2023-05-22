package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.events.battles.CatchComboEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.storage.playerData.CaptureCombo;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CaptureCombo.class)
public abstract class CaptureComboMixin {

    @Shadow(remap = false)
    private Species lastCapture;
    @Shadow(remap = false)
    private int captureCount;

    private long lastCaptureTime;

    /**
     * @author StrangeOne101
     * @reason Make catch combos not reset if the species is different
     */
    @Overwrite(remap = false)
    public void onCapture(ServerPlayerEntity player, Species species) {
        if (PixelmonConfigProxy.getBattle().isAllowCatchCombo()) {
            if (this.lastCapture == species || (TweaksConfig.catchComboIgnoreSpecies.get()
                    && System.currentTimeMillis() - this.lastCaptureTime < 1000 * 60 * 5)) {
                ++this.captureCount;
            } else {
                this.captureCount = 1;
            }

            this.lastCapture = species;
            this.lastCaptureTime = System.currentTimeMillis();
            Pixelmon.EVENT_BUS.post(new CatchComboEvent.ComboIncrement(player, this.lastCapture, this.captureCount));
        } else {
            this.clearCombo();
        }

    }

    @Shadow(remap = false)
    public void clearCombo() {}
}
