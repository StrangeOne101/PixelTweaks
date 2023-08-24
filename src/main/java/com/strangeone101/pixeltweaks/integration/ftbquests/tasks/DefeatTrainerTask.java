package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DefeatTrainerTask extends Task {

    public int count = 1;
    public Tristate gymLeader = Tristate.DEFAULT;
    public DefeatTrainerTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_TRAINER;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("count", count);
        gymLeader.write(nbt, "gymLeader");
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        count = nbt.getInt("count");
        gymLeader = Tristate.read(nbt, "gymLeader");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        gymLeader.write(buffer);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
        gymLeader = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addTristate("gymLeader", gymLeader, v -> gymLeader = v, Tristate.DEFAULT);
    }

    public void defeatTrainer(TeamData team, NPCTrainer trainer) {
        if (!team.isCompleted(this) && team.file.isServerSide() && (gymLeader == Tristate.DEFAULT || gymLeader.isTrue() == trainer.isGymLeader)) {
            team.addProgress(this, 1L);
        }
    }
}
