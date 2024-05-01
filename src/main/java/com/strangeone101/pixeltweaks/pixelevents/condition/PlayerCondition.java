package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.ClientBattleManager;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerCondition extends Condition<Player> {
    public UUID uuid;
    public String name;
    public int minPartyLevel = 0;
    public int maxPartyLevel = 100;

    @Override
    public boolean conditionMet(Player player) {
        if (player == null) return false; //Not a player owned battle

        if (uuid != null && !player.getUUID().equals(uuid)) return false;
        if (name != null && !player.getName().getString().equals(name)) return false;

        //Right now, getting levels from the party requires being in single player
        if (Minecraft.getInstance().isLocalServer()) {
            PlayerPartyStorage storage = null;
            try {
                storage = StorageProxy.getParty(player.getUUID()).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

            int minLevel = 100;
            int maxLevel = 0;

            for (int i = 0; i < 6; i++) {
                Pokemon pokemon = storage.get(new StoragePosition(-1, i));
                if (pokemon != null) {
                    minLevel = Math.min(minLevel, pokemon.getPokemonLevel());
                    maxLevel = Math.max(maxLevel, pokemon.getPokemonLevel());
                }
            }

            if (minLevel < minPartyLevel) return false;
            if (maxLevel > maxPartyLevel) return false;
        }

        return true;
    }

    @Override
    public Player itemFromPixelmon(PixelmonEntity entity) {
        if (entity.getOwner() instanceof Player) {
            return (Player) entity.getOwner();
        }
        return null;
    }

    @Override
    public String toString() {
        return "PlayerCondition{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", minPartyLevel=" + minPartyLevel +
                ", maxPartyLevel=" + maxPartyLevel +
                '}';
    }
}
