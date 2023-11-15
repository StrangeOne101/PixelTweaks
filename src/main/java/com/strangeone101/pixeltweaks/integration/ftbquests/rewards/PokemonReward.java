package com.strangeone101.pixeltweaks.integration.ftbquests.rewards;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.pokemon.requirement.impl.FormRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.GenderRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.GenerationRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.LegendaryRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PaletteRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.ShinyRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.TypeRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.UltraBeastRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class PokemonReward extends Reward {

    public transient PokemonSpecification cachedSpec;
    public int count = 1;
    public short shinyChance = 4096;

    public PokemonReward(Quest q) {
        super(q);
    }

    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("spec", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putInt("count", this.count);
        nbt.putShort("shinyChance", this.shinyChance);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        String spec = nbt.getString("spec");
        this.cachedSpec = spec.isEmpty() ? null : PokemonSpecificationProxy.create(spec);
        this.count = nbt.getInt("count");
        this.shinyChance = nbt.getShort("shinyChance");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeVarInt(this.count);
        buffer.writeVarInt(this.shinyChance);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        String spec = buffer.readString();
        this.cachedSpec = spec.isEmpty() ? null : PokemonSpecificationProxy.create(spec);
        this.count = buffer.readVarInt();
        this.shinyChance = (short) buffer.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.add("spec", new PokemonConfig(true), this.cachedSpec, v -> this.cachedSpec = v, PokemonSpecificationProxy.create("random"));
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
        config.addInt("shinyChance", this.shinyChance, v -> this.shinyChance = v.shortValue(), 4096, 0, Short.MAX_VALUE);
    }

    @Override
    public void claim(ServerPlayerEntity player, boolean notify) {
        int c = this.count;

        while (c > 0) {
            Pokemon pokemon = this.cachedSpec.create();

            if (this.shinyChance > 0 && player.getRNG().nextInt(this.shinyChance) == 0
                    && (pokemon.getPalette().is("none") || pokemon.getPalette().is(""))) {
                pokemon.setShiny(true);
            }

            if (notify) {
                new DisplayRewardToastMessage(this.id, new TranslationTextComponent("ftbquests.reward.pixelmon.pokemon.toast",
                        this.getPokemon()), Icon.getIcon(pokemon.getSprite())).sendTo(player);
            }
            if (!StorageProxy.getParty(player).add(pokemon)) {
                PixelTweaks.LOGGER.warn("Failed to add pokemon to player's party! Storage full! Reward ID: " + this.id + ", Pokemon: " + pokemon.getDisplayName());
            }
            c--;

            this.cachedSpec = PokemonSpecificationProxy.create(this.cachedSpec.toString()); //Ensures the next pokemon will be randomized in case some requirements cache stuff. E.g. random species
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        if (cachedSpec != null && cachedSpec.getValue(SpeciesRequirement.class).isPresent() && this.cachedSpec != null && !this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            return Icon.getIcon(cachedSpec.create().getSprite());
        }
        return Icon.getIcon("pixelmon:items/pokeballs/poke_ball");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        return getPokemon();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? this.count + "" : "";
    }

    protected ITextComponent getPokemon() {
        TranslationTextComponent pokemon = new TranslationTextComponent("pixeltweaks.lang.pokemon");
        if (cachedSpec == null) {
            return pokemon;
        }
        List<ITextComponent> componentList = new ArrayList<>();

        if (cachedSpec.getValue(SpeciesRequirement.class).isPresent() && this.cachedSpec != null && !this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
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
        if (this.cachedSpec != null && this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            TranslationTextComponent random = new TranslationTextComponent("pixeltweaks.lang.random");
            componentList.add(random);
        }

        StringTextComponent all = new StringTextComponent("");
        for (int i = componentList.size() - 1; i >= 0; i--) {
            all.appendSibling(componentList.get(i));
            if (i != 0) all.appendString(" ");
        }
        return all;

    }
}
