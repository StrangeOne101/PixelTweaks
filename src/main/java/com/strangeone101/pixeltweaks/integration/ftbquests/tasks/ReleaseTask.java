package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ReleaseTask extends PokemonTask {

    public ReleaseTask(long id, Quest q) {
        super(id, q);
    }

    public Tristate hasOT = Tristate.DEFAULT;

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.RELEASE_POKEMON;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        hasOT.write(nbt, "hasOT");
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.hasOT = Tristate.read(nbt, "hasOT");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        hasOT.write(buffer);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.hasOT = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addTristate("hasOT", this.hasOT, v -> this.hasOT = v, Tristate.DEFAULT);
    }

    public void releasePokemon(TeamData team, ServerPlayer player, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (this.hasOT.isDefault() || pokemon.getOriginalTrainerUUID().equals(player.getUUID()) == this.hasOT.isTrue())) {
            team.addProgress(this, 1L);
        }
    }
}
