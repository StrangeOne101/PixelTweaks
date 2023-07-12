package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;

public class PokemonWrapperIngredientHelper implements IIngredientHelper<JEIPokemonWrapper> {

    @Override
    public IFocus<?> translateFocus(IFocus<JEIPokemonWrapper> focus, IFocusFactory focusFactory) {
        return IIngredientHelper.super.translateFocus(focus, focusFactory);
    }

    @Nullable
    @Override
    public JEIPokemonWrapper getMatch(Iterable<JEIPokemonWrapper> ingredients, JEIPokemonWrapper ingredientToMatch) {
        for (JEIPokemonWrapper ingredient : ingredients) {
            if (ingredient.equals(ingredientToMatch)) {
                return ingredient;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public JEIPokemonWrapper getMatch(Iterable<JEIPokemonWrapper> ingredients, JEIPokemonWrapper ingredientToMatch, UidContext context) {
        for (JEIPokemonWrapper ingredient : ingredients) {
            if (ingredient.equals(ingredientToMatch)) {
                return ingredient;
            }
        }

        return null;
    }

    @Override
    public String getDisplayName(JEIPokemonWrapper ingredient) {
        String species = I18n.format(ingredient.getForm().getParentSpecies().getTranslationKey());
        String form = ingredient.getForm().getLocalizedName();
        if (form != null && !form.isEmpty() && !form.equals("Default") && !form.equals("None")) species = form + " " + species;

        return species;
    }

    @Override
    public String getUniqueId(JEIPokemonWrapper ingredient) {
        return getResourceId(ingredient);
    }

    @Override
    public String getUniqueId(JEIPokemonWrapper ingredient, UidContext context) {
        return getResourceId(ingredient);
    }

    @Override
    public String getModId(JEIPokemonWrapper ingredient) {
        return "pixelmon";
    }

    @Override
    public String getDisplayModId(JEIPokemonWrapper ingredient) {
        return "Pixelmon Mod";
    }

    @Override
    public String getResourceId(JEIPokemonWrapper ingredient) {
        String species = ingredient.getForm().getParentSpecies().getName();
        String form = ingredient.getForm().getName();
        if (form != null && !form.isEmpty()) species = form + "_" + species;

        return species;
    }

    @Override
    public JEIPokemonWrapper copyIngredient(JEIPokemonWrapper ingredient) {
        return ingredient;
    }

    @Override
    public boolean isValidIngredient(JEIPokemonWrapper ingredient) {
        return true;
    }

    @Override
    public String getErrorInfo(@Nullable JEIPokemonWrapper ingredient) {
        return getResourceId(ingredient);
    }
}
