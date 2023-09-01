package com.strangeone101.pixeltweaks.mixin.integration;

import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.UpdateClientPlayerDataPacket;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.strangeone101.pixeltweaks.integration.ftbquests.TaskUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;

@Mixin(UpdateClientPlayerDataPacket.class)
public abstract class FTBQuests_Pokedollar {

    @Shadow(remap = false)
    private BigDecimal playerMoney;

    @Inject(method = "handlePacket", at = @At(value = "FIELD", target = "Lcom/pixelmonmod/pixelmon/storage/ClientData;playerMoney:Ljava/math/BigDecimal;", ordinal = 0), remap = false)
    public void onHandlePacket(NetworkEvent.Context context, CallbackInfo ci) {
        //Check if it is single player
        if (ModIntegration.ftbQuests() && !Minecraft.getInstance().isIntegratedServerRunning()) {
            TaskUtils.updateClientPokedollars(playerMoney);
        }
    }
}
