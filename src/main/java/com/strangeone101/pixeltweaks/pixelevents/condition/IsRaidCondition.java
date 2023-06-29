package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

public class IsRaidCondition extends Condition<PixelmonEntity> {

    @Override
    public boolean conditionMet(PixelmonEntity entity) {
        return entity.isRaidPokemon();
    }

    @Override
    public PixelmonEntity itemFromPixelmon(PixelmonEntity entity) {
        return entity;
    }
}
