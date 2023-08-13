package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PokemonTask extends Task {

    public int count = 1;
    public String pokemonSpec = "";
    public transient PokemonSpecification cachedSpec;
    public PokemonTask(Quest q) {
        super(q);
    }

    @Override
    public long getMaxProgress() {
        return this.count;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("pokemon", this.pokemonSpec);
        nbt.putInt("count", this.count);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
        this.count = nbt.getInt("count");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.pokemonSpec);
        buffer.writeVarInt(this.count);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.pokemonSpec = buffer.readString();
        this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
        this.count = buffer.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addString("pokemon", this.pokemonSpec, (v) -> {
            this.pokemonSpec = v;
            this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
        }, "");
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
    }
}
