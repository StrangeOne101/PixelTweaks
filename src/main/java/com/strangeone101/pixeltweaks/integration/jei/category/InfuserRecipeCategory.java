package com.strangeone101.pixeltweaks.integration.jei.category;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.pixelmon.api.recipe.InfuserRecipe;
import com.pixelmonmod.pixelmon.api.recipe.QuantifiedIngredient;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.config.Constants;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "infuser");
    private IDrawable bg;
    protected final IDrawableAnimated animatedFlame;
    protected final IDrawableAnimated animatedInfusion;

    private final ItemStackRenderer renderer = new ItemStackRenderer();
    private final IDrawable icon;

    public InfuserRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.protein);
        this.icon = gui.createDrawableIngredient(itemStack);
        ResourceLocation loc = ResourceLocationHelper.of("pixelmon", "textures/gui/infuser.png");
        this.bg = gui.drawableBuilder(loc, 15, 7, 154, 58).build();
        this.animatedFlame = gui.createAnimatedDrawable(gui.createDrawable(loc, 180, 0, 14, 14), 300, IDrawableAnimated.StartDirection.TOP, true);
        this.animatedInfusion = gui.createAnimatedDrawable(gui.createDrawable(loc, 180, 14, 14, 14), 300, IDrawableAnimated.StartDirection.LEFT, false);
    }
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends InfuserRecipe> getRecipeClass() {
        return InfuserRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.pixeltweaks.infuser.title");
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
    public void setIngredients(InfuserRecipe recipe, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

        List<List<ItemStack>> allItems = new ArrayList<>(2);
        for (QuantifiedIngredient ingredient : recipe.getQualifiedIngredients()) {
            ItemStack[] stacks = ingredient.ingredient.getMatchingStacks();
            for (ItemStack stack : stacks) stack.setCount(ingredient.quantity);
            allItems.add(Arrays.asList(stacks));
        }
        ingredients.setInputLists(VanillaTypes.ITEM, allItems);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfuserRecipe recipe, IIngredients ingredients) {
        int num = 0;
        //recipeLayout.getItemStacks().init(0, true, renderer, 13, 23, 16, 16, 0, 0);

        recipeLayout.getItemStacks().init(0, true, renderer, 67, 15, 16, 16, 0, 0);
        recipeLayout.getItemStacks().init(1, true, renderer, 67, 34, 16, 16, 0, 0);
        recipeLayout.getItemStacks().init(2, false, renderer, 117, 19, 24, 24, 4, 4);

        for (List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {
            recipeLayout.getItemStacks().set(num++, stacks);
        }
        for (List<ItemStack> stacks : ingredients.getOutputs(VanillaTypes.ITEM)) {
            recipeLayout.getItemStacks().set(2, stacks);
        }
    }

    @Override
    public void draw(InfuserRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        this.animatedFlame.draw(matrixStack, 14, 7);
        this.animatedInfusion.draw(matrixStack, 93, 25);
    }
}
