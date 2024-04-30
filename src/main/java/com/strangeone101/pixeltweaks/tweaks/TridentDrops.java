package com.strangeone101.pixeltweaks.tweaks;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.GeneralConfig;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TridentDrops {

    private Species[] species = {PixelmonSpecies.FERALIGATR.getValueUnsafe(),
            PixelmonSpecies.CLAWITZER.getValueUnsafe(), PixelmonSpecies.HUNTAIL.getValueUnsafe(), PixelmonSpecies.KYOGRE.getValueUnsafe(),
            PixelmonSpecies.SAMUROTT.getValueUnsafe(), PixelmonSpecies.EMPOLEON.getValueUnsafe(), PixelmonSpecies.KELDEO.getValueUnsafe(),
            PixelmonSpecies.KABUTOPS.getValueUnsafe(), PixelmonSpecies.CRAWDAUNT.getValueUnsafe()};

    public TridentDrops() {
        Pixelmon.EVENT_BUS.addListener(this::onDropEvent);
    }

    public void onDropEvent(DropEvent event) {
        if (event.entity instanceof PixelmonEntity && ((PixelmonEntity) event.entity).getPokemon().isPokemon(species)) {
            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                event.addDrop(new ItemStack(Items.TRIDENT));
            }
        }
    }
}
