package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.pokemon.requirement.impl.*;
import com.pixelmonmod.pixelmon.api.pokemon.PokerusStrain;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class PokemonTask extends Task {

    public int count = 1;
    public boolean invert = false;
    public transient PokemonSpecification cachedSpec;
    public PokemonTask(Quest q) {
        super(q);
    }

    @Override
    public long getMaxProgress() {
        return this.count;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("pokemon", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putInt("count", this.count);
        nbt.putBoolean("invert", this.invert);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        String pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec);
        this.count = nbt.getInt("count");
        this.invert = nbt.getBoolean("invert");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeVarInt(this.count);
        buffer.writeBoolean(this.invert);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        String pokemonSpec = buffer.readString();
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec);
        this.count = buffer.readVarInt();
        this.invert = buffer.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.add("pokemon", new PokemonConfig(false), this.cachedSpec, v -> this.cachedSpec = v, null);
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
        config.addBool("invert", this.invert, v -> this.invert = v, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        TranslationTextComponent title = new TranslationTextComponent("ftbquests.task." + this.getType().id.getNamespace() + '.' + this.getType().id.getPath() + ".title");
        title.appendString(" ");
        if (count > 1) {
            title.appendString(count + "x ");
        }
        title.appendSibling(getPokemon());
        return title;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        if (cachedSpec != null && cachedSpec.getValue(SpeciesRequirement.class).isPresent() && !this.cachedSpec.toString().isEmpty() && !this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            return Icon.getIcon(cachedSpec.create().getSprite());
        }

        return super.getAltIcon();
    }

    @OnlyIn(Dist.CLIENT)
    protected ITextComponent getPokemon() {
        return getPokemon(cachedSpec);
    }

    @OnlyIn(Dist.CLIENT)
    public static ITextComponent getPokemon(PokemonSpecification spec) {
        TranslationTextComponent pokemon = new TranslationTextComponent("pixeltweaks.lang.pokemon");
        if (spec == null) {
            return pokemon;
        }
        List<ITextComponent> componentList = new ArrayList<>();
        if (spec.getValue(PokeBallRequirement.class).isPresent()) {
            TranslationTextComponent ball = new TranslationTextComponent("item.pixelmon." +
                    spec.getValue(PokeBallRequirement.class).get().getName().toLowerCase());
            TranslationTextComponent ballText = new TranslationTextComponent("pixeltweaks.lang.ball", ball);
            componentList.add(ballText);
        }
        if (spec.getValue(PokerusRequirement.class).isPresent()) {
            boolean pokerus = spec.getValue(PokerusRequirement.class).get() != PokerusStrain.UNINFECTED;
            if (pokerus) {
                TranslationTextComponent pokerusText = new TranslationTextComponent("pixeltweaks.lang.pokerus");
                componentList.add(pokerusText);
            }
        }

        if (spec.getValue(SpeciesRequirement.class).isPresent()) {
            TranslationTextComponent species = spec.getValue(SpeciesRequirement.class).get().getValueUnsafe().getNameTranslation();
            componentList.add(species);
        } else {
            if (spec.getValue(LegendaryRequirement.class).isPresent()) {
                boolean legend = spec.getValue(LegendaryRequirement.class).get();
                if (!legend) {
                    TranslationTextComponent legendText = new TranslationTextComponent("pixeltweaks.lang.legendary");
                    TranslationTextComponent nonText = new TranslationTextComponent("pixeltweaks.lang.not", legendText);

                    componentList.add(nonText);
                } else {
                    TranslationTextComponent legendText = new TranslationTextComponent("pixeltweaks.lang.legendary");
                    componentList.add(legendText);
                }
            }
            if (spec.getValue(UltraBeastRequirement.class).isPresent()) {
                boolean ultra = spec.getValue(UltraBeastRequirement.class).get();
                if (!ultra) {
                    TranslationTextComponent ultraText = new TranslationTextComponent("pixeltweaks.lang.ultrabeast");
                    TranslationTextComponent nonText = new TranslationTextComponent("pixeltweaks.lang.not", ultraText);

                    componentList.add(nonText);
                } else {
                    TranslationTextComponent ultraText = new TranslationTextComponent("pixeltweaks.lang.ultrabeast");
                    componentList.add(ultraText);
                }
            }
            if (spec.getValue(GenerationRequirement.class).isPresent()) {
                TranslationTextComponent newType = new TranslationTextComponent("pixeltweaks.lang.generation",
                        spec.getValue(GenerationRequirement.class).get());
                componentList.add(newType);
            }
            if (spec.getValue(TypeRequirement.class).isPresent()) {
                TranslationTextComponent type = new TranslationTextComponent("type." +
                        spec.getValue(TypeRequirement.class).get().getSecond().name().toLowerCase());
                TranslationTextComponent newType = new TranslationTextComponent("pixeltweaks.lang.type", type);
                componentList.add(newType);
            }

            componentList.add(0, pokemon);
        }
        if (spec.getValue(FormRequirement.class).isPresent()) {
            String form = spec.getValue(FormRequirement.class).get().toLowerCase();

            TranslationTextComponent formComponent = new TranslationTextComponent("pixelmon.generic.form." + form);

            if (spec.getValue(SpeciesRequirement.class).isPresent()) {
                Species species = spec.getValue(SpeciesRequirement.class).get().getValueUnsafe();
                if (I18n.hasKey("pixelmon." + species.getName().toLowerCase() + ".form." + form)) {
                    formComponent = new TranslationTextComponent("pixelmon." + species.getName().toLowerCase() + ".form." + form);
                }
            }

            componentList.add(formComponent);
        }
        if (spec.getValue(PaletteRequirement.class).isPresent()) {
            TranslationTextComponent form = new TranslationTextComponent("pixelmon.palette." +
                    spec.getValue(PaletteRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (spec.getValue(GenderRequirement.class).isPresent()) {
            TranslationTextComponent gender = new TranslationTextComponent(spec.getValue(GenderRequirement.class).get().getTranslationKey());
            componentList.add(gender);
        }
        if (spec.getValue(ShinyRequirement.class).isPresent()) {
            boolean bool = spec.getRequirement(ShinyRequirement.class).get().getValue();
            TranslationTextComponent shiny = new TranslationTextComponent("pixelmon.palette.shiny");
            if (!bool) shiny = new TranslationTextComponent("pixeltweaks.lang.not", new TranslationTextComponent("pixelmon.palette.shiny"));
            componentList.add(shiny);
        }
        if (spec.getValue(EggRequirement.class).isPresent()) {
            boolean bool = spec.getRequirement(EggRequirement.class).get().getValue();
            TranslationTextComponent egg = new TranslationTextComponent("pixelmon.egg");
            if (!bool) egg = new TranslationTextComponent("pixeltweaks.lang.not", new TranslationTextComponent("pixelmon.egg"));
            componentList.add(egg);
        }

        StringTextComponent all = new StringTextComponent("");
        for (int i = componentList.size() - 1; i >= 0; i--) {
            all.appendSibling(componentList.get(i));
            if (i != 0) all.appendString(" ");
        }
        return all;

    }
}
