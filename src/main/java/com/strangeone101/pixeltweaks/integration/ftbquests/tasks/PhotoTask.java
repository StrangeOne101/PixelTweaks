package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class PhotoTask extends PokemonTask {

    public boolean ignoreBosses = true;

    public PhotoTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.TAKE_PHOTO;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putBoolean("ignoreBosses", this.ignoreBosses);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.ignoreBosses = nbt.getBoolean("ignoreBosses");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeBoolean(this.ignoreBosses);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.ignoreBosses = buffer.readBoolean();
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addBool("ignore_bosses", this.ignoreBosses, v -> this.ignoreBosses = v, true);
    }

    public void takePhoto(TeamData team, PixelmonEntity entity) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(entity.getPokemon()) != this.invert)
                && (!entity.isBossPokemon() || ignoreBosses)) {
            team.addProgress(this, 1);
        }
    }
}
