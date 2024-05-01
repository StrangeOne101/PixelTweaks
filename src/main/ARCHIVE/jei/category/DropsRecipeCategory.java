package com.strangeone101.pixeltweaks.integration.jei.category;

import com.mojang.blaze3d.matrix.GuiGraphics;
import com.pixelmonmod.pixelmon.api.pokemon.drops.ItemWithChance;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.strangeone101.pixeltweaks.integration.jei.JEIIntegration;
import com.strangeone101.pixeltweaks.integration.jei.PokemonIngredient;
import com.strangeone101.pixeltweaks.integration.jei.PokemonIngredientRenderer;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DropsRecipeCategory implements IRecipeCategory<PokemonDropInformation> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "drops");
    private static final PokemonIngredientRenderer renderer = new PokemonIngredientRenderer(3F);

    private IDrawable bg;

    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public DropsRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.quick_claw);

        this.icon = gui.createDrawableIngredient(itemStack);
        this.slotDrawable = gui.getSlotDrawable();
        this.bg = gui.drawableBuilder(new ResourceLocation("pixeltweaks", "textures/jei/drops_bg2.png"), 0, 0, 112, 100).setTextureSize(112, 100).build();

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
    public Component getTitle() {
        return Component.translatable("jei.pixeltweaks.drops.title");
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
    public void setIngredients(IRecipeLayoutBuilder builder, PokemonDropInformation recipe, IFocusGroup focuses) {
        IIngredients ingredients = builder.getIngredients();
        ingredients.setInput(JEIIntegration.WRAPPED_POKEMON, new PokemonIngredient(recipe.getPokemonSpec()));

        List<ItemStack> items = new ArrayList<>();
        for (ItemWithChance drop : recipe.getDrops()) {
            items.add(drop.getItemStack());
        }
        ingredients.setOutputs(VanillaTypes.ITEM, items);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PokemonDropInformation recipe, IIngredients ingredients) {
        recipeLayout.getIngredientsGroup(JEIIntegration.WRAPPED_POKEMON).init(0, true, renderer, 14 - 8, 6, 48, 48, 0, 0);
        recipeLayout.getIngredientsGroup(JEIIntegration.WRAPPED_POKEMON).set(0, ingredients.getInputs(JEIIntegration.WRAPPED_POKEMON).get(0));
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            recipeLayout.getItemStacks().init(i + 1, false, 8 + (26 * i), 65);
            recipeLayout.getItemStacks().set(i + 1, recipe.getDrops().get(i).getItemStack());
        }
    }

    @Override
    public void draw(PokemonDropInformation recipe, GuiGraphics matrixStack, double mouseX, double mouseY) {
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            ItemWithChance drop = recipe.getDrops().get(i);
            double chance = drop.getChance() * 100;
            String chanceString = String.format("%.0f", chance) + "%";
            int w = Minecraft.getInstance().fontRenderer.getStringWidth(chanceString);
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, chanceString, 8 + (26 * i) + 10 - ((float) w / 2), 66 + 24, 0xFFFFFF);

            //String amountString = drop.getMin() + "-" + drop.getMax();
            //Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, amountString, 8 + (26 * i), 66 + 4, 0xFFFFFF);

        }
    }


}
