package com.strangeone101.pixeltweaks.mixin.integration;

import com.pixelmonmod.pixelmon.api.battles.BagSection;
import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.items.ItemData;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.strangeone101.pixeltweaks.integration.backpack.CuriosIntegration;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.List;

@Mixin(BattleItemScanner.class)
public class SophisticatedBackpackMixin {

    @Inject(method = "scan", at = @At(value = "INVOKE",
            target = "Lcom/pixelmonmod/pixelmon/api/battles/BattleItemScanner;checkInventory(Lnet/minecraft/entity/player/ServerPlayerEntity;Lcom/pixelmonmod/pixelmon/api/battles/BagSection;Lnet/minecraft/inventory/IInventory;Ljava/util/List;)V"
            , remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectScan(ServerPlayerEntity player, BagSection section, CallbackInfoReturnable<List<ItemData>> callback, List<ItemData> itemData) {
        if (ModIntegration.sophisticatedBackpacks() && ModIntegration.curiosApi()) {
            CuriosIntegration.scan(player, section, itemData);
        }
    }

    /**
     * @author StrangeOne101
     * @reason Add Sophisticated Backpack support
     */
    @Overwrite(remap = false)
    public static ItemStack findMatchingItem(ItemStack toMatch, ServerPlayerEntity player) {
        ItemStack stack = BattleItemScanner.findMatchingItem(toMatch, player, player.inventory);
        if (stack == null && ModIntegration.sophisticatedBackpacks() && ModIntegration.curiosApi()) {
            stack = CuriosIntegration.find(toMatch, player);
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
        if (stack == null && ModIntegration.sophisticatedBackpacks() && ModIntegration.curiosApi()) {
            stack = CuriosIntegration.consume(toMatch, player);
        }
        return stack;
    }



}
