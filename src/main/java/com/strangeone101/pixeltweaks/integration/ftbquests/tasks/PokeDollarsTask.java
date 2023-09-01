package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PokeDollarsTask extends Task {

    public int amount = 1000;
    public Tristate consumeMoney = Tristate.DEFAULT;

    public PokeDollarsTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDOLLARS;
    }

    @Override
    public long getMaxProgress() {
        return this.amount;
    }

    @Override
    public boolean consumesResources() {
        return consumeMoney.get(quest.chapter.file.defaultTeamConsumeItems);
    }

    @Override
    public boolean canInsertItem() {
        return consumesResources();
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putInt("amount", this.amount);
        consumeMoney.write(nbt, "consumeMoney");
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.amount = nbt.getInt("amount");
        this.consumeMoney = Tristate.read(nbt, "consumeMoney");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.amount);
        consumeMoney.write(buffer);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.amount = buffer.readVarInt();
        consumeMoney = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addInt("amount", this.amount, v -> this.amount = v, 1, 1, Integer.MAX_VALUE);
        config.addTristate("consumeMoney", consumeMoney, v -> consumeMoney = v, Tristate.DEFAULT);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayerEntity player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        updateMoney(teamData, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        return new TranslationTextComponent("ftbquests.task.pixelmon.pokedollars.title", this.amount);
    }

    public void updateMoney(TeamData teamData, ServerPlayerEntity player) {
        if (teamData.isCompleted(this)) {
            return;
        }

        int m = StorageProxy.getParty(player).getBalance().intValue();
        if (m <= 0) return;
        PixelTweaks.LOGGER.debug("Player has " + m + " PokeDollars");
        boolean complete = m >= this.amount;
        m = complete ? this.amount : m;

        if (teamData.file.isServerSide()) {
            teamData.setProgress(this, m);

            if (consumesResources() && complete) {
                BankAccountProxy.getBankAccount(player).ifPresent(a -> a.take(this.amount));
                //StorageProxy.getParty(player).take(this.amount);
            }
        }
    }


}
