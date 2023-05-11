package com.strangeone101.pixeltweaks.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.Resources;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.ScreenParticleEngine;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.OpponentElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;
import com.pixelmonmod.pixelmon.client.storage.ClientStorageManager;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.text.DecimalFormat;

@Mixin(value = OpponentElement.class)
public abstract class OpponentElementMixin extends PixelmonWidget {

    @Shadow(remap = false)
    private static ResourceLocation OPPONENT;
    @Shadow(remap = false)
    private static ResourceLocation HEALTHY;
    @Shadow(remap = false)
    private static ResourceLocation CAUTION;
    @Shadow(remap = false)
    private static ResourceLocation WARNING;
    @Shadow(remap = false)
    private static ResourceLocation SHINY;
    @Shadow(remap = false)
    private static ResourceLocation CAUGHT;
    @Shadow(remap = false)
    private static ResourceLocation TARGET;
    @Shadow(remap = false)
    private Screen parent;
    @Shadow(remap = false)
    private PixelmonClientData enemy;
    @Shadow(remap = false)
    private ScreenParticleEngine particleEngine;

    @Override
    public void drawElement(MatrixStack matrix, float scale) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        ScreenHelper.drawImage(matrix, OPPONENT, (float)this.x, (float)(this.y - 3), 160.0F, 50.0F, this.zLevel);
        float healthPercent = this.enemy.health / (float)this.enemy.maxHealth;
        ScreenHelper.drawBar(matrix, (double)(this.x + 44), (double)(this.y + 20), 109.0D, 10.0D, healthPercent, this.enemy.getHealthColor());
        ScreenHelper.drawImage(healthPercent <= 0.5F ? (healthPercent <= 0.25F ? WARNING : CAUTION) : HEALTHY, matrix, (float)(this.x - 10), (float)(this.y - 18), 60.0F, 60.0F, this.zLevel);
        float[] rgb = {1F, 1F, 1F};
        boolean boss = this.enemy.bossTier.isBoss();
        boolean shiny = this.enemy.palette.equalsIgnoreCase("shiny") && !boss;

        if (boss) {
            //Dye the image based on this.enemy.bossTier.getColor()
            rgb = this.enemy.bossTier.getColor().getColorComponents(null);
        }

        ResourceLocation texture = ScreenHelper.getPokemonSprite(this.enemy, this.parent.getMinecraft());
        Minecraft.getInstance().getTextureManager().bindTexture(texture == null ? TextureManager.RESOURCE_LOCATION_EMPTY : texture);
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.color4f(rgb[0], rgb[1], rgb[2], 1F);
        int ix = x + 1;
        int iy = y - 4;
        matrix.translate((double)(x - (float)ix), (double)(y - (float)iy), (double)zLevel);
        AbstractGui.blit(matrix, ix, iy, 0F, 0F, (int)40F, (int)40F, (int)40F, (int)40F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        matrix.pop();

        //ScreenHelper.drawImage(ScreenHelper.getPokemonSprite(this.enemy, this.parent.getMinecraft()), matrix, (float)(this.x + 1), (float)(this.y - 3), 40.0F, 40.0F, this.zLevel);
        RenderSystem.blendColor(1F, 1F, 1F, 1F);
        this.particleEngine.drawAtOffset(matrix, this.enemy.pokemonUUID.toString(), (double)(this.x + 5), (double)(this.y + 1), (double) RandomHelper.rand.nextInt(26), (double)RandomHelper.rand.nextInt(26));
        float offset = 0.0F;
        if (ClientStorageManager.pokedex.hasCaught(this.enemy.species)) {
            ScreenHelper.drawImage(matrix, CAUGHT, (float)(this.x + 52), (float)(this.y + 5), 8.0F, 8.0F, this.zLevel);
            offset += 9.0F;
        }

        if (this.enemy.getGender() != Gender.NONE) {
            ScreenHelper.drawImage(this.enemy.getGender() == Gender.MALE ? Resources.male : Resources.female, matrix, (float)(this.x + 52) + offset, (float)(this.y + 5), 5.0F, 8.0F, this.zLevel);
            offset += 7.0F;
        }

        int labelColor = boss ? 0xff7c75 : (shiny ? -7545 : -986896);
        ScreenHelper.drawScaledString(matrix, this.enemy.getDisplayName(), (float)(this.x + 52) + offset, (float)this.y + 5.75F, labelColor, 16.0F);
        ScreenHelper.drawScaledStringRightAligned(matrix, "Lv." + this.enemy.level, (float)(this.x + 149), (float)this.y + 7.0F, -986896, false, 12.0F);
        if (PixelmonConfigProxy.getGraphics().isAdvancedBattleInformation()) {
            DecimalFormat df = new DecimalFormat(".#");
            String percentage = df.format((double)healthPercent * 100.0D).replace(".0", "");
            if (percentage.isEmpty()) {
                percentage = "0";
            }

            ScreenHelper.drawScaledStringRightAligned(matrix, percentage + "%", (float)(this.x + 145), (float)this.y + 22.0F, -986896, false, 14.0F);
        }

        if (this.enemy.status != -1 && StatusType.getEffect(this.enemy.status) != null) {
            float[] texturePair2 = StatusType.getTexturePos(StatusType.getEffect(this.enemy.status));
            ScreenHelper.bindTexture(Resources.status);
            ScreenHelper.simpleDrawImageQuad(matrix, (float)(this.x + 54 + ScreenHelper.getStringWidth(this.enemy.getDisplayName())) + offset, (float)(this.y + 4), 10.5F, 10.5F, texturePair2[0] / 768.0F, texturePair2[1] / 768.0F, (texturePair2[0] + 240.0F) / 768.0F, (texturePair2[1] + 240.0F) / 768.0F, this.zLevel);
        }

        if (ClientProxy.battleManager.catchCombo != 0) {
            ScreenHelper.drawScaledString(matrix, I18n.format("gui.battle.catch_combo", new Object[]{ClientProxy.battleManager.catchCombo}), (float)(this.x + 22) + offset, (float)this.y + 38.75F, -986896, 12.0F);
        }

        if (shiny && RandomHelper.rand.nextInt(80) == 0) {
            int size = 7 + RandomHelper.rand.nextInt(7);
            this.particleEngine.addParticle(new ScreenParticleEngine.GuiParticle(this.enemy.pokemonUUID.toString(), SHINY, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0F, 0.8F, 0.3F, 0.0F, (float)size, (float)size, 120, (particle, matrixStack) -> {
                int x = particle.age;
                int m = particle.maxAge;
                int h = m / 2;
                particle.a = (float)(x <= h ? x : h - (x - h)) / (float)h;
            }));
        }

    }
}
