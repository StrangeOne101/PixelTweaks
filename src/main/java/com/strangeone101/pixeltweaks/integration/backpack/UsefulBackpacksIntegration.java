package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import info.u_team.useful_backpacks.item.BackpackItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UsefulBackpacksIntegration {

    public UsefulBackpacksIntegration() {
        if (ModIntegration.usefulBackpacks()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> {
                return stack.getItem() instanceof BackpackItem;
            }, (player, section, inventory, stack, items) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                SimpleContainer inv = backpack.getInventory(player, stack);
                if (inv != null) {
                    BattleItemScanner.checkInventory(player, section, inv, items);
                }

            }, (player, stack, toMatch) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                SimpleContainer inv = backpack.getInventory(player, stack);
                return inv == null ? null : BattleItemScanner.findItemFromIterable(toMatch, inv.getContainerSize(), inv::getItem);
            }, (player, stack, toMatch) -> {
                BackpackItem backpack = (BackpackItem)stack.getItem();
                SimpleContainer inv = backpack.getInventory(player, stack);
                if (inv == null) {
                    return null;
                } else {
                    for(int i = 0; i < inv.getContainerSize(); ++i) {
                        ItemStack slot = inv.getItem(i);
                        if (ItemStack.isSameItem(slot, toMatch) && ItemStack.isSameItemSameTags(slot, toMatch)) {
                            inv.removeItem(i, 1);
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
