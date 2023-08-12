package com.strangeone101.pixeltweaks.integration.ftbquests;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;

public class HatchTask extends PokemonTask {

    public HatchTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.HATCH_EGG;
    }
}
