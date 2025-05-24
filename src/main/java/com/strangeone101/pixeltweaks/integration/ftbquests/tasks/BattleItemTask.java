package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.NBTUtils;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.pixelmonmod.pixelmon.init.registry.ItemRegistration.*;

public class BattleItemTask extends PokemonTask {

    public enum ItemType {



        ANY,
        POTION(ItemRegistration.POTION.get(), SUPER_POTION.get(), HYPER_POTION.get(), MAX_POTION.get(), MOOMOO_MILK.get(), FULL_RESTORE.get(), LAVA_COOKIE.get(), ENERGY_POWDER.get(),
                ENERGY_ROOT.get(), LEMONADE.get(), SODA_POP.get(), FRESH_WATER.get(), KOMALA_COFFEE.get(), TAPU_COCOA.get(), PINAP_JUICE.get(), ROSERADE_TEA.get(), SKITTY_SMOOTHIE.get()),
        REVIVE(ItemRegistration.REVIVE.get(), MAX_REVIVE.get(), REVIVAL_HERB.get()),
        BERRY(CHERI_BERRY.get(), CHESTO_BERRY.get(), PECHA_BERRY.get(), RAWST_BERRY.get(), ASPEAR_BERRY.get(), LEPPA_BERRY.get(), PERSIM_BERRY.get(), ORAN_BERRY.get(),
                LUM_BERRY.get(), SITRUS_BERRY.get(), FIGY_BERRY.get(), WIKI_BERRY.get(), MAGO_BERRY.get(), IAPAPA_BERRY.get()),
        STATUS_HEAL(HEAL_POWDER.get(), BURN_HEAL.get(), FULL_HEAL.get(), FULL_RESTORE.get(), ICE_HEAL.get(), PARALYZE_HEAL.get(), ANTIDOTE.get(), AWAKENING.get(), RAGE_CANDY_BAR.get(),
                LAVA_COOKIE.get(), OLD_GATEAU.get(), CASTELIACONE.get(), LUMIOSE_GALETTE.get(), SHALOUR_SABLE.get(), BIG_MALASADA.get(), CHERI_BERRY.get(), CHESTO_BERRY.get(),
                PECHA_BERRY.get(), RAWST_BERRY.get(), ASPEAR_BERRY.get(), PERSIM_BERRY.get(), LUM_BERRY.get()),
        BOOST(X_ACCURACY.get(), X_ATTACK.get(), X_SPECIAL_ATTACK.get(), X_DEFENSE.get(), X_SPECIAL_DEFENSE.get(), X_SPEED.get(), DIRE_HIT.get(), GUARD_SPEC.get(), RED_FLUTE.get(),
                BLUE_FLUTE.get(), GREEN_FLUTE.get(), YELLOW_FLUTE.get()),
        ESCAPE(ESCAPE_ROPE.get(), FLUFFY_TAIL.get()),
        HERB(ENERGY_POWDER.get(), ENERGY_ROOT.get(), REVIVAL_HERB.get(), HEAL_POWDER.get()),
        CUSTOM;

        private Item[] items;
        private Set<Item> itemSet;

        ItemType(Item... items) {
            this.items = items;
            this.itemSet = new HashSet<>(Arrays.asList(items));
        };

        public boolean contains(Item item) {
            return itemSet.contains(item);
        }
    }

    public ItemType type = ItemType.REVIVE;
    public ItemStack customItem = LAVA_COOKIE.get().getDefaultInstance();

    public BattleItemTask(long id, Quest q) {
        super(id, q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.BATTLE_ITEM;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putByte("type", (byte) type.ordinal());
        if (type == ItemType.CUSTOM) {
            ItemStack.SINGLE_ITEM_CODEC.encodeStart(NbtOps.INSTANCE, this.customItem).ifSuccess((t) -> {
                nbt.put("custom_item", t);
            });
        }
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        type = ItemType.values()[nbt.getByte("type")];
        if (type == ItemType.CUSTOM) {
            ItemStack.SINGLE_ITEM_CODEC.decode(NbtOps.INSTANCE, nbt.get("custom_item")).ifSuccess(t -> {
                customItem = t.getFirst();
            });
        }
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(type.ordinal());
        if (type == ItemType.CUSTOM) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, customItem);
        }
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        type = ItemType.values()[buffer.readByte()];
        if (type == ItemType.CUSTOM) {
            customItem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addEnum("itemType", type, v -> type = v, NameMap.of(ItemType.REVIVE, ItemType.values())
                .nameKey(v -> "pixeltweaks.battle_item_type." + v.name().toLowerCase())
                .icon(v -> {
                    switch(v) {
                        case HERB:
                            return Icon.getIcon(REVIVAL_HERB.getId());
                        case BOOST:
                            return Icon.getIcon(X_ACCURACY.getId());
                        case POTION:
                            return Icon.getIcon(POTION.getId());
                        case REVIVE:
                            return Icon.getIcon(REVIVE.getId());
                        case STATUS_HEAL:
                            return Icon.getIcon(PARALYZE_HEAL.getId());
                        case BERRY:
                            return Icon.getIcon(RAZZ_BERRY.getId());
                        case ESCAPE:
                            return Icon.getIcon(ESCAPE_ROPE.getId());
                    }
                    return null;
                }).create());
        config.addItemStack("customItem", customItem, v -> customItem = v, REVIVE.get().getDefaultInstance(), true, false);
    }
}
