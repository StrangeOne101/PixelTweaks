package com.strangeone101.pixeltweaks.jei.category;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.jei.PokeLootPool;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokeLootRecipeCategory implements IRecipeCategory<PokeLootPool> {

    public static final ResourceLocation UID = new ResourceLocation("pixeltweaks", "pokeloot");
    private IDrawable bg;

    private final IDrawable icon;

    private final IDrawable[] pokeballs;

    ITickTimer timer;

    public PokeLootRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(PixelmonItems.poke_ball);
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString("PokeBallID", "beast_ball");
        itemStack.setTag(compoundNBT);
        //this.icon = gui.createDrawableIngredient(itemStack);
        this.bg = gui.drawableBuilder(new ResourceLocation("pixeltweaks", "textures/jei/pokeloot_bg.png"), 0, 0, 144, 90).setTextureSize(144, 90).build();

        this.pokeballs = new IDrawable[4];
        this.pokeballs[0] = gui.drawableBuilder(new ResourceLocation("pixelmon", "textures/items/pokeballs/poke_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[1] = gui.drawableBuilder(new ResourceLocation("pixelmon", "textures/items/pokeballs/ultra_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[2] = gui.drawableBuilder(new ResourceLocation("pixelmon", "textures/items/pokeballs/master_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[3] = gui.drawableBuilder(new ResourceLocation("pixelmon", "textures/items/pokeballs/beast_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.icon = this.pokeballs[3];


    }
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends PokeLootPool> getRecipeClass() {
        return PokeLootPool.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.pixeltweaks.pokeloot.title");
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
    public void setIngredients(PokeLootPool recipe, IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getItems());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PokeLootPool recipe, IIngredients ingredients) {
        boolean focus = recipeLayout.getFocus(VanillaTypes.ITEM) != null && recipeLayout.getFocus(VanillaTypes.ITEM).getMode() == IFocus.Mode.OUTPUT;

        int items = ingredients.getOutputs(VanillaTypes.ITEM).size() - (focus ? 1 : 0);
        int offset = focus ? 1 : 0;

        final int columns = 8;

        List<ItemStack> allItems = new ArrayList<>(recipe.getItems());

        if (focus) {
            recipeLayout.getItemStacks().init(0, false, 0, 18);
            recipeLayout.getItemStacks().set(0, recipeLayout.getFocus(VanillaTypes.ITEM).getValue());
            allItems.removeIf(item -> recipeLayout.getFocus(VanillaTypes.ITEM).getValue().isItemEqual(item));
        }

        Collections.shuffle(allItems);
        for (int i = offset; i < items && i < columns * 4; i++) {

            Collections.rotate(allItems, 7);
            int x = (i % columns) * 18;
            int y = (i / columns) * 18 + 18;
            recipeLayout.getItemStacks().init(i, false, x, y);
            recipeLayout.getItemStacks().set(i, allItems);
        }
    }

    @Override
    public void draw(PokeLootPool recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        String title = I18n.format("jei.pixeltweaks.pokeloot.subtitle." + recipe.getTier());
        int w = Minecraft.getInstance().fontRenderer.getStringWidth(title);
        int x = (bg.getWidth() / 2) - (w / 2) + 6;
        Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, title, x, 4, 0xFFFFFF);
        //Minecraft.getInstance().getItemRenderer().renderItem(null, recipe.getTierIcon(), ItemCameraTransforms.TransformType.GUI, false, matrixStack, null, 0);

        ResourceLocation pokeball = new ResourceLocation("pixelmon", "items/pokeballs/" + recipe.getTierPokeball());

        //Draw pokeball in the gui
        //Minecraft.getInstance().getTextureManager().bindTexture(pokeball);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.pokeballs[recipe.getTier() - 1].draw(matrixStack, x - 20, 0);
        //PokemonWrapperIngredientRenderer.renderItem(matrixStack, recipe.getTierIcon(), x - 12, 4, 1.0F);
        //PokemonWrapperIngredientRenderer.renderItem(matrixStack, recipe.getTierIcon(), x - 12, 4, 1.0F);

        //String amountString = drop.getMin() + "-" + drop.getMax();
        //Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, amountString, 8 + (26 * i), 66 + 4, 0xFFFFFF);

    }
}
