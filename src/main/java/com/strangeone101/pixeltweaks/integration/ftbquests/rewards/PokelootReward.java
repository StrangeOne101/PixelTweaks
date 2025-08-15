package com.strangeone101.pixeltweaks.integration.ftbquests.rewards;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.init.registry.SoundRegistration;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class PokelootReward extends Reward {

    public int count = 1;
    public boolean playSound = true;
    public LootTier lootTier = LootTier.ONE;

    public PokelootReward(long id, Quest q) {
        super(id, q);
    }

    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKELOOT;
    }

    @Override
    public void claim(ServerPlayer player, boolean notify) {
        if (playSound) {
            player.playSound(SoundRegistration.POKELOOT_OBTAINED.get(), 0.2F, 1.0F);
        }

        if (notify) {
            NetworkManager.sendToPlayer(player, new DisplayRewardToastMessage(this.id, Component.translatable("ftbquests.reward.pixelmon.pokeloot.toast",
                    this.count, Component.translatable("ftbquests.reward.pixelmon.pokeloot." + lootTier.name().toLowerCase())),
                    Icon.getIcon("pixeltweaks:textures/gui/pokeloot/" + (lootTier.ordinal() + 1) + ".png"), true));
        }

        for (int i = 0; i < count; i++) {
            ItemStackHooks.giveItem(player, RandomHelper.getRandomElementFromCollection(lootTier.getItems()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Icon getAltIcon() {
        return Icon.getIcon("pixeltweaks:textures/gui/pokeloot/" + (lootTier.ordinal() + 1) + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getAltTitle() {
        return (Component.literal(this.count + "x ")).append(
                Component.translatable("ftbquests.reward.pixelmon.pokeloot.title",
                Component.translatable("ftbquests.reward.pixelmon.pokeloot." + lootTier.name().toLowerCase())));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? this.count + "" : "";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addBool("play_sound", playSound, v -> playSound = v, true);
        config.addEnum("loot_tier", lootTier, v -> lootTier = v, NameMap.of(LootTier.ONE, LootTier.values())
                .nameKey(v -> "ftbquests.reward.pixelmon.pokeloot." + v.name().toLowerCase())
                .create());
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("count", count);
        nbt.putBoolean("play_sound", playSound);
        nbt.putByte("loot_tier", (byte) lootTier.ordinal());
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        count = nbt.getInt("count");
        playSound = nbt.getBoolean("play_sound");
        lootTier = LootTier.values()[nbt.getByte("loot_tier")];
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        buffer.writeBoolean(playSound);
        buffer.writeByte(lootTier.ordinal());
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
        playSound = buffer.readBoolean();
        lootTier = LootTier.values()[buffer.readByte()];
    }

    public enum LootTier {

        ONE(DropItemRegistry.tier1),
        TWO(DropItemRegistry.tier2),
        THREE(DropItemRegistry.tier3),
        FOUR(DropItemRegistry.ultraSpace);

        private List<ItemStack> items;

        LootTier(List<ItemStack> items) {
            this.items = items;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }


}
