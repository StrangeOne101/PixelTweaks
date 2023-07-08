package com.strangeone101.pixeltweaks.client.overlay;

import com.google.common.collect.Sets;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import com.strangeone101.pixeltweaks.pixelevents.Event;
import com.strangeone101.pixeltweaks.pixelevents.EventRegistry;
import com.strangeone101.pixeltweaks.pixelevents.IValidator;
import com.strangeone101.pixeltweaks.pixelevents.condition.PokemonCondition;
import com.strangeone101.pixeltweaks.pixelevents.condition.PokemonListCondition;

import java.util.*;
import java.util.stream.Collectors;

public class PokemonOverlay extends Event implements IValidator {

    public static Map<PokemonSpecification, Set<PokemonOverlay>> NON_SPECIES_EVENT = new LinkedHashMap<>();
    public static Map<Species, Map<PokemonSpecification, Set<PokemonOverlay>>> SPECIES_EVENTS = new HashMap<>();

    public OverlayLayer[] layers;

    public int dualModelLayer = 0;

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public boolean validate() {
        return this.conditions.stream().anyMatch(c -> c instanceof PokemonCondition || c instanceof PokemonListCondition) && layers.length > 0;
    }

    @Override
    public String getError() {
        return "At least one condition must be a pokemon condition and there must be at least one layer!";
    }

    public static void onAllRegistered() {
        Collection<PokemonOverlay> events = EventRegistry.getEvents(PokemonOverlay.class);

        for (PokemonOverlay overlay : events) {
            for (Condition c : overlay.conditions) {
                if (c instanceof PokemonCondition) {
                    PokemonSpecification spec = ((PokemonCondition) c).spec;
                    if (spec.getRequirement(SpeciesRequirement.class).isPresent()) {
                        Species species = spec.create().getSpecies();
                        PokemonOverlay.SPECIES_EVENTS.putIfAbsent(species, new HashMap<>());

                        PokemonOverlay.SPECIES_EVENTS.get(species).putIfAbsent(spec, new LinkedHashSet<>());
                        PokemonOverlay.SPECIES_EVENTS.get(species).get(spec).add(overlay);
                    } else {
                        PokemonOverlay.NON_SPECIES_EVENT.putIfAbsent(spec, new LinkedHashSet<>());
                        PokemonOverlay.NON_SPECIES_EVENT.get(spec).add(overlay);
                    }

                } else if (c instanceof PokemonListCondition) {
                    ((PokemonListCondition) c).spec.forEach(spec -> {
                        if (spec.getRequirement(SpeciesRequirement.class).isPresent()) {
                            Species species = spec.create().getSpecies();
                            PokemonOverlay.SPECIES_EVENTS.putIfAbsent(species, new HashMap<>());

                            PokemonOverlay.SPECIES_EVENTS.get(species).putIfAbsent(spec, new LinkedHashSet<>());
                            PokemonOverlay.SPECIES_EVENTS.get(species).get(spec).add(overlay);
                        } else {
                            PokemonOverlay.NON_SPECIES_EVENT.putIfAbsent(spec, new LinkedHashSet<>());
                            PokemonOverlay.NON_SPECIES_EVENT.get(spec).add(overlay);
                        }
                    });
                }
            };

            //Remove any pokemon conditions from the overlay as they are not needed anymore
            overlay.conditions = overlay.conditions.stream().filter(c -> !(c instanceof PokemonCondition || c instanceof PokemonListCondition)).collect(Collectors.toList());
        }
    }

    private static TreeSet<PokemonOverlay> newTreeset() {
        return Sets.newTreeSet(
                (e1, e2) -> {
                    if (e1.getPriority() == e2.getPriority()) return e2.hashCode() - e1.hashCode();
                    return e2.getPriority() - e1.getPriority();
                });
    }

    @Override
    public String toString() {
        return "PokemonOverlay{" +
                "layers=" + Arrays.toString(layers) +
                ", dualModelLayer=" + dualModelLayer +
                ", conditions=" + conditions +
                ", priority=" + priority +
                '}';
    }
}
