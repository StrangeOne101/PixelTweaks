package com.strangeone101.pixeltweaks.mixin.integration;

import com.pixelmonmod.pixelmon.api.battles.BagSection;
import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.items.ItemData;
import com.strangeone101.pixeltweaks.integration.ModIntegration;
import com.strangeone101.pixeltweaks.integration.backpack.CuriosIntegration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
            target = "Lcom/pixelmonmod/pixelmon/api/battles/BattleItemScanner;checkInventory(Lnet/minecraft/server/level/ServerPlayer;Lcom/pixelmonmod/pixelmon/api/battles/BagSection;Lnet/minecraft/world/Container;Ljava/util/List;)V"
            , remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void scan(ServerPlayer player, BagSection section, CallbackInfoReturnable<List<ItemData>> cir, List<ItemData> itemData) {
        if (ModIntegration.sophisticatedBackpacks() && ModIntegration.curiosApi()) {
            CuriosIntegration.scan(player, section, itemData);
        }
    }

    /**
     * @author StrangeOne101
     * @reason Add Sophisticated Backpack support
     */
    @Overwrite(remap = false)
    public static ItemStack findMatchingItem(ItemStack toMatch, ServerPlayer player) {
        ItemStack stack = BattleItemScanner.findMatchingItem(toMatch, player, player.getInventory());
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
    public static ItemStack consumeItem(ItemStack toMatch, ServerPlayer player) {
        ItemStack stack = BattleItemScanner.consumeItem(toMatch, player, player.getInventory());
        if (stack == null && ModIntegration.sophisticatedBackpacks() && ModIntegration.curiosApi()) {
            stack = CuriosIntegration.consume(toMatch, player);
        }
        return stack;
    }



}
