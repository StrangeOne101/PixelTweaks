package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.TimeHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class TimeCondition extends Condition<Level> {
    public Time time = Time.DAY;
    boolean invert = false;

    @Override
    public boolean conditionMet(Level item) {
        return time.checkTime.test(item.getDayTime()) != invert;
    }

    @Override
    public Level itemFromPixelmon(PixelmonEntity entity) {
        return entity.level();
    }

    @Override
    public String toString() {
        return "TimeCondition{" +
                "time=" + time.name() +
                ", invert=" + invert +
                '}';
    }

    public static enum Time {
        DAY(time -> time >= 1000 && time < 13000),
        NIGHT(time -> time < 1000 || time >= 13000);

        private final Predicate<Long> checkTime;

        Time(Predicate<Long> checkTime) {
            this.checkTime = checkTime;
        }
    }

    // Add getters and setters for the 'range' field
}
