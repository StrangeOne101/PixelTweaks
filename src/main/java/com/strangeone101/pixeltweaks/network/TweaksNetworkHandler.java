package com.strangeone101.pixeltweaks.network;

import mezz.jei.api.constants.ModIds;
import mezz.jei.config.IWorldConfig;
import mezz.jei.config.ServerInfo;
import mezz.jei.network.PacketHandler;
import mezz.jei.network.PacketHandlerClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

public class TweaksNetworkHandler {

    public static final ResourceLocation CHANNEL = new ResourceLocation("pixeltweaks", "channel");

    private final EventNetworkChannel channel;

    private boolean onServer;

    public TweaksNetworkHandler() {
        channel = NetworkRegistry.newEventChannel(CHANNEL, () -> "1", s -> {
            onServer = !NetworkRegistry.ABSENT.equals(s);
            return true;
        }, s -> true);
    }

    public void createServerPacketHandler() {
        PacketHandler packetHandler = new PacketHandler();
        channel.addListener(packetHandler::onPacket);
    }

    @OnlyIn(Dist.CLIENT)
    public void createClientPacketHandler(IWorldConfig worldConfig) {
        PacketHandlerClient packetHandler = new PacketHandlerClient(worldConfig);
        channel.addListener(packetHandler::onPacket);
    }
}
