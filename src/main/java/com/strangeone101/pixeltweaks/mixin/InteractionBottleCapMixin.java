package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.pokemon.BottleCapEvent;
import com.pixelmonmod.pixelmon.api.interactions.IInteraction;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreenPacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.entities.pixelmon.interactions.InteractionBottleCap;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import com.pixelmonmod.pixelmon.enums.items.EnumBottleCap;
import com.pixelmonmod.pixelmon.items.BottlecapItem;
import com.strangeone101.pixeltweaks.TweaksConfig;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InteractionBottleCap.class)
public abstract class InteractionBottleCapMixin implements IInteraction {

    @Override
    public boolean processInteract(PixelmonEntity pixelmon, Player player, InteractionHand hand, ItemStack itemstack) {
        if (!player.level().isClientSide && hand != InteractionHand.OFF_HAND && itemstack.getItem() instanceof BottlecapItem) {
            Pokemon data = pixelmon.getPokemon();
            if (data.getOwnerPlayer() != player) {
                return false;
            } else if (data.getPokemonLevel() < TweaksConfig.hypertrainLevel.get()) {
                ChatHandler.sendChat(player, "pixelmon.interaction.bottlecap.level", new Object[]{pixelmon.getNickname(), TweaksConfig.hypertrainLevel.get()});
                return true;
            } else {
                IVStore ivs = data.getIVs();
                boolean isMax = ivs.getStat(BattleStatsType.HP) + ivs.getStat(BattleStatsType.ATTACK) + ivs.getStat(BattleStatsType.DEFENSE) + ivs.getStat(BattleStatsType.SPECIAL_ATTACK) + ivs.getStat(BattleStatsType.SPECIAL_DEFENSE) + ivs.getStat(BattleStatsType.SPEED) == 186;
                boolean isHt = ivs.isHyperTrained(BattleStatsType.HP) && ivs.isHyperTrained(BattleStatsType.ATTACK) && ivs.isHyperTrained(BattleStatsType.DEFENSE) && ivs.isHyperTrained(BattleStatsType.SPECIAL_ATTACK) && ivs.isHyperTrained(BattleStatsType.SPECIAL_DEFENSE) && ivs.isHyperTrained(BattleStatsType.SPEED);
                if (!isMax && !isHt) {
                    BottlecapItem bottleCap = (BottlecapItem)itemstack.getItem();
                    if (Pixelmon.EVENT_BUS.post(new BottleCapEvent(pixelmon, player, bottleCap.type, itemstack))) {
                        return false;
                    } else {
                        if (bottleCap.type == EnumBottleCap.GOLD) {
                            ivs.setHyperTrained((BattleStatsType)null, true);
                            data.getStats().setLevelStats(data.getNature(), data.getForm(), data.getPokemonLevel());
                            data.markDirty(new EnumUpdateType[]{EnumUpdateType.HP, EnumUpdateType.Stats});
                            ChatHandler.sendChat(player, "pixelmon.interaction.bottlecap.goldcap", new Object[]{pixelmon.getNickname()});
                            itemstack.shrink(1);
                        } else {
                            BattleStatsType[] types = BattleStatsType.getEVIVStatValues();
                            int[] screenData = new int[types.length + 1];

                            for(int i = 0; i < types.length; ++i) {
                                screenData[i] = !ivs.isHyperTrained(types[i]) && ivs.getStat(types[i]) != 31 ? getHTValue(types[i], data) : 0;
                            }

                            screenData[6] = pixelmon.getId();
                            OpenScreenPacket.open(player, EnumGuiScreen.BottleCap, screenData);
                        }

                        return true;
                    }
                } else {
                    ChatHandler.sendChat(player, "pixelmon.interaction.bottlecap.full", new Object[]{pixelmon.getNickname()});
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    @Shadow(remap = false)
    private static int getHTValue(BattleStatsType type, Pokemon pokemon) {
        return 0;
    }
}
