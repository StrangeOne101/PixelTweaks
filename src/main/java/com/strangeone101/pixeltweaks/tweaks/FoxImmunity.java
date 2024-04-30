package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.api.interactions.IInteraction;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.DamageHandler;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class FoxImmunity {

    public FoxImmunity() {
        if (TweaksConfig.foxesLoveBerries.get()) {
            PixelTweaks.LOGGER.info("Registered fox immunity tweak");

            MinecraftForge.EVENT_BUS.addListener(this::onEntityTakeDamage);
            PixelmonEntity.interactionList.add(new FoxInteraction());
            DamageHandler.registerPixelmonDamageSourceHandler("berry_bushes", (source, entity) -> {
                if (isFox(entity)) {
                    return Optional.of(false);
                }
                return Optional.empty();
            });
        }
    }

    public void onEntityTakeDamage(LivingDamageEvent event) {
        if (event.getSource().is(DamageTypes.SWEET_BERRY_BUSH) && event.getEntity() instanceof PixelmonEntity) {
            if (isFox((PixelmonEntity) event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean isFox(PixelmonEntity entity) {
        switch (entity.getSpecies().getDex()) {
            case 37: //Vulpix
            case 38: //Ninetales
            case 570: //Zorua
            case 571: //Zoroark
            case 653: //Fennekin
            case 654: //Braixen
            case 655: //Delphox
            case 827: //Nickit
            case 828: //Thievul
            case 133: //Eevee
                return true;
            default:
                return false;
        }
    }

    public static class FoxInteraction implements IInteraction {

        @Override
        public boolean processInteract(PixelmonEntity pixelmonEntity, Player playerEntity, InteractionHand hand, ItemStack itemStack) {
            if (playerEntity instanceof ServerPlayer && isFox(pixelmonEntity) && itemStack.getItem() == Items.SWEET_BERRIES) {
                if (pixelmonEntity.getPokemon().getHealth() < pixelmonEntity.getPokemon().getMaxHealth()) {
                    pixelmonEntity.getPokemon().setHealth(Math.min(pixelmonEntity.getPokemon().getHealth() + 5, pixelmonEntity.getPokemon().getMaxHealth()));
                    pixelmonEntity.level().playSound(null, pixelmonEntity.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1F, 1F);

                    if (!playerEntity.isCreative()) {
                        itemStack.shrink(1);
                    }
                }

                return true;
            }

            return false;
        }
    }

}
