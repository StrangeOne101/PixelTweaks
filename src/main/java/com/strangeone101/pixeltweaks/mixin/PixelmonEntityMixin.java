package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.pixelmon.entities.pixelmon.AbstractHoldsItemsEntity;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.DamageHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PixelmonEntity.class)
public abstract class PixelmonEntityMixin extends AbstractHoldsItemsEntity {


    public PixelmonEntityMixin(EntityType<PixelmonEntity> type, Level par1World) {
        super(type, par1World);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        DamageHandler.getPixelmonHandlers().forEach(f -> {
            Optional<Boolean> b = f.apply(source, (PixelmonEntity) (Object) this);
            b.ifPresent(cir::setReturnValue);
        });
    }
}
