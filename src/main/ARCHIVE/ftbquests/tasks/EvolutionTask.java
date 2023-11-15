package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.types.InteractEvolution;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EvolutionTask extends PokemonTask {

    public enum EvolutionType {
        ANY, LEVELING, TRADING, ITEM, TICKING;
    }

    public EvolutionType evoType = EvolutionType.ANY;
    public ItemStack item = ItemStack.EMPTY;

    public EvolutionTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.EVOLVE_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putByte("evolution_type", (byte) evoType.ordinal());
        if (evoType == EvolutionType.ITEM) {
            NBTUtils.write(nbt, "item", item);
        }
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        evoType = EvolutionType.values()[nbt.getByte("evolution_type")];
        if (evoType == EvolutionType.ITEM) {
            item = NBTUtils.read(nbt, "item");
        }
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(evoType.ordinal());
        if (evoType == EvolutionType.ITEM) {
            buffer.writeItemStack(item);
        }
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        evoType = EvolutionType.values()[buffer.readByte()];
        if (evoType == EvolutionType.ITEM) {
            item = buffer.readItemStack();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addEnum("evolution_type", evoType, v -> evoType = v, NameMap.of(EvolutionType.ANY, EvolutionType.values())
                .nameKey(s -> "pixeltweaks.evolution_type." + s.toString().toLowerCase())
                .icon(type -> {
                    String icon = "pixeltweaks:textures/gui/types/unknown.png";
                    switch (type) {
                        case ITEM:
                            icon = "pixelmon:items/evolutionstones/firestone";
                            break;
                        case TRADING:
                            icon = "pixelmon:items/linking_cord";
                            break;
                        case LEVELING:
                            icon = "pixelmon:items/healingitems/rarecandy";
                            break;
                        case TICKING:
                            icon = "pixelmon:textures/items/healingitems/repel.png";
                            break;
                    }
                    return Icon.getIcon(new ResourceLocation(icon));
                }).create(), EvolutionType.ANY);
        config.addItemStack("item", item, v -> item = v, ItemStack.EMPTY, true, true);
    }

    public void evolvePokemon(TeamData team, ServerPlayerEntity player, Pokemon pokemon, Evolution evolution) {
        if (!team.isCompleted(this) && (this.cachedSpec == null || this.cachedSpec.matches(pokemon) != this.invert) && matches(evolution)) {
            team.addProgress(this, 1L);
        }
    }

    private boolean matches(Evolution evolution) {
        String evoType = evolution.evoType;
        if (this.evoType == EvolutionType.ANY || evoType == null) return true;
        switch (evoType) {
            case "leveling":
                return this.evoType == EvolutionType.LEVELING;
            case "trade":
                return this.evoType == EvolutionType.TRADING;
            case "interact":
                return this.evoType == EvolutionType.ITEM && evolution instanceof InteractEvolution
                        && (item.isEmpty() || ((InteractEvolution) evolution).item.getItemStack().isItemEqualIgnoreDurability(item));
            case "ticking":
                return this.evoType == EvolutionType.TICKING;
            default:
                return false;
        }

    }
}
