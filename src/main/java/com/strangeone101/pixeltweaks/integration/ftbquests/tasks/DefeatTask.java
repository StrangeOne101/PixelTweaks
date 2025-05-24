package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.pixelmon.battles.controller.participants.RaidPixelmonParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DefeatTask extends PokemonTask {

    public Tristate wild = Tristate.DEFAULT;
    public transient PokemonSpecification cachedUsedSpec;
    public boolean invertUsed;

    public DefeatTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_POKEMON;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        wild.write(nbt, "wild");
        nbt.putString("usedPokemonSpec", cachedUsedSpec == null ? "" : cachedUsedSpec.toString());
        nbt.putBoolean("invertUsed", invertUsed);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        wild = Tristate.read(nbt, "wild");
        String usedPokemonSpec = nbt.getString("usedPokemonSpec");
        cachedUsedSpec = usedPokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(usedPokemonSpec).get();
        invertUsed = nbt.getBoolean("invertUsed");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        wild.write(buffer);
        buffer.writeUtf(cachedUsedSpec == null ? "" : cachedUsedSpec.toString());
        buffer.writeBoolean(invertUsed);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        wild = Tristate.read(buffer);
        String usedPokemonSpec = buffer.readUtf();
        cachedUsedSpec = usedPokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(usedPokemonSpec).get();
        invertUsed = buffer.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        if (cachedUsedSpec != null && cachedUsedSpec.getValue(SpeciesRequirement.class).isPresent()) {
            return Icon.getIcon(cachedUsedSpec.create().getSprite());
        }

        return super.getAltIcon();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        if (cachedUsedSpec == null) return super.getAltTitle();

        MutableComponent pokemonDefeat = Component.literal("");
        if (count > 1) {
            pokemonDefeat.append(count + "x ");
        }
        pokemonDefeat.append(getPokemon());

        Component usedPokemon = getPokemon(cachedUsedSpec);

        return Component.translatable("ftbquests.task."
                + this.getType().getTypeId().getNamespace() + '.' + this.getType().getTypeId().getPath() + ".title.with",
                pokemonDefeat, usedPokemon);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addTristate("wild", wild, v -> wild = v, Tristate.DEFAULT);
        config.add("usedPokemonSpec", new PokemonConfig(false), this.cachedUsedSpec, v -> this.cachedUsedSpec = v, null);
        //config.addString("usedPokemonSpec", usedPokemonSpec, v -> usedPokemonSpec = v, "");
        config.addBool("invertUsed", invertUsed, v -> invertUsed = v, false);
    }

    public void defeatPokemon(TeamData team, PixelmonEntity pokemon, PixelmonEntity usedPokemon) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert)
        && (wild == Tristate.DEFAULT || (pokemon.getPixelmonWrapper().getParticipant() instanceof WildPixelmonParticipant
                        || pokemon.getPixelmonWrapper().getParticipant() instanceof RaidPixelmonParticipant) == wild.get(true))
        && (this.cachedUsedSpec == null || this.cachedUsedSpec.matches(usedPokemon) != this.invertUsed)) {
            team.addProgress(this, 1L);
        }
    }
}
