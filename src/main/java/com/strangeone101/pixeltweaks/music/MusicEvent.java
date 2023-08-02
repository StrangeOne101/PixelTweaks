package com.strangeone101.pixeltweaks.music;

import com.google.common.collect.Sets;
import com.strangeone101.pixeltweaks.pixelevents.Event;
import com.strangeone101.pixeltweaks.pixelevents.EventListener;
import com.strangeone101.pixeltweaks.pixelevents.IValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MusicEvent extends Event implements IValidator {

    public Music music;
    public Sound sound;

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public boolean validate() {
        return (music == null) != (sound == null); //One of them HAS to be null, but not both
    }

    @Override
    public String getError() {
        return "Music or Sound must be provided!";
    }

    public static class BGM extends MusicEvent {}

    public static class Battle extends MusicEvent {}

    public static class BattleAction extends MusicEvent implements EventListener {

        public static Map<Action, Set<BattleAction>> REGISTRY = new HashMap<>();

        public Action action;

        public enum Action {
            HIT, FAINT, CATCH, CATCH_FAIL, RUN, START, WIN, WIPEOUT, SWITCH, THROW_POKEBALL,
            SUPER_EFFECTIVE_HIT, NOT_VERY_EFFECTIVE_HIT, EFFECTIVE_HIT, HIT_MISS, HIT_NO_EFFECT, HIT_CRITICAL, HIT_FAIL, FLINCH,
            STATS_UP, STATS_DOWN, STATS_UP_HARSH, STATS_DOWN_HARSH, BERRY_EAT, POTION, REVIVE,
            WRAP, BURN, SLEEP, FREEZE, PARALYZE, POISON, CONFUSION, DROWSY, GETTING_PUMPED, LOVE,
            SPIKES, WEATHER_SUNNY, WEATHER_RAINY, WEATHER_SNOW, WEATHER_SANDSTORM, WEATHER_HAIL, WEATHER_VERY_SUNNY, WEATHER_VERY_RAINY,
            MEGA_EVOLVE, DYNAMAX, DYNAMAX_LOST, ZMOVE, TERASTALLIZE
        }

        @Override
        public boolean validate() {
            return super.validate() && action != null;
        }

        @Override
        public String getError() {
            return "Music or sound music be provided with a battle action!";
        }

        @Override
        public void onRegister() {
            REGISTRY.putIfAbsent(this.action, Sets.newTreeSet(
                    (e1, e2) -> {
                        if (e1.getPriority() == e2.getPriority()) return e2.hashCode() - e1.hashCode();
                        return e2.getPriority() - e1.getPriority();
                    }));

            REGISTRY.get(this.action).add(this);
        }
    }
}
