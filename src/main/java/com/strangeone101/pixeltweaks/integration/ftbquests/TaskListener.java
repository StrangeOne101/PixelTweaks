package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.daycare.event.DayCareEvent;
import com.pixelmonmod.pixelmon.api.economy.EconomyEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BreedTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.CatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.DefeatTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.EvolutionTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.HatchTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokeDollarsTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.TradeTask;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskListener {

    public TaskListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::betterOnCatch);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onDefeatPokemon);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRaidCatch);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBalanceSet);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBalanceChange);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEvolve);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onHatch);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBreed);
    }

    private List<CatchTask> catchTasks = null;
    private List<DefeatTask> defeatTasks = null;
    private List<TradeTask> tradeTasks = null;
    private List<PokeDollarsTask> pokeDollarsTasks = null;
    private List<EvolutionTask> evolutionTasks = null;
    private List<HatchTask> hatchTasks = null;
    private List<BreedTask> breedTasks = null;

    public void betterOnCatch(PokemonReceivedEvent event) {
        if (catchTasks == null) {
            catchTasks = ServerQuestFile.INSTANCE.collect(CatchTask.class);
        }
        if (tradeTasks == null) {
            tradeTasks = ServerQuestFile.INSTANCE.collect(TradeTask.class);
        }

        if (catchTasks.isEmpty() && tradeTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());
        Pokemon pokemon = event.getPokemon();

        if (event.getCause().equals(PokemonReceivedEvent.Constants.POKE_BALL) || event.getCause().equals(PokemonReceivedEvent.Constants.FOSSIL)) {
            for (CatchTask task : catchTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.catchPokemon(data, pokemon);
                }
            }
        } else if (event.getCause().equals(PokemonReceivedEvent.Constants.TRADE)) {
            for (TradeTask task : tradeTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.tradePokemon(data, pokemon);
                }
            }
        }
    }

    public void onRaidCatch(CaptureEvent.SuccessfulRaidCapture event) {
        if (catchTasks == null) {
            catchTasks = ServerQuestFile.INSTANCE.collect(CatchTask.class);
        }

        if (catchTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());
        Pokemon pokemon = event.getRaidPokemon();
        pokemon.setBall(event.getPokeBall().getBallType());
        pokemon.setOriginalTrainer(event.getPlayer());

        for (CatchTask task : catchTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.catchRaidPokemon(data, pokemon);
            }
        }
    }

    public void onDefeatPokemon(AttackEvent.Damage event) {
        if (!event.willBeFatal()) return;

        if (defeatTasks == null) {
            defeatTasks = ServerQuestFile.INSTANCE.collect(DefeatTask.class);
        }

        if (defeatTasks.isEmpty()) {
            return;
        }

        Set<TeamData> teamsInBattle = new HashSet<>();

        PixelmonWrapper wrapper = event.target;
        BattleController controller = event.getBattleController();
        for (PixelmonWrapper opponent : controller.getOpponentPokemon(wrapper)) {
            if (opponent.getPlayerOwner() != null) {
                TeamData data = ServerQuestFile.INSTANCE.getData(opponent.getPlayerOwner());

                if (!teamsInBattle.contains(data)) {
                    teamsInBattle.add(data);

                    for (DefeatTask task : defeatTasks) {
                        if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                            task.defeatPokemon(data, wrapper.entity);
                        }
                    }
                }
            }
        }
    }

    public void onBalanceSet(EconomyEvent.SetBalance event) {
        moneyThing(event);
    }

    public void onBalanceChange(EconomyEvent.PostTransaction event) {
        moneyThing(event);
    }

    private void moneyThing(EconomyEvent event) {
        if (pokeDollarsTasks == null) {
            pokeDollarsTasks = ServerQuestFile.INSTANCE.collect(PokeDollarsTask.class);
        }

        if (pokeDollarsTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (PokeDollarsTask task : pokeDollarsTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.updateMoney(data, event.getPlayer());
            }
        }
    }

    public void onEvolve(EvolveEvent.Post event) {
        if (evolutionTasks == null) {
            evolutionTasks = ServerQuestFile.INSTANCE.collect(EvolutionTask.class);
        }

        if (evolutionTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (EvolutionTask task : evolutionTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.evolvePokemon(data, event.getPlayer(), event.getPokemon(), event.getEvolution());
            }
        }
    }

    public void onHatch(EggHatchEvent event) {
        if (hatchTasks == null) {
            hatchTasks = ServerQuestFile.INSTANCE.collect(HatchTask.class);
        }

        if (hatchTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (HatchTask task : hatchTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.onHatch(data, event.getPokemon());
            }
        }
    }

    public void onBreed(DayCareEvent.PostTimerBegin event) {
        if (breedTasks == null) {
            breedTasks = ServerQuestFile.INSTANCE.collect(BreedTask.class);
        }

        if (breedTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (BreedTask task : breedTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.onBreed(data, event.getBox().getParentOne(), event.getBox().getParentTwo());
            }
        }
    }
}
