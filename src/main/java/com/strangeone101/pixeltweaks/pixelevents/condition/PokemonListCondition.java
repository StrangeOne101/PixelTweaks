package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

import java.util.List;

public class PokemonListCondition extends Condition<Pokemon> {
    public List<PokemonSpecification> spec;
    public Boolean wild;

    @Override
    public boolean conditionMet(Pokemon pokemon) {
        if (wild != null && (pokemon.getOriginalTrainer() == null) != wild) return false;
        if (spec != null) {
            for (PokemonSpecification s : spec) {
                if (s.matches(pokemon)) return true;
            }
            return false;
        }

        return true;
    }

    @Override
    public Pokemon itemFromPixelmon(PixelmonEntity entity) {
        return entity.getPokemon();
    }

    @Override
    public String toString() {
        return "PokemonListCondition{" +
                "spec='" + spec + '\'' +
                ", wild=" + wild +
                '}';
    }

    // Add getters and setters for the 'species' field
}
