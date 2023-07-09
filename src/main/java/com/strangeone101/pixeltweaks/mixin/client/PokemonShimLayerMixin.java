package com.strangeone101.pixeltweaks.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.client.models.PixelmonModelBase;
import com.pixelmonmod.pixelmon.client.render.PixelmonRendering;
import com.pixelmonmod.pixelmon.client.render.entity.layers.PokemonShimLayer;
import com.pixelmonmod.pixelmon.client.render.entity.renderers.AbstractPokemonRenderer;
import com.pixelmonmod.pixelmon.entities.pixelmon.AbstractClientEntity;
import com.strangeone101.pixeltweaks.client.overlay.OverlayLayer;
import com.strangeone101.pixeltweaks.client.overlay.PixelmonEntityLayerExtension;
import com.strangeone101.pixeltweaks.client.overlay.PokemonOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonShimLayer.class)
public abstract class PokemonShimLayerMixin<E extends AbstractClientEntity> extends LayerRenderer<E, PixelmonModelBase<E>> {

    public PokemonShimLayerMixin(IEntityRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Shadow(remap = false)
    private PixelmonModelBase<E> model;
    @Final
    @Shadow(remap = false)
    private AbstractPokemonRenderer<E> renderer;

    @Inject(method = "renderPixelmon(Lcom/pixelmonmod/pixelmon/entities/pixelmon/AbstractClientEntity;FFFFFFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZI)V"
            , at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;pop()V", ordinal = 1), remap = false)
    public void onRender(E pixelmon, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
                         float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight, boolean fromPokedex,
                         int pass, CallbackInfo info) {

        if (!(pixelmon instanceof PixelmonEntityLayerExtension) || ((PixelmonEntityLayerExtension) pixelmon).getPTOverlay() == null)
            return;

        PokemonOverlay overlay = ((PixelmonEntityLayerExtension)pixelmon).getPTOverlay();

        float distance = Minecraft.getInstance().renderViewEntity != null ? pixelmon.getDistance(Minecraft.getInstance().renderViewEntity) : 20F;
        int packedOverlay = LivingRenderer.getPackedOverlay(pixelmon, 0.0F);

        float scale = 1F;
        for (OverlayLayer layer : overlay.layers) {
            if (layer.offset != 0F) scale += layer.offset;

            int light = layer.emissive ? 15728880 : packedLight;
            float alpha = layer.alpha;

            if (layer.emissive && distance > (float)PixelmonConfigProxy.getGraphics().getEmissiveTexturesDistance()) {
                continue; //Don't render emissive textures if they are too far away
            }

            if (layer.fade != null) {
                long existed = pixelmon.ticksExisted * 50L;
                if (existed < layer.fade.start) {
                    alpha *= ((double)existed / layer.fade.start);
                }
            }

            if (scale != 1F) {
                matrix.scale(scale, scale, scale);
            }

            float[] colors = layer.color.getColorComponents(new float[4]);
            colors[3] = alpha;

            this.model.render(pixelmon, matrix, buffer.getBuffer(PixelmonRendering.getSMDCulledRenderType(layer.texture)), light, packedOverlay, colors[0], colors[1], colors[2], colors[3]);
        }
    }

    @Unique
    private static RenderType pixelTweaks$render(ResourceLocation texture) {
        RenderType.State state = RenderType.State.getBuilder().shadeModel(RenderState.SHADE_ENABLED)
                .texture(new RenderState.TextureState(texture, false, false))
                .transparency(RenderType.TRANSLUCENT_TRANSPARENCY)
                .depthTest(RenderType.DEPTH_EQUAL)
                .diffuseLighting(RenderType.DIFFUSE_LIGHTING_ENABLED)
                .alpha(RenderType.DEFAULT_ALPHA)
                .cull(RenderType.CULL_DISABLED)
                .lightmap(RenderType.LIGHTMAP_ENABLED)
                .overlay(RenderType.OVERLAY_ENABLED)
                .writeMask(RenderType.COLOR_WRITE)
                .target(RenderType.TRANSLUCENT_TARGET)
                .fog(RenderType.NO_FOG)
                .build(true);
        return RenderType.makeType("custom_smd_pixeltweaks", DefaultVertexFormats.ENTITY, 4, 256, false, false, state);
    }
}
