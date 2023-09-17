package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.api.battles.BattleItemScanner;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.battles.controller.Experience;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
                Set<PixelmonWrapper> attackers = faintedPokemon.getAttackers();
                for (PixelmonWrapper pw : attackers) {
                    if (pw.getParticipant() == teamOwner)
                        calcExp(faintedPokemon, pw, 1.0D);
                }
                ItemStack expAllFound = BattleItemScanner.findMatchingItem(pixelTweaks$getExpAllActive(), player.player);
                boolean hasExpAll = expAllFound != null;

                for (PixelmonWrapper pw : teamOwner.allPokemon) {
                    if (!attackers.contains(pw)) {
                        if (hasExpAll || pw.getHeldItem().getHeldItemType() == EnumHeldItems.expShare) {
                            calcExp(faintedPokemon, pw, 0.5D);
                        }
                    }
                }
                player.givePlayerExp(faintedPokemon);
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
            pixelTweaks$EXP_ALL_ACTIVE = new ItemStack(PixelmonItems.exp_all);
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("Activated", true);
            pixelTweaks$EXP_ALL_ACTIVE.setTag(tag);
        }
        return pixelTweaks$EXP_ALL_ACTIVE;
    }
}
