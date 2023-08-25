package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.battles.controller.participants.RaidPixelmonParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DefeatTask extends PokemonTask {

    public Tristate wild = Tristate.DEFAULT;

    public DefeatTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        wild.write(nbt, "wild");
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        wild = Tristate.read(nbt, "wild");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        wild.write(buffer);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        wild = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addTristate("wild", wild, v -> wild = v, Tristate.DEFAULT);
    }

    public void defeatPokemon(TeamData team, PixelmonEntity pokemon) {
        if (!team.isCompleted(this) && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon.getPokemon()) != this.invert)
        && (wild == Tristate.DEFAULT || (pokemon.getPixelmonWrapper().getParticipant() instanceof WildPixelmonParticipant
                        || pokemon.getPixelmonWrapper().getParticipant() instanceof RaidPixelmonParticipant) == wild.get(true))) {
            team.addProgress(this, 1L);
        }
    }
}
