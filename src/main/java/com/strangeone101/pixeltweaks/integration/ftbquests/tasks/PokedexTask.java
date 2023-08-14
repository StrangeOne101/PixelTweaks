package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.HashSet;
import java.util.Set;

public abstract class PokedexTask extends Task {

    public enum PokedexFilter {
        ALL,
        GENERATION,
        TYPE,
        LEGEND,
        MYTHICAL,
        LEGEND_AND_MYTHICAL,
        ULTRA_BEAST,
    }

    protected int maxPokedexSize = PixelmonSpecies.getAll().size();
    protected transient Set<Integer> filteredPokedex = new HashSet<>();

    public boolean caught = true;
    public PokedexFilter filter = PokedexFilter.ALL;
    public Element type = Element.NORMAL;
    public byte genMinFilter = 1;
    public byte genMaxFilter = 9;

    public PokedexTask(Quest q) {
        super(q);
    }

    @Override
    public long getMaxProgress() {
        return maxPokedexSize;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putByte("filter", (byte) filter.ordinal());
        nbt.putBoolean("caught", caught);
        if (filter == PokedexFilter.TYPE) {
            nbt.putByte("type", (byte) type.ordinal());
        } else if (filter == PokedexFilter.GENERATION) {
            nbt.putByte("genMin", genMinFilter);
            nbt.putByte("genMax", genMaxFilter);
        }
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        filter = PokedexFilter.values()[nbt.getByte("filter")];
        caught = nbt.getBoolean("caught");
        if (filter == PokedexFilter.TYPE) {
            type = Element.values()[nbt.getByte("type")];
        } else if (filter == PokedexFilter.GENERATION) {
            genMinFilter = nbt.getByte("genMin");
            genMaxFilter = nbt.getByte("genMax");
        }
        calculateAmount();
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(filter.ordinal());
        buffer.writeBoolean(caught);
        if (filter == PokedexFilter.TYPE) {
            buffer.writeByte(type.ordinal());
        } else if (filter == PokedexFilter.GENERATION) {
            buffer.writeByte(genMinFilter);
            buffer.writeByte(genMaxFilter);
        }
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        filter = PokedexFilter.values()[buffer.readByte()];
        caught = buffer.readBoolean();
        if (filter == PokedexFilter.TYPE) {
            type = Element.values()[buffer.readByte()];
        } else if (filter == PokedexFilter.GENERATION) {
            genMinFilter = buffer.readByte();
            genMaxFilter = buffer.readByte();
        }

        calculateAmount();
    }

    @Override
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addEnum("filter", filter, v -> {
            filter = v;
            calculateAmount();
        }, NameMap.of(PokedexFilter.ALL, PokedexFilter.values())
                .nameKey(v -> "pixeltweaks.pokedex_filter_type." + v.name().toLowerCase()).create(), PokedexFilter.ALL);
        config.addBool("caught", caught, v -> caught = v, true);
        config.addEnum("type", type, v -> {
            type = v;
            calculateAmount();
        }, NameMap.of(Element.NORMAL, Element.getElements().toArray(new Element[0]))
                .nameKey(v -> "type." + v.name().toLowerCase()).create(), Element.NORMAL);

        config.addInt("genMin", genMinFilter, v -> {
            genMinFilter = v.byteValue();
            calculateAmount();
        }, (byte) 1, (byte) 1, (byte) 9);
        config.addInt("genMax", genMaxFilter, v -> {
            genMaxFilter = v.byteValue();
            calculateAmount();
        }, (byte) 9, (byte) 1, (byte) 9);
    }

    protected void calculateAmount() {
        Set<Integer> all = new HashSet<>();
        if (this.filter == PokedexFilter.TYPE) {
            PixelmonSpecies.getAll().parallelStream().forEach(species -> {
                Stats form = species.getDefaultForm();
                if (form.getTypes().contains(this.type)) {
                    all.add(species.getDex());
                }
            });
        } else if (this.filter == PokedexFilter.GENERATION) {
            for (int current = this.genMinFilter; current <= this.genMaxFilter; current++) {
                all.addAll(PixelmonSpecies.getGenerationDex(current));
            }
        } else if (this.filter == PokedexFilter.LEGEND) {
            all.addAll(PixelmonSpecies.getLegendaries(true));
        } else if (this.filter == PokedexFilter.MYTHICAL) {
            all.addAll(PixelmonSpecies.getMythicals());
        } else if (this.filter == PokedexFilter.LEGEND_AND_MYTHICAL) {
            all.addAll(PixelmonSpecies.getLegendaries(false));
        } else if (this.filter == PokedexFilter.ULTRA_BEAST) {
            all.addAll(PixelmonSpecies.getUltraBeasts());
        } else {
            for (int gen : PixelmonSpecies.getGenerations()) {
                all.addAll(PixelmonSpecies.getGenerationDex(gen));
            }
        }

        this.filteredPokedex = all;
        this.maxPokedexSize = all.size();
    }

    public void updatePokedex(TeamData teamData, ServerPlayerEntity player) {
        if (teamData.isCompleted(this)) return;

        int ordinalToCheck = this.caught ? 2 : 1;
        long time = System.currentTimeMillis();
        int progress = (int) StorageProxy.getParty(player).playerPokedex.getSeenMap().entrySet().parallelStream()
                .filter(entry -> this.filteredPokedex.contains(entry.getKey()) && entry.getValue().ordinal() >= ordinalToCheck
                ).count();
        PixelTweaks.LOGGER.debug("Took " + (System.currentTimeMillis() - time) + "ms to calculate pokedex progress for " + player.getDisplayName().getString() + " for task " + this.toString());

        teamData.setProgress(this, progress);
    }
}
