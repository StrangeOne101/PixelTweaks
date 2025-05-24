package com.strangeone101.pixeltweaks.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.List;

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
