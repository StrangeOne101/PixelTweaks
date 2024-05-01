package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.world.level.Level;

public class WeatherCondition extends Condition<Level> {
    public WeatherType weather = WeatherType.CLEAR;
    boolean invert = false;

    @Override
    public boolean conditionMet(Level item) {
        return WeatherType.get(item) == weather != invert;
    }

    @Override
    public Level itemFromPixelmon(PixelmonEntity entity) {
        return entity.level();
    }

    @Override
    public String toString() {
        return "WeatherCondition{" +
                "weather=" + weather.name() +
                ", invert=" + invert +
                '}';
    }
}
