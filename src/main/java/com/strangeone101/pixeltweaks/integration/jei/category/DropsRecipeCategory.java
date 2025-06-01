package com.strangeone101.pixeltweaks.integration.jei.category;

import com.pixelmonmod.pixelmon.api.pokemon.drops.ItemWithChance;
import com.pixelmonmod.pixelmon.api.pokemon.drops.PokemonDropInformation;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.strangeone101.pixeltweaks.integration.jei.JEIIntegration;
import com.strangeone101.pixeltweaks.integration.jei.PokemonIngredient;
import com.strangeone101.pixeltweaks.integration.jei.PokemonIngredientRenderer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
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


public class DropsRecipeCategory implements IRecipeCategory<PokemonDropInformation> {

    public static final RecipeType<PokemonDropInformation> TYPE = RecipeType.create("pixeltweaks", "drops", PokemonDropInformation.class);
    private static final PokemonIngredientRenderer renderer = new PokemonIngredientRenderer(3F);

    private IDrawable bg;

    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public DropsRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(ItemRegistration.QUICK_CLAW);

        this.icon = gui.createDrawableItemStack(itemStack);
        this.slotDrawable = gui.getSlotDrawable();
        this.bg = gui.drawableBuilder(ResourceLocation.fromNamespaceAndPath("pixeltweaks", "textures/jei/drops_bg2.png"), 0, 0, 112, 100).setTextureSize(112, 100).build();
    }

    @Override
    public RecipeType<PokemonDropInformation> getRecipeType() {
        return TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, PokemonDropInformation recipe, IFocusGroup focuses) {
        builder.addInputSlot(14 - 8, 6)
                .setCustomRenderer(JEIIntegration.WRAPPED_POKEMON, renderer)
                .setPosition(14 - 8, 6, 48, 48, HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .addIngredient(JEIIntegration.WRAPPED_POKEMON, new PokemonIngredient(recipe.getPokemonSpec()));


        for (int i = 0; i < recipe.getDrops().size(); i++) {
            builder.addOutputSlot(8 + (26 * i), 65)
            .setPosition(8 + (26 * i), 65)
            .addItemStack(recipe.getDrops().get(i).getItemStack());
        }
    }

    @Override
    public void draw(PokemonDropInformation recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            ItemWithChance drop = recipe.getDrops().get(i);
            double chance = drop.getChance() * 100;
            String chanceString = String.format("%.0f", chance) + "%";
            int w = Minecraft.getInstance().font.width(chanceString);
            guiGraphics.drawString(Minecraft.getInstance().font, chanceString, 8 + (26 * i) + 10 - ((float) w / 2), 66 + 24, 0xFFFFFF, true);
        }
    }
}
