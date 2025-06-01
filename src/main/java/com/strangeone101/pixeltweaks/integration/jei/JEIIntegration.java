package com.strangeone101.pixeltweaks.integration.jei;

import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;

import com.pixelmonmod.pixelmon.api.recipe.InfuserRecipe;
import com.pixelmonmod.pixelmon.client.gui.inventory.InventoryPixelmonBase;
import com.pixelmonmod.pixelmon.client.gui.inventory.InventoryPixelmonExtendedScreen;
import com.pixelmonmod.pixelmon.client.gui.machines.infuser.InfuserContainer;
import com.pixelmonmod.pixelmon.client.gui.machines.infuser.InfuserScreen;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.init.registry.ContainerRegistration;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import com.pixelmonmod.pixelmon.init.registry.RecipeRegistration;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.integration.jei.category.DropsRecipeCategory;
import com.strangeone101.pixeltweaks.integration.jei.category.InfuserRecipeCategory;
import com.strangeone101.pixeltweaks.integration.jei.category.PokeLootRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@JeiPlugin
public class JEIIntegration implements IModPlugin, ISubtypeInterpreter<ItemStack> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("pixeltweaks", "jei");

    public static final IIngredientType<Stats> POKEMON = () -> Stats.class;
    public static final IIngredientType<PokemonIngredient> WRAPPED_POKEMON = () -> PokemonIngredient.class;

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ItemRegistration.POKE_BALL.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.POKE_BALL_LID.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN1.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN2.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN3.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN4.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN5.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN6.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN7.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN8.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TM_GEN9.get(), this);
        registration.registerSubtypeInterpreter(ItemRegistration.TR_GEN8.get(), this);

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(WRAPPED_POKEMON, new HashSet<>(), new PokemonIngredientHelper(), new PokemonIngredientRenderer(), PokemonIngredient.CODEC);


    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new DropsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PokeLootRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new InfuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PokemonDropInformation> drops = new ArrayList<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            drops.addAll(DropItemRegistry.pokemonDrops.get(species));
        }
        registration.addRecipes(DropsRecipeCategory.TYPE, drops);

        Supplier<RecipeType<InfuserRecipe>> type = RecipeRegistration.INFUSER_RECIPE_TYPE;
        if (Minecraft.getInstance().level == null) {
            PixelTweaks.LOGGER.warn("World is null! JEI recipes will not be registered!");
            return;
        }

        List<InfuserRecipe> infuserRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type.get()).stream().map(rh -> rh.value()).collect(Collectors.toList());
        registration.addRecipes(InfuserRecipeCategory.TYPE, infuserRecipes);
        PixelTweaks.LOGGER.info("Registered " + infuserRecipes.size() + " infuser recipes to JEI!");

        PokeLootPool tier1 = new PokeLootPool(1, DropItemRegistry.tier1);
        PokeLootPool tier2 = new PokeLootPool(2, DropItemRegistry.tier2);
        PokeLootPool tier3 = new PokeLootPool(3, DropItemRegistry.tier3);
        PokeLootPool tier4 = new PokeLootPool(4, DropItemRegistry.ultraSpace);

        registration.addRecipes(PokeLootRecipeCategory.TYPE, List.of(tier1, tier2, tier3, tier4));

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        //IRecipeCategory<?> category = jeiRuntime.getRecipeManager().createRecipeLookup(DropsRecipeCategory.UID).;
        //PixelTweaks.LOGGER.debug("Debug category " + category);

        /*PixelTweaks.LOGGER.debug("Debug nums " + DropItemRegistry.pokemonDrops.keySet().size());

        List<PokemonDropInformation> drops = new ArrayList<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            for (PokemonDropInformation info : DropItemRegistry.pokemonDrops.get(species)) {
                if (info.getDrops().removeIf(itemWithChance -> itemWithChance.getItemStack() == null || itemWithChance.getItemStack() == ItemStack.EMPTY)) {
                    PixelTweaks.LOGGER.warn("Pokemon '" + info.getPokemonSpec().toString() + "' has an air item in its drops! Are the drop items correct?");
                }
                drops.add(info);
            }
        }
        jeiRuntime.getRecipeManager().addRecipes(DropsRecipeCategory.UID, drops);
        PixelTweaks.LOGGER.info("Registered " + drops.size() + " drop recipes to JEI!");

        PokeLootPool tier1 = new PokeLootPool(1, DropItemRegistry.tier1);
        PokeLootPool tier2 = new PokeLootPool(2, DropItemRegistry.tier2);
        PokeLootPool tier3 = new PokeLootPool(3, DropItemRegistry.tier3);
        PokeLootPool tier4 = new PokeLootPool(4, DropItemRegistry.ultraSpace);

        jeiRuntime.getRecipeManager().addRecipes(PokeLootRecipeCategory.UID, List.of(tier1, tier2, tier3, tier4));*/

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        //The thing bellow allows you to provide a custom ingredient that is clickable in the GUI. This can be used for pokemon
        /*registration.addGuiContainerHandler(InventoryPixelmonExtendedScreen.class, new IGuiContainerHandler<InventoryPixelmonExtendedScreen>() {
            @Override
            public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(InventoryPixelmonExtendedScreen containerScreen, double mouseX, double mouseY) {
                containerScreen.

                return IGuiContainerHandler.super.getClickableIngredientUnderMouse(containerScreen, mouseX, mouseY);
            }
        });*/

        registration.addRecipeClickArea(InfuserScreen.class, 108, 32, 16, 16, InfuserRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //Doesn't work due to pixelmon bugs, I think. I think the slot numbers are all wrong

        registration.addRecipeTransferHandler(InfuserContainer.class, ContainerRegistration.INFUSER.get(), InfuserRecipeCategory.TYPE, 1, 2, 9, 36);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (ingredient.get(PixelmonDataComponents.POKE_BALL) != null) {
            return ingredient.get(PixelmonDataComponents.POKE_BALL).getValue().orElse(PokeBallRegistry.POKE_BALL.getValueUnsafe());
        } else if (ingredient.get(DataComponents.CUSTOM_DATA) != null && ingredient.get(DataComponents.CUSTOM_DATA).contains("tm")) {
            return ingredient.get(DataComponents.CUSTOM_DATA).getUnsafe().getShort("tm");
        }
        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        if (ingredient.get(PixelmonDataComponents.POKE_BALL) != null) {
            return ingredient.get(PixelmonDataComponents.POKE_BALL).getValue().orElse(PokeBallRegistry.POKE_BALL.getValueUnsafe()).getName();
        } else if (ingredient.get(DataComponents.CUSTOM_DATA) != null && ingredient.get(DataComponents.CUSTOM_DATA).contains("tm")) {
            return String.valueOf(ingredient.get(DataComponents.CUSTOM_DATA).getUnsafe().getShort("tm"));
        }
        return "";
    }
}
