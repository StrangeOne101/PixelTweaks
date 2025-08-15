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
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonRewardTypes;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.client.FTBQuestsNetClient;
import dev.ftb.mods.ftbquests.net.DisplayItemRewardToastMessage;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class PokemonReward extends Reward {

    public transient PokemonSpecification cachedSpec;
    public int count = 1;
    public short shinyChance = 4096;

    public PokemonReward(long id, Quest q) {
        super(id, q);
    }

    @Override
    public RewardType getType() {
        return PokemonRewardTypes.POKEMON;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString("spec", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putInt("count", this.count);
        nbt.putShort("shinyChance", this.shinyChance);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        String spec = nbt.getString("spec");
        this.cachedSpec = spec.isEmpty() ? null : PokemonSpecificationProxy.create(spec).get();
        this.count = nbt.getInt("count");
        this.shinyChance = nbt.getShort("shinyChance");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeVarInt(this.count);
        buffer.writeVarInt(this.shinyChance);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        String spec = buffer.readUtf();
        this.cachedSpec = spec.isEmpty() ? null : PokemonSpecificationProxy.create(spec).get();
        this.count = buffer.readVarInt();
        this.shinyChance = (short) buffer.readVarInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.add("spec", new PokemonConfig(true), this.cachedSpec, v -> this.cachedSpec = v, PokemonSpecificationProxy.create("random").get());
        config.addInt("count", this.count, v -> this.count = v, 1, 1, Integer.MAX_VALUE);
        config.addInt("shinyChance", this.shinyChance, v -> this.shinyChance = v.shortValue(), 4096, 0, Short.MAX_VALUE);
    }

    @Override
    public void claim(ServerPlayer player, boolean notify) {
        int c = this.count;

        while (c > 0) {
            Pokemon pokemon = this.cachedSpec.create();

            if (this.shinyChance > 0 && player.getRandom().nextInt(this.shinyChance) == 0
                    && (pokemon.getPalette().is("none") || pokemon.getPalette().is(""))) {
                pokemon.setShiny(true);
            }

            if (notify) {
                NetworkManager.sendToPlayer(player, new DisplayRewardToastMessage(this.id, Component.translatable("ftbquests.reward.pixelmon.pokemon.toast",
                        this.getPokemon()), Icon.getIcon(pokemon.getSprite()), true));
            }
            try {
                if (!StorageProxy.getParty(player).get().add(pokemon)) {
                    PixelTweaks.LOGGER.warn("Failed to add pokemon to player's party! Storage full! Reward ID: " + this.id + ", Pokemon: " + pokemon.getDisplayName());
                }
            } catch (Exception e) {
                PixelTweaks.LOGGER.error("Failed to add pokemon to player's party! Storage could not be gotten!");
                e.printStackTrace();
            }
            c--;

            this.cachedSpec = PokemonSpecificationProxy.create(this.cachedSpec.toString()).get(); //Ensures the next pokemon will be randomized in case some requirements cache stuff. E.g. random species
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        if (cachedSpec != null && cachedSpec.getValue(SpeciesRequirement.class).isPresent() && this.cachedSpec != null && !this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            return Icon.getIcon(cachedSpec.create().getSprite());
        }
        return Icon.getIcon("pixelmon:item/pokeballs/poke_ball");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        return getPokemon();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getButtonText() {
        return this.count > 1 ? this.count + "" : "";
    }

    protected Component getPokemon() {
        MutableComponent pokemon = Component.translatable("pixeltweaks.lang.pokemon");
        if (cachedSpec == null) {
            return pokemon;
        }
        List<Component> componentList = new ArrayList<>();

        if (cachedSpec.getValue(SpeciesRequirement.class).isPresent() && this.cachedSpec != null && !this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            MutableComponent species = Component.translatable("pixelmon." +
                    cachedSpec.getValue(SpeciesRequirement.class).get().getKey().toLowerCase());
            componentList.add(species);
        } else {
            if (cachedSpec.getValue(LegendaryRequirement.class).isPresent()) {
                boolean legend = cachedSpec.getValue(LegendaryRequirement.class).get();
                if (!legend) {
                    MutableComponent legendText = Component.translatable("pixeltweaks.lang.legendary");
                    MutableComponent nonText = Component.translatable("pixeltweaks.lang.not", legendText);

                    componentList.add(nonText);
                } else {
                    MutableComponent legendText = Component.translatable("pixeltweaks.lang.legendary");
                    componentList.add(legendText);
                }
            }
            if (cachedSpec.getValue(UltraBeastRequirement.class).isPresent()) {
                boolean ultra = cachedSpec.getValue(UltraBeastRequirement.class).get();
                if (!ultra) {
                    MutableComponent ultraText = Component.translatable("pixeltweaks.lang.ultrabeast");
                    MutableComponent nonText = Component.translatable("pixeltweaks.lang.not", ultraText);

                    componentList.add(nonText);
                } else {
                    MutableComponent ultraText = Component.translatable("pixeltweaks.lang.ultrabeast");
                    componentList.add(ultraText);
                }
            }
            if (cachedSpec.getValue(GenerationRequirement.class).isPresent()) {
                MutableComponent newType = Component.translatable("pixeltweaks.lang.generation",
                        cachedSpec.getValue(GenerationRequirement.class).get().intValue());
                componentList.add(newType);
            }
            if (cachedSpec.getValue(TypeRequirement.class).isPresent()) {
                MutableComponent type = Component.translatable("type." +
                        cachedSpec.getValue(TypeRequirement.class).get().getSecond().location().toString().toLowerCase());
                MutableComponent newType = Component.translatable("pixeltweaks.lang.type", type);
                componentList.add(newType);
            }

            componentList.add(0, pokemon);
        }
        if (cachedSpec.getValue(FormRequirement.class).isPresent()) {
            MutableComponent form = Component.translatable("pixelmon.generic.form." +
                    cachedSpec.getValue(FormRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (cachedSpec.getValue(PaletteRequirement.class).isPresent()) {
            MutableComponent form = Component.translatable("pixelmon.palette." +
                    cachedSpec.getValue(PaletteRequirement.class).get().toLowerCase());
            componentList.add(form);
        }
        if (cachedSpec.getValue(GenderRequirement.class).isPresent()) {
            MutableComponent gender = Component.translatable(cachedSpec.getValue(GenderRequirement.class).get().getTranslationKey());
            componentList.add(gender);
        }
        if (cachedSpec.getValue(ShinyRequirement.class).isPresent()) {
            MutableComponent shiny = Component.translatable("pixelmon.palette.shiny");
            componentList.add(shiny);
        }
        if (this.cachedSpec != null && this.cachedSpec.toString().split(" ")[0].equalsIgnoreCase("random")) {
            MutableComponent random = Component.translatable("pixeltweaks.lang.random");
            componentList.add(random);
        }

        MutableComponent all = Component.literal("");
        for (int i = componentList.size() - 1; i >= 0; i--) {
            all.append(componentList.get(i));
            if (i != 0) all.append(" ");
        }
        return all;

    }
}
