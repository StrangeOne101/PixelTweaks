package com.strangeone101.pixeltweaks.integration.ftbquests.rewards;

import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PokeDollarReward extends Reward {

    public int count = 100;

    public PokeDollarReward(Quest q) {
        super(q);
    }



    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKEDOLLARS;
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("count", count);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        count = nbt.getInt("count");
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
    }

    @Override
    public void claim(ServerPlayerEntity serverPlayerEntity, boolean b) {
        StorageProxy.getParty(serverPlayerEntity).add(count);

        new DisplayRewardToastMessage(this.id, new TranslationTextComponent("ftbquests.reward.pixelmon.pokedollars.toast", this.count),
                Icon.getIcon("pixelmon:textures/gui/pokedollar.png")).sendTo(serverPlayerEntity);
    }

    @OnlyIn(Dist.CLIENT)
    public IFormattableTextComponent getAltTitle() {
        return (new StringTextComponent(StringUtils.formatDouble(this.count, true) + " ")).appendSibling(this.getType().getDisplayName());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? StringUtils.formatDouble(this.count, true) : "";
    }
}
