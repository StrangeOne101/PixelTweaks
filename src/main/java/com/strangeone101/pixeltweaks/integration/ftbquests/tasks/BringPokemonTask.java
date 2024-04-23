package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.LocationTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BringPokemonTask extends LocationTask {

    public boolean invert = false;
    public transient PokemonSpecification cachedSpec;
    public int slot = 0;

    public BringPokemonTask(Quest quest) {
        super(quest);
    }

    public TaskType getType() {
        return PokemonTaskTypes.BRING_POKEMON;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putString("pokemon", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putByte("slot", (byte) this.slot);
        nbt.putBoolean("invert", this.invert);
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        String pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec);
        this.invert = nbt.getBoolean("invert");
        this.slot = nbt.getByte("slot");
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeString(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeBoolean(this.invert);
        buffer.writeByte(this.slot);
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        String pokemonSpec = buffer.readString();
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec);
        this.invert = buffer.readBoolean();
        this.slot = buffer.readByte();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);

        config.add("pokemon", new PokemonConfig(false), this.cachedSpec, v -> this.cachedSpec = v, null);
        config.addBool("invert", this.invert, v -> this.invert = v, false);
        config.addEnum("slot", this.slot, v -> this.slot = v, NameMap.of(0, new Integer[]{0, 1, 2, 3, 4, 5, 6})
                .nameKey(v -> "pixeltweaks.partyslot." + v).create(), 0);
        config.addInt("x", this.x, (v) -> {
            this.x = v;
        }, (int) Minecraft.getInstance().player.getPosX(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("y", this.y, (v) -> {
            this.y = v;
        }, (int) Minecraft.getInstance().player.getPosY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("z", this.z, (v) -> {
            this.z = v;
        }, (int) Minecraft.getInstance().player.getPosZ(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("w", this.w, (v) -> {
            this.w = v;
        }, 1, 1, Integer.MAX_VALUE);
        config.addInt("h", this.h, (v) -> {
            this.h = v;
        }, 0, 0, Integer.MAX_VALUE);
        config.addInt("d", this.d, (v) -> {
            this.d = v;
        }, 1, 1, Integer.MAX_VALUE);

    }

    private String getLocation() {
        if (this.h == 0) return this.x + ", " + this.z; //If height is 0, ignore Y

        return this.x + ", " + this.y + ", " + this.z;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getAltTitle() {
        TranslationTextComponent title = new TranslationTextComponent("ftbquests.task." + this.getType().id.getNamespace() + '.' + this.getType().id.getPath() + ".title", new StringTextComponent(this.invert ? "anything but " : "a ").appendSibling(PokemonTask.getPokemon(this.cachedSpec)), getLocation());
        return title;
    }




    @Override
    public boolean canSubmit(TeamData teamData, ServerPlayerEntity player) {
        if (this.ignoreDimension || this.dimension == player.world.getDimensionKey()) {
            int py = MathHelper.floor(player.getPosY());
            if (this.h == 0 || (py >= this.y && py < this.y + this.h)) {
                int px = MathHelper.floor(player.getPosX());
                if (px >= this.x && px < this.x + this.w) {
                    int pz = MathHelper.floor(player.getPosZ());
                    if (pz >= this.z && pz < this.z + this.d) {
                        if (this.slot != 0) {
                            Pokemon pokemon = StorageProxy.getParty(player).get(this.slot - 1);
                            if (pokemon == null) return false;

                            return this.cachedSpec.matches(pokemon) != this.invert;

                        } else {
                            return StorageProxy.getParty(player).findOne(pokemon -> this.cachedSpec.matches(pokemon) != this.invert) != null;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return 5;
    }
}
