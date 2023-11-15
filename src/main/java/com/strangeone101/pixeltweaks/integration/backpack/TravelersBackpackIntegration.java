package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackIntegration {

    public TravelersBackpackIntegration() {
        if (ModIntegration.travelersBackpack()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> stack.getItem() instanceof TravelersBackpackItem
                    , (player, section, inventory, stack, items) -> {
                TravelersBackpackContainer inv = new TravelersBackpackContainer(stack, player, (byte)1);
                List<ItemStack> invItems = new ArrayList<>();
                for (int i = 0; i < inv.getHandler().getSlots(); i++) {
                    invItems.add(inv.getHandler().getStackInSlot(i));
                }
                BattleItemScanner.checkInventory(player, section, invItems, items);
            }, (player, stack, toMatch) -> {
                TravelersBackpackContainer inv = new TravelersBackpackContainer(stack, player, (byte)1);
                IItemHandler handler = inv.getHandler();
                return BattleItemScanner.findItemFromIterable(toMatch, handler.getSlots(), handler::getStackInSlot);
            }, (player, stack, toMatch) -> {
                TravelersBackpackContainer inv = new TravelersBackpackContainer(stack, player, (byte)1);
                IItemHandler handler = inv.getHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack slot = handler.getStackInSlot(i);
                    if (ItemStack.isSameItem(slot, toMatch) && ItemStack.isSameItemSameTags(slot, toMatch)) {
                        inv.getHandler().extractItem(i, 1, false);
                        return slot;
                    }
                }

                return null;
            }));
        }
    }
}
