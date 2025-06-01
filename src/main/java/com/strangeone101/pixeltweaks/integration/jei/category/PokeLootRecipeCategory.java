package com.strangeone101.pixeltweaks.integration.jei.category;

import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import com.strangeone101.pixeltweaks.integration.jei.PokeLootPool;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PokeLootRecipeCategory implements IRecipeCategory<PokeLootPool> {

    public static final RecipeType<PokeLootPool> TYPE = RecipeType.create("pixeltweaks", "pokeloot", PokeLootPool.class);
    private IDrawable bg;

    private final IDrawable icon;

    private final IDrawable[] pokeballs;

    public PokeLootRecipeCategory(IGuiHelper gui){
        ItemStack itemStack = new ItemStack(ItemRegistration.POKE_BALL);
        itemStack.set(PixelmonDataComponents.POKE_BALL, PokeBallRegistry.BEAST_BALL);
        //this.icon = gui.createDrawableIngredient(itemStack);
        this.bg = gui.createBlankDrawable(123 + 32, 90 + 16);

        this.pokeballs = new IDrawable[4];
        this.pokeballs[0] = gui.drawableBuilder(ResourceLocation.fromNamespaceAndPath("pixelmon", "textures/item/pokeballs/poke_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[1] = gui.drawableBuilder(ResourceLocation.fromNamespaceAndPath("pixelmon", "textures/item/pokeballs/ultra_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[2] = gui.drawableBuilder(ResourceLocation.fromNamespaceAndPath("pixelmon", "textures/item/pokeballs/master_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.pokeballs[3] = gui.drawableBuilder(ResourceLocation.fromNamespaceAndPath("pixelmon", "textures/item/pokeballs/beast_ball.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.icon = this.pokeballs[3];
    }

    @Override
    public RecipeType<PokeLootPool> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.pixeltweaks.pokeloot.title");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return bg;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PokeLootPool recipe, IFocusGroup focuses) {
        Stream<ItemStack> stream = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT).map(ifocus -> ifocus.getTypedValue().getIngredient());

        List<ItemStack> allItems = new ArrayList<>(recipe.getItems());
        stream = stream.filter(itemStack -> !allItems.remove(itemStack));

        int offset = 0;
        final int columns = 7;

        for (ItemStack stack : stream.toArray(ItemStack[]::new)) {
            int x = (offset % columns) * 18;
            int y = (offset / columns) * 18 + 18;
            builder.createFocusLink(builder.addSlot(RecipeIngredientRole.OUTPUT)
                    .setPosition(x + 1, y + 1)
                    .addItemStack(stack));
            offset++;
        }



        for (int i = offset; i < allItems.size(); i++) {
            //Collections.shuffle(allItems);
            int x = (i % columns) * 18;
            int y = (i / columns) * 18 + 18;
            builder.addSlot(RecipeIngredientRole.OUTPUT)
                    .addItemStack(allItems.get(i - offset));

        }
    }



    @Override
    public void draw(PokeLootPool recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Component title = Component.translatable("jei.pixeltweaks.pokeloot.subtitle." + recipe.getTier());
        int w = Minecraft.getInstance().font.width(title);
        int x = (bg.getWidth() / 2) - (w / 2) + 6;

        //Draw string
        guiGraphics.drawString(Minecraft.getInstance().font, title, x, 4, 0xFFFFFF, true);

        //Draw icon
        this.pokeballs[recipe.getTier() - 1].draw(guiGraphics, x - 20, 0);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, PokeLootPool recipe, IFocusGroup focuses) {
        IScrollGridWidget widget = builder.addScrollGridWidget(builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT), 7, 4);
        widget.setPosition(16, 0, getWidth() - 32, getHeight() - 32, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    }
}
