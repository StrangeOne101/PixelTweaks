package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.battles.BattleResults;
import com.pixelmonmod.pixelmon.api.daycare.event.DayCareEvent;
import com.pixelmonmod.pixelmon.api.economy.EconomyEvent;
import com.pixelmonmod.pixelmon.api.events.CameraEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.LevelUpEvent;
import com.pixelmonmod.pixelmon.api.events.PokedexEvent;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.moveskills.UseMoveSkillEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.Scheduling;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.BattleStage;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.BattleMoveTask;
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
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.PokedexTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.TradeTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.tasks.WipeoutTask;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TaskListener {

    public TaskListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::betterOnCatch);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onDefeatPokemon);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRaidCatch);
        //Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBalanceSet);
        //Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBalanceChange);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEvolve);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onHatch);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBreed);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOW, this::onPokedexUpdate);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onLevelUp);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onWipeout);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBattleMove);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onTakePhoto);
        Pixelmon.EVENT_BUS.addListener(EventPriority.LOWEST, this::onExternalMove);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::onLogin);
        ClearFileCacheEvent.EVENT.register(this::onFTBCacheClear);

    }

    private List<CatchTask> catchTasks = null;
    private List<DefeatTask> defeatTasks = null;
    private List<TradeTask> tradeTasks = null;
    private List<PokeDollarsTask> pokeDollarsTasks = null;
    private List<EvolutionTask> evolutionTasks = null;
    private List<HatchTask> hatchTasks = null;
    private List<BreedTask> breedTasks = null;
    private List<PokedexTask> pokedexTasks = null;
    private List<LevelTask> levelTasks = null;
    private List<WipeoutTask> wipeoutTasks = null;
    private List<DefeatTrainerTask> defeatTrainerTasks = null;
    private List<DefeatPlayersTask> defeatPlayersTasks = null;
    private List<BattleMoveTask> battleMoveTasks = null;
    private List<PhotoTask> photoTasks = null;
    private List<ExternalMoveTask> externalMoveTasks = null;

    @Deprecated
    private Set<UUID> antiOverflow = new HashSet<>();
    private Set<UUID> hatchCommandFix = new HashSet<>();

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

        if (event.getCause().equals(PokemonReceivedEvent.Constants.TRADE)) {
            for (TradeTask task : tradeTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.tradePokemon(data, pokemon);
                }
            }
        } else {
            for (CatchTask task : catchTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    if (event.getCause().equals(PokemonReceivedEvent.Constants.POKE_BALL)) {
                        task.catchPokemon(data, pokemon);
                    } else if (event.getCause().equals(PokemonReceivedEvent.Constants.FOSSIL)) {
                        task.onFossil(data, pokemon);
                    } else if (event.getCause().equals(PokemonReceivedEvent.Constants.CHRISTMAS)) {
                        task.onChristmas(data, pokemon);
                    } else if (event.getCause().equals(PokemonReceivedEvent.Constants.COMMAND)
                            || event.getCause().equals(PokemonReceivedEvent.Constants.GIFT_COMMAND)) {
                        task.onCommand(data, pokemon);
                    }
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
        if (controller.getStage() == BattleStage.PICKACTION) return; //For some reason, this event is called when the battle starts without triggering a move???
        
        for (PixelmonWrapper opponent : controller.getOpponentPokemon(wrapper)) {
            if (opponent.getPlayerOwner() != null) {
                TeamData data = ServerQuestFile.INSTANCE.getData(opponent.getPlayerOwner());

                if (!teamsInBattle.contains(data)) {
                    teamsInBattle.add(data);

                    for (DefeatTask task : defeatTasks) {
                        if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                            task.defeatPokemon(data, wrapper.entity, event.user.entity);
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
        if (antiOverflow.contains(event.getPlayer().getUniqueID())) {
            return;
        }
        Scheduling.schedule(1, () -> {
            if (pokeDollarsTasks == null) {
                pokeDollarsTasks = ServerQuestFile.INSTANCE.collect(PokeDollarsTask.class);
            }

            if (pokeDollarsTasks.isEmpty()) {
                return;
            }

            TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

            antiOverflow.add(event.getPlayer().getUniqueID());
            Scheduling.schedule(2, () -> antiOverflow.remove(event.getPlayer().getUniqueID()), false);
            for (PokeDollarsTask task : pokeDollarsTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.updateMoney(data, event.getPlayer());
                }
            }
        }, false);
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

    public void onHatch(EggHatchEvent.Post event) {
        if (hatchCommandFix.contains(event.getPokemon().getUUID())) return;

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

        hatchCommandFix.add(event.getPokemon().getUUID());
        Scheduling.schedule(1, () -> hatchCommandFix.remove(event.getPokemon().getUUID()), false);
    }

    public void onBreed(DayCareEvent.PostCollect event) {
        if (breedTasks == null) {
            breedTasks = ServerQuestFile.INSTANCE.collect(BreedTask.class);
        }

        if (breedTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (BreedTask task : breedTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.onBreed(data, event.getParentOne(), event.getParentTwo());
            }
        }
    }

    private void updatePokedex(ServerPlayerEntity player) {
        if (pokedexTasks == null) {
            pokedexTasks = new ArrayList<>();
            pokedexTasks.addAll(ServerQuestFile.INSTANCE.collect(PokedexPercentageTask.class));
            pokedexTasks.addAll(ServerQuestFile.INSTANCE.collect(PokedexAmountTask.class));
        }

        if (pokedexTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(player);

        for (PokedexTask task : pokedexTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.updatePokedex(data, player);
            }
        }
    }

    public void onPokedexUpdate(PokedexEvent.Post event) {
        updatePokedex(event.getPlayer());
    }

    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            updatePokedex((ServerPlayerEntity) event.getPlayer());
    }

    public void onLevelUp(LevelUpEvent.Post event) {
        if (levelTasks == null) {
            levelTasks = ServerQuestFile.INSTANCE.collect(LevelTask.class);
        }

        if (levelTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        for (LevelTask task : levelTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.onLevel(data, event.getPokemon(), event.getCause());
            }
        }
    }

    public void onWipeout(BattleEndEvent event) {
        if (wipeoutTasks == null) {
            wipeoutTasks = ServerQuestFile.INSTANCE.collect(WipeoutTask.class);
        }
        if (defeatPlayersTasks == null) {
            defeatPlayersTasks = ServerQuestFile.INSTANCE.collect(DefeatPlayersTask.class);
        }
        if (defeatTrainerTasks == null) {
            defeatTrainerTasks = ServerQuestFile.INSTANCE.collect(DefeatTrainerTask.class);
        }

        if (wipeoutTasks.isEmpty() && defeatPlayersTasks.isEmpty() && defeatTrainerTasks.isEmpty()) {
            return;
        }

        if (event.getBattleController().isRaid() || event.getBattleController().isSimulation()
                || event.getBattleController().getPlayers().isEmpty()) return;

        boolean pvp = event.getBattleController().isPvP();
        boolean trainer = event.getBattleController().participants.stream().anyMatch(p -> p instanceof TrainerParticipant);

        for (Map.Entry<BattleParticipant, BattleResults> entry : event.getResults().entrySet()) {
            if (entry.getKey() instanceof PlayerParticipant) {
                ServerPlayerEntity player = ((PlayerParticipant) entry.getKey()).player;
                TeamData data = ServerQuestFile.INSTANCE.getData(player);

                if (entry.getValue() == BattleResults.DEFEAT) {


                    for (WipeoutTask task : wipeoutTasks) {
                        if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                            task.onWipeout(data, player);
                        }
                    }
                } else if (entry.getValue() == BattleResults.VICTORY) {
                    if (pvp) {
                        ServerPlayerEntity other = event.getPlayers().stream().filter(p -> p != player).findFirst().get();
                        for (DefeatPlayersTask task : defeatPlayersTasks) {
                            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                                task.onDefeat(data, other);
                            }
                        }
                    } else if (trainer) {
                        TrainerParticipant participant = (TrainerParticipant) event.getBattleController().participants.stream().filter(p -> p instanceof TrainerParticipant).findFirst().get();
                        NPCTrainer trainerNPC = participant.trainer;
                        for (DefeatTrainerTask task : defeatTrainerTasks) {
                            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                                task.defeatTrainer(data, trainerNPC);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onBattleMove(AttackEvent.Use event) {
        if (battleMoveTasks == null) {
            battleMoveTasks = ServerQuestFile.INSTANCE.collect(BattleMoveTask.class);
        }
        if (battleMoveTasks.isEmpty()) {
            return;
        }

        if (event.user.getPlayerOwner() == null) return;

        TeamData data = ServerQuestFile.INSTANCE.getData(event.user.getPlayerOwner());

        for (BattleMoveTask task : battleMoveTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                PixelTweaks.LOGGER.debug("Battle move task for move: " + event.attack.getAttackName());
                task.onBattleMove(data, event.user.pokemon, event.getAttack());
            }
        }
    }

    public void onTakePhoto(CameraEvent.TakePhoto event) {
        if (photoTasks == null) {
            photoTasks = ServerQuestFile.INSTANCE.collect(PhotoTask.class);
        }

        if (photoTasks.isEmpty()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.player);

        for (PhotoTask task : photoTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.takePhoto(data, event.pixelmon);
            }
        }
    }

    public void onExternalMove(UseMoveSkillEvent event) {
        if (externalMoveTasks == null) {
            externalMoveTasks = ServerQuestFile.INSTANCE.collect(ExternalMoveTask.class);
        }

        if (externalMoveTasks.isEmpty()) {
            return;
        }

        if (event.pixelmon.getOwner() instanceof ServerPlayerEntity) {
            TeamData data = ServerQuestFile.INSTANCE.getData(event.pixelmon.getOwner());

            for (ExternalMoveTask task : externalMoveTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.onMove(data, event.moveSkill.id, event.pixelmon.getPokemon());
                }
            }
        }


    }

    private void onFTBCacheClear(ClearFileCacheEvent event) {
        if (event.getFile().isServerSide()) {
            this.catchTasks = null;
            this.defeatPlayersTasks = null;
            this.defeatTasks = null;
            this.defeatTrainerTasks = null;
            this.battleMoveTasks = null;
            this.externalMoveTasks = null;
            this.evolutionTasks = null;
            this.levelTasks = null;
            this.photoTasks = null;
            this.wipeoutTasks = null;
            this.pokedexTasks = null;
            this.breedTasks = null;
            this.hatchTasks = null;
            this.tradeTasks = null;
            this.pokeDollarsTasks = null;
        }
    }
}
