package com.strangeone101.pixeltweaks.tweaks;

import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.ModIntegration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;

public class SimplyBackpacksIntegration {

    public SimplyBackpacksIntegration() {
        if (ModIntegration.simplyBackpacks()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> stack.getItem() instanceof BackpackItem, (player, section, inventory, stack, items) -> {
                BackpackData data = BackpackItem.getData(stack);
                IItemHandler handler = data.getHandler();
                if (handler instanceof IItemHandlerModifiable) {
                    List<ItemStack> backpackItems = new ArrayList<>();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        backpackItems.add(handler.getStackInSlot(i));
                    }
                    BattleItemScanner.checkInventory(player, section, backpackItems, items);
                }
            }, (player, stack, toMatch) -> {
                BackpackData data = BackpackItem.getData(stack);
                IItemHandler handler = data.getHandler();
                if (handler instanceof IItemHandlerModifiable) {
                    return BattleItemScanner.findItemFromIterable(toMatch, handler.getSlots(), handler::getStackInSlot);
                }

                return null;
            }, (player, stack, toMatch) -> {
                BackpackData data = BackpackItem.getData(stack);
                IItemHandler handler = data.getHandler();
                if (handler instanceof IItemHandlerModifiable) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack slot = handler.getStackInSlot(i);
                        if (ItemStack.areItemsEqual(slot, toMatch) && ItemStack.areItemStackTagsEqual(slot, toMatch)) {
                            handler.extractItem(i, 1, false);
                            return slot;
                        }
                    }
                }
                return null;
            }));
        }
    }
}
