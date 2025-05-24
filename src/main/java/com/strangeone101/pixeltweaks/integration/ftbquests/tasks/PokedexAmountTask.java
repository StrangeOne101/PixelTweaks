package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokedexAmountTask extends PokedexTask {

    public int count = 1;

    public PokedexAmountTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDEX_AMOUNT;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("count", this.count);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.count = nbt.getInt("count");
        if (this.filter == PokedexFilter.SINGLE_MON) {
            this.count = 1;
        }
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.count);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.count = buffer.readVarInt();
        if (this.filter == PokedexFilter.SINGLE_MON) {
            this.count = 1;
        }
    }

    @Override
    public void calculateAmount() {
        super.calculateAmount();
        this.count = Math.min(this.count, this.maxPokedexSize);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addInt("count", this.count, v -> this.count = v, 10, 1, this.maxPokedexSize);
    }
}
