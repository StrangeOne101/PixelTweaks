package com.strangeone101.pixeltweaks.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PokemonIngredientRenderer implements IIngredientRenderer<Stats> {

    @Override
    public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable Stats ingredient) {
        if (ingredient == null) return;
        RenderHelper.enableStandardItemLighting();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Pokemon pokemon = PokemonBuilder.builder().species(ingredient.getParentSpecies()).form(ingredient).build();
        itemRenderer.renderItemAndEffectIntoGUI(SpriteItemHelper.getPhoto(pokemon), xPosition, yPosition);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public List<ITextComponent> getTooltip(Stats ingredient, ITooltipFlag tooltipFlag) {
        List<ITextComponent> allTooltips = new ArrayList<>();
        allTooltips.add(ingredient.getParentSpecies().getTranslatedName());
        String form = ingredient.getLocalizedName();
        if (form != null && !form.isEmpty()) allTooltips.add(ITextComponent.getTextComponentOrEmpty(form));
        return allTooltips;
    }
}
