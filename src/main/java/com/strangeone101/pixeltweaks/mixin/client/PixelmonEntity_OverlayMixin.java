package com.strangeone101.pixeltweaks.mixin.client;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.AbstractHoldsItemsEntity;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.client.overlay.PixelmonEntityLayerExtension;
import com.strangeone101.pixeltweaks.client.overlay.PokemonOverlay;
import com.strangeone101.pixeltweaks.pixelevents.Condition;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@OnlyIn(Dist.CLIENT)
@Mixin(PixelmonEntity.class)
public class PixelmonEntity_OverlayMixin extends AbstractHoldsItemsEntity implements PixelmonEntityLayerExtension {

    @Unique
    @OnlyIn(Dist.CLIENT)
    private PokemonOverlay pixelTweaks$overlay;

    public PixelmonEntity_OverlayMixin(EntityType<PixelmonEntity> type, Level par1World) {
        super(type, par1World);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void setPTOverlay(PokemonOverlay overlay) {
        this.pixelTweaks$overlay = overlay;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PokemonOverlay getPTOverlay() {
        return pixelTweaks$overlay;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setPokemon(Pokemon pokemon) {
        super.setPokemon(pokemon);

        this.pixelTweaks$set(pokemon);
    }

    @Unique
    private void pixelTweaks$set(Pokemon pokemon) {
        if (PokemonOverlay.SPECIES_EVENTS.containsKey(pokemon.getSpecies())) {
            for (PokemonSpecification spec : PokemonOverlay.SPECIES_EVENTS.get(pokemon.getSpecies()).keySet()) {
                if (spec.matches(pokemon)) {
                    for (PokemonOverlay overlay : PokemonOverlay.SPECIES_EVENTS.get(pokemon.getSpecies()).get(spec)) {
                        if (overlay.conditions.stream().allMatch(c -> c.conditionMet((PixelmonEntity)((Object)this)))) {
                            this.setPTOverlay(overlay);
                            break;
                        }
                    }
                }
            }
        } else {
                for (PokemonSpecification spec : PokemonOverlay.NON_SPECIES_EVENT.keySet()) {
                if (spec.matches(pokemon)) {
                    outer:
                    for (PokemonOverlay overlay : PokemonOverlay.NON_SPECIES_EVENT.get(spec)) {
                        for (Condition c : overlay.conditions) {
                            if (!c.conditionMet((PixelmonEntity)((Object)this))) {
                                PixelTweaks.LOGGER.debug("Failed " + c.toString());
                                continue outer;
                            }
                            PixelTweaks.LOGGER.debug("Passed " + c.toString());
                        }
                        this.setPTOverlay(overlay);
                    }
                }
            }
        }
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (key.getId() == PokemonBase.SYNC_POKEMON_BASE.getParameterId()) {
            this.pixelTweaks$set(this.pokemon);
        }
    }
}
