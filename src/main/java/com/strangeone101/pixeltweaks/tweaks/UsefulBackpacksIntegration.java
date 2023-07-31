package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.items.PokeBagItem;
import com.strangeone101.pixeltweaks.ModIntegration;
import info.u_team.useful_backpacks.item.BackpackItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class UsefulBackpacksIntegration {

    public UsefulBackpacksIntegration() {
        if (ModIntegration.backpacks()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> {
                return stack.getItem() instanceof BackpackItem;
            }, (player, section, inventory, stack, items) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                IInventory inv = backpack.getInventory(player, stack);
                if (inv != null) {
                    BattleItemScanner.checkInventory(player, section, inv, items);
                }

            }, (player, stack, toMatch) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                IInventory inv = backpack.getInventory(player, stack);
                return inv == null ? null : BattleItemScanner.findItemFromIterable(toMatch, inv.getSizeInventory(), inv::getStackInSlot);
            }, (player, stack, toMatch) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                IInventory inv = backpack.getInventory(player, stack);
                if (inv == null) {
                    return null;
                } else {
                    for(int i = 0; i < inv.getSizeInventory(); ++i) {
                        ItemStack slot = inv.getStackInSlot(i);
                        if (ItemStack.areItemsEqual(slot, toMatch) && ItemStack.areItemStackTagsEqual(slot, toMatch)) {
                            inv.decrStackSize(i, 1);
                            backpack.saveInventory(inv, stack);
                            return slot;
                        }
                    }

                    return null;
                }
            }));
        }
    }
}
