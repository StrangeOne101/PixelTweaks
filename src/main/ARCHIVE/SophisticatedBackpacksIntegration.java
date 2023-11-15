package com.strangeone101.pixeltweaks.integration.backpack;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.NoopBackpackWrapper;

import java.util.ArrayList;
import java.util.List;

public class SophisticatedBackpacksIntegration {

    public SophisticatedBackpacksIntegration() {
        if (ModIntegration.sophisticatedBackpacks()) {
            BattleItemScanner.addScanner(new BattleItemScanner.InventoryScanner((stack) -> stack.getItem() instanceof BackpackItem
                , (player, section, inventory, stack, items) -> {

                LazyOptional<IBackpackWrapper> backpackWrapper = stack.getCapability((CapabilityBackpackWrapper.getCapabilityInstance()));
                if (backpackWrapper.isPresent()) {
                    IBackpackWrapper realWrapper = backpackWrapper.orElse(NoopBackpackWrapper.INSTANCE);
                    IItemHandler handler = realWrapper.getInventoryHandler();
                    List<ItemStack> invStacks = new ArrayList<>();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        invStacks.add(handler.getStackInSlot(i));
                    }
                    BattleItemScanner.checkInventory(player, section, invStacks, false, items);
                }
            }, (player, stack, toMatch) -> {
                LazyOptional<IBackpackWrapper> backpackWrapper = stack.getCapability((CapabilityBackpackWrapper.getCapabilityInstance()));
                if (backpackWrapper.isPresent()) {
                    IBackpackWrapper realWrapper = backpackWrapper.orElse(NoopBackpackWrapper.INSTANCE);
                    IItemHandler handler = realWrapper.getInventoryHandler();
                    return BattleItemScanner.findItemFromIterable(toMatch, handler.getSlots(), handler::getStackInSlot);
                }

                return null;
            }, (player, stack, toMatch) -> {
                LazyOptional<IBackpackWrapper> backpackWrapper = stack.getCapability((CapabilityBackpackWrapper.getCapabilityInstance()));
                if (backpackWrapper.isPresent()) {
                    IBackpackWrapper realWrapper = backpackWrapper.orElse(NoopBackpackWrapper.INSTANCE);
                    IItemHandler handler = realWrapper.getInventoryHandler();
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
