package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BreedTask extends Task {

    public int count = 1;

    public transient PokemonSpecification parent1;
    public transient PokemonSpecification parent2;

    public BreedTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.BREED_POKEMON;
    }

    @Override
    public long getMaxProgress() {
        return this.count;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("parent1", this.parent1 == null ? "" : this.parent1.toString());
        nbt.putString("parent2", this.parent2 == null ? "" : this.parent2.toString());
        nbt.putInt("count", this.count);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        String parent1Specs = nbt.getString("parent1");
        this.parent1 = parent1Specs.isEmpty() ? null : PokemonSpecificationProxy.create(parent1Specs);
        String parent2Specs = nbt.getString("parent2");
        this.parent2 = parent2Specs.isEmpty() ? null : PokemonSpecificationProxy.create(parent2Specs);
        this.count = nbt.getInt("count");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.parent1 == null ? "" : this.parent1.toString());
        buffer.writeString(this.parent2 == null ? "" : this.parent2.toString());
        buffer.writeVarInt(this.count);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        String parent1Specs = buffer.readString();
        this.parent1 = parent1Specs.isEmpty() ? null : PokemonSpecificationProxy.create(parent1Specs);
        String parent2Specs = buffer.readString();
        this.parent2 = parent2Specs.isEmpty() ? null : PokemonSpecificationProxy.create(parent2Specs);
        this.count = buffer.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.add("parent1", new PokemonConfig(false), this.parent1, v -> this.parent1 = v, null);
        config.add("parent2", new PokemonConfig(false), this.parent2, v -> this.parent2 = v, null);
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
    }

    public void onBreed(TeamData data, Pokemon parentOne, Pokemon parentTwo) {
        if (data.isCompleted(this)) return;

        if (this.parent1 != null && this.parent2 != null) { //When both parents are specified
            if (this.parent1.matches(parentOne) && this.parent2.matches(parentTwo)) {
                data.addProgress(this, 1);
            } else if (this.parent1.matches(parentTwo) && this.parent2.matches(parentOne)) {
                data.addProgress(this, 1);
            }
        } else if (this.parent1 != null) {
            if (this.parent1.matches(parentOne) || this.parent1.matches(parentTwo)) {
                data.addProgress(this, 1);
            }
        } else if (this.parent2 != null) {
            if (this.parent2.matches(parentOne) || this.parent2.matches(parentTwo)) {
                data.addProgress(this, 1);
            }
        } else {
            data.addProgress(this, 1); //If no parents are specified, just add progress
        }
    }
}
