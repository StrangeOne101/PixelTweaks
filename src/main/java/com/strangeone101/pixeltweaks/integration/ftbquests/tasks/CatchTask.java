package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatchTask extends PokemonTask {

    public enum GetType {
        ANY, ANY_EXCEPT_COMMANDS, CATCH, RAID, CATCH_OR_RAID, FOSSIL, CHRISTMAS, COMMAND;
    }
    public GetType getType = GetType.ANY_EXCEPT_COMMANDS;

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
        nbt.putByte("catchType", (byte) getType.ordinal());
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        getType = GetType.values()[nbt.getByte("catchType")];
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(getType.ordinal());
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        getType = GetType.values()[buffer.readByte()];
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addEnum("type", getType, v -> getType = v, NameMap.of(GetType.ANY_EXCEPT_COMMANDS, GetType.values())
                .nameKey(v -> "pixeltweaks.catch_type." + v.name().toLowerCase())
                .create());
    }

    public void catchPokemon(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon))
                //&& (fromRaid == Tristate.DEFAULT || fromRaid.isFalse())) { //If it isn't from raids
             && (getType == GetType.CATCH || getType == GetType.CATCH_OR_RAID || getType == GetType.ANY || getType == GetType.ANY_EXCEPT_COMMANDS)) {
                team.addProgress(this, 1L);
        }
    }

    public void catchRaidPokemon(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (getType == GetType.CATCH_OR_RAID || getType == GetType.RAID || getType == GetType.ANY || getType == GetType.ANY_EXCEPT_COMMANDS)) { //If it IS from raids
            team.addProgress(this, 1L);
        }
    }

    public void onCommand(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (getType == GetType.COMMAND || getType == GetType.ANY)) { //If it is from commands
            team.addProgress(this, 1L);
        }
    }

    public void onChristmas(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (getType == GetType.CHRISTMAS || getType == GetType.ANY || getType == GetType.ANY_EXCEPT_COMMANDS)) { //If it is from commands
            team.addProgress(this, 1L);
        }
    }

    public void onFossil(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (getType == GetType.FOSSIL || getType == GetType.ANY || getType == GetType.ANY_EXCEPT_COMMANDS)) { //If it is from fossils
            team.addProgress(this, 1L);
        }
    }

    public void onEggHatch(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
                && (getType == GetType.ANY || getType == GetType.ANY_EXCEPT_COMMANDS)) { //If it is from eggs
            team.addProgress(this, 1L);
        }
    }
}
