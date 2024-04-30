package com.strangeone101.pixeltweaks.integration.jei.category;

import com.pixelmonmod.pixelmon.api.recipe.InfuserRecipe;
import com.pixelmonmod.pixelmon.api.recipe.QuantifiedIngredient;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import com.pixelmonmod.pixelmon.init.registry.RecipeTypeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.TextComponentHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "infuser");
    public static final RecipeType<InfuserRecipe> TYPE = RecipeType.create("pixelmon", "infuser", InfuserRecipe.class);
    private IDrawable bg;
    protected final IDrawableAnimated animatedFlame;
    protected final IDrawableAnimated animatedInfusion;

    private final ItemStackRenderer renderer = new ItemStackRenderer();
    private final IDrawable icon;

    public InfuserRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.protein);
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
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, InfuserRecipe recipe, IFocusGroup ingredients) {
        int num = 0;
        //recipeLayout.getItemStacks().init(0, true, renderer, 13, 23, 16, 16, 0, 0);

        IRecipeSlotBuilder one = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 67, 15);
        IRecipeSlotBuilder two = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 67, 34);
        //recipeLayout.getItemStacks().init(1, true, renderer, 67, 34, 16, 16, 0, 0);
        IRecipeSlotBuilder out = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 117, 19);

        //recipeLayout.getItemStacks().init(2, false, renderer, 117, 19, 24, 24, 4, 4);



        for (IFocus<ItemStack> stacks : ingredients.getItemStackFocuses(RecipeIngredientRole.INPUT).toList()) {
            if (num == 0) {
                one.addItemStack(stacks.getTypedValue().getIngredient());
                num++;
            } else {
                two.addItemStack(stacks.getTypedValue().getIngredient());
            }
        }
        for (IFocus<ItemStack> stacks : ingredients.getItemStackFocuses(RecipeIngredientRole.OUTPUT).toList()) {
            out.addItemStack(stacks.getTypedValue().getIngredient());
        }
    }

    @Override
    public void draw(InfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        this.animatedFlame.draw(matrixStack, 14, 7);
        this.animatedInfusion.draw(matrixStack, 93, 25);
    }
}
