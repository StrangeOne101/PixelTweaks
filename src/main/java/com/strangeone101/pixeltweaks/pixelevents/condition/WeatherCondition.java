package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class WeatherCondition extends Condition<World> {
    public WeatherType weather = WeatherType.CLEAR;
    boolean invert = false;

    @Override
    public boolean conditionMet(World item) {
        return WeatherType.get(item) == weather != invert;
    }

    @Override
    public World itemFromPixelmon(PixelmonEntity entity) {
        return entity.getEntityWorld();
    }

    @Override
    public String toString() {
        return "WeatherCondition{" +
                "weather=" + weather.name() +
                ", invert=" + invert +
                '}';
    }
}
