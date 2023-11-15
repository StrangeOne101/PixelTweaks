package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WipeoutTask extends Task {

    public int count;

    public WipeoutTask(Quest quest) {
        super(quest);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.WIPEOUT;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("count", count);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        buffer.writeVarInt(count);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        count = nbt.getInt("count");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
    }

    public void onWipeout(TeamData team, ServerPlayerEntity player) {
        if (!team.isCompleted(this) && team.file.isServerSide()) {
            team.addProgress(this, 1L);
        }
    }
}
