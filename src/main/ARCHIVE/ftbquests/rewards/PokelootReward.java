package com.strangeone101.pixeltweaks.integration.ftbquests.rewards;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.init.registry.SoundRegistration;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import me.shedaniel.architectury.hooks.ItemStackHooks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class PokelootReward extends Reward {

    public int count = 1;
    public boolean playSound = true;
    public LootTier lootTier = LootTier.ONE;

    public PokelootReward(Quest q) {
        super(q);
    }

    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKELOOT;
    }

    @Override
    public void claim(ServerPlayerEntity player, boolean notify) {
        if (playSound) {
            player.playSound(SoundRegistration.POKELOOT_OBTAINED.get(), SoundCategory.BLOCKS, 0.2F, 1.0F);
        }

        if (notify) {
            new DisplayRewardToastMessage(this.id, new TranslationTextComponent("ftbquests.reward.pixelmon.pokeloot.toast",
                    this.count, new TranslationTextComponent("ftbquests.reward.pixelmon.pokeloot." + lootTier.name().toLowerCase())),
                    Icon.getIcon("pixeltweaks:textures/gui/pokeloot/" + (lootTier.ordinal() + 1) + ".png")).sendTo(player);
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
    public IFormattableTextComponent getAltTitle() {
        return (new StringTextComponent(this.count + "x ")).appendSibling(
                new TranslationTextComponent("ftbquests.reward.pixelmon.pokeloot.title",
                new TranslationTextComponent("ftbquests.reward.pixelmon.pokeloot." + lootTier.name().toLowerCase())));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? this.count + "" : "";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addBool("play_sound", playSound, v -> playSound = v, true);
        config.addEnum("loot_tier", lootTier, v -> lootTier = v, NameMap.of(LootTier.ONE, LootTier.values())
                .nameKey(v -> "ftbquests.reward.pixelmon.pokeloot." + v.name().toLowerCase())
                .create());
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("count", count);
        nbt.putBoolean("play_sound", playSound);
        nbt.putByte("loot_tier", (byte) lootTier.ordinal());
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        count = nbt.getInt("count");
        playSound = nbt.getBoolean("play_sound");
        lootTier = LootTier.values()[nbt.getByte("loot_tier")];
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        buffer.writeBoolean(playSound);
        buffer.writeByte(lootTier.ordinal());
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
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
