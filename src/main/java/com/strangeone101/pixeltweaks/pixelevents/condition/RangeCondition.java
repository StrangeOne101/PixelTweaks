package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;

public class RangeCondition extends Condition<Double> {
    public double range;

    @Override
    public boolean conditionMet(Double item) {
        return range >= item;
    }

    @Override
    public Double itemFromPixelmon(PixelmonEntity entity) {
        return Math.sqrt(entity.getPosition().distanceSq(Minecraft.getInstance().player.getPosition()));
    }

    @Override
    public String toString() {
        return "RangeCondition{" +
                "range=" + range +
                '}';
    }

    // Add getters and setters for the 'range' field
}
