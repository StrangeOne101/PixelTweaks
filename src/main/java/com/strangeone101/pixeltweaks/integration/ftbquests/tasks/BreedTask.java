package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.egg.impl.AbstractEggGroup;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BreedTask extends Task {

    public int count = 1;
    public String parent1Specs = "";
    public String parent2Specs = "";

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
        nbt.putString("parent1", this.parent1Specs);
        nbt.putString("parent2", this.parent2Specs);
        nbt.putInt("count", this.count);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.parent1Specs = nbt.getString("parent1");
        this.parent1 = PokemonSpecificationProxy.create(this.parent1Specs);
        this.parent2Specs = nbt.getString("parent2");
        this.parent2 = PokemonSpecificationProxy.create(this.parent2Specs);
        this.count = nbt.getInt("count");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.parent1Specs);
        buffer.writeString(this.parent2Specs);
        buffer.writeVarInt(this.count);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.parent1Specs = buffer.readString();
        this.parent1 = PokemonSpecificationProxy.create(this.parent1Specs);
        this.parent2Specs = buffer.readString();
        this.parent2 = PokemonSpecificationProxy.create(this.parent2Specs);
        this.count = buffer.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addString("parent1", this.parent1Specs, (v) -> {
            this.parent1Specs = v;
            this.parent1 = PokemonSpecificationProxy.create(this.parent1Specs);
        }, "");
        config.addString("parent2", this.parent2Specs, (v) -> {
            this.parent2Specs = v;
            this.parent2 = PokemonSpecificationProxy.create(this.parent2Specs);
        }, "");
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
    }
}
