package com.strangeone101.pixeltweaks.client;

import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.PixelTweaks;

public class BattleHelper {

    public static PixelmonEntity getFromNickname(String nickname) {
        PixelTweaks.LOGGER.debug("Nickname is " + nickname);
        if (nickname == null || nickname.isEmpty() || ClientProxy.battleManager == null) return null;

        if (ClientProxy.battleManager.displayedEnemyPokemon != null) {
            for (PixelmonClientData data : ClientProxy.battleManager.displayedEnemyPokemon) {
                if (data.nickname.equals(nickname)) {
                    return ClientProxy.battleManager.getEntity(data.pokemonUUID);
                }
            }
        }

        if (ClientProxy.battleManager.displayedOurPokemon != null) {
            for (PixelmonClientData data : ClientProxy.battleManager.displayedOurPokemon) {
                if (data.nickname.equals(nickname)) {
                    return ClientProxy.battleManager.getEntity(data.pokemonUUID);
                }
            }
        }
        if (ClientProxy.battleManager.displayedAllyPokemon != null) {
            for (PixelmonClientData data : ClientProxy.battleManager.displayedAllyPokemon) {
                if (data.nickname.equals(nickname)) {
                    return ClientProxy.battleManager.getEntity(data.pokemonUUID);
                }
            }
        }

        return null;
    }
}
