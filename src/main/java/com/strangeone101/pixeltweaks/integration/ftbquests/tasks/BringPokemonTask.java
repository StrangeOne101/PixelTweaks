package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonConfig;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.FTBLibrary;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.ResourceConfigValue;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.config.ui.SelectImageResourceScreen;
import dev.ftb.mods.ftblibrary.config.ui.SelectableResource;
import dev.ftb.mods.ftblibrary.icon.IResourceIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.AbstractBooleanTask;
import dev.ftb.mods.ftbquests.quest.task.LocationTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.OptionalLong;

public class BringPokemonTask extends AbstractBooleanTask {

    public boolean invert = false;
    public transient PokemonSpecification cachedSpec;
    public int slot = 0;
    public boolean pokemonOutOfBall;
    public Tristate hasOT = Tristate.DEFAULT;

    private ResourceKey<Level> dimension;
    private boolean ignoreDimension;
    private int x;
    private int y;
    private int z;
    private int w;
    private int h;
    private int d;

    public BringPokemonTask(long id, Quest quest) {
        super(id, quest);

        this.dimension = Level.OVERWORLD;
        this.ignoreDimension = false;
        this.x = this.y = this.z = 0;
        this.w = this.h = this.d = 1;
    }

    public TaskType getType() {
        return PokemonTaskTypes.BRING_POKEMON;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putString("dimension", this.dimension.location().toString());
        nbt.putBoolean("ignore_dimension", this.ignoreDimension);
        nbt.putIntArray("position", new int[]{this.x, this.y, this.z});
        nbt.putIntArray("size", new int[]{this.w, this.h, this.d});
        nbt.putString("pokemon", this.cachedSpec == null ? "" : this.cachedSpec.toString());
        nbt.putByte("slot", (byte) this.slot);
        nbt.putBoolean("invert", this.invert);
        nbt.putBoolean("pokemon_out_of_ball", this.pokemonOutOfBall);
        this.hasOT.write(nbt, "hasOT");
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(nbt.getString("dimension")));
        this.ignoreDimension = nbt.getBoolean("ignore_dimension");
        int[] pos = nbt.getIntArray("position");
        if (pos.length == 3) {
            this.x = pos[0];
            this.y = pos[1];
            this.z = pos[2];
        }

