package com.strangeone101.pixeltweaks.pixelevents.condition;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.pixelevents.Condition;

public class PokemonCondition extends Condition<PixelmonEntity> {
    public PokemonSpecification spec;
    public Boolean wild;
    public boolean invert;

    @Override
    public boolean conditionMet(PixelmonEntity pokemon) {
        if (wild != null && (pokemon.isTame() != wild) != invert) return false;
        if (spec != null && spec.matches(pokemon.getPokemon()) != invert) return false;

        return true;
    }

    @Override
    public PixelmonEntity itemFromPixelmon(PixelmonEntity entity) {
        return entity;
    }

    @Override
    public String toString() {
        return "PokemonCondition{" +
                "spec='" + spec + '\'' +
                ", wild=" + wild +
                ", invert=" + invert +
                '}';
    }

    // Add getters and setters for the 'species' field
}
