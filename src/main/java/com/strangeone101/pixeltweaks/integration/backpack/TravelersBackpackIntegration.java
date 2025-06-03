package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackIntegration {

    public TravelersBackpackIntegration() {
        if (ModIntegration.travelersBackpack()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> stack.getItem() instanceof TravelersBackpackItem tbi
                    , (player, section, inventory, stack, items) -> {
                if (stack.has(ModDataComponents.BACKPACK_CONTAINER.get())) {
                    BackpackContainerContents contents = stack.get(ModDataComponents.BACKPACK_CONTAINER.get());
                    BattleItemScanner.checkInventory(player, section, contents.getItems(), items);
                }
            }, (player, stack, toMatch) -> {
                if (!stack.has(ModDataComponents.BACKPACK_CONTAINER.get())) {
                    return null;
                }
                BackpackContainerContents contents = stack.get(ModDataComponents.BACKPACK_CONTAINER.get());
                return BattleItemScanner.findItemFromIterable(toMatch, contents.getItems().size(), s -> contents.getItems().get(s));
            }, (player, stack, toMatch) -> {
                if (!stack.has(ModDataComponents.BACKPACK_CONTAINER.get())) {
                    return null;
                }
                BackpackContainerContents contents = stack.get(ModDataComponents.BACKPACK_CONTAINER.get());
                for (int i = 0; i < contents.getItems().size(); i++) {
                    ItemStack slot = contents.getItems().get(i);
                    if (ItemStack.isSameItemSameComponents(slot, toMatch)) {
                        slot.shrink(1);
                        contents.getItems().set(i, slot);
                        stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), contents);
                        return slot;
                    }
                }

                return null;
            }));
        }
    }
}
