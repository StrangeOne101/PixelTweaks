package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import com.strangeone101.pixeltweaks.struct.SpecificTime;
import jdk.jfr.Experimental;

@Experimental
public class CalendarCondition extends Condition<Void> {

    public boolean invert = false;
    public SpecificTime start;
    public SpecificTime end;

    @Override
    protected boolean conditionMet(Void item) {
        return SpecificTime.isBetween(start, end) != invert;
    }

    @Override
    public Void itemFromPixelmon(PixelmonEntity entity) {
        return null;
    }
}
