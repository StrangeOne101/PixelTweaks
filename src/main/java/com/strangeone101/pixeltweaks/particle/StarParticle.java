package com.strangeone101.pixeltweaks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.client.particle.ParticleMathHelper;
import com.pixelmonmod.pixelmon.client.particle.particles.shadow.ShadowParticleFactory;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class StarParticle extends FakeParticle {

    //private static IAnimatedSprite texture = loadTexture(new ResourceLocation(PixelTweaks.MODID, "star"));
    private static final ResourceLocation tex = new ResourceLocation(PixelTweaks.MODID, "textures/particles/stars_0.png");
    private static final ResourceLocation tex2 = new ResourceLocation(PixelTweaks.MODID, "textures/particles/stars_1.png");

    public static SpriteSet SPRITES;

    private float angleDirection;
    private int startAge;
    private float prevScale;
    private float prevAlpha;
    private float localScale;
    private float drag = 0.9F;

    private static final float[] sizeScale = {0.1F, 0.20F, 0.35F, 0.5F, 0.65F, 0.8F, 0.85F, 1F, 1F, 1F, 1F, 0.85F, 0.45F, 0.2F, 0.1F};

    private static final int[] fadeColors = {0x00D5ED, 0xD400E8, 0xE52A00, 0x95E500, 0xE27C00, 0x5200E0};
    private static final int color = 0xEFEF00;

    public StarParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.xd = motionX + (Math.random() * 2.0D - 1.0D) * (double)0.1F;
        this.yd = motionY + (Math.random() * 2.0D - 1.0D) * (double)0.1F;
        this.zd = motionZ + (Math.random() * 2.0D - 1.0D) * (double)0.1F;

        this.startAge = world.random.nextInt(3);
        this.lifetime = this.startAge + 15;
        this.localScale = 0.5F + (world.random.nextFloat() * 1.5F);
        this.roll = world.random.nextFloat();
        this.oRoll = this.roll;
        this.rCol = 1F - (world.random.nextFloat() * 0.015F);
        this.gCol = 1F - (world.random.nextFloat() * 0.015F);
        this.bCol = 1F - (world.random.nextFloat() * 0.015F);
        this.gravity = 0.4F;
        this.angleDirection = world.random.nextFloat() > 0.5F ? 1F : -1F;
        this.localScale = 0F;
        this.setSize(0F, 0F);

        this.pickSprite(SPRITES);
    }

    @Override
    public void tick() {
        int realAge = this.age - this.startAge;
        if (realAge < 0) {
            this.age++;
            return;
        } else {
            standardTick();
        }

        this.oRoll = this.roll;
        this.roll += (this.angleDirection * 0.5F);
        //this.particleScale = 0.1F * sizeScale[this.age % 20];// * this.getViewScale();
        this.prevScale = this.localScale;
        float f = 0.10F * sizeScale[realAge % 15] * this.getViewScale() * localScale;
        this.setSize(f, f);
        this.localScale = f;

        //Make the particle fade out as it is nearing the end of its life
        this.prevAlpha = this.alpha;
        this.alpha = realAge >= 10 ? 1F - ((realAge - 10) / 5F) : 1F;
    }

    private void standardTick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double)this.gravity;
            this.move(this.x, this.y, this.z);
            this.xd *= (double)drag;
            this.yd *= (double)drag;
            this.zd *= (double)drag;
            if (this.onGround) {
                this.xd *= (double)0.7F;
                this.zd *= (double)0.7F;
            }

        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float f = this.alpha;
        float f2 = Mth.lerp(partialTicks, this.prevAlpha, this.alpha);
        this.alpha = f2;
        super.render(buffer, renderInfo, partialTicks);
        this.alpha = f;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevScale, this.localScale);
    }

    @Override
    protected void setSize(float particleWidth, float particleHeight) {
        double xOff = this.bbWidth - particleWidth;
        double yOff = this.bbHeight - particleHeight;

        super.setSize(particleWidth, particleHeight);

        this.move(xOff, yOff, xOff);
    }

    //Get the scale to render the particle at based on how far away the player is
    private float getViewScale() {
        return 0.1F + (float) (Math.sqrt(Minecraft.getInstance().player.position().distanceToSqr(this.x, this.y, this.z))) / 5.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(PixelTweaks.MODID, "star");
    }
}
