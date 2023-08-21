package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
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
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.gui.CustomToast;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.client.Minecraft;
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
    public String pokemonSpec = "";
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
        nbt.putString("pokemon", this.pokemonSpec);
        nbt.putInt("count", this.count);
        nbt.putBoolean("invert", this.invert);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        this.pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
        this.count = nbt.getInt("count");
        this.invert = nbt.getBoolean("invert");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.pokemonSpec);
        buffer.writeVarInt(this.count);
        buffer.writeBoolean(this.invert);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        this.pokemonSpec = buffer.readString();
        this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
        this.count = buffer.readVarInt();
        this.invert = buffer.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.addString("pokemon", this.pokemonSpec, (v) -> {
            this.pokemonSpec = v;
            try {
                this.cachedSpec = PokemonSpecificationProxy.create(this.pokemonSpec);
            } catch (Exception e) {
                CustomToast toast = new CustomToast(new TranslationTextComponent("pixeltweaks.errors.invalid_spec.title"),
                        Icon.getIcon("minecraft:item/barrier"), new TranslationTextComponent("pixeltweaks.errors.invalid_spec.desc"));
                this.cachedSpec = null;
                Minecraft.getInstance().getToastGui().add(toast);
            }

        }, "");
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
        if (cachedSpec.getValue(SpeciesRequirement.class).isPresent()) {
            return Icon.getIcon(cachedSpec.create().getSprite());
        }

        return super.getAltIcon();
    }

    protected ITextComponent getPokemon() {
        TranslationTextComponent pokemon = new TranslationTextComponent("pixeltweaks.lang.pokemon");
        if (cachedSpec == null) {
            return pokemon;
        }
        List<ITextComponent> componentList = new ArrayList<>();
        if (cachedSpec.getValue(PokeBallRequirement.class).isPresent()) {
            TranslationTextComponent ball = new TranslationTextComponent("item.pixelmon." +
                    cachedSpec.getValue(PokeBallRequirement.class).get().getName().toLowerCase());
            TranslationTextComponent ballText = new TranslationTextComponent("pixeltweaks.lang.ball", ball);
            componentList.add(ballText);
        }
        if (cachedSpec.getValue(PokerusRequirement.class).isPresent()) {
            boolean pokerus = cachedSpec.getValue(PokerusRequirement.class).get() != PokerusStrain.UNINFECTED;
            if (pokerus) {
                TranslationTextComponent pokerusText = new TranslationTextComponent("pixeltweaks.lang.pokerus");
                componentList.add(pokerusText);
            }
        }

        if (cachedSpec.getValue(SpeciesRequirement.class).isPresent()) {
            TranslationTextComponent species = new TranslationTextComponent("pixelmon." +
                    cachedSpec.getValue(SpeciesRequirement.class).get().getKey().toLowerCase());
            componentList.add(species);
        } else {
            if (cachedSpec.getValue(LegendaryRequirement.class).isPresent()) {
                boolean legend = cachedSpec.getValue(LegendaryRequirement.class).get();
                if (!legend) {
                    TranslationTextComponent legendText = new TranslationTextComponent("pixeltweaks.lang.legendary");
                    TranslationTextComponent nonText = new TranslationTextComponent("pixeltweaks.lang.not", legendText);

                    componentList.add(nonText);
                } else {
                    TranslationTextComponent legendText = new TranslationTextComponent("pixeltweaks.lang.legendary");
                    componentList.add(legendText);
                }
            }
            if (cachedSpec.getValue(UltraBeastRequirement.class).isPresent()) {
                boolean ultra = cachedSpec.getValue(UltraBeastRequirement.class).get();
                if (!ultra) {
                    TranslationTextComponent ultraText = new TranslationTextComponent("pixeltweaks.lang.ultrabeast");
                    TranslationTextComponent nonText = new TranslationTextComponent("pixeltweaks.lang.not", ultraText);

                    componentList.add(nonText);
                } else {
                    TranslationTextComponent ultraText = new TranslationTextComponent("pixeltweaks.lang.ultrabeast");
                    componentList.add(ultraText);
                }
            }
            if (cachedSpec.getValue(GenerationRequirement.class).isPresent()) {
                TranslationTextComponent newType = new TranslationTextComponent("pixeltweaks.lang.generation",
                        cachedSpec.getValue(GenerationRequirement.class).get().intValue());
                componentList.add(newType);
            }
            if (cachedSpec.getValue(TypeRequirement.class).isPresent()) {
                TranslationTextComponent type = new TranslationTextComponent("type." +
                        cachedSpec.getValue(TypeRequirement.class).get().getSecond().name().toLowerCase());
                TranslationTextComponent newType = new TranslationTextComponent("pixeltweaks.lang.type", type);
                componentList.add(newType);
            }

            componentList.add(0, pokemon);
        }
        if (cachedSpec.getValue(FormRequirement.class).isPresent()) {
            TranslationTextComponent form = new TranslationTextComponent("pixelmon.generic.form." +
                    cachedSpec.getValue(FormRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (cachedSpec.getValue(PaletteRequirement.class).isPresent()) {
            TranslationTextComponent form = new TranslationTextComponent("pixelmon.palette." +
                    cachedSpec.getValue(PaletteRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (cachedSpec.getValue(GenderRequirement.class).isPresent()) {
            TranslationTextComponent gender = new TranslationTextComponent(cachedSpec.getValue(GenderRequirement.class).get().getTranslationKey());
            componentList.add(gender);
        }
        if (cachedSpec.getValue(ShinyRequirement.class).isPresent()) {
            TranslationTextComponent shiny = new TranslationTextComponent("pixelmon.palette.shiny");
            componentList.add(shiny);
        }

        StringTextComponent all = new StringTextComponent("");
        for (int i = componentList.size() - 1; i >= 0; i--) {
            all.appendSibling(componentList.get(i));
            all.appendString(" ");
        }
        return all;

    }
}
