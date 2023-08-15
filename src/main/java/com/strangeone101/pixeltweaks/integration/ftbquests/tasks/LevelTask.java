package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.enums.ExperienceGainType;
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

public class LevelTask extends PokemonTask {

    public enum ExperienceCause {

        ANY(ExperienceGainType.values()),
        BATTLE(ExperienceGainType.BATTLE),
        RARE_CANDY(ExperienceGainType.RARE_CANDY),
        EXP_CANDY(ExperienceGainType.SMALL_EXP_CANDY, ExperienceGainType.MEDIUM_EXP_CANDY, ExperienceGainType.LARGE_EXP_CANDY, ExperienceGainType.EXTRA_LARGE_EXP_CANDY, ExperienceGainType.EXTRA_SMALL_EXP_CANDY),
        ALL_CANDIES(ExperienceGainType.RARE_CANDY, ExperienceGainType.SMALL_EXP_CANDY, ExperienceGainType.MEDIUM_EXP_CANDY, ExperienceGainType.LARGE_EXP_CANDY, ExperienceGainType.EXTRA_LARGE_EXP_CANDY, ExperienceGainType.EXTRA_SMALL_EXP_CANDY),
        SODA(ExperienceGainType.RARE_SODA, ExperienceGainType.ULTRA_RARE_SODA),
        ALL_ITEMS(ExperienceGainType.RARE_CANDY, ExperienceGainType.RARE_SODA, ExperienceGainType.ULTRA_RARE_SODA, ExperienceGainType.SMALL_EXP_CANDY, ExperienceGainType.MEDIUM_EXP_CANDY, ExperienceGainType.LARGE_EXP_CANDY, ExperienceGainType.EXTRA_LARGE_EXP_CANDY, ExperienceGainType.EXTRA_SMALL_EXP_CANDY),
        CURRY(ExperienceGainType.CURRY_CHARIZARD, ExperienceGainType.CURRY_WOBBUFFET, ExperienceGainType.CURRY_KOFFING, ExperienceGainType.CURRY_COPPERAJAH, ExperienceGainType.CURRY_MILCERY);

        private final ExperienceGainType[] internalCauses;

        ExperienceCause(ExperienceGainType... internalCauses) {
            this.internalCauses = internalCauses;
        }

        public boolean matches(ExperienceGainType type) {
            for (ExperienceGainType cause : internalCauses) {
                if (cause == type) {
                    return true;
                }
            }
            return false;
        }
    }

    public ExperienceCause cause = ExperienceCause.ANY;

    public LevelTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.LEVEL_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putByte("cause", (byte) cause.ordinal());
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        cause = ExperienceCause.values()[nbt.getByte("cause")];
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(cause.ordinal());
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        cause = ExperienceCause.values()[buffer.readByte()];
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addEnum("cause", cause, v -> cause = v, NameMap.of(ExperienceCause.ANY, ExperienceCause.values())
                .nameKey(exc -> "pixeltweaks.level_up_cause." + exc.name().toLowerCase()).create());
    }

    public void onLevel(TeamData team, Pokemon pokemon, ExperienceGainType cause) {
        if (!team.isCompleted(this) && team.file.isServerSide() && this.cause.matches(cause)
                && (this.pokemonSpec.isEmpty() || this.cachedSpec.matches(pokemon) != this.invert)) {
            team.addProgress(this, 1L);
        }
    }
}
