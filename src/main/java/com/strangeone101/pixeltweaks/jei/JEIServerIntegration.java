package com.strangeone101.pixeltweaks.jei;

import com.google.common.collect.ImmutableMap;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.jei.recipe.DropsRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class JEIServerIntegration {


    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "pixeltweaks");
    public static final RegistryObject<DropsRecipe.Serializer> DROPS_RECIPES = RECIPE_SERIALIZERS.register("drops", DropsRecipe.Serializer::new);
    public static final IRecipeType<DropsRecipe> DROPS_RECIPE_TYPE = new DropsRecipe.Type();

    public JEIServerIntegration() {
        register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onDatapackReload);
    }

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        Registry.register(Registry.RECIPE_TYPE, "drops", DROPS_RECIPE_TYPE);
    }

    public void onDatapackReload(AddReloadListenerEvent event) {
        Map<ResourceLocation, IRecipe<?>> recipes = new HashMap<>();

        for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
            for (PokemonDropInformation info : DropItemRegistry.pokemonDrops.get(species)) {
                if (info.getDrops().removeIf(itemWithChance -> itemWithChance.getItemStack() == null || itemWithChance.getItemStack() == ItemStack.EMPTY)) {
                    PixelTweaks.LOGGER.warn("Pokemon '" + info.getPokemonSpec().toString() + "' has an air item in its drops! Are the drop items correct?");
                }

                DropsRecipe recipe = new DropsRecipe(info);
                recipes.put(recipe.getId(), recipe);
            }
        }
        HashMap<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = new HashMap<>(event.getDataPackRegistries().getRecipeManager().recipes);
        map.put(DROPS_RECIPE_TYPE, recipes);
        event.getDataPackRegistries().getRecipeManager().recipes = ImmutableMap.copyOf(map);
        PixelTweaks.LOGGER.info("Registered " + recipes.size() + " drops recipes via vanilla!");
    }
}
