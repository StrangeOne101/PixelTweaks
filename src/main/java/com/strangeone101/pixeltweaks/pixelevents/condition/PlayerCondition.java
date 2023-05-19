package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class PlayerCondition extends Condition<PlayerEntity> {
    public UUID uuid;
    public String name;
    public int minPartyLevel = 0;
    public int maxPartyLevel = 100;

    @Override
    public boolean conditionMet(PlayerEntity player) {
        if (player == null) return false; //Not a player owned battle

        if (uuid != null && !player.getUniqueID().equals(uuid)) return false;
        if (name != null && !player.getName().getString().equals(name)) return false;

        PlayerPartyStorage storage = StorageProxy.getParty(player.getUniqueID());

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

        return true;
    }

    @Override
    public PlayerEntity itemFromPixelmon(PixelmonEntity entity) {
        if (entity.getOwner() instanceof PlayerEntity) {
            return (PlayerEntity) entity.getOwner();
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
