package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BreedTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.CatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.DefeatTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.EvolutionTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.HatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokeDollarsTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokedexAmountTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokedexPercentageTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.TradeTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.util.ResourceLocation;
import dev.ftb.mods.ftblibrary.icon.Icon;

public class PokemonTaskTypes {

    public static TaskType CATCH_POKEMON;
    public static TaskType HATCH_EGG;
    public static TaskType EVOLVE_POKEMON;
    public static TaskType DEFEAT_POKEMON;
    public static TaskType WIPEOUT;
    public static TaskType BREED_POKEMON;
    public static TaskType DEFEAT_TRAINER;
    public static TaskType LEVEL_POKEMON;
    public static TaskType POKEDEX_AMOUNT;
    public static TaskType POKEDEX_PERCENTAGE;
    public static TaskType POKEDOLLARS;
    public static TaskType TRADE_POKEMON;


    public static void register() {
        try {
            CATCH_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "catch_pokemon"),
                    CatchTask::new, () -> Icon.getIcon("pixelmon:items/pokeballs/poke_ball"));

            DEFEAT_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "defeat_pokemon"),
                    DefeatTask::new, () -> Icon.getIcon("pixelmon:items/healingitems/m_exp_candy"));

            EVOLVE_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "evolve_pokemon"),
                    EvolutionTask::new, () -> Icon.getIcon("pixelmon:items/healingitems/rarecandy"));

            TRADE_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "trade_pokemon"),
                    TradeTask::new, () -> Icon.getIcon("pixelmon:items/linking_cord"));

            HATCH_EGG = TaskTypes.register(new ResourceLocation("pixelmon", "hatch_egg"),
                    HatchTask::new, () -> Icon.getIcon("pixeltweaks:textures/gui/egg.png"));

            BREED_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "breed_pokemon"),
                    BreedTask::new, () -> Icon.getIcon("pixeltweaks:textures/gui/egg.png"));

            POKEDEX_AMOUNT = TaskTypes.register(new ResourceLocation("pixelmon", "pokedex_amount"),
                    PokedexAmountTask::new, () -> Icon.getIcon("pixelmon:textures/items/pokedex.png"));

            POKEDEX_PERCENTAGE = TaskTypes.register(new ResourceLocation("pixelmon", "pokedex_percentage"),
                    PokedexPercentageTask::new, () -> Icon.getIcon("pixelmon:textures/items/pokedex.png"));

            POKEDOLLARS = TaskTypes.register(new ResourceLocation("pixelmon", "pokedollars"),
                    PokeDollarsTask::new, () -> Icon.getIcon("pixelmon:textures/gui/pokedollar.png"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
