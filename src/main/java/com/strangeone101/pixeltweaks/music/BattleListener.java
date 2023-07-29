package com.strangeone101.pixeltweaks.music;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleMessageEvent;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.ClientBattleManager;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BattleListener {

    public BattleListener() {
        Pixelmon.EVENT_BUS.addListener(this::onBattleMessage);
        Pixelmon.EVENT_BUS.addListener(this::onEffectivenessAttack);
    }

    public void onBattleMessage(BattleMessageEvent event) {
        MusicEvent.BattleAction.Action action = null;
        MusicEvent.BattleAction.Action subAction = null;

        switch (event.textComponent.getKey()) {
            case "pixelmon.effect.accuracyincreased":
            case "pixelmon.effect.attackincreased":
            case "pixelmon.effect.defenseincreased":
            case "pixelmon.effect.spatkincreased":
            case "pixelmon.effect.spdefincreased":
            case "pixelmon.effect.speedincreased":
            case "pixelmon.effect.evasionincreased":
                action = MusicEvent.BattleAction.Action.STATS_UP;
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
                action = MusicEvent.BattleAction.Action.STATS_UP_HARSH;
                subAction = MusicEvent.BattleAction.Action.STATS_UP;
                break;
            case "pixelmon.effect.accuracydecreased":
            case "pixelmon.effect.attackdecreased":
            case "pixelmon.effect.defensedecreased":
            case "pixelmon.effect.spatkdecreased":
            case "pixelmon.effect.spdefdecreased":
            case "pixelmon.effect.speeddecreased":
            case "pixelmon.effect.evasiondecreased":
                action = MusicEvent.BattleAction.Action.STATS_DOWN;
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
                action = MusicEvent.BattleAction.Action.STATS_DOWN_HARSH;
                subAction = MusicEvent.BattleAction.Action.STATS_DOWN;
                break;
            case "pixelmon.effect.burnt":
            case "pixelmon.status.burnhurt":
            case "pixelmon.status.burn.added":
                action = MusicEvent.BattleAction.Action.BURN;
                break;
            case "pixelmon.effect.frozesolid":
            case "pixelmon.status.frozensolid":
                action = MusicEvent.BattleAction.Action.FREEZE;
                break;
            case "pixelmon.status.isparalyzed":
                action = MusicEvent.BattleAction.Action.PARALYZE;
                break;
            case "pixelmon.effect.poisoned":
            case "pixelmon.effect.badlypoisoned":
            case "pixelmon.status.hurtbypoison":
                action = MusicEvent.BattleAction.Action.POISON;
                break;
            case "pixelmon.status.fellasleep":
            case "pixelmon.status.stillsleeping":
            case "pixelmon.effect.fallasleep":
                action = MusicEvent.BattleAction.Action.SLEEP;
                break;
            case "pixelmon.status.confused":
            case "pixelmon.effect.confusion":
                action = MusicEvent.BattleAction.Action.CONFUSION;
                break;
            case "pixelmon.status.inlove":
                action = MusicEvent.BattleAction.Action.LOVE;
                break;
            case "pixelmon.status.drowsy":
                action = MusicEvent.BattleAction.Action.DROWSY;
                break;
            case "battlecontroller.fainted":
            case "battlecontroller.hasfainted":
                action = MusicEvent.BattleAction.Action.FAINT;
                break;
            case "battlecontroller.outofpokemon":
                action = MusicEvent.BattleAction.Action.WIPEOUT;
                break;
            case "battlecontroller.sendout":
                action = MusicEvent.BattleAction.Action.SWITCH;
                break;
            case "battlecontroller.win":
                action = MusicEvent.BattleAction.Action.WIN;
                break;
            case "battlecontroller.flinched":
                action = MusicEvent.BattleAction.Action.FLINCH;
                break;
            case "battlecontroller.escaped":
                action = MusicEvent.BattleAction.Action.RUN;
                break;
            case "battlecontroller.initbattle.you":
            case "battlecontroller.initbattle.toyou":
                action = MusicEvent.BattleAction.Action.START;
                break;
        }
        String nickname = event.textComponent.getFormatArgs().length > 0 ? event.textComponent.getFormatArgs()[0].toString() : null;

        if (subAction != null) {
            PixelTweaks.LOGGER.debug("Playing action " + action + "/" + subAction + " for " + nickname);
            SoundManager.playBattleAction(getFromNickname(nickname), action, subAction);
        } else if (action != null) {
            PixelTweaks.LOGGER.debug("Playing action " + action + " for " + nickname);
            SoundManager.playBattleAction(getFromNickname(nickname), action);
        }

    }

    public void onEffectivenessAttack(AttackEvent.TypeEffectiveness event) {
        MusicEvent.BattleAction.Action action = null;
        if (event.getMultiplier() == 0) {
            action = MusicEvent.BattleAction.Action.HIT_NO_EFFECT;
        } else if (event.getMultiplier() < 1) {
            action = MusicEvent.BattleAction.Action.NOT_VERY_EFFECTIVE_HIT;
        } else if (event.getMultiplier() > 1) {
            action = MusicEvent.BattleAction.Action.SUPER_EFFECTIVE_HIT;
        }

        if (action != null) {
            SoundManager.playBattleAction(event.target.entity, action, MusicEvent.BattleAction.Action.HIT);
        }
    }

    private PixelmonEntity getFromNickname(String nickname) {
        PixelTweaks.LOGGER.debug("Nickname is " + nickname);
        if (nickname == null || nickname.isEmpty()) return null;

        for (PixelmonClientData data : ClientProxy.battleManager.displayedEnemyPokemon) {
            if (data.nickname.equals(nickname)) {
                return ClientProxy.battleManager.getEntity(data.pokemonUUID);
            }
        }
        for (PixelmonClientData data : ClientProxy.battleManager.displayedOurPokemon) {
            if (data.nickname.equals(nickname)) {
                return ClientProxy.battleManager.getEntity(data.pokemonUUID);
            }
        }
        for (PixelmonClientData data : ClientProxy.battleManager.displayedAllyPokemon) {
            if (data.nickname.equals(nickname)) {
                return ClientProxy.battleManager.getEntity(data.pokemonUUID);
            }
        }
        return null;
    }
}
