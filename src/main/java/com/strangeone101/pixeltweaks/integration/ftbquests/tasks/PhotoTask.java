package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PhotoTask extends PokemonTask {

    public boolean ignoreBosses = true;

    public PhotoTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.TAKE_PHOTO;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putBoolean("ignoreBosses", this.ignoreBosses);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.ignoreBosses = nbt.getBoolean("ignoreBosses");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeBoolean(this.ignoreBosses);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.ignoreBosses = buffer.readBoolean();
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addBool("ignore_bosses", this.ignoreBosses, v -> this.ignoreBosses = v, true);
    }

    public void takePhoto(TeamData team, PixelmonEntity entity) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(entity.getPokemon()) != this.invert)
                && (!entity.isBossPokemon() || ignoreBosses)) {
            team.addProgress(this, 1);
        }
    }
}
