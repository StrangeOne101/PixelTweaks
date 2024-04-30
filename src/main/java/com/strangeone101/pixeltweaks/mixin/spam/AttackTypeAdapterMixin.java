package com.strangeone101.pixeltweaks.mixin.spam;

import com.google.gson.stream.JsonReader;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.stats.AttackTypeAdapter;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.strangeone101.pixeltweaks.PixelTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;
import java.util.Set;

@Mixin(AttackTypeAdapter.class)
public class AttackTypeAdapterMixin {

    @Inject(method = "read(Lcom/google/gson/stream/JsonReader;)Lcom/pixelmonmod/pixelmon/battles/attacks/Attack;",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    public void onRead(JsonReader in, CallbackInfoReturnable<Attack> cir, String name) {
        if (!PixelTweaks.UNKNOWN_MOVES.contains(name)) {
            PixelTweaks.UNKNOWN_MOVES.add(name);
            Pixelmon.LOGGER.warn("Attack name invalid: " + name);
        }
        cir.setReturnValue(new Attack(AttackRegistry.TACKLE));
    }
}
