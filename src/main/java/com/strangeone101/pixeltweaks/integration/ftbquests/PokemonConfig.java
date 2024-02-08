package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.api.pokemon.requirement.impl.BossRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.FormRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.PaletteRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.TypeRequirement;
import com.pixelmonmod.api.requirement.Requirement;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.boss.BossTierRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.GenderProperties;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.strangeone101.pixeltweaks.mixin.AbstractSpecificationMixin;
import dev.ftb.mods.ftblibrary.config.ConfigFromString;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PokemonConfig extends ConfigFromString<PokemonSpecification> {

    private static final Map<Integer, Set<String>> CACHE = new HashMap<>();

    private boolean requiresSpecies;

    public PokemonConfig(boolean requiresSpecies) {
        this.requiresSpecies = requiresSpecies;
    }

    @Override
    public String getStringFromValue(PokemonSpecification v) {
        return v == null ? "" : v.toString();
    }

    @Override
    public boolean parse(Consumer<PokemonSpecification> consumer, String s) {
        try {
            if (s.isEmpty()) { //If the spec is blank, return a null spec, but only if it doesn't require a species
                if (!requiresSpecies) {
                    if (consumer != null) consumer.accept(null);
                    return true;
                }
                return false;
            }

            if (s.charAt(0) == '*') { //Override the parsing of the spec
                if (consumer != null) consumer.accept(PokemonSpecificationProxy.create(s.substring(1)));
                return true;
            }

            PokemonSpecification pokemonSpecification = PokemonSpecificationProxy.create(s);

            String[] args = s.split(" ");
            if (!requiresSpecies && args[0].equalsIgnoreCase("random")) {
                return false;
            } else if (requiresSpecies) {
                if (args[0].equalsIgnoreCase("random")) {
                    if (consumer != null) consumer.accept(pokemonSpecification);
                    return true;
                }
                if (!pokemonSpecification.getValue(SpeciesRequirement.class).isPresent()
                        || pokemonSpecification.getValue(SpeciesRequirement.class).get().getKey().equalsIgnoreCase("MISSINGNO")) {
                    return false;
                }
            }

            int toMatch = args.length;
            if (args[0].equalsIgnoreCase("random")) {
                toMatch--;
            }

            List<Requirement<?,?,?>> requirements = ((AbstractSpecificationMixin)pokemonSpecification).getRequirements();
            if (requirements.size() != toMatch) { //A quick match to see if some spec requirement was missed or not parsed at all
                return false;
            }

            for (Requirement<?, ?, ?> requirement : requirements) { //Check all requirements and make sure they are correct
                if (!isCorrect(requirement, pokemonSpecification)) {
                    return false;
                }
            }

            if (consumer != null) consumer.accept(pokemonSpecification);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCorrect(Requirement<?, ?, ?> requirement, PokemonSpecification s) {
        if (requirement instanceof SpeciesRequirement) {
            return ((SpeciesRequirement)requirement).getValue().getValue().isPresent();
        } else if (requirement instanceof TypeRequirement) { //Types will parse to Normal type if the type isn't valid. So we manually check
            int place = ((TypeRequirement)requirement).getValue().getFirst();
            String stringPlace = "type:";
            if (place != -1) stringPlace = "type" + place + ":";

            int inString = s.toString().indexOf(stringPlace);
            if (inString == -1) return false;

            String type = s.toString().substring(inString + stringPlace.length()).split(" ")[0];

            return Element.hasType(type.toUpperCase());
        } else if (requirement instanceof FormRequirement) {
            String form = ((FormRequirement)requirement).getValue().equalsIgnoreCase("none") //Default form
                    ? "" : ((FormRequirement)requirement).getValue();

            //If they specified a species, check if that species has the form
            if (s.getValue(SpeciesRequirement.class).isPresent() && !s.toString().split(" ")[0].equalsIgnoreCase("random")) {
                return form.equals("base") || form.equals("none") || s.getValue(SpeciesRequirement.class).get().getValue().get().hasForm(form);
            }
            //Otherwise, check if any pokemon has the form
            return PixelmonSpecies.getAll().stream() //Within all pokemon
                    .anyMatch(species -> species.hasForm(form));
        } else if (requirement instanceof PaletteRequirement) {
            if (s.getValue(SpeciesRequirement.class).isPresent() && !s.toString().split(" ")[0].equalsIgnoreCase("random")) {
                Species species = s.getValue(SpeciesRequirement.class).get().getValue().get();

                //We use a cache system since typing will do this every character, and this is quite the stream filter
                if (!CACHE.containsKey(species.getDex())) {
                    CACHE.put(species.getDex(), species.getForms().stream() //Within all forms
                            .map(Stats::getGenderProperties).flatMap(Collection::stream) //Within all genders
                            .map(GenderProperties::getPalettes).flatMap(Arrays::stream) //Within all palettes
                                    .map(palette -> palette.getName().toLowerCase()) //Cache the names of all the palettes
                            .collect(Collectors.toSet()));
                }

                return CACHE.get(species.getDex()).contains(((PaletteRequirement)requirement).getValue().toLowerCase()); //Check if it exists
            } else {
                if (!CACHE.containsKey(-1)) {
                    CACHE.put(-1, PixelmonSpecies.getAll().stream() //Within all pokemon
                            .map(Species::getForms).flatMap(Collection::stream) //Within all forms
                            .map(Stats::getGenderProperties).flatMap(Collection::stream) //Within all genders
                            .map(GenderProperties::getPalettes).flatMap(Arrays::stream) //Within all palettes
                            .map(palette -> palette.getName().toLowerCase()) //Cache the names of all the palettes
                            .collect(Collectors.toSet()));
                }
                return CACHE.get(-1).contains(((PaletteRequirement)requirement).getValue().toLowerCase()); //Check if it exists
            }
        } else if (requirement instanceof BossRequirement) {
            return ((BossRequirement)requirement).getValue() != null && BossTierRegistry.getBossTier(((BossRequirement)requirement).getValue()).isPresent();
        }
        return requirement.fits(s.toString()) && requirement.getValue() != null;
    }
}
