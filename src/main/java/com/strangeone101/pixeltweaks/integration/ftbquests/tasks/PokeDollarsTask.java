package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokeDollarsTask extends Task {

    public int amount = 1000;
    public Tristate consumeMoney = Tristate.DEFAULT;

    public PokeDollarsTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDOLLARS;
    }

    @Override
    public long getMaxProgress() {
        return this.amount;
    }

    @Override
    public boolean consumesResources() {
        return consumeMoney.get(getQuest().getChapter().file.isDefaultTeamConsumeItems());
    }

    @Override
    public boolean canInsertItem() {
        return consumesResources();
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("amount", this.amount);
        consumeMoney.write(nbt, "consumeMoney");
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.amount = nbt.getInt("amount");
        this.consumeMoney = Tristate.read(nbt, "consumeMoney");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.amount);
        consumeMoney.write(buffer);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.amount = buffer.readVarInt();
        consumeMoney = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addInt("amount", this.amount, v -> this.amount = v, 1, 1, Integer.MAX_VALUE);
        config.addTristate("consumeMoney", consumeMoney, v -> consumeMoney = v, Tristate.DEFAULT);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        updateMoney(teamData, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        return Component.translatable("ftbquests.task.pixelmon.pokedollars.title", this.amount);
    }

    public void updateMoney(TeamData teamData, ServerPlayer player) {
        if (teamData.isCompleted(this) || BankAccountProxy.getBankAccount(player).getNow(null) == null) {
            return;
        }

        int m = BankAccountProxy.getBankAccount(player).getNow(null).getBalance().intValue();
        if (m <= 0) return;
        PixelTweaks.LOGGER.debug("Player has " + m + " PokeDollars");
        boolean complete = m >= this.amount;
        m = complete ? this.amount : m;

        if (teamData.getFile().isServerSide()) {
            teamData.setProgress(this, m);

            if (consumesResources() && complete) {
                BankAccountProxy.getBankAccount(player).getNow(null).take(this.amount);
                //StorageProxy.getParty(player).take(this.amount);
            }
        }
    }


}
