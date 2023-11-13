package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BattleItemTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BattleMoveTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.SubmitPokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BreedTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.CatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.DefeatPlayersTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.DefeatTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.DefeatTrainerTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.EvolutionTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.ExternalMoveTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.HatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.LevelTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PhotoTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokeDollarsTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokedexAmountTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokedexPercentageTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.TradeTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.WipeoutTask;
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
    public static TaskType DEFEAT_PLAYER;
    public static TaskType LEVEL_POKEMON;
    public static TaskType POKEDEX_AMOUNT;
    public static TaskType POKEDEX_PERCENTAGE;
    public static TaskType POKEDOLLARS;
    public static TaskType TRADE_POKEMON;
    public static TaskType BATTLE_MOVE;
    public static TaskType BATTLE_ITEM;
    public static TaskType TAKE_PHOTO;
    public static TaskType EXTERNAL_MOVE;
    public static TaskType SUBMIT_POKEMON;


    public static void register() {
        try {
            CATCH_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "catch_pokemon"),
                    CatchTask::new, () -> Icon.getIcon("pixelmon:items/pokeballs/poke_ball"));

            DEFEAT_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "defeat_pokemon"),
                    DefeatTask::new, () -> Icon.getIcon("pixelmon:items/healingitems/m_exp_candy"));

            EVOLVE_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "evolve_pokemon"),
                    EvolutionTask::new, () -> Icon.getIcon("pixelmon:items/evolutionstones/firestone"));

            LEVEL_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "level_pokemon"),
                    LevelTask::new, () -> Icon.getIcon("pixelmon:items/healingitems/rarecandy"));

            TRADE_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "trade_pokemon"),
                    TradeTask::new, () -> Icon.getIcon("pixelmon:items/linking_cord"));

            BREED_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "breed_pokemon"),
                    BreedTask::new, () -> Icon.getIcon("pixeltweaks:textures/gui/egg.png"));

            HATCH_EGG = TaskTypes.register(new ResourceLocation("pixelmon", "hatch_egg"),
                    HatchTask::new, () -> Icon.getIcon("pixeltweaks:textures/gui/egg2.png"));

            POKEDEX_AMOUNT = TaskTypes.register(new ResourceLocation("pixelmon", "pokedex_amount"),
                    PokedexAmountTask::new, () -> Icon.getIcon("pixelmon:textures/items/pokedex.png"));

            POKEDEX_PERCENTAGE = TaskTypes.register(new ResourceLocation("pixelmon", "pokedex_percentage"),
                    PokedexPercentageTask::new, () -> Icon.getIcon("pixelmon:textures/items/pokedex.png"));

            POKEDOLLARS = TaskTypes.register(new ResourceLocation("pixelmon", "pokedollars"),
                    PokeDollarsTask::new, () -> Icon.getIcon("pixelmon:textures/gui/pokedollar.png"));

            WIPEOUT = TaskTypes.register(new ResourceLocation("pixelmon", "wipeout"),
                    WipeoutTask::new, () -> Icon.getIcon("pixelmon:items/helditems/redcard"));

            DEFEAT_PLAYER = TaskTypes.register(new ResourceLocation("pixelmon", "defeat_player"),
                    DefeatPlayersTask::new, () -> Icon.getIcon("pixelmon:textures/gui/ribbons/isi.png"));

            DEFEAT_TRAINER = TaskTypes.register(new ResourceLocation("pixelmon", "defeat_trainer"),
                    DefeatTrainerTask::new, () -> Icon.getIcon("ftblibrary:textures/icons/player.png"));

            BATTLE_MOVE = TaskTypes.register(new ResourceLocation("pixelmon", "battle_move"),
                    BattleMoveTask::new, () -> Icon.getIcon("pixelmon:items/tms/tmfire"));

            //BATTLE_ITEM = TaskTypes.register(new ResourceLocation("pixelmon", "battle_item"),
                 //   BattleItemTask::new, () -> Icon.getIcon("pixelmon:items/healingitems/maxrevive"));

            TAKE_PHOTO = TaskTypes.register(new ResourceLocation("pixelmon", "take_photo"),
                    PhotoTask::new, () -> Icon.getIcon("pixelmon:items/camera"));

            EXTERNAL_MOVE = TaskTypes.register(new ResourceLocation("pixelmon", "external_move"),
                    ExternalMoveTask::new, () -> Icon.getIcon("pixelmon:textures/gui/overlay/externalmoves/cut.png"));

            SUBMIT_POKEMON = TaskTypes.register(new ResourceLocation("pixelmon", "submit_pokemon"),
                    SubmitPokemonTask::new, () -> Icon.getIcon("pixelmon:items/pokeballs/premier_ball"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
