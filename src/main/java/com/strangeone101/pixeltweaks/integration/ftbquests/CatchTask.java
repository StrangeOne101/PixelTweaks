package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatchTask extends PokemonTask {
    public Tristate fromRaid = Tristate.DEFAULT;

    public CatchTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.CATCH_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        fromRaid.write(nbt, "from_raid");
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        fromRaid = Tristate.read(nbt, "from_raid");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        fromRaid.write(buffer);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        fromRaid = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addTristate("from_raid", fromRaid, v -> fromRaid = v, Tristate.DEFAULT);
    }

    public void catchPokemon(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon))
                && (fromRaid == Tristate.DEFAULT || fromRaid.isFalse())) { //If it isn't from raids
            team.addProgress(this, 1L);
        }
    }

    public void catchRaidPokemon(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon))
                && (fromRaid == Tristate.DEFAULT || fromRaid.isTrue())) { //If it IS from raids
            team.addProgress(this, 1L);
        }
    }
}
