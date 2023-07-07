package com.strangeone101.pixeltweaks.mixin.client;

import com.pixelmonmod.pixelmon.comm.data.AbstractPixelmonPacket;
import com.pixelmonmod.pixelmon.comm.data.PacketDirection;
import com.pixelmonmod.pixelmon.comm.packetHandlers.sync.DataSyncControlPacket;
import com.strangeone101.pixeltweaks.ClientScheduler;
import com.strangeone101.pixeltweaks.client.overlay.OverlayLayer;
import com.strangeone101.pixeltweaks.client.overlay.PokemonOverlay;
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(DataSyncControlPacket.class)
public abstract class DataSyncControlPacketMixin extends AbstractPixelmonPacket {

    @Shadow(remap = false)
    private String context;
    @Shadow(remap = false)
    private boolean lock;
    public DataSyncControlPacketMixin(PacketDirection direction) {
        super(direction);
    }

    @Inject(method = "handlePacket", at = @At("TAIL"), remap = false)
    public void handle(CallbackInfo info) {
        if (!lock && context.equals("pixelmon")) {
            PokemonOverlay.onAllRegistered();
        }
    }
}
