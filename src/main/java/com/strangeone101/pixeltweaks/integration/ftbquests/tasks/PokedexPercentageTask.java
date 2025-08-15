package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.api.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.api.pokedex.Region;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PokedexPercentageTask extends PokedexTask {

    public double percentage = 50.0;
    public transient int count;

    public PokedexPercentageTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.POKEDEX_PERCENTAGE;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putFloat("percentage", (float) this.percentage);
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.percentage = nbt.getFloat("percentage");
        doMath();
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeFloat((float) this.percentage);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.percentage = buffer.readFloat();
        doMath();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addDouble("percentage", this.percentage, v -> {
            this.percentage = v;
            calculateAmount();
        }, 0.0, 0.01, 100.00);
    }

    @Override
    public String formatProgress(TeamData teamData, long progress) {
        long l = Math.min(progress, this.maxPokedexSize);
        double p = ((double) l / (double)this.maxPokedexSize) * 100.0;
        return StringUtils.formatDouble(p) + "%";
    }

    @Override
    public String formatMaxProgress() {
        return StringUtils.formatDouble(percentage) + "%";
    }

    @Override
    public void calculateAmount() {
        super.calculateAmount();
        doMath();
    }

    private void doMath() {
        this.count = (int) Math.ceil((this.percentage / 100.0) * ((double)this.maxPokedexSize));
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

        Component pokedex = Component.translatable("pixeltweaks.lang.pokedex");

        if (this.pokedex != Pokedex.NATIONAL_DEX) {
            pokedex = Minecraft.getInstance().level.registryAccess().registry(Pokedex.REGISTRY).get().get(this.pokedex).name();
        }

        region.append(pokedex);

        MutableComponent title = Component.translatable("ftbquests.task." + this.getType().getTypeId().getNamespace() + '.' + this.getType().getTypeId().getPath() + ".title",
                catchOrRegister, this.percentage, type, region);
        return title;
    }

}
