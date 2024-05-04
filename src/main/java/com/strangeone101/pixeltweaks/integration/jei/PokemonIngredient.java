package com.strangeone101.pixeltweaks.integration.jei;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.requirement.impl.GenderRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PaletteRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;

import java.util.Objects;
import java.util.Optional;

public class PokemonIngredient {

    private Stats stats;
    private Optional<Gender> gender = Optional.empty();
    private Optional<PaletteProperties> palette = Optional.empty();

    protected Pokemon buildPokemon;

    public PokemonIngredient(PokemonSpecification specification) {
        this.buildPokemon = specification.create();

        this.stats = buildPokemon.getForm();

        if (specification.getValue(GenderRequirement.class).isPresent()) {
            gender = Optional.of(buildPokemon.getGender());
        }
        if (specification.getValue(PaletteRequirement.class).isPresent()) {
            palette = Optional.of(buildPokemon.getPalette());
        }
    }

    public Optional<Gender> getGender() {
        return gender;
    }

    public Optional<PaletteProperties> getPalette() {
        return palette;
    }

    public Stats getForm() {
        return stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonIngredient that = (PokemonIngredient) o;

        return stats.getParentSpecies().equals(that.stats.getParentSpecies()) && stats.getName().equalsIgnoreCase(that.stats.getName()) && Objects.equals(gender, that.gender) && Objects.equals(palette, that.palette);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stats, gender, palette);
    }
}
