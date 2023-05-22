package com.strangeone101.pixeltweaks;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.util.DamageSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class DamageHandler {

    private static final Map<String, BiFunction<DamageSource, PixelmonEntity, Optional<Boolean>>> DAMAGE_SOURCE_HANDLERS = new HashMap<>();


    public static void registerPixelmonDamageSourceHandler(String name, BiFunction<DamageSource, PixelmonEntity, Optional<Boolean>> handler) {
        DAMAGE_SOURCE_HANDLERS.put(name, handler);
    }

    public static Collection<BiFunction<DamageSource, PixelmonEntity, Optional<Boolean>>> getPixelmonHandlers() {
        return DAMAGE_SOURCE_HANDLERS.values();
    }
}
