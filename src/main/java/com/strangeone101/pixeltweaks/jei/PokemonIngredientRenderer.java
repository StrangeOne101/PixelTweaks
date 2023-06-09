package com.strangeone101.pixeltweaks.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
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
        //itemRenderer.renderItemAndEffectIntoGUI(SpriteItemHelper.getPhoto(pokemon), xPosition, yPosition);
        renderItem(matrixStack, SpriteItemHelper.getPhoto(pokemon), xPosition, yPosition, 2.0F);
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

    public void renderItem(MatrixStack matrixStack, ItemStack stack, int x, int y, float scale) {
        RenderSystem.pushMatrix();
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);

        IBakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(stack);
        bakedmodel = bakedmodel.getOverrides().getOverrideModel(bakedmodel, stack, null, null);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float half = 8.0F * scale;
        float full = 16.0F * scale;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F + Minecraft.getInstance().getItemRenderer().zLevel);
        RenderSystem.translatef(half, half, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(full, full, full);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();
        if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }


}
