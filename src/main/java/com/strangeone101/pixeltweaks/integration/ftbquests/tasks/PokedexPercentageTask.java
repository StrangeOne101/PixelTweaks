package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PokedexPercentageTask extends PokedexTask {

    public double percentage = 50.0;
    public transient int count;

    public PokedexPercentageTask(Quest q) {
        super(q);
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
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putFloat("percentage", (float) this.percentage);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.percentage = nbt.getFloat("percentage");
        doMath();
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeFloat((float) this.percentage);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.percentage = buffer.readFloat();
        doMath();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addDouble("percentage", this.percentage, v -> {
            this.percentage = v;
            doMath();
        }, 0.0, 0.01, 100.00);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        double p = ((double) progress / (double)this.maxPokedexSize) * 100.0;
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
        this.count = (int) (Math.ceil(this.percentage / 100.0) * ((double)this.maxPokedexSize));
    }


}
