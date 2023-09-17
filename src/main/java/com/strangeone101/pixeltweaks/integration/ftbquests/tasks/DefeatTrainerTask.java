package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.boss.BossTier;
import com.pixelmonmod.pixelmon.api.pokemon.boss.BossTierRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DefeatTrainerTask extends Task {

    public int count = 1;
    public Tristate gymLeader = Tristate.DEFAULT;
    public String bossTier = "";
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
        nbt.putString("bossTier", bossTier);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        count = nbt.getInt("count");
        gymLeader = Tristate.read(nbt, "gymLeader");
        bossTier = nbt.getString("bossTier");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        gymLeader.write(buffer);
        buffer.writeString(bossTier);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
        gymLeader = Tristate.read(buffer);
        bossTier = buffer.readString();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addTristate("gymLeader", gymLeader, v -> gymLeader = v, Tristate.DEFAULT);

        List<String> bossTiers = BossTierRegistry.getBossTiers().stream().map(BossTier::getID).collect(Collectors.toList());
        bossTiers.add(0, ""); //Add "any" at the top

        config.addEnum("bossTier", bossTier, v -> bossTier = v,
                NameMap.of("", bossTiers)
                .nameKey(v -> {
                    if (v.isEmpty()) return "pixeltweaks.boss_tier.any";
                    return BossTierRegistry.getBossTier(v).get().getName();
                })
                .color(v -> {
                    if (v.isEmpty()) return Color4I.WHITE;
                    Color c = BossTierRegistry.getBossTier(v).get().getColor();
                    return Color4I.rgb(c.getRed(), c.getGreen(), c.getBlue());
                }).create(), "");
    }

    public void defeatTrainer(TeamData team, NPCTrainer trainer) {
        if (!team.isCompleted(this) && team.file.isServerSide()
                && (gymLeader == Tristate.DEFAULT || gymLeader.isTrue() == trainer.isGymLeader)
                && (bossTier.isEmpty() || trainer.getBossTier().getID().equalsIgnoreCase(bossTier))) {
            team.addProgress(this, 1L);
        }
    }
}
