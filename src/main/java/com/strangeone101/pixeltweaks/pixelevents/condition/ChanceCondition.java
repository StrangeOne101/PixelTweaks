package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCondition extends Condition<Void> {

    public double chance = 1.0;

    @Override
    protected boolean conditionMet(Void item) {
        return ThreadLocalRandom.current().nextDouble() >= chance;
    }

    @Override
    public Void itemFromPixelmon(PixelmonEntity entity) {
        return null;
    }
}
