package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class PokeDollarsTask extends Task {

    public int amount;

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
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("amount", this.amount);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.amount = nbt.getInt("amount");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.amount);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.amount = buffer.readVarInt();
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addInt("amount", this.amount, v -> this.amount = v, 1, 1, Integer.MAX_VALUE);
    }
}
