package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.api.pokedex.Region;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokedexAmountTask extends PokedexTask {

    public int count = 1;

    public PokedexAmountTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDEX_AMOUNT;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putInt("count", this.count);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.count = nbt.getInt("count");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarInt(this.count);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.count = buffer.readVarInt();
    }

    @Override
    public void calculateAmount() {
        super.calculateAmount();
        this.count = Math.min(this.count, (int) this.maxPokedexSize);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addInt("count", this.count, v -> this.count = v, 10, 1, (int) this.maxPokedexSize);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        MutableComponent catchOrRegister = Component.translatable("pixeltweaks.lang." + (this.caught ? "catch" : "seen"));

        MutableComponent type = Component.literal("");
        if (this.type != Type.MYSTERY) {
            type.append(Minecraft.getInstance().level.registryAccess().registry(PixelmonRegistry.TYPE_REGISTRY).get().get(this.type).name());
            type.append(" ");
        }
        if (this.filter != PokedexFilter.ALL) {
            type.append(Component.translatable("pixeltweaks.lang." + this.filter.name().toLowerCase()));
            type.append(" ");
        }

        MutableComponent region = Component.literal("");
        if (this.region != null) {
            region.append(Minecraft.getInstance().level.registryAccess().registry(Region.REGISTRY).get().get(this.region).name());
            region.append(" ");
        }

        Component pokedex = Component.translatable("pixeltweaks.pokedex." + this.pokedex.location().getPath().toLowerCase());

        region.append(pokedex);

        MutableComponent title = Component.translatable("ftbquests.task." + this.getType().getTypeId().getNamespace() + '.' + this.getType().getTypeId().getPath() + ".title",
                catchOrRegister, this.count, type, region);
        return title;
    }
}
