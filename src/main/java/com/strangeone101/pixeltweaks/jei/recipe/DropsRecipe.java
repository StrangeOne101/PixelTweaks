package com.strangeone101.pixeltweaks.jei.recipe;

import com.google.gson.JsonObject;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.drops.ItemWithChance;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.strangeone101.pixeltweaks.jei.JEIServerIntegration;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class DropsRecipe implements IRecipe<IInventory> {

    public static int NEXT_ID = 0x0000;

    private ResourceLocation id;
    private PokemonDropInformation info;
    public DropsRecipe(PokemonDropInformation info) {
        this.id = new ResourceLocation("pixeltweaks", "drops_" + Integer.toHexString(NEXT_ID++));

        this.info = info;
    }

    public DropsRecipe(PokemonDropInformation info, ResourceLocation id) {
        this.id = id;
        this.info = info;
    }

    public PokemonDropInformation getInfo() {
        return info;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<DropsRecipe> getSerializer() {
        return JEIServerIntegration.DROPS_RECIPES.get();
    }

    public static class Type implements IRecipeType<DropsRecipe> {
        public Type() {
        }
    }

    @Override
    public IRecipeType<DropsRecipe> getType() {
        return JEIServerIntegration.DROPS_RECIPE_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DropsRecipe> {

        @Override
        public DropsRecipe read(ResourceLocation recipeId, JsonObject json) {
            return null;
        }

        @Nullable
        @Override
        public DropsRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String spec = buffer.readString();
            int size = buffer.readVarInt();
            ItemWithChance[] list = new ItemWithChance[size];

            for(int i = 0; i < size; ++i) {
                ItemStack stack = buffer.readItemStack();
                int min = buffer.readVarInt();
                int max = buffer.readVarInt();
                double chance = buffer.readDouble();
                list[i] = new ItemWithChance(stack, min, max, chance);
            }

            return new DropsRecipe(new PokemonDropInformation(PokemonSpecificationProxy.create(spec), list), recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, DropsRecipe recipe) {
            buffer.writeString(recipe.info.getPokemonSpec().toString());
            buffer.writeVarInt(recipe.info.getDrops().size());

            for (ItemWithChance item : recipe.info.getDrops()) {
                buffer.writeItemStack(item.getItemStack());
                buffer.writeVarInt(item.getMin());
                buffer.writeVarInt(item.getMax());
                buffer.writeDouble(item.getChance());
            }
        }
    }
}
