package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.storage.TrainerPartyStorage;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

public class TrainerCondition extends Condition<NPCTrainer> {
    public String trainer;
    public int minPartyLevel = 0;
    public int maxPartyLevel = 100;

    @Override
    public boolean conditionMet(NPCTrainer trainer) {
        if (trainer == null) return false; //Not a trainer owned battle

        if (this.trainer == null || (!trainer.getName().getString().equals(this.trainer) &&
                (trainer.getCustomName() != null && !trainer.getCustomName().getString().equals(this.trainer))))
            return false;

        TrainerPartyStorage storage = trainer.getPokemonStorage();

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
    public NPCTrainer itemFromPixelmon(PixelmonEntity entity) {
        if (entity.getOwner() instanceof NPCTrainer) {
            return (NPCTrainer) entity.getOwner();
        }
        return null;
    }

    @Override
    public String toString() {
        return "TrainerCondition{" +
                "trainer='" + trainer + '\'' +
                ", minPartyLevel=" + minPartyLevel +
                ", maxPartyLevel=" + maxPartyLevel +
                '}';
    }
}
