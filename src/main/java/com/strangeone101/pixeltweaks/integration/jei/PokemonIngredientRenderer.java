package com.strangeone101.pixeltweaks.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.RenderTypeHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PokemonIngredientRenderer implements IIngredientRenderer<PokemonIngredient> {

    private float scale;
    public PokemonIngredientRenderer(float scale) {
        this.scale = scale;
    }

    public PokemonIngredientRenderer() {
        this(1F);
    }

    @Override
    public void render(GuiGraphics matrixStack, @Nullable PokemonIngredient ingredient) {
        if (ingredient == null) return;
        RenderSystem.enableDepthTest();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Pokemon pokemon = ingredient.buildPokemon;
        //itemRenderer.renderItemAndEffectIntoGUI(SpriteItemHelper.getPhoto(pokemon), xPosition, yPosition);
        renderItem(matrixStack, SpriteItemHelper.getPhoto(pokemon), 0, 0, this.scale);
        RenderSystem.disableBlend();
    }

    @Override
    public List<Component> getTooltip(PokemonIngredient ingredient, TooltipFlag tooltipFlag) {
        List<Component> allTooltips = new ArrayList<>();
        String species = ingredient.getForm().getParentSpecies().getTranslatedName().getString();
        String form = ingredient.getForm().getLocalizedName();
        String rawForm = ingredient.getForm().getName();
        if (form != null && !form.isEmpty() && !rawForm.equals("") && !rawForm.equals("Default")) species = form + " " + species;
        allTooltips.add(Component.literal(species));
        if (ingredient.getPalette().isPresent()) {
            allTooltips.add(Component.literal(ChatFormatting.GRAY + ingredient.getPalette().get().getLocalizedName()));
        }
        if (ingredient.getGender().isPresent()) {
            allTooltips.add(Component.literal(ChatFormatting.GRAY + ingredient.getGender().get().getLocalizedName()));
        }
        return allTooltips;
    }

    public static void renderItem(GuiGraphics matrixStack, ItemStack stack, int x, int y, float scale) {
        matrixStack.pose().pushPose();
        Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, false);

        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
        bakedmodel = bakedmodel.getOverrides().resolve(bakedmodel, stack, null, null, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float half = 8.0F * scale;
        float full = 16.0F * scale;

        matrixStack.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pose().translate((float)x, (float)y, 100.0F + 400);
        matrixStack.pose().translate(half, half, 0.0F);
        matrixStack.pose().scale(1.0F, -1.0F, 1.0F);
        matrixStack.pose().scale(full, full, full);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedmodel.useAmbientOcclusion();
        /*if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }*/

        Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GUI, false, matrixStack.pose(), irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();

        RenderSystem.disableDepthTest();

        matrixStack.pose().popPose();
    }


}
