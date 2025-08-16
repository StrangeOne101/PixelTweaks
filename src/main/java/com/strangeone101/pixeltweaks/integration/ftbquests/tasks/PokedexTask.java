package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import static com.pixelmonmod.pixelmon.api.pokedex.status.PokedexRegistrationStatus.*;
import com.pixelmonmod.pixelmon.api.pokedex.PokeDexStorageProxy;
import com.pixelmonmod.pixelmon.api.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.api.pokedex.Region;
import com.pixelmonmod.pixelmon.api.pokedex.status.PokedexRegistrationStatus;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PokedexTask extends Task {

    public enum PokedexFilter {
        ALL,
        STARTERS,
        LEGEND,
        MYTHICAL,
        LEGEND_AND_MYTHICAL,
        ULTRA_BEAST,
        PARADOX
    }

    public static final List<Integer> PARADOX = Arrays.asList(new Integer[] {984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 1005, 1006, 1009, 1010, 1020, 1021, 1022, 1023});

    protected transient Set<PokemonBase> filteredPokedex = new HashSet<>();
    protected transient long maxPokedexSize = 0;

    public boolean caught = true;
    public PokedexFilter filter = PokedexFilter.ALL;
    public ResourceKey<Type> type = Type.MYSTERY;
    public ResourceKey<Region> region = null;
    public ResourceKey<Pokedex> pokedex = Pokedex.NATIONAL_DEX;

    public PokedexTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public long getMaxProgress() {
        return maxPokedexSize;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putByte("filter", (byte) filter.ordinal());
        nbt.putBoolean("caught", caught);
        nbt.putString("pokeType", type == null ? "" : type.location().toString());
        nbt.putString("region", region == null ? "": region.location().toString());
        nbt.putString("pokedex", pokedex == null ? "" : pokedex.location().toString());
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        filter = PokedexFilter.values()[nbt.getByte("filter")];
        caught = nbt.getBoolean("caught");
        String typeString = nbt.getString("pokeType");
        type = typeString.isEmpty() ? Type.MYSTERY : ResourceKey.create(PixelmonRegistry.TYPE_REGISTRY, ResourceLocation.parse(typeString));
        String regionString = nbt.getString("region");
        region = regionString.isEmpty() ? null : ResourceKey.create(Region.REGISTRY, ResourceLocation.parse(regionString));
        String pokedexString = nbt.getString("pokedex");
        pokedex = pokedexString.isEmpty() ? Pokedex.NATIONAL_DEX : ResourceKey.create(Pokedex.REGISTRY, ResourceLocation.parse(pokedexString));

        calculateAmount();
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(filter.ordinal());
        buffer.writeBoolean(caught);
        buffer.writeUtf(type == null ? "" : type.location().toString());
        buffer.writeUtf(region == null ? "" : region.location().toString());
        buffer.writeUtf(pokedex == null ? "" : pokedex.location().toString());
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        filter = PokedexFilter.values()[buffer.readByte()];
        caught = buffer.readBoolean();
        String typeString = buffer.readUtf();
        type = typeString.isEmpty() ? Type.MYSTERY : ResourceKey.create(PixelmonRegistry.TYPE_REGISTRY, ResourceLocation.parse(typeString));
        String regionString = buffer.readUtf();
        region = regionString.isEmpty() ? null : ResourceKey.create(Region.REGISTRY, ResourceLocation.parse(regionString));
        String pokedexString = buffer.readUtf();
        pokedex = pokedexString.isEmpty() ? Pokedex.NATIONAL_DEX : ResourceKey.create(Pokedex.REGISTRY, ResourceLocation.parse(pokedexString));

        calculateAmount();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        PokedexFilter[] filterTypes = PokedexFilter.values();

        List<ResourceKey<Type>> types = new ArrayList<>();
        types.addAll(Minecraft.getInstance().level.registryAccess().registry(PixelmonRegistry.TYPE_REGISTRY).get().registryKeySet());
        types.remove(Type.STELLAR);

        List<String> regions = new ArrayList<>();
        regions.add("");
        regions.addAll(Minecraft.getInstance().level.registryAccess().registry(Region.REGISTRY).get().registryKeySet().stream()
                .map(ResourceKey::location)
                .map(ResourceLocation::toString)
                .toList());

        config.addEnum("filter", filter, v -> {
            filter = v;
            calculateAmount();
        }, NameMap.of(PokedexFilter.ALL, filterTypes)
                .nameKey(v -> "pixeltweaks.pokedex_filter_type." + v.name().toLowerCase()).create(), PokedexFilter.ALL);
        config.addBool("caught", caught, v -> caught = v, true);
        config.addEnum("type", type, v -> {
            type = v;
            calculateAmount();
        }, NameMap.of(Type.MYSTERY, types)
                .name(v -> v == Type.MYSTERY ? Component.translatable("pixeltweaks.pokedex_filter.all") : Minecraft.getInstance().level.registryAccess().registry(PixelmonRegistry.TYPE_REGISTRY).get().get(v).name())
                .icon(v -> Icon.getIcon(Minecraft.getInstance().level.registryAccess().registry(PixelmonRegistry.TYPE_REGISTRY).get().get(v).icon().getTexture()))
                .create(), Type.MYSTERY);

        config.addEnum("region", region == null ? "" : region.location().toString(), v -> {
            region = v.equals("") ? null : ResourceKey.create(Region.REGISTRY, ResourceLocation.parse(v));
            calculateAmount();
        }, NameMap.of("", regions)
                .name(v -> v.equals("") ? Component.translatable("pixeltweaks.pokedex_filter.globalregion") : Minecraft.getInstance().level.registryAccess().registry(Region.REGISTRY).get().get(ResourceLocation.parse(v)).name())
                .create(), "");

        config.addEnum("pokedex", pokedex, v -> {
            pokedex = v;
            calculateAmount();
        }, NameMap.of(Pokedex.NATIONAL_DEX, Minecraft.getInstance().level.registryAccess().registry(Pokedex.REGISTRY).get().registryKeySet().toArray(new ResourceKey[0]))
                .name(v -> Component.literal(v.location().getPath()))
                .create(), Pokedex.NATIONAL_DEX);
    }


    protected void calculateAmount() {

        this.filteredPokedex = getPokedex().pokemon().getPokemon().parallelStream().filter(pokemon -> {
            //If the type is set, check if the pokemon has that type. If not, don't bother
            //filtering further
            if (this.type != null && this.type != Type.MYSTERY) {
                if (!pokemon.getForm().hasType(this.type)) {
                    return false; //Return because we are in the forEach method
                }
            }
            //If the region is set, check if the pokemon is in that region. If not, don't bother
            boolean found = false;
            for (Region r : getRegions()) {
                if (r.pokemon().contains(pokemon.getDex())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false; //Return because we are in the forEach method
            }
            if (this.filter == PokedexFilter.STARTERS) {
                for (Region r : getRegions()) {
                    for (PokemonBase starter : r.starters()) {
                        if (starter.getSpecies().equals(pokemon.getSpecies())) {
                            return true;
                        }
                    }
                }
            } else if (this.filter == PokedexFilter.LEGEND) {
                if (pokemon.getForm().getTags().isLegendary(true)) {
                    return true;
                }
            } else if (this.filter == PokedexFilter.MYTHICAL) {
                if (pokemon.getForm().getTags().isMythical()) {
                    return true;
                }
            } else if (this.filter == PokedexFilter.LEGEND_AND_MYTHICAL) {
                if (pokemon.getForm().getTags().isLegendary(false)) {
                    return true;
                }
            } else if (this.filter == PokedexFilter.ULTRA_BEAST) {
                if (pokemon.getForm().getTags().isUltraBeast()) {
                    return true;
                }
            } else if (this.filter == PokedexFilter.PARADOX) {
                if (PARADOX.contains(pokemon.getSpecies().getDex())) {
                    return true;
                }
            } else {
                return true;
            }
            return false;
        }).collect(Collectors.toSet());

        this.maxPokedexSize = filteredPokedex.size();
    }

    public void updatePokedex(TeamData teamData, ServerPlayer player) {
        if (teamData.isCompleted(this) || !teamData.getFile().isServerSide()) return;

        try {
            PokeDexStorageProxy.getStorage(player).thenAccept(pokedexStorage -> {
                if (pokedexStorage == null) return;

                long progress = filteredPokedex.parallelStream()
                        .filter(poke -> {
                            if (this.caught) {
                                return pokedexStorage.getStatus(poke) == CAUGHT;
                            }
                            return pokedexStorage.getStatus(poke) == SEEN || pokedexStorage.getStatus(poke) == CAUGHT;
                        }).count();
                progress = Math.min(progress, this.maxPokedexSize);
                teamData.setProgress(this, (int) progress);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Region[] getRegions() {
        RegistryAccess registry;
        if (Environment.get().getDist().isClient() && Minecraft.getInstance().level != null) {
            registry = Minecraft.getInstance().level.registryAccess();
        } else {
            registry = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        if (this.region == null) {
            return registry.registry(Region.REGISTRY).get().registryKeySet().stream()
                    .map(registry.registry(Region.REGISTRY).get()::get)
                    .toArray(Region[]::new);
        } else {
            return new Region[]{registry.registry(Region.REGISTRY).get().get(this.region)};
        }
    }

    private Pokedex getPokedex() {
        RegistryAccess registry;
        if (Environment.get().getDist().isClient() && Minecraft.getInstance().level != null) {
            registry = Minecraft.getInstance().level.registryAccess();
        } else {
            registry = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        return registry.registry(Pokedex.REGISTRY).get().get(this.pokedex);
    }
}
