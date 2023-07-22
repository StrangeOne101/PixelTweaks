package com.strangeone101.pixeltweaks.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;


import javax.annotation.Nullable;

public class BiomeIngredientHelper implements IIngredientHelper<ResourceLocation> {
    @Nullable
    @Override
    public ResourceLocation getMatch(Iterable<ResourceLocation> ingredients, ResourceLocation ingredientToMatch) {
        return getMatch(ingredients, ingredientToMatch, UidContext.Ingredient);
    }

    @Nullable
    @Override
    public ResourceLocation getMatch(Iterable<ResourceLocation> ingredients, ResourceLocation ingredientToMatch, UidContext context) {
        for (ResourceLocation loc : ingredients) {
            if (loc.equals(ingredientToMatch)) return loc;
        }

        return null;
    }

    @Override
    public String getDisplayName(ResourceLocation ingredient) {
        return I18n.format("biome." + ingredient.getNamespace() + "." + ingredient.getPath());
    }

    @Override
    public String getUniqueId(ResourceLocation ingredient) {
        return ingredient.toString();
    }

    @Override
    public String getUniqueId(ResourceLocation ingredient, UidContext context) {
        return ingredient.toString();
    }

    @Override
    public String getModId(ResourceLocation ingredient) {
        return ingredient.getNamespace();
    }

    @Override
    public String getDisplayModId(ResourceLocation ingredient) {
        return ingredient.getNamespace();
    }

    @Override
    public String getResourceId(ResourceLocation ingredient) {
        return ingredient.toString();
    }

    @Override
    public ResourceLocation copyIngredient(ResourceLocation ingredient) {
        return ingredient;
    }

    @Override
    public boolean isValidIngredient(ResourceLocation ingredient) {
        return Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY).getOptional(ingredient).isPresent();
    }

    @Override
    public boolean isIngredientOnServer(ResourceLocation ingredient) {
        return isValidIngredient(ingredient);
    }

    @Override
    public String getErrorInfo(@Nullable ResourceLocation ingredient) {
        return ingredient == null ? "null" : ingredient.toString();
    }
}
