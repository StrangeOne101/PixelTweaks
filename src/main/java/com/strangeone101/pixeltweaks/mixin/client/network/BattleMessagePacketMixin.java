package com.strangeone101.pixeltweaks.mixin.client.network;

import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.battles.tasks.BattleMessagePacket;
import com.pixelmonmod.pixelmon.battles.tasks.BattleTaskPacket;
import com.pixelmonmod.pixelmon.client.gui.battles.ClientBattleManager;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.client.BattleHelper;
import com.strangeone101.pixeltweaks.music.MusicEvent;
import com.strangeone101.pixeltweaks.music.SoundManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.strangeone101.pixeltweaks.music.MusicEvent.BattleAction.Action;

import java.util.Arrays;
import java.util.function.Predicate;

@Mixin(BattleMessagePacket.class)
public abstract class BattleMessagePacketMixin extends BattleTaskPacket {

    @Unique
    private static Predicate<TranslationTextComponent> pixelTweaks$future = null;

    @Unique
    private static TranslationTextComponent pixelTweaks$last = null;
    @Shadow(remap = false)
    private ITextComponent component;

    @Inject(method = "process", at = @At("HEAD"), cancellable = true, remap = false)
    public void onProcess(ClientBattleManager bm, CallbackInfoReturnable<Boolean> cir) {
        if (!(component instanceof TranslationTextComponent)) {
            return;
        }

        TranslationTextComponent tcomponent = (TranslationTextComponent) component;

        if (pixelTweaks$future != null) {
            if (pixelTweaks$future.test(tcomponent)) {
                return;
            }
        }

        MusicEvent.BattleAction.Action action = null;
        MusicEvent.BattleAction.Action subAction = null;

        switch (tcomponent.getKey()) {
            case "pixelmon.effect.accuracyincreased":
            case "pixelmon.effect.attackincreased":
            case "pixelmon.effect.defenseincreased":
            case "pixelmon.effect.spatkincreased":
            case "pixelmon.effect.spdefincreased":
            case "pixelmon.effect.speedincreased":
            case "pixelmon.effect.evasionincreased":
                action = Action.STATS_UP;
                break;
            case "pixelmon.effect.accuracyincreased2":
            case "pixelmon.effect.attackincreased2":
            case "pixelmon.effect.defenseincreased2":
            case "pixelmon.effect.spatkincreased2":
            case "pixelmon.effect.spdefincreased2":
            case "pixelmon.effect.speedincreased2":
            case "pixelmon.effect.evasionincreased2":
            case "pixelmon.effect.accuracyincreased3":
            case "pixelmon.effect.attackincreased3":
            case "pixelmon.effect.defenseincreased3":
            case "pixelmon.effect.spatkincreased3":
            case "pixelmon.effect.spdefincreased3":
            case "pixelmon.effect.speedincreased3":
            case "pixelmon.effect.evasionincreased3":
                action = Action.STATS_UP_HARSH;
                subAction = Action.STATS_UP;
                break;
            case "pixelmon.effect.accuracydecreased":
            case "pixelmon.effect.attackdecreased":
            case "pixelmon.effect.defensedecreased":
            case "pixelmon.effect.spatkdecreased":
            case "pixelmon.effect.spdefdecreased":
            case "pixelmon.effect.speeddecreased":
            case "pixelmon.effect.evasiondecreased":
                action = Action.STATS_DOWN;
                break;
            case "pixelmon.effect.accuracydecreased2":
            case "pixelmon.effect.attackdecreased2":
            case "pixelmon.effect.defensedecreased2":
            case "pixelmon.effect.spatkdecreased2":
            case "pixelmon.effect.spdefdecreased2":
            case "pixelmon.effect.speeddecreased2":
            case "pixelmon.effect.evasiondecreased2":
            case "pixelmon.effect.accuracydecreased3":
            case "pixelmon.effect.attackdecreased3":
            case "pixelmon.effect.defensedecreased3":
            case "pixelmon.effect.spatkdecreased3":
            case "pixelmon.effect.spdefdecreased3":
            case "pixelmon.effect.speeddecreased3":
            case "pixelmon.effect.evasiondecreased3":
                action = Action.STATS_DOWN_HARSH;
                subAction = Action.STATS_DOWN;
                break;
            case "pixelmon.effect.burnt":
            case "pixelmon.status.burnhurt":
            case "pixelmon.status.burn.added":
                action = Action.BURN;
                break;
            case "pixelmon.effect.frozesolid":
            case "pixelmon.status.frozensolid":
                action = Action.FREEZE;
                break;
            case "pixelmon.status.isparalyzed":
                action = Action.PARALYZE;
                break;
            case "pixelmon.effect.poisoned":
            case "pixelmon.effect.badlypoisoned":
            case "pixelmon.status.hurtbypoison":
                action = Action.POISON;
                break;
            case "pixelmon.status.fellasleep":
            case "pixelmon.status.stillsleeping":
            case "pixelmon.effect.fallasleep":
            case "pixelmon.effect.healthsleep":
                action = Action.SLEEP;
                break;
            case "pixelmon.status.confused":
            case "pixelmon.effect.confusion":
            case "pixelmon.effect.thrash":
                action = Action.CONFUSION;
                break;
            case "pixelmon.status.inlove":
                action = Action.LOVE;
                break;
            case "pixelmon.status.drowsy":
                action = Action.DROWSY;
                break;
            case "pixelmon.effect.critincreased":
                action = Action.GETTING_PUMPED;
                break;
            case "pixelmon.effect.wrapped":
            case "pixelmon.effect.squeezed":
                action = Action.WRAP;
                break;
            case "pixelmon.effect.spikes":
            case "pixelmon.effect.morespikes":
            case "pixelmon.effect.floatingstones":
            case "pixelmon.effect.sharpsteel":
                action = Action.SPIKES;
                break;
            case "pixelmon.effect.toxicspikes":
            case "pixelmon.effect.moretoxicspikes":
                action = Action.POISON;
                subAction = Action.SPIKES;
                break;
            case "pixelmon.effect.raining":
                action = Action.WEATHER_RAINY;
                break;
            case "pixelmon.effect.starthail":
                action = Action.WEATHER_HAIL;
                break;
            case "pixelmon.status.extremelyheavyrain":
                action = Action.WEATHER_VERY_RAINY;
                subAction = Action.WEATHER_RAINY;
                break;
            case "pixelmon.effect.sandstorm":
                action = Action.WEATHER_SANDSTORM;
                break;
            case "pixelmon.effect.startsnow":
                action = Action.WEATHER_SNOW;
                break;
            case "pixelmon.effect.harshsunlight":
                action = Action.WEATHER_SUNNY;
                break;
            case "pixelmon.effect.extremelybrightlight":
                action = Action.WEATHER_VERY_SUNNY;
                subAction = Action.WEATHER_SUNNY;
                break;
            case "battlecontroller.fainted":
            case "battlecontroller.hasfainted":
                action = Action.FAINT;
                break;
            case "battlecontroller.outofpokemon":
                action = Action.WIPEOUT;
                break;
            case "battlecontroller.sendout":
                action = Action.SWITCH;
                break;
            case "battlecontroller.win":
                action = Action.WIN;
                pixelTweaks$future = null;
                break;
            case "battlecontroller.flinched":
                action = Action.FLINCH;
                break;
            case "battlecontroller.escaped":
                action = Action.RUN;
                pixelTweaks$future = null;
                break;
            case "pixelmon.pokeballs.throw":
                action = Action.THROW_POKEBALL;
                break;
            case "pixelmon.pokeballs.brokefree":
                action = Action.CATCH_FAIL;
                break;
            case "pixelmon.pokeballs.capture":
                action = Action.CATCH;
                break;
            case "pixelmon.effect.revived":
                action = Action.REVIVE;
                subAction = Action.POTION;
                break;
            case "pixelmon.effect.restorehealth":
                action = Action.POTION;
                break;
            case "pixelmon.helditems.consumerestorehp":
            case "pixelmon.helditems.consumeleppa":
            case "pixelmon.helditems.pinchberry":
            case "pixelmon.helditems.custapberry":
            case "pixelmon.helditems.pumkinberry":
                action = Action.BERRY_EAT;
                break;
            case "pixelmon.battletext.megareact":
            case "pixelmon.battletext.ashgreninja.react":
            case "pixelmon.battletext.rayquazamegareact":
                action = Action.MEGA_EVOLVE;
                break;
            case "pixelmon.battletext.dynamax":
                action = Action.DYNAMAX;
                break;
            case "pixelmon.battletext.dynamaxlost":
                action = Action.DYNAMAX_LOST;
                break;
            case "battlecontroller.initbattle.you":
            case "battlecontroller.initbattle.toyou":
                action = Action.START;
                pixelTweaks$future = null;
                break;
            case "battlecontroller.forfeit":
            case "battlecontroller.forfeitself":
            case "battlecontroller.draw":
                pixelTweaks$future = null;
                break;
            case "pixelmon.battletext.used":
                if (pixelTweaks$future != null) pixelTweaks$future.test(tcomponent);

                pixelTweaks$future = (component) -> {
                    String key = component.getKey();
                    String lastPokemon = pixelTweaks$last != null ? pixelTweaks$last.getFormatArgs()[0].toString() : null;
                    switch (key) {
                        case "pixelmon.battletext.used":
                            TranslationTextComponent textComponent = (TranslationTextComponent) pixelTweaks$last.getFormatArgs()[1];
                            String move = textComponent.getKey().split("\\.", 2)[1];
                            AttackRegistry.getAttackBase(move).ifPresent(attackBase -> {
                                if (attackBase.getAttackCategory() != AttackCategory.STATUS) {
                                    SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.EFFECTIVE_HIT, Action.HIT);
                                }
                            });
                            return false;
                        case "pixelmon.battletext.missedattack":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.HIT_MISS);
                            return true;
                        case "pixelmon.battletext.butmovefailed":
                        case "pixelmon.battletext.movefailed":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.HIT_FAIL);
                            return true;
                        case "pixelmon.battletext.noeffect":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.HIT_NO_EFFECT);
                            return true;
                        case "pixelmon.battletext.criticalhit":
                        case "pixelmon.battletext.criticalhittarget":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.HIT_CRITICAL, Action.HIT);
                            return true;
                        case "pixelmon.battletext.wasnoteffective":
                        case "pixelmon.battletext.wasnoteffectivetarget":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.NOT_VERY_EFFECTIVE_HIT, Action.HIT);
                            return true;
                        case "pixelmon.battletext.supereffective":
                        case "pixelmon.battletext.supereffectivetarget":
                            SoundManager.playBattleAction(BattleHelper.getFromNickname(lastPokemon), Action.SUPER_EFFECTIVE_HIT, Action.HIT);
                            return true;
                        default:
                            return false;
                    }
                };

                pixelTweaks$last = tcomponent;
        }
        String nickname = tcomponent.getFormatArgs().length > 0 ? tcomponent.getFormatArgs()[0].toString() : null;

        if (subAction != null) {
            PixelTweaks.LOGGER.debug("Playing action " + action + "/" + subAction + " for " + nickname);
            SoundManager.playBattleAction(BattleHelper.getFromNickname(nickname), action, subAction);
        } else if (action != null) {
            PixelTweaks.LOGGER.debug("Playing action " + action + " for " + nickname);
            SoundManager.playBattleAction(BattleHelper.getFromNickname(nickname), action);
        }


    }
}
