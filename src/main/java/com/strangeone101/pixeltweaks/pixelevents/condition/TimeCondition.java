package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.TimeHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.client.Minecraft;
import net.minecraft.command.impl.TimeCommand;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class TimeCondition extends Condition<World> {
    public Time time = Time.DAY;
    boolean invert = false;

    @Override
    public boolean conditionMet(World item) {
        return time.checkTime.test(item.getDayTime()) != invert;
    }

    @Override
    public World itemFromPixelmon(PixelmonEntity entity) {
        return entity.getEntityWorld();
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
