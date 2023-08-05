package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.recipe.InfuserRecipe;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.client.gui.machines.infuser.InfuserScreen;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.init.registry.RecipeTypeRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.jei.category.DropsRecipeCategory;
import com.strangeone101.pixeltweaks.jei.category.InfuserRecipeCategory;
import com.strangeone101.pixeltweaks.jei.category.PokeLootRecipeCategory;
import com.strangeone101.pixeltweaks.jei.recipe.DropsRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "jei");

    public static final IIngredientType<Stats> POKEMON = () -> Stats.class;
    public static final IIngredientType<PokemonIngredient> WRAPPED_POKEMON = () -> PokemonIngredient.class;

    public JEIIntegration() {
        //JEIServerIntegration.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //Registry.register(Registry.RECIPE_TYPE, "drops", JEIServerIntegration.DROPS_RECIPE_TYPE);
    }


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
        registration.register(WRAPPED_POKEMON, new HashSet<>(), new PokemonIngredientHelper(), new PokemonIngredientRenderer());

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DropsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PokeLootRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new InfuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        /*Set<PokemonDropInformation> drops = new HashSet<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            drops.addAll(DropItemRegistry.pokemonDrops.get(species));
        }
        registration.addRecipes(drops, DropsRecipeCategory.UID);*/

        IRecipeType<InfuserRecipe> type = RecipeTypeRegistration.INFUSER_RECIPE_TYPE;
        if (Minecraft.getInstance().world == null) {
            PixelTweaks.LOGGER.warn("World is null! JEI recipes will not be registered!");
            return;
        }

        Collection<InfuserRecipe> infuserRecipes = Minecraft.getInstance().world.getRecipeManager().getRecipesForType(type);
        registration.addRecipes(infuserRecipes, InfuserRecipeCategory.UID);
        PixelTweaks.LOGGER.info("Registered " + infuserRecipes.size() + " infuser recipes to JEI!");

        Collection<DropsRecipe> dropsRecipes = Minecraft.getInstance().world.getRecipeManager().getRecipesForType(JEIServerIntegration.DROPS_RECIPE_TYPE);

        registration.addRecipes(dropsRecipes, DropsRecipeCategory.UID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeCategory<?> category = jeiRuntime.getRecipeManager().getRecipeCategory(DropsRecipeCategory.UID);
        PixelTweaks.LOGGER.debug("Debug category " + category);

        PixelTweaks.LOGGER.debug("Debug nums " + DropItemRegistry.pokemonDrops.keySet().size());

        Set<PokemonDropInformation> drops = new HashSet<>();

        /*for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            for (PokemonDropInformation info : DropItemRegistry.pokemonDrops.get(species)) {
                if (info.getDrops().removeIf(itemWithChance -> itemWithChance.getItemStack() == null || itemWithChance.getItemStack() == ItemStack.EMPTY)) {
                    PixelTweaks.LOGGER.warn("Pokemon '" + info.getPokemonSpec().toString() + "' has an air item in its drops! Are the drop items correct?");
                }
                drops.add(info);


            }
        }
        drops.forEach(drop -> jeiRuntime.getRecipeManager().addRecipe(drop, DropsRecipeCategory.UID));
        PixelTweaks.LOGGER.info("Registered " + drops.size() + " drop recipes to JEI!");*/

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
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(InfuserScreen.class, 108, 32, 16, 16, InfuserRecipeCategory.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //Doesn't work due to pixelmon bugs, I think. I think the slot numbers are all wrong
        //registration.addRecipeTransferHandler(InfuserContainer.class, InfuserRecipeCategory.UID, 1, 2, 9, 36);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