        int[] size = nbt.getIntArray("size");
        if (pos.length == 3) {
            this.w = size[0];
            this.h = size[1];
            this.d = size[2];
        }
        String pokemonSpec = nbt.getString("pokemon");
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec).get();
        this.invert = nbt.getBoolean("invert");
        this.slot = nbt.getByte("slot");
        this.pokemonOutOfBall = nbt.getBoolean("pokemon_out_of_ball");
        this.hasOT = Tristate.read(nbt, "hasOT");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeResourceLocation(this.dimension.location());
        buffer.writeBoolean(this.ignoreDimension);
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeVarInt(this.z);
        buffer.writeVarInt(this.w);
        buffer.writeVarInt(this.h);
        buffer.writeVarInt(this.d);
        buffer.writeUtf(this.cachedSpec == null ? "" : this.cachedSpec.toString());
        buffer.writeBoolean(this.invert);
        buffer.writeByte(this.slot);
        buffer.writeBoolean(this.pokemonOutOfBall);
        this.hasOT.write(buffer);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.dimension = ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation());
        this.ignoreDimension = buffer.readBoolean();
        this.x = buffer.readVarInt();
        this.y = buffer.readVarInt();
        this.z = buffer.readVarInt();
        this.w = buffer.readVarInt();
        this.h = buffer.readVarInt();
        this.d = buffer.readVarInt();
        String pokemonSpec = buffer.readUtf();
        this.cachedSpec = pokemonSpec.isEmpty() ? null : PokemonSpecificationProxy.create(pokemonSpec).get();
        this.invert = buffer.readBoolean();
        this.slot = buffer.readByte();
        this.pokemonOutOfBall = buffer.readBoolean();
        this.hasOT = Tristate.read(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.add("pokemon", new PokemonConfig(false), this.cachedSpec, v -> this.cachedSpec = v, null);
        config.addBool("invert", this.invert, v -> this.invert = v, false);
        config.addEnum("slot", this.slot, v -> this.slot = v, NameMap.of(0, new Integer[]{0, 1, 2, 3, 4, 5, 6})
                .nameKey(v -> "pixeltweaks.partyslot." + v).create(), 0);
        config.addBool("pokemon_out_of_ball", this.pokemonOutOfBall, v -> this.pokemonOutOfBall = v, false);
        config.addTristate("has_ot", this.hasOT, v -> this.hasOT = v, Tristate.DEFAULT);
        config.addInt("x", this.x, (v) -> {
            this.x = v;
        }, (int) Minecraft.getInstance().player.getX(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("y", this.y, (v) -> {
            this.y = v;
        }, (int) Minecraft.getInstance().player.getY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("z", this.z, (v) -> {
            this.z = v;
        }, (int) Minecraft.getInstance().player.getZ(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        config.addInt("w", this.w, (v) -> {
            this.w = v;
        }, 5, 1, Integer.MAX_VALUE);
        config.addInt("h", this.h, (v) -> {
            this.h = v;
        }, 0, 0, Integer.MAX_VALUE);
        config.addInt("d", this.d, (v) -> {
            this.d = v;
        }, 5, 1, Integer.MAX_VALUE);
        config.addEnum("dim", this.dimension.location().toString(), (v) -> {
            this.dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(v.toString()));
        }, NameMap.of("minecraft:overworld", Minecraft.getInstance().getConnection().levels().stream().map(v -> v.location().toString()).toArray(String[]::new))
                .nameKey(Object::toString).create(), "minecraft:overworld");
        config.addBool("ignore_dim", this.ignoreDimension, (v) -> {
            this.ignoreDimension = v;
        }, false);
        config.add("reset_button", new ResetButton(this, config), null, (v) -> {},null);
    }

    private String getLocation() {
        if (this.h == 0) return this.x + ", " + this.z; //If height is 0, ignore Y

        return this.x + ", " + this.y + ", " + this.z;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        MutableComponent title = Component.translatable("ftbquests.task." + this.getType().getTypeId().getNamespace() + '.' + this.getType().getTypeId().getPath() + ".title", Component.literal(this.invert ? "anything but " : "a ").append(PokemonTask.getPokemon(this.cachedSpec)), getLocation());
        return title;
    }




    @Override
    public boolean canSubmit(TeamData teamData, ServerPlayer player) {
        if (this.ignoreDimension || this.dimension == player.level().dimension()) {
            int py = Mth.floor(player.getY());
            if (this.h == 0 || (py >= this.y && py < this.y + this.h)) {
                int px = Mth.floor(player.getX());
                if (px >= this.x && px < this.x + this.w) {
                    int pz = Mth.floor(player.getZ());
                    if (pz >= this.z && pz < this.z + this.d) {
                        try {
                            if (this.slot != 0) {
                                Pokemon pokemon = StorageProxy.getParty(player).get().get(this.slot - 1);
                                if (pokemon == null) return false;
                                if (this.pokemonOutOfBall && pokemon.getEntity() == null) return false;
                                if (this.hasOT != Tristate.DEFAULT && this.hasOT.isTrue() != (pokemon.getOriginalTrainerUUID() == player.getUUID())) return false;

                                return this.cachedSpec.matches(pokemon) != this.invert;

                            } else {
                                return StorageProxy.getParty(player).get().findOne(pokemon ->
                                        this.cachedSpec.matches(pokemon) != this.invert &&
                                                (!this.pokemonOutOfBall || pokemon.getEntity() != null) &&
                                                (this.hasOT == Tristate.DEFAULT || this.hasOT.isTrue() == (pokemon.getOriginalTrainerUUID() == player.getUUID()))
                                ) != null;
                            }


                        } catch (Exception e) {
                            //If the player is not in a world, or the party is null, return false
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return 10;
    }

    public class ResetButton extends ConfigValue<String> {
        private boolean allowEmpty = true;
        public BringPokemonTask task;
        public ConfigGroup group;

        public ResetButton(BringPokemonTask task, ConfigGroup group) {
            this.task = task;
            this.group = group;
        }

        public void onClicked(Widget clicked, MouseButton button, ConfigCallback callback) {
            Screen screen = new ConfirmScreen((result) -> {
                if (result) {
                    group.getValues().stream().forEach(v -> {
                        if (v instanceof ConfigValue vi) {
                            if (v.id.equals("x")) vi.setValue(Minecraft.getInstance().player.getBlockX());
                            else if (v.id.equals("y")) vi.setValue(Minecraft.getInstance().player.getBlockY());
                            else if (v.id.equals("z")) vi.setValue(Minecraft.getInstance().player.getBlockZ());
                            else if (v.id.equals("dim")) vi.setValue(Minecraft.getInstance().player.level().dimension().location().toString());
                        }
                    });
                }
                callback.save(result);
                clicked.getGui().openGui();
                clicked.getGui().onInit();
                clicked.getGui().refreshWidgets();

            }, Component.translatable("ftbquests.task.pixelmon.bring_pokemon.reset_button.title"), Component.translatable("ftbquests.task.pixelmon.bring_pokemon.reset_button.description"));

            Minecraft.getInstance().setScreen(screen);
        }

        public boolean isEmpty() {
            return true;
        }

        public void addInfo(TooltipList list) {
            super.addInfo(list);
        }

        @Override
        public String getValue() {
            return "[Click me!]";
        }
    }

}
