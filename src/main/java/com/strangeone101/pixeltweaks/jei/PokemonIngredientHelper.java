package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Stat;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class PokemonIngredientHelper implements IIngredientHelper<Stats> {

    @Override
    public IFocus<?> translateFocus(IFocus<Stats> focus, IFocusFactory focusFactory) {
        return IIngredientHelper.super.translateFocus(focus, focusFactory);
    }

    @Nullable
    @Override
    public Stats getMatch(Iterable<Stats> ingredients, Stats ingredientToMatch) {
        for (Stats ingredient : ingredients) {
            if (ingredientToMatch.getParentSpecies().getName().equals(ingredient.getParentSpecies().getName()) &&
                    ingredientToMatch.getName().equals(ingredient.getName())) {
                return ingredient;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Stats getMatch(Iterable<Stats> ingredients, Stats ingredientToMatch, UidContext context) {
        for (Stats ingredient : ingredients) {
            if (ingredientToMatch.getParentSpecies().getName().equals(ingredient.getParentSpecies().getName()) &&
                    ingredientToMatch.getName().equals(ingredient.getName())) {
                return ingredient;
            }
        }

        return null;
    }

    @Override
    public String getDisplayName(Stats ingredient) {
        String species = I18n.format(ingredient.getParentSpecies().getTranslationKey());
        String form = ingredient.getLocalizedName();
        if (form != null && !form.isEmpty() && !form.equals("Default")) species = form + " " + species;

        return species;
    }

    @Override
    public String getUniqueId(Stats ingredient) {
        return getResourceId(ingredient);
    }

    @Override
    public String getUniqueId(Stats ingredient, UidContext context) {
        return getResourceId(ingredient);
    }

    @Override
    public String getModId(Stats ingredient) {
        return "pixelmon";
    }

    @Override
    public String getDisplayModId(Stats ingredient) {
        return "Pixelmon";
    }

    @Override
    public String getResourceId(Stats ingredient) {
        String species = ingredient.getParentSpecies().getName();
        String form = ingredient.getName();
        if (form != null && !form.isEmpty()) species = form + "_" + species;

        return species;
    }

    @Override
    public Stats copyIngredient(Stats ingredient) {
        return ingredient.clone();
    }

    @Override
    public boolean isValidIngredient(Stats ingredient) {
        return true;
    }

    @Override
    public String getErrorInfo(@Nullable Stats ingredient) {
        return getResourceId(ingredient);
    }
}
