package com.strangeone101.pixeltweaks.mixin.client;

import com.pixelmonmod.pixelmon.comm.data.AbstractPixelmonPacket;
import com.pixelmonmod.pixelmon.comm.data.PacketDirection;
import com.pixelmonmod.pixelmon.comm.packetHandlers.sync.SyncSpeciesPacket;
import com.strangeone101.pixeltweaks.client.overlay.PokemonOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SyncSpeciesPacket.class)
public abstract class SyncSpeciesPacketMixin extends AbstractPixelmonPacket {

    @Shadow(remap = false)
    private boolean last;
    public SyncSpeciesPacketMixin() {
        super(true);
    }

    @Inject(method = "handlePacket", at = @At("TAIL"), remap = false)
    public void handle(CallbackInfo info) {
        if (last) {
            PokemonOverlay.onAllRegistered();
        }
    }
}
