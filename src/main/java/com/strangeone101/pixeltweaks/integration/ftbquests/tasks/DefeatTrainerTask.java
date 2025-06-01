package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.entities.npcs.NPC;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

public class DefeatTrainerTask extends Task {

    public int count = 1;
    @Deprecated
    public Tristate gymLeader = Tristate.DEFAULT;
    @Deprecated
    public String bossTier = "";
    public String customName;
    public transient Component customNameCache;
    public UUID trainerUUID;
    public DefeatTrainerTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_TRAINER;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("count", count);
        //gymLeader.write(nbt, "gymLeader");
        //nbt.putString("bossTier", bossTier);
        nbt.putString("customName", customName == null ? "" : customName);
        nbt.putString("trainerUUID", trainerUUID == null ? "" : trainerUUID.toString());
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        count = nbt.getInt("count");
        //gymLeader = Tristate.read(nbt, "gymLeader");
        //bossTier = nbt.getString("bossTier");
        customName = nbt.getString("customName");
        recalculateNameCache();
        String uuidString = nbt.getString("trainerUUID");
        if (!uuidString.isEmpty()) {
            trainerUUID = UUID.fromString(uuidString);
        }
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        //gymLeader.write(buffer);
        //buffer.writeUtf(bossTier);
        buffer.writeUtf(customName == null ? "" : customName);
        buffer.writeUtf(trainerUUID == null ? "" : trainerUUID.toString());
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
        //gymLeader = Tristate.read(buffer);
        //bossTier = buffer.readUtf();
        customName = buffer.readUtf();
        recalculateNameCache();
        String uuidString = buffer.readUtf();
        if (!uuidString.isEmpty()) {
            trainerUUID = UUID.fromString(uuidString);
        }
    }

    public void recalculateNameCache() {
        if (customName != null && !customName.isEmpty()) {
            if (I18n.exists(customName)) {
                customNameCache = Component.translatable(customName);
            } else {
                customNameCache = Component.literal(customName);
            }
        } else {
            customNameCache = null;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addString("customName", customName, v -> {
            customName = v;
            recalculateNameCache();
        }, "");
        config.addString("trainerUUID", trainerUUID == null ? "" : trainerUUID.toString(), v -> {
            if (v.isEmpty()) {
                trainerUUID = null;
            } else {
                trainerUUID = UUID.fromString(v);
            }
        }, "");
        //config.addTristate("gymLeader", gymLeader, v -> gymLeader = v, Tristate.DEFAULT);

        /*List<String> bossTiers = BossTierRegistry.getBossTiers().stream().map(BossTier::getID).collect(Collectors.toList());
        bossTiers.add(0, ""); //Add "any" at the top

        config.addEnum("bossTier", bossTier, v -> bossTier = v,
                NameMap.of("", bossTiers)
                .nameKey(v -> {
                    if (v.isEmpty()) return "pixeltweaks.boss_tier.any";
                    return BossTierRegistry.getBossTier(v).get().getName();
                })
                .color(v -> {
                    if (v.isEmpty()) return Color4I.WHITE;
                    Color c = BossTierRegistry.getBossTier(v).get().getColor();
                    return Color4I.rgb(c.getRed(), c.getGreen(), c.getBlue());
                }).create(), "");*/


    }

    public void defeatTrainer(TeamData team, NPC trainer) {
        if (!team.isCompleted(this) && team.getFile().isServerSide()) {
            if ((customNameCache == null) != trainer.hasCustomName()) return;
            if (customNameCache != null && !customNameCache.equals(trainer.getCustomName())) return;
            if (trainerUUID != null && !trainer.getUUID().equals(trainerUUID)) return;

            team.addProgress(this, 1L);
        }
    }
}
