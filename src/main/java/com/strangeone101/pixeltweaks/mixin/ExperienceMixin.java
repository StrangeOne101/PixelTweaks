package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.battles.controller.Experience;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.items.HeldItem;
import com.pixelmonmod.pixelmon.items.helpers.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.launch.MixinInitialisationError;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Set;

@Mixin(Experience.class)
public class ExperienceMixin {

    /**
     * @author StrangeOne101
     * @reason Make ExpAll work from within backpacks
     */
    @Overwrite(remap = false)
    public static void awardExp(List<BattleParticipant> participants, BattleParticipant losingTeamOwner, PixelmonWrapper faintedPokemon) {
        if (faintedPokemon == null || faintedPokemon.bc.isRaid() || faintedPokemon.bc.isLevelingDisabled())
            return;
        for (BattleParticipant teamOwner : participants) {
            if (teamOwner.team != losingTeamOwner.team && teamOwner instanceof PlayerParticipant) {
                PlayerParticipant player = (PlayerParticipant)teamOwner;
                List<PixelmonWrapper> attackers = faintedPokemon.getOpponentPokemon();
                for (PixelmonWrapper pw : attackers) {
                    if (pw.getParticipant() == teamOwner)
                        calcExp(faintedPokemon, pw, 1.0D);
                }
                ItemStack expAllFound = BattleItemScanner.findMatchingItem(pixelTweaks$getExpAllActive(), player.player);
                boolean hasExpAll = expAllFound != null;

                for (PixelmonWrapper pw : teamOwner.allPokemon) {
                    if (!attackers.contains(pw)) {
                        if (hasExpAll || ((HeldItem)pw.getHeldItem().getItem()).getHeldItemType() == EnumHeldItems.expShare) {
                            calcExp(faintedPokemon, pw, 0.5D);
                        }
                    }
                }
                player.givePlayerExperience(faintedPokemon);
            }
        }
    }

    @Shadow(remap = false)
    private static void calcExp(PixelmonWrapper faintedPokemon, PixelmonWrapper expReceiver, double scaleFactor) {
        throw new MixinInitialisationError("Mixin failed to shadow calcExp");
    }

    @Unique
    private static ItemStack pixelTweaks$EXP_ALL_ACTIVE;

    @Unique
    private static ItemStack pixelTweaks$getExpAllActive() {
        if (pixelTweaks$EXP_ALL_ACTIVE == null) {
            pixelTweaks$EXP_ALL_ACTIVE = new ItemStack(ItemRegistration.EXP_ALL);
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Activated", true);
            ItemHelper.setTag(pixelTweaks$EXP_ALL_ACTIVE, tag);
        }
        return pixelTweaks$EXP_ALL_ACTIVE;
    }
}
