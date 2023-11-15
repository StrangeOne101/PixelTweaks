package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;

public class HatchTask extends PokemonTask {

    public HatchTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.HATCH_EGG;
    }

    public void onHatch(TeamData team, Pokemon pokemon) {
        if (!team.isCompleted(this) && team.file.isServerSide() && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)) {
            team.addProgress(this, 1L);
        }
    }
}
