package com.strangeone101.pixeltweaks.particle;

import com.google.common.base.Charsets;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class FakeParticle extends TextureSheetParticle {

    protected FakeParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
    }

    public abstract ResourceLocation getResourceLocation();


    public static class FakeParticleTexture implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        public FakeParticleTexture (List<TextureAtlasSprite> sprites) {
            this.sprites = sprites;
        }

        @Override
        public TextureAtlasSprite get(int particleAge, int particleMaxAge) {
            return this.sprites.get(particleAge * (this.sprites.size() - 1) / particleMaxAge);
        }

        @Override
        public TextureAtlasSprite get(RandomSource rand) {
            return this.sprites.get(rand.nextInt(this.sprites.size()));
        }
    }
}
