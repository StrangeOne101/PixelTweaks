package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.TrainerPartyStorage;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.ParticipantType;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.ClientBattleManager;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.npcs.registry.BaseTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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


        if (customSteveTexture != null && !customSteveTexture.isEmpty() && !trainer.getCustomSteveTexture().equals(customSteveTexture)) return false;
        if (name != null && !name.isEmpty() && trainer.getName() != null && !trainer.getName().getString().equals(name)) return false;
        if (gymLeader != null && isGymLeaderAlt(trainer) != gymLeader) return false;
        if (trainerType != null && !trainerType.isEmpty() && !trainer.getBaseTrainer().name.equalsIgnoreCase(trainerType)) return false;
        if (textureIndex != null && trainer.getTextureIndex() != textureIndex) return false;
        if (trainer.getTrainerLevel() < minPartyLevel) return false;
        if (trainer.getTrainerLevel() > maxPartyLevel) return false;
        return true;
    }

    public boolean isGymLeaderAlt(NPCTrainer trainer){
        ItemStack[] winnings =  trainer.getWinnings();

        return false;
    }

    @Override
    public NPCTrainer itemFromPixelmon(PixelmonEntity entity) {
        BattleController bt = entity.battleController;
        if (bt != null) {
            for (BattleParticipant p : bt.participants) {
                Entity participantEntity = p.getEntity();
                if (participantEntity instanceof NPCTrainer) {
                    return (NPCTrainer) participantEntity;
                }
            }
        } else {
            ClientBattleManager bm = ClientProxy.battleManager;
            for (ParticipantType[] type : bm.battleSetup) {
                if (type[0] == ParticipantType.Trainer) {
                    Iterable<Entity> entities = Minecraft.getInstance().world.getAllEntities();
                    Entity closest = findClosestEntityToBlockPos(entities, entity.getPosition(), entity);

                    if (closest instanceof NPCTrainer) {
                        return (NPCTrainer) closest;
                    }
                    break;
                }
            }
        }
        return null;
    }


    public Entity findClosestEntityToBlockPos(Iterable<Entity> entities, BlockPos position, Entity exclude) {
        double minDistanceSquared = Double.MAX_VALUE;
        Entity closest = null;

        for (Entity e : entities) {
            if (e.equals(exclude)) {
                continue;
            }

            double distanceSquared = e.getDistanceSq(position.getX(), position.getY(), position.getZ());
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = e;
            }
        }

        return closest;
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
