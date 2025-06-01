package com.strangeone101.pixeltweaks.integration.jei.category;


import com.pixelmonmod.pixelmon.api.recipe.InfuserRecipe;

import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {

    public static final RecipeType<InfuserRecipe> TYPE = RecipeType.create("pixeltweaks", "infuser", InfuserRecipe.class);
    private IDrawable bg;
    protected final IDrawableAnimated animatedFlame;
    protected final IDrawableAnimated animatedInfusion;
    private final IDrawable icon;

    public InfuserRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(ItemRegistration.PROTEIN);
        this.icon = gui.createDrawableItemStack(itemStack);
        ResourceLocation loc = ResourceLocationHelper.of("pixelmon", "textures/gui/infuser.png");
        this.bg = gui.drawableBuilder(loc, 15, 7, 154, 58).build();
        this.animatedFlame = gui.createAnimatedDrawable(gui.createDrawable(loc, 180, 0, 14, 14), 300, IDrawableAnimated.StartDirection.TOP, true);
        this.animatedInfusion = gui.createAnimatedDrawable(gui.createDrawable(loc, 180, 14, 14, 14), 300, IDrawableAnimated.StartDirection.LEFT, false);
    }


    @Override
    public RecipeType<InfuserRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.pixeltweaks.infuser.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT)
                .setPosition(117, 19, 24, 24, HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        builder.addSlot(RecipeIngredientRole.INPUT)
                .setPosition(67, 15, 16, 16, HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .addIngredients(recipe.ingredients().get(0).ingredient());
        builder.addSlot(RecipeIngredientRole.INPUT)
                .setPosition(67, 34, 16, 16, HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .addIngredients(recipe.ingredients().get(1).ingredient());
    }

    @Override
    public void draw(InfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.animatedFlame.draw(guiGraphics, 31, 7);
        this.animatedInfusion.draw(guiGraphics, 93, 25);
    }
}
