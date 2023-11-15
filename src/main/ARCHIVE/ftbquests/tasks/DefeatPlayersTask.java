package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public class DefeatPlayersTask extends Task {

    public int count = 1;
    public String username = "";
    public String uuid = "";
    public transient UUID realUUID;

    public DefeatPlayersTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_PLAYER;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("count", count);
        if (!username.isEmpty()) {
            nbt.putString("username", username);
        }
        if (!uuid.isEmpty()) {
            nbt.putString("uuid", uuid);
        }
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);

        count = nbt.getInt("count");
        if (nbt.contains("username")) {
            username = nbt.getString("username");
        }
        if (nbt.contains("uuid")) {
            uuid = nbt.getString("uuid");
            if (!uuid.isEmpty()) realUUID = UUID.fromString(uuid);
        }
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(count);
        buffer.writeString(username);
        buffer.writeString(uuid);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        count = buffer.readVarInt();
        username = buffer.readString();
        uuid = buffer.readString();
        if (!uuid.isEmpty()) realUUID = UUID.fromString(uuid);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addInt("count", count, v -> count = v, 1, 1, Integer.MAX_VALUE);
        config.addString("username", username, v -> username = v, "");
        config.addString("uuid", uuid, v -> {
            uuid = v;
            if (!uuid.isEmpty()) realUUID = UUID.fromString(uuid);
        }, "");
    }

    public void onDefeat(TeamData teamData, ServerPlayerEntity defeated) {
        if (!teamData.isCompleted(this)
                && (username.isEmpty() || defeated.getGameProfile().getName().equals(username))
                && (uuid.isEmpty() || defeated.getGameProfile().getId().equals(realUUID))) {
            teamData.addProgress(this, 1);
        }
    }
}
