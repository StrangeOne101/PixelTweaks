package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SubmitPokemonTask extends PokemonTask {

    public enum Party {
        ONLY_PARTY, ONLY_BOX, ALL;
    }
    public Party party = Party.ONLY_PARTY;
    public Tristate hasOT = Tristate.DEFAULT;
    public boolean consumePokemon = true;

    public SubmitPokemonTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.SUBMIT_POKEMON;
    }

    @Override
    public long getMaxProgress() {
        return !this.consumePokemon ? 1 : this.count;
    }

    @Override
    public boolean consumesResources() {
        return consumePokemon;
    }

    @Override
    public boolean canInsertItem() {
        return consumesResources();
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putByte("party", (byte) this.party.ordinal());
        hasOT.write(nbt, "hasOT");
        nbt.putBoolean("consumePokemon", this.consumePokemon);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.party = Party.values()[nbt.getByte("party")];
        this.hasOT = Tristate.read(nbt, "hasOT");
        this.consumePokemon = nbt.getBoolean("consumePokemon");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(this.party.ordinal());
        hasOT.write(buffer);
        buffer.writeBoolean(this.consumePokemon);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.party = Party.values()[buffer.readByte()];
        this.hasOT = Tristate.read(buffer);
        this.consumePokemon = buffer.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addEnum("party", this.party, v -> this.party = v, NameMap.of(Party.ONLY_PARTY, Party.values())
                .nameKey(v -> "pixeltweaks.party." + v.name().toLowerCase())
                .create());
        config.addTristate("hasOT", this.hasOT, v -> this.hasOT = v, Tristate.DEFAULT);
        config.addBool("consumePokemon", this.consumePokemon, v -> this.consumePokemon = v, true);
    }

    @Override
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        String key = "ftbquests.task.pixelmon.submit_pokemon.lore";
        if (!consumePokemon) key += "NoConsume";
        list.add(Component.translatable(key,
                Component.translatable("pixeltweaks.party." + this.party.name().toLowerCase())).withStyle(ChatFormatting.GRAY));
        super.addMouseOverText(list, teamData);
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this)) {
            return;
        }

        if (teamData.getFile().isServerSide()) {
            if (this.party != Party.ONLY_PARTY) {
                PCStorage storage = StorageProxy.getStorageManager().getPCForPlayer(player).getNow(null);
                for (int box = 0; box < storage.getBoxCount(); box++) {
                    PCBox pcbox = storage.getBox(box);
                    for (int slot = 0; slot < 30; slot++) {
                        Pokemon pokemon = pcbox.get(slot);
                        if (pokemon != null && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert) && (this.hasOT.isDefault()
                                || pokemon.getOriginalTrainerUUID().equals(player.getUUID()) == this.hasOT.isTrue())) {
                            teamData.addProgress(this, 1L);
                            if (this.consumePokemon) //Only consume if the task is set to consume
                                pcbox.set(slot, null);
                            return;
                        }
                    }
                }
            }
            if (this.party != Party.ONLY_BOX) {
                Pokemon[] pokemons = StorageProxy.getParty(player).getNow(null).getAll();

                for (int slot = 0; slot < 6; slot++) {
                    Pokemon pokemon = pokemons[slot];

                    if (pokemon != null && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert) && (this.hasOT.isDefault()
                            || pokemon.getOriginalTrainerUUID().equals(player.getUUID()) == this.hasOT.isTrue())) {
                        teamData.addProgress(this, 1L);
                        if (this.consumePokemon) //Only consume if the task is set to consume
                            StorageProxy.getParty(player).getNow(null).set(slot, null);
                        return;
                    }
                }
            }

            //CustomToast toast = new CustomToast(Component.translatable("pixeltweaks.errors.pokemon_not_found.title"),
                    //Icon.getIcon("pixelmon:textures/gui/exclamation"), Component.translatable("pixeltweaks.errors.pokemon_not_found.desc"));

        }
    }
}
