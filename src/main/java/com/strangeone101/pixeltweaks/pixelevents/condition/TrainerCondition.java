package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.TrainerPartyStorage;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

import java.util.List;

public class TrainerCondition extends Condition<NPCTrainer> {
    public String trainerType;
    public Integer textureIndex;
    public String customSteveTexture;
    public String name;
    public Boolean gymLeader;
    public int minPartyLevel = 0;
    public int maxPartyLevel = 100;

    @Override
    public boolean conditionMet(NPCTrainer trainer) {
        if (trainer == null) return false; //Not a trainer owned battle


        if (trainerType != null && !trainerType.isEmpty() && !trainer.getBaseTrainer().name.equalsIgnoreCase(trainerType)) return false;
        if (textureIndex != null && trainer.getTextureIndex() != textureIndex) return false;
        if (customSteveTexture != null && !customSteveTexture.isEmpty() && !trainer.getCustomSteveTexture().equals(customSteveTexture)) return false;
        if (name != null && !name.isEmpty() && trainer.getName() != null && !trainer.getName().getString().equals(name)) return false;
        if (gymLeader != null && trainer.isGymLeader != gymLeader) return false;

        if (trainer.getTrainerLevel() < minPartyLevel) return false;
        if (trainer.getTrainerLevel() > maxPartyLevel) return false;

        return true;
    }

    @Override
    public NPCTrainer itemFromPixelmon(PixelmonEntity entity) {
        BattleController bt = entity.battleController;
        List<BattleParticipant> participants = bt.participants;
        for (BattleParticipant p : participants) {
            if (p.getEntity() instanceof NPCTrainer){
                return (NPCTrainer) p.getEntity();
            }
        }
/*        if (entity.getOwner() instanceof NPCTrainer) {
            return (NPCTrainer) entity.getOwner();
        }*/
        return null;
    }

    @Override
    public String toString() {
        return "TrainerCondition{" +
                "trainerType='" + trainerType + '\'' +
                ", textureIndex=" + textureIndex +
                ", customSteveTexture='" + customSteveTexture + '\'' +
                ", name='" + name + '\'' +
                ", gymLeader=" + gymLeader +
                ", minPartyLevel=" + minPartyLevel +
                ", maxPartyLevel=" + maxPartyLevel +
                '}';
    }
}
