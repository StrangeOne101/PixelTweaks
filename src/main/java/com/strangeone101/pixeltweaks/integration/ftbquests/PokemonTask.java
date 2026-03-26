package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.pokemon.requirement.impl.EggRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.FormRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.GenderRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.GenerationRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.LegendaryRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PaletteRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PokeBallRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PokerusRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.ShinyRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.TypeRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.UltraBeastRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.PokerusStrain;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class PokemonTask extends Task {

    public int count = 1;
    public boolean invert = false;
    public transient PokemonSpecification cachedSpec;
    public PokemonTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public long getMaxProgress() {
        return this.count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString("pokemon", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putInt("count", this.count);
        nbt.putBoolean("invert", this.invert);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        String pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec).get();
        this.count = nbt.getInt("count");
        this.invert = nbt.getBoolean("invert");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeVarInt(this.count);
        buffer.writeBoolean(this.invert);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        String pokemonSpec = buffer.readUtf();
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec).get();
        this.count = buffer.readVarInt();
        this.invert = buffer.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.add("pokemon", new PokemonConfig(false), this.cachedSpec, v -> this.cachedSpec = v, null);
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
        config.addBool("invert", this.invert, v -> this.invert = v, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        MutableComponent title = Component.translatable("ftbquests.task." + this.getType().getTypeId().getNamespace() + '.' + this.getType().getTypeId().getPath() + ".title");
        title.append(" ");
        if (count > 1) {
            title.append(count + "x ");
        }
        title.append(getPokemon());
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
    protected Component getPokemon() {
        return getPokemon(cachedSpec);
    }

    @OnlyIn(Dist.CLIENT)
    public static Component getPokemon(PokemonSpecification spec) {
        MutableComponent pokemon = Component.translatable("pixeltweaks.lang.pokemon");
        if (spec == null) {
            return pokemon;
        }
        List<Component> componentList = new ArrayList<>();

        if (spec.getValue(PokerusRequirement.class).isPresent()) {
            boolean pokerus = spec.getValue(PokerusRequirement.class).get() != PokerusStrain.UNINFECTED;
            if (pokerus) {
                MutableComponent pokerusText = Component.translatable("pixeltweaks.lang.pokerus");
                componentList.add(pokerusText);
            }
        }

        if (spec.getValue(SpeciesRequirement.class).isPresent()) {
            MutableComponent species = spec.getValue(SpeciesRequirement.class).get().getValueUnsafe().getNameTranslation();
            componentList.add(species);
        } else {
            if (spec.getValue(LegendaryRequirement.class).isPresent()) {
                boolean legend = spec.getValue(LegendaryRequirement.class).get();
                if (!legend) {
                    MutableComponent legendText = Component.translatable("pixeltweaks.lang.legendary");
                    MutableComponent nonText = Component.translatable("pixeltweaks.lang.not", legendText);

                    componentList.add(nonText);
                } else {
                    MutableComponent legendText = Component.translatable("pixeltweaks.lang.legendary");
                    componentList.add(legendText);
                }
            }
            if (spec.getValue(UltraBeastRequirement.class).isPresent()) {
                boolean ultra = spec.getValue(UltraBeastRequirement.class).get();
                if (!ultra) {
                    MutableComponent ultraText = Component.translatable("pixeltweaks.lang.ultrabeast");
                    MutableComponent nonText = Component.translatable("pixeltweaks.lang.not", ultraText);

                    componentList.add(nonText);
                } else {
                    MutableComponent ultraText = Component.translatable("pixeltweaks.lang.ultrabeast");
                    componentList.add(ultraText);
                }
            }
            if (spec.getValue(GenerationRequirement.class).isPresent()) {
                MutableComponent newType = Component.translatable("pixeltweaks.lang.generation",
                        spec.getValue(GenerationRequirement.class).get());
                componentList.add(newType);
            }
            if (spec.getValue(TypeRequirement.class).isPresent()) {
                MutableComponent type = Component.translatable("type." +
                        spec.getValue(TypeRequirement.class).get().getSecond().location().getPath().toString().toLowerCase());
                MutableComponent newType = Component.translatable("pixeltweaks.lang.type", type);
                componentList.add(newType);
            }

            componentList.add(0, pokemon);
        }
        if (spec.getValue(FormRequirement.class).isPresent()) {
            String form = spec.getValue(FormRequirement.class).get().toLowerCase();

            MutableComponent formComponent = Component.translatable("pixelmon.generic.form." + form);

            if (spec.getValue(SpeciesRequirement.class).isPresent()) {
                Species species = spec.getValue(SpeciesRequirement.class).get().getValueUnsafe();
                if (I18n.exists("pixelmon." + species.getName().toLowerCase() + ".form." + form)) {
                    formComponent = Component.translatable("pixelmon." + species.getName().toLowerCase() + ".form." + form);
                }
            }

            componentList.add(formComponent);
        }
        if (spec.getValue(PaletteRequirement.class).isPresent()) {
            MutableComponent form = Component.translatable("pixelmon.palette." +
                    spec.getValue(PaletteRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (spec.getValue(GenderRequirement.class).isPresent()) {
            MutableComponent gender = Component.translatable(spec.getValue(GenderRequirement.class).get().getTranslationKey());
            componentList.add(gender);
        }
        if (spec.getValue(ShinyRequirement.class).isPresent()) {
            boolean bool = spec.getRequirement(ShinyRequirement.class).get().getValue();
            MutableComponent shiny = Component.translatable("pixelmon.palette.shiny");
            if (!bool) shiny = Component.translatable("pixeltweaks.lang.not", Component.translatable("pixelmon.palette.shiny"));
            componentList.add(shiny);
        }
        if (spec.getValue(EggRequirement.class).isPresent()) {
            boolean bool = spec.getRequirement(EggRequirement.class).get().getValue();
            MutableComponent egg = Component.translatable("pixelmon.egg");
            if (!bool) egg = Component.translatable("pixeltweaks.lang.not", Component.translatable("pixelmon.egg"));
            componentList.add(egg);
        }

        if (spec.getValue(PokeBallRequirement.class).isPresent()) {
            MutableComponent ball = Component.translatable("item.pixelmon." +
                    spec.getValue(PokeBallRequirement.class).get().getName().toLowerCase());
            MutableComponent ballText = Component.translatable("pixeltweaks.lang.ball", ball);
            componentList.add(ballText);
        }

        MutableComponent all = Component.literal("");
        for (int i = componentList.size() - 1; i >= 0; i--) {
            all.append(componentList.get(i));
            if (i != 0) all.append(" ");
        }
        return all;

    }

    @Override
    public void addMouseOverText(TooltipList list, TeamData teamData) {

        List<Component> clonedList = new ArrayList<>();
        if (list.getLines().isEmpty()) {
            super.addMouseOverText(list, teamData);
            return;
        }
        clonedList.add(list.getLines().get(0));
        for (int i = 1; i < list.getLines().size(); i++) {
            Component line = list.getLines().get(i);
            Style style = line.getStyle();
            if (line.getContents() instanceof PlainTextContents contents) {
                String text = contents.text();
                for (String s : text.split("\n")) {
                    clonedList.add(Component.literal(s).withStyle(style));
                }
            } else if (line.getContents() instanceof TranslatableContents contents) {
                String content = I18n.get(contents.getKey());
                for (String s : content.split("\n")) {
                    clonedList.add(Component.literal(s).withStyle(style));
                }
            } else {
                clonedList.add(line);
            }
        }
        list.getLines().clear();
        list.getLines().addAll(clonedList);

        super.addMouseOverText(list, teamData);

    }
}
