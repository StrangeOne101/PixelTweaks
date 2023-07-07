package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class DropsRecipe {

    public PokemonSpecification specification;

    public transient Pokemon builtPokemon;

    public DropsRecipe(PokemonSpecification specification) {
        this.specification = specification;

        this.builtPokemon = specification.create();
    }
}
