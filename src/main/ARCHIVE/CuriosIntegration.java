package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BagSection;
import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.items.ItemData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;

public class CuriosIntegration {

    public static void scan(ServerPlayer player, BagSection section, List<ItemData> itemData) {
        IDynamicStackHandler handler = CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            stacks.add(handler.getStackInSlot(i));
        }
        BattleItemScanner.checkInventory(player, section, stacks, itemData);
    }

    public static ItemStack find(ItemStack toMatch, ServerPlayer player) {
        IDynamicStackHandler handler = CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
        ItemStack[] stacks = new ItemStack[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            stacks[i] = handler.getStackInSlot(i);
        }
        return BattleItemScanner.findMatchingItem(toMatch, player, new SimpleContainer(stacks));
    }

    public static ItemStack consume(ItemStack toMatch, ServerPlayer player) {
        IDynamicStackHandler handler = CapabilityUtils.CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
        ItemStack[] stacks = new ItemStack[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            stacks[i] = handler.getStackInSlot(i);
        }
        return BattleItemScanner.consumeItem(toMatch, player, new SimpleContainer(stacks));
    }


}
