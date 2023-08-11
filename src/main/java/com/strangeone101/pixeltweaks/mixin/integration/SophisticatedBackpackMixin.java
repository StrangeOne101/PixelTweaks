package com.strangeone101.pixeltweaks.mixin.integration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.battles.BagSection;
import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.items.ItemData;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.curios.CuriosCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(BattleItemScanner.class)
public class SophisticatedBackpackMixin {

    @Inject(method = "scan", at = @At(value = "INVOKE",
            target = "Lcom/pixelmonmod/pixelmon/api/battles/BattleItemScanner;checkInventory(Lnet/minecraft/entity/player/ServerPlayerEntity;Lcom/pixelmonmod/pixelmon/api/battles/BagSection;Lnet/minecraft/inventory/IInventory;Ljava/util/List;)V"
            , remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectScan(ServerPlayerEntity player, BagSection section, CallbackInfoReturnable<List<ItemData>> callback, List<ItemData> itemData) {
        if (ModIntegration.sophisticatedBackpacks()) {
            IDynamicStackHandler handler = CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < handler.getSlots(); i++) {
                stacks.add(handler.getStackInSlot(i));
            }
            BattleItemScanner.checkInventory(player, section, stacks, itemData);
        }
    }

    /**
     * @author StrangeOne101
     * @reason Add Sophisticated Backpack support
     */
    @Overwrite(remap = false)
    public static ItemStack findMatchingItem(ItemStack toMatch, ServerPlayerEntity player) {
        ItemStack stack = BattleItemScanner.findMatchingItem(toMatch, player, player.inventory);
        if (stack == null && ModIntegration.sophisticatedBackpacks()) {
            IDynamicStackHandler handler = CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
            ItemStack[] stacks = new ItemStack[handler.getSlots()];
            for (int i = 0; i < handler.getSlots(); i++) {
                stacks[i] = handler.getStackInSlot(i);
            }
            stack = BattleItemScanner.findMatchingItem(toMatch, player, new Inventory(stacks));
        }
        return stack;
    }

    /**
     * @author StrangeOne101
     * @reason Add Sophisticated Backpack support
     */
    @Overwrite(remap = false)
    public static ItemStack consumeItem(ItemStack toMatch, ServerPlayerEntity player) {
        ItemStack stack = BattleItemScanner.consumeItem(toMatch, player, player.inventory);
        if (stack == null && ModIntegration.sophisticatedBackpacks()) {
            IDynamicStackHandler handler = CuriosCompat.getFromCuriosSlotStackHandler(player, "back", ICurioStacksHandler::getStacks, (IDynamicStackHandler)null);
            ItemStack[] stacks = new ItemStack[handler.getSlots()];
            for (int i = 0; i < handler.getSlots(); i++) {
                stacks[i] = handler.getStackInSlot(i);
            }
            stack = BattleItemScanner.consumeItem(toMatch, player, new Inventory(stacks));
        }
        return stack;
    }



}
