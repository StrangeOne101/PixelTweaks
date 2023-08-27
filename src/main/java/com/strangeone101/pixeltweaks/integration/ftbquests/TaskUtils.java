package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokeDollarsTask;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;

import java.math.BigDecimal;

public class TaskUtils {

    public static void updateClientPokedollars(BigDecimal amount) {
        TeamData data = ClientQuestFile.INSTANCE.getData(Minecraft.getInstance().player);

        ClientQuestFile.INSTANCE.collect(PokeDollarsTask.class).forEach(task -> {
            data.setProgress(task, amount.intValue());
        });

    }
}
