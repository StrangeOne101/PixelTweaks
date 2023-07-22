package com.strangeone101.pixeltweaks.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;

public class PokemonIngredientHelper implements IIngredientHelper<PokemonIngredient> {

    @Override
    public IFocus<?> translateFocus(IFocus<PokemonIngredient> focus, IFocusFactory focusFactory) {
        return IIngredientHelper.super.translateFocus(focus, focusFactory);
    }

    @Nullable
    @Override
    public PokemonIngredient getMatch(Iterable<PokemonIngredient> ingredients, PokemonIngredient ingredientToMatch) {
        for (PokemonIngredient ingredient : ingredients) {
            if (ingredient.equals(ingredientToMatch)) {
                return ingredient;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public PokemonIngredient getMatch(Iterable<PokemonIngredient> ingredients, PokemonIngredient ingredientToMatch, UidContext context) {
        for (PokemonIngredient ingredient : ingredients) {
            if (ingredient.equals(ingredientToMatch)) {
                return ingredient;
            }
        }

        return null;
    }

    @Override
    public String getDisplayName(PokemonIngredient ingredient) {
        String species = I18n.format(ingredient.getForm().getParentSpecies().getTranslationKey());
        String form = ingredient.getForm().getLocalizedName();
        if (form != null && !form.isEmpty() && !form.equals("Default") && !form.equals("None")) species = form + " " + species;

        return species;
    }

    @Override
    public String getUniqueId(PokemonIngredient ingredient) {
        return getResourceId(ingredient);
    }

    @Override
    public String getUniqueId(PokemonIngredient ingredient, UidContext context) {
        return getResourceId(ingredient);
    }

    @Override
    public String getModId(PokemonIngredient ingredient) {
        return "pixelmon";
    }

    @Override
    public String getDisplayModId(PokemonIngredient ingredient) {
        return "Pixelmon Mod";
    }

    @Override
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
