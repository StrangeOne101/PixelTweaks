package com.strangeone101.pixeltweaks.integration.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class PokemonIngredientHelper implements IIngredientHelper<PokemonIngredient> {


    @Override
    public IIngredientType<PokemonIngredient> getIngredientType() {
        return JEIIntegration.WRAPPED_POKEMON;
    }

    @Override
    public String getDisplayName(PokemonIngredient ingredient) {
        String species = I18n.get(ingredient.getForm().getParentSpecies().getTranslationKey());
        String form = ingredient.getForm().getLocalizedName();
        if (form != null && !form.isEmpty() && !form.equals("Default") && !form.equals("None")) species = form + " " + species;

        return species;
    }

    @Override
    public String getUniqueId(PokemonIngredient ingredient, UidContext context) {
        return getResourceId(ingredient);
    }

    @Override
    public String getDisplayModId(PokemonIngredient ingredient) {
        return "pixelmon";
    }

    @Override
    public ResourceLocation getResourceLocation(PokemonIngredient pokemonIngredient) {
        return new ResourceLocation("pixelmon", getResourceId(pokemonIngredient));
    }

    public String getResourceId(PokemonIngredient ingredient) {
        String species = ingredient.getForm().getParentSpecies().getName();
        String form = ingredient.getForm().getName();
        if (form != null && !form.isEmpty()) species = form + "_" + species;

        return species;
    }

    @Override
    public PokemonIngredient copyIngredient(PokemonIngredient ingredient) {
        return ingredient;
    }

    @Override
    public boolean isValidIngredient(PokemonIngredient ingredient) {
        return true;
    }

    @Override
    public String getErrorInfo(@Nullable PokemonIngredient ingredient) {
        return getResourceId(ingredient);
    }
}
