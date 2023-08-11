package com.strangeone101.pixeltweaks.integration.ftbquests;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.util.ResourceLocation;
import dev.ftb.mods.ftblibrary.icon.Icon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class PokemonTaskTypes {

    public static TaskType CATCH_POKEMON;
    public static TaskType HATCH_EGG;
    public static TaskType EVOLVE_POKEMON;
    public static TaskType DEFEAT_POKEMON;
    public static TaskType WIPEOUT;
    public static TaskType BREED_POKEMON;
    public static TaskType DEFEAT_TRAINER;
    public static TaskType LEVEL_POKEMON;


    public static void register() {
        try {
            Method registerMethod = TaskTypes.class.getDeclaredMethod("register", ResourceLocation.class, TaskType.Provider.class, Supplier.class);
            registerMethod.setAccessible(true);

            CATCH_POKEMON = (TaskType) registerMethod.invoke(null, new ResourceLocation("ftbquests", "catch_pokemon"), (TaskType.Provider) CatchTask::new, (Supplier<Icon>)() -> {
                return Icon.getIcon("pixelmon:item/poke_ball");
            });

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }



    }


}
