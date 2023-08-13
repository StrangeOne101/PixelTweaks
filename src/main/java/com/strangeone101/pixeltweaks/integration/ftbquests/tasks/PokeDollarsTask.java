package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class PokeDollarsTask extends Task {

    public int amount = 1000;
    public Tristate consumeMoney = Tristate.DEFAULT;

    public PokeDollarsTask(Quest q) {
        super(q);
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
        return consumeMoney.get(quest.chapter.file.defaultTeamConsumeItems);
    }

    @Override
    public boolean canInsertItem() {
        return consumesResources();
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("amount", this.amount);
        consumeMoney.write(nbt, "consumeMoney");
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.amount = nbt.getInt("amount");
        this.consumeMoney = Tristate.read(nbt, "consumeMoney");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.amount);
        consumeMoney.write(buffer);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.amount = buffer.readVarInt();
        consumeMoney = Tristate.read(buffer);
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addInt("amount", this.amount, v -> this.amount = v, 1, 1, Integer.MAX_VALUE);
        config.addTristate("consumeMoney", consumeMoney, v -> consumeMoney = v, Tristate.DEFAULT);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayerEntity player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        updateMoney(teamData, player);
    }

    public void updateMoney(TeamData teamData, ServerPlayerEntity player) {
        if (teamData.isCompleted(this)) {
            return;
        }

        int m = StorageProxy.getParty(player).getBalance().intValue();
        boolean complete = m >= this.amount;
        m = complete ? this.amount : m;

        if (teamData.file.isServerSide()) {
            teamData.addProgress(this, m);

            if (consumesResources()) {
                StorageProxy.getParty(player).setBalance(m - this.amount);
            }
        }
    }


}
