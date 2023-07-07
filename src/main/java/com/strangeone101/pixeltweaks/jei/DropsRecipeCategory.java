package com.strangeone101.pixeltweaks.jei;

import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class DropsRecipeCategory implements IRecipeCategory<DropsRecipe> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "drops");
    private static final ItemStackRenderer renderer = new ItemStackRenderer();

    private IDrawable bg;

    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public DropsRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.pixelmon_sprite);
        CompoundNBT tagCompound = new CompoundNBT();
        itemStack.setTag(tagCompound);
        tagCompound.putShort("ndex", (short)PixelmonSpecies.WOOPER.getValueUnsafe().getDex());

        this.icon = gui.createDrawableIngredient(itemStack);
        this.slotDrawable = gui.getSlotDrawable();
        this.bg = gui.drawableBuilder(new ResourceLocation("pixeltweaks", "textures/jei/drops_bg.png"), 0, 0, 112, 90).setTextureSize(112, 90).build();
    }
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends DropsRecipe> getRecipeClass() {
        return DropsRecipe.class;
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
    public void setIngredients(DropsRecipe recipe, IIngredients ingredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DropsRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, renderer, 26, 18, 32, 32, 0, 0);
        recipeLayout.getItemStacks().set(0, SpriteItemHelper.getPhoto(recipe.builtPokemon));


    }
}
