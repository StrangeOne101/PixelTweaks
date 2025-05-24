package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokedexPercentageTask extends PokedexTask {

    public double percentage = 50.0;
    public transient int count;

    public PokedexPercentageTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDEX_PERCENTAGE;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putFloat("percentage", (float) this.percentage);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.percentage = nbt.getFloat("percentage");
        doMath();
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeFloat((float) this.percentage);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.percentage = buffer.readFloat();
        doMath();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addDouble("percentage", this.percentage, v -> {
            this.percentage = v;
            calculateAmount();
        }, 0.0, 0.01, 100.00);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        long l = Math.min(progress, this.maxPokedexSize);
        double p = ((double) l / (double)this.maxPokedexSize) * 100.0;
        return StringUtils.formatDouble(p) + "%";
    }

    @Override
    public String formatMaxProgress() {
        return StringUtils.formatDouble(percentage) + "%";
    }

    @Override
    public void calculateAmount() {
        super.calculateAmount();
        doMath();
    }

    private void doMath() {
        this.count = (int) Math.ceil((this.percentage / 100.0) * ((double)this.maxPokedexSize));
    }


}
