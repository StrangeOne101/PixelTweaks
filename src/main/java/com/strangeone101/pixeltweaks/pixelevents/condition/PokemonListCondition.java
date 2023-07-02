package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

import java.util.List;

public class PokemonListCondition extends Condition<PixelmonEntity> {
    public List<PokemonSpecification> spec;
    public Boolean wild;
    public boolean invert;

    @Override
    public boolean conditionMet(PixelmonEntity pokemon) {
        if (wild != null && ((pokemon.getPokemon().getOriginalTrainer() == null) != wild)) return invert;
        if (spec != null) {
            for (PokemonSpecification s : spec) {
                if (s.matches(pokemon.getPokemon())) return !invert;
            }
            return invert;
        }

        return !invert;
    }

    @Override
    public PixelmonEntity itemFromPixelmon(PixelmonEntity entity) {
        return entity;
    }

    @Override
    public String toString() {
        return "PokemonListCondition{" +
                "spec='" + spec + '\'' +
                ", wild=" + wild +
                ", invert=" + invert +
                '}';
    }

    // Add getters and setters for the 'species' field
}
