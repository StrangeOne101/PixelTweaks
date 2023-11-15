package com.strangeone101.pixeltweaks.integration.ftbquests.tasks;

import static com.pixelmonmod.pixelmon.api.registries.PixelmonItems.*;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTask;
import com.strangeone101.pixeltweaks.integration.ftbquests.PokemonTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BattleItemTask extends PokemonTask {

    public enum ItemType {

        ANY,
        POTION(potion, super_potion, hyper_potion, max_potion, moomoo_milk, full_restore, lava_cookie, energy_powder,
                energy_root, lemonade, soda_pop, fresh_water, komala_coffee, tapu_cocoa, pinap_juice, roserade_tea, skitty_smoothie),
        REVIVE(revive, max_revive, revival_herb),
        BERRY(cheri_berry, chesto_berry, pecha_berry, rawst_berry, aspear_berry, leppa_berry, persim_berry, oran_berry,
                lum_berry, sitrus_berry, figy_berry, wiki_berry, mago_berry, iapapa_berry),
        STATUS_HEAL(heal_powder, burn_heal, full_heal, full_restore, ice_heal, paralyze_heal, antidote, awakening, rage_candy_bar,
                lava_cookie, old_gateau, casteliacone, lumiose_galette, shalour_sable, big_malasada, cheri_berry, chesto_berry,
                pecha_berry, rawst_berry, aspear_berry, persim_berry, lum_berry),
        BOOST(x_accuracy, x_attack, x_special_attack, x_defense, x_special_defense, x_speed, dire_hit, guard_spec, red_flute,
                blue_flute, green_flute, yellow_flute),
        ESCAPE(escape_rope, fluffy_tail),
        HERB(energy_powder, energy_root, revival_herb, heal_powder),
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
    public ItemStack customItem = lava_cookie.getDefaultInstance();

    public BattleItemTask(Quest q) {
        super(q);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.BATTLE_ITEM;
    }

    @Override
    public void writeData(CompoundNBT nbt) {
        super.writeData(nbt);
        nbt.putByte("type", (byte) type.ordinal());
        if (type == ItemType.CUSTOM) {
            NBTUtils.write(nbt, "custom_item", customItem);
        }
    }

    @Override
    public void readData(CompoundNBT nbt) {
        super.readData(nbt);
        type = ItemType.values()[nbt.getByte("type")];
        if (type == ItemType.CUSTOM) {
            customItem = NBTUtils.read(nbt, "custom_item");
        }
    }

    @Override
    public void writeNetData(PacketBuffer buffer) {
        super.writeNetData(buffer);
        buffer.writeByte(type.ordinal());
        if (type == ItemType.CUSTOM) {
            buffer.writeItemStack(customItem);
        }
    }

    @Override
    public void readNetData(PacketBuffer buffer) {
        super.readNetData(buffer);
        type = ItemType.values()[buffer.readByte()];
        if (type == ItemType.CUSTOM) {
            customItem = buffer.readItemStack();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addEnum("itemType", type, v -> type = v, NameMap.of(ItemType.REVIVE, ItemType.values())
                .nameKey(v -> "pixeltweaks.battle_item_type." + v.name().toLowerCase())
                .icon(v -> {
                    switch(v) {
                        case HERB:
                            return Icon.getIcon(revival_herb.getRegistryName());
                        case BOOST:
                            return Icon.getIcon(x_accuracy.getRegistryName());
                        case POTION:
                            return Icon.getIcon(potion.getRegistryName());
                        case REVIVE:
                            return Icon.getIcon(revive.getRegistryName());
                        case STATUS_HEAL:
                            return Icon.getIcon(paralyze_heal.getRegistryName());
                        case BERRY:
                            return Icon.getIcon(razz_berry.getRegistryName());
                        case ESCAPE:
                            return Icon.getIcon(escape_rope.getRegistryName());
                    }
                    return null;
                }).create());
        config.addItemStack("customItem", customItem, v -> customItem = v, revive.getDefaultInstance(), true, false);
    }
}
