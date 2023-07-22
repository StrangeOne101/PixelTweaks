package com.strangeone101.pixeltweaks.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.pixelmon.api.config.BetterSpawnerConfig;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BiomeIngredientRenderer implements IIngredientRenderer<ResourceLocation> {

    private static Map<ResourceLocation, ResourceLocation> IMAGES = new HashMap<>();

    public enum Type {ITEM, SQUARE, FULL}

    private Type type;

    public BiomeIngredientRenderer() {
        this(Type.ITEM);
    }

    public BiomeIngredientRenderer(Type type) {
        this.type = type;
    }
    @Override
    public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable ResourceLocation ingredient) {
        //Get the image resourcelocation from IMAGES map
        ResourceLocation imageLoc = IMAGES.get(ingredient);
        if (imageLoc == null) {
            drawBlank(ingredient, matrixStack, xPosition, yPosition);
            return;
        }

        //Get the image from the texture manager
        Minecraft.getInstance().textureManager.bindTexture(imageLoc);

        draw(imageLoc, matrixStack, xPosition, yPosition);
    }

    @Override
    public List<ITextComponent> getTooltip(ResourceLocation ingredient, ITooltipFlag tooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();
        if (tooltipFlag.isAdvanced()) tooltip.add(new StringTextComponent(ingredient.toString()));
        return tooltip;
    }

    public void draw(ResourceLocation rl, MatrixStack stack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(rl);

        int u = 32;
        int v = 0;
        int width = this.type == Type.ITEM ? 16 : (type == Type.SQUARE ? 64 : 128);
        int height = this.type == Type.ITEM ? 16 : 64;
        float f = 1.0F / 128;
        float f1 = 1.0F / 64;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        Matrix4f matrix = stack.getLast().getMatrix();
        bufferbuilder.pos(matrix, x, y + height, 0).tex(u * f, (v + (float)height) * f1).endVertex();
        bufferbuilder.pos(matrix, x + width, y + height, 0).tex((u + (float)width) * f, (v + (float)height) * f1).endVertex();
        bufferbuilder.pos(matrix, x + width, y, 0).tex((u + (float)width) * f, v * f1).endVertex();
        bufferbuilder.pos(matrix, x, y, 0).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public void drawBlank(ResourceLocation rl, MatrixStack stack, int x, int y) {
        Biome biome = Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY).getOrDefault(rl);

        int[] colors = {0x5f5f5f, 0x5b5b5b, 0x585858, 0x545454, 0x505050};

        if (biome.getCategory() == Biome.Category.OCEAN) {
            colors[0] = biome.getSkyColor();
            colors[1] = biome.getSkyColor();
            colors[2] = biome.getWaterColor();
            colors[3] = biome.getWaterColor();
            colors[4] = 0x402d29;
        } else if (biome.getCategory() == Biome.Category.THEEND) {
            colors[0] = 0x161724;
            colors[1] = 0x161724;
            colors[2] = 0xdbd7ad;
            colors[3] = 0xdbd7ad;
            colors[4] = 0x161724;
        } else if (biome.getCategory() == Biome.Category.FOREST || biome.getCategory() == Biome.Category.JUNGLE
                || biome.getCategory() == Biome.Category.TAIGA) {
            colors[0] = biome.getSkyColor();
            colors[1] = biome.getFoliageColor();
            colors[2] = biome.getFoliageColor();
            colors[3] = biome.getGrassColor(0, 0);
            colors[4] = 0x5e5e5e;
        } else if (biome.getCategory() == Biome.Category.EXTREME_HILLS) {
            colors[0] = biome.getSkyColor();
            colors[1] = biome.getFoliageColor();
            colors[2] = biome.getGrassColor(0, 0);
            colors[3] = 0x5e5e5e;
            colors[4] = 0x5e5e5e;
        } else if (biome.getCategory() == Biome.Category.BEACH) {
            colors[0] = biome.getSkyColor();
            colors[1] = biome.getSkyColor();
            colors[2] = biome.getWaterColor();
            colors[3] = 0xffe8a3;
            colors[4] = 0x5e5e5e;
        }

        int width = this.type == Type.ITEM ? 16 : (type == Type.SQUARE ? 64 : 128);
        int height = this.type == Type.ITEM ? 16 : 64;

        Tessellator tessellator = Tessellator.getInstance();

        //Draw all 5 colors
        for (int i = 0; i < 5; i++) {
            int color = colors[i];
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = color & 0xff;

            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            Matrix4f matrix = stack.getLast().getMatrix();
            bufferbuilder.pos(matrix, x, y + height - (i * 2), 0).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(matrix, x + width, y + height - (i * 2), 0).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(matrix, x + width, y + height - (i * 2) - 2, 0).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(matrix, x, y + height - (i * 2) - 2, 0).color(r, g, b, 255).endVertex();
            tessellator.draw();
        }

    }

    public static void loadBiomes() {
        for (Biome b : Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY)) {
            ResourceLocation imageLoc = new ResourceLocation("pixeltweaks", "textures/jei/biomes/"
                    + b.getRegistryName().getNamespace() + "/" + b.getRegistryName().getPath() + ".png");

            if (Minecraft.getInstance().textureManager.getTexture(imageLoc) != null) {
                IMAGES.put(b.getRegistryName(), imageLoc);
            } else {
                IMAGES.put(b.getRegistryName(), new ResourceLocation("pixeltweaks", "textures/jei/biomes/missing.png"));
            }
        }
    }
}
