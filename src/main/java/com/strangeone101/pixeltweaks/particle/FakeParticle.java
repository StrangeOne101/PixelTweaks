package com.strangeone101.pixeltweaks.particle;

import com.google.common.base.Charsets;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.TexturesParticle;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
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

    public static TextureAtlasSprite loadTexture(ResourceLocation location) {

        String fileLoc = "particles/" + location.getPath() + ".json";

        try (
                Reader reader = new InputStreamReader(Objects.requireNonNull(PixelTweaks.getResource(fileLoc)),Charsets.UTF_8);
        ) {

            Minecraft.getInstance().textureManager.
            TexturesParticle texturesparticle = TexturesParticle.deserialize(JsonUtils..fromJson(reader));
            List<ResourceLocation> list = texturesparticle.getTextures();

            FakeParticleTexture texture = new FakeParticleTexture(list.stream().map((particleTextureID)
                    -> new ResourceLocation(particleTextureID.getNamespace(), "particle/" + particleTextureID.getPath()))
                    .map((resourceLoc) -> atlasTexture.getSprite(resourceLoc)).collect(Collectors.toList()));

            return texture;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FakeParticleTexture(Arrays.asList(atlasTexture.getSprite(MissingTextureSprite.getLocation())));
    }

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
