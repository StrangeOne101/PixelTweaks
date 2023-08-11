package com.strangeone101.pixeltweaks.integration.ftbquests;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;

public class CatchTask extends Task {

    public CatchTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.CATCH_POKEMON;
    }
}
