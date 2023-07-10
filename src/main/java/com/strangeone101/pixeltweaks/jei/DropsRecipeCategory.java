package com.strangeone101.pixeltweaks.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.drops.ItemWithChance;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import com.strangeone101.pixeltweaks.PixelTweaks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DropsRecipeCategory implements IRecipeCategory<PokemonDropInformation> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "drops");
    private static final PokemonIngredientRenderer renderer = new PokemonIngredientRenderer();

    private IDrawable bg;

    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public DropsRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.pixelmon_sprite);
        CompoundNBT tagCompound = new CompoundNBT();
        itemStack.setTag(tagCompound);
        tagCompound.putShort("ndex", (short)194);

        this.icon = gui.createDrawableIngredient(itemStack);
        this.slotDrawable = gui.getSlotDrawable();
        this.bg = gui.drawableBuilder(new ResourceLocation("pixeltweaks", "textures/jei/drops_bg.png"), 0, 0, 112, 106).setTextureSize(112, 106).build();
    }
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends PokemonDropInformation> getRecipeClass() {
        return PokemonDropInformation.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.pixeltweaks.drops.title");
    }

    @Override
    public IDrawable getBackground() {
        return bg;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(PokemonDropInformation recipe, IIngredients ingredients) {
        Pokemon pokemon = recipe.getPokemonSpec().create();
        ingredients.setInput(JEIIntegration.POKEMON, pokemon.getForm());

        List<ItemStack> items = new ArrayList<>();
        for (ItemWithChance drop : recipe.getDrops()) {
            items.add(drop.getItemStack());
        }
        ingredients.setOutputs(VanillaTypes.ITEM, items);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PokemonDropInformation recipe, IIngredients ingredients) {
        recipeLayout.getIngredientsGroup(JEIIntegration.POKEMON).init(0, true, renderer, 20, 12, 32, 32, 0, 0);
        recipeLayout.getIngredientsGroup(JEIIntegration.POKEMON).set(0, ingredients.getInputs(JEIIntegration.POKEMON).get(0));
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            recipeLayout.getItemStacks().init(i + 1, false, 9 + (25 * i), 66);
            recipeLayout.getItemStacks().set(i + 1, recipe.getDrops().get(i).getItemStack());
        }
    }

    @Override
    public void draw(PokemonDropInformation recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            ItemWithChance drop = recipe.getDrops().get(i);
            double chance = drop.getChance() * 100;
            String chanceString = String.format("%.0f", chance) + "%";
            int w = Minecraft.getInstance().fontRenderer.getStringWidth(chanceString);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, chanceString, 9 + (25 * i) + 15 - (w / 2), 66 + 24, 0xFFFFFF);
        }
    }
}
