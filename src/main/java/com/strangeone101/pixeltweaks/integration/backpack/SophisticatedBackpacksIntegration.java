package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

import java.util.ArrayList;
import java.util.List;

public class SophisticatedBackpacksIntegration {

    public SophisticatedBackpacksIntegration() {
        if (ModIntegration.sophisticatedBackpacks()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> stack.getItem() instanceof BackpackItem
                , (player, section, inventory, stack, items) -> {

                IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                IItemHandler handler = wrapper.getInventoryHandler();
                List<ItemStack> invStacks = new ArrayList<>();
                for (int i = 0; i < handler.getSlots(); i++) {
                    invStacks.add(handler.getStackInSlot(i));
                }
                BattleItemScanner.checkInventory(player, section, invStacks, false, items);
            }, (player, stack, toMatch) -> {
                IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                IItemHandler handler = wrapper.getInventoryHandler();
                return BattleItemScanner.findItemFromIterable(toMatch, handler.getSlots(), handler::getStackInSlot);
            }, (player, stack, toMatch) -> {
                IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                IItemHandler handler = wrapper.getInventoryHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack slot = handler.getStackInSlot(i);
                    if (ItemStack.isSameItem(slot, toMatch) && ItemStack.isSameItemSameComponents(slot, toMatch)) {
                        handler.extractItem(i, 1, false);

                        return slot;
                    }
                }

                return null;
            }));
        }
    }
}
