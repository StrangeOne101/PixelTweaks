package com.strangeone101.pixeltweaks.integration.ftbquests.rewards;

import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.client.FTBQuestsNetClient;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokeDollarReward extends Reward {

    public int count = 100;

    public PokeDollarReward(long id, Quest q) {
        super(id, q);
    }



    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKEDOLLARS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("count", count);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        count = nbt.getInt("count");
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
    }

    @Override
    public void claim(ServerPlayer ServerPlayer, boolean b) {
        BankAccountProxy.getBankAccountNow(ServerPlayer).add(count);
        //StorageProxy.getParty(ServerPlayer).add(count);

        NetworkManager.sendToPlayer(ServerPlayer, new DisplayRewardToastMessage(this.id, Component.translatable("ftbquests.reward.pixelmon.pokedollars.toast", this.count),
                Icon.getIcon("pixelmon:textures/gui/pokedollar.png"), true));
    }

    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        return (Component.literal(StringUtils.formatDouble(this.count, true) + " ")).append(this.getType().getDisplayName());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? StringUtils.formatDouble(this.count, true) : "";
    }
}
