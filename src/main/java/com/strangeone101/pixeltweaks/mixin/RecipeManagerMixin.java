package com.strangeone101.pixeltweaks.mixin;

import net.minecraft.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    /*@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/crafting/RecipeManager;recipes:Ljava/util/Map;",
                    ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void beforeRecipesAreApplied(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn,
               IProfiler profilerIn, CallbackInfo ci, Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map) {
        if (ModIntegration.jei()) {
            int amount = 0;
            for (Species species : DropItemRegistry.pokemonDrops.keySet()) {
                for (PokemonDropInformation info : DropItemRegistry.pokemonDrops.get(species)) {
                    if (info.getDrops().removeIf(itemWithChance -> itemWithChance.getItemStack() == null || itemWithChance.getItemStack() == ItemStack.EMPTY)) {
                        PixelTweaks.LOGGER.warn("Pokemon '" + info.getPokemonSpec().toString() + "' has an air item in its drops! Are the drop items correct?");
                    }

                    DropsRecipe recipe = new DropsRecipe(info);
                    map.computeIfAbsent(recipe.getType(), (recipeType) -> ImmutableMap.builder())
                            .put(recipe.getId(), recipe);
                    amount++;
                }
            }
            PixelTweaks.LOGGER.info("Registered " + amount + " drops recipes via vanilla!");
        }
    }*/
}
