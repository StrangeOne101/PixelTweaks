package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.strangeone101.pixeltweaks.PixelTweaks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "jei");

    public static final IIngredientType<Stats> POKEMON = () -> Stats.class;
    public static final IIngredientType<JEIPokemonWrapper> WRAPPED_POKEMON = () -> JEIPokemonWrapper.class;

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(PixelmonItems.poke_ball);
        registration.useNbtForSubtypes(PixelmonItems.poke_ball_lid);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen1);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen2);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen3);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen4);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen5);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen6);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen7);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen8);
        registration.useNbtForSubtypes(PixelmonItems.tm_gen9);
        registration.useNbtForSubtypes(PixelmonItems.tr_gen8);

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(POKEMON, new HashSet<>(), new PokemonIngredientHelper(), new PokemonIngredientRenderer());
        registration.register(WRAPPED_POKEMON, new HashSet<>(), new PokemonWrapperIngredientHelper(), new PokemonWrapperIngredientRenderer());

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DropsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PokeLootRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        /*Set<PokemonDropInformation> drops = new HashSet<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            drops.addAll(DropItemRegistry.pokemonDrops.get(species));
        }
        registration.addRecipes(drops, DropsRecipeCategory.UID);*/
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeCategory<?> category = jeiRuntime.getRecipeManager().getRecipeCategory(DropsRecipeCategory.UID);
        PixelTweaks.LOGGER.info("Debug category " + category);

        PixelTweaks.LOGGER.info("Debug nums " + DropItemRegistry.pokemonDrops.keySet().size());

        Set<PokemonDropInformation> drops = new HashSet<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            drops.addAll(DropItemRegistry.pokemonDrops.get(species));
        }
        drops.forEach(drop -> jeiRuntime.getRecipeManager().addRecipe(drop, DropsRecipeCategory.UID));
        PixelTweaks.LOGGER.info("Registered " + drops.size() + " drop recipes to JEI!");

        PokeLootPool tier1 = new PokeLootPool(1, DropItemRegistry.tier1);
        PokeLootPool tier2 = new PokeLootPool(2, DropItemRegistry.tier2);
        PokeLootPool tier3 = new PokeLootPool(3, DropItemRegistry.tier3);
        PokeLootPool tier4 = new PokeLootPool(4, DropItemRegistry.ultraSpace);

        jeiRuntime.getRecipeManager().addRecipe(tier1, PokeLootRecipeCategory.UID);
        jeiRuntime.getRecipeManager().addRecipe(tier2, PokeLootRecipeCategory.UID);
        jeiRuntime.getRecipeManager().addRecipe(tier3, PokeLootRecipeCategory.UID);
        jeiRuntime.getRecipeManager().addRecipe(tier4, PokeLootRecipeCategory.UID);

    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
