package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonFaintEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
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
    }

    private List<CatchTask> catchTasks = null;
    private List<DefeatTask> defeatTasks = null;
    private List<TradeTask> tradeTasks = null;

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
}
