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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DefeatTask extends PokemonTask {

    public Tristate wild = Tristate.DEFAULT;
    public transient PokemonSpecification cachedUsedSpec;
    public boolean invertUsed;

    public DefeatTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.DEFEAT_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        wild.write(nbt, "wild");
        nbt.putString("usedPokemonSpec", cachedUsedSpec == null ? "" : cachedUsedSpec.toString());
        nbt.putBoolean("invertUsed", invertUsed);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        wild = Tristate.read(nbt, "wild");
        String usedPokemonSpec = nbt.getString("usedPokemonSpec");
        cachedUsedSpec = usedPokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(usedPokemonSpec);
        invertUsed = nbt.getBoolean("invertUsed");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        wild.write(buffer);
        buffer.writeString(cachedUsedSpec == null ? "" : cachedUsedSpec.toString());
        buffer.writeBoolean(invertUsed);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        wild = Tristate.read(buffer);
        String usedPokemonSpec = buffer.readString();
        cachedUsedSpec = usedPokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(usedPokemonSpec);
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
    public ITextComponent getAltTitle() {
        if (cachedUsedSpec == null) return super.getAltTitle();

        StringTextComponent pokemonDefeat = new StringTextComponent("");
        if (count > 1) {
            pokemonDefeat.appendString(count + "x ");
        }
        pokemonDefeat.appendSibling(getPokemon());

        ITextComponent usedPokemon = getPokemon(cachedUsedSpec);

        return new TranslationTextComponent("ftbquests.task."
                + this.getType().id.getNamespace() + '.' + this.getType().id.getPath() + ".title.with",
                pokemonDefeat, usedPokemon);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
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
