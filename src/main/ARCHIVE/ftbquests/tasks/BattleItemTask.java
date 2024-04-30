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
        POTION(PixelmonItems.potion, PixelmonItems.super_potion, PixelmonItems.hyper_potion, PixelmonItems.max_potion, PixelmonItems.moomoo_milk, PixelmonItems.full_restore, PixelmonItems.lava_cookie, PixelmonItems.energy_powder,
                PixelmonItems.energy_root, PixelmonItems.lemonade, PixelmonItems.soda_pop, PixelmonItems.fresh_water, PixelmonItems.komala_coffee, PixelmonItems.tapu_cocoa, PixelmonItems.pinap_juice, PixelmonItems.roserade_tea, PixelmonItems.skitty_smoothie),
        REVIVE(PixelmonItems.revive, PixelmonItems.max_revive, PixelmonItems.revival_herb),
        BERRY(PixelmonItems.cheri_berry, PixelmonItems.chesto_berry, PixelmonItems.pecha_berry, PixelmonItems.rawst_berry, PixelmonItems.aspear_berry, PixelmonItems.leppa_berry, PixelmonItems.persim_berry, PixelmonItems.oran_berry,
                PixelmonItems.lum_berry, PixelmonItems.sitrus_berry, PixelmonItems.figy_berry, PixelmonItems.wiki_berry, PixelmonItems.mago_berry, PixelmonItems.iapapa_berry),
        STATUS_HEAL(PixelmonItems.heal_powder, PixelmonItems.burn_heal, PixelmonItems.full_heal, PixelmonItems.full_restore, PixelmonItems.ice_heal, PixelmonItems.paralyze_heal, PixelmonItems.antidote, PixelmonItems.awakening, PixelmonItems.rage_candy_bar,
                PixelmonItems.lava_cookie, PixelmonItems.old_gateau, PixelmonItems.casteliacone, PixelmonItems.lumiose_galette, PixelmonItems.shalour_sable, PixelmonItems.big_malasada, PixelmonItems.cheri_berry, PixelmonItems.chesto_berry,
                PixelmonItems.pecha_berry, PixelmonItems.rawst_berry, PixelmonItems.aspear_berry, PixelmonItems.persim_berry, PixelmonItems.lum_berry),
        BOOST(PixelmonItems.x_accuracy, PixelmonItems.x_attack, PixelmonItems.x_special_attack, PixelmonItems.x_defense, PixelmonItems.x_special_defense, PixelmonItems.x_speed, PixelmonItems.dire_hit, PixelmonItems.guard_spec, PixelmonItems.red_flute,
                PixelmonItems.blue_flute, PixelmonItems.green_flute, PixelmonItems.yellow_flute),
        ESCAPE(PixelmonItems.escape_rope, PixelmonItems.fluffy_tail),
        HERB(PixelmonItems.energy_powder, PixelmonItems.energy_root, PixelmonItems.revival_herb, PixelmonItems.heal_powder),
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
    public ItemStack customItem = PixelmonItems.lava_cookie.getDefaultInstance();

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
                            return Icon.getIcon(PixelmonItems.revival_herb.getRegistryName());
                        case BOOST:
                            return Icon.getIcon(PixelmonItems.x_accuracy.getRegistryName());
                        case POTION:
                            return Icon.getIcon(PixelmonItems.potion.getRegistryName());
                        case REVIVE:
                            return Icon.getIcon(PixelmonItems.revive.getRegistryName());
                        case STATUS_HEAL:
                            return Icon.getIcon(PixelmonItems.paralyze_heal.getRegistryName());
                        case BERRY:
                            return Icon.getIcon(PixelmonItems.razz_berry.getRegistryName());
                        case ESCAPE:
                            return Icon.getIcon(PixelmonItems.escape_rope.getRegistryName());
                    }
                    return null;
                }).create());
        config.addItemStack("customItem", customItem, v -> customItem = v, PixelmonItems.revive.getDefaultInstance(), true, false);
    }
}
