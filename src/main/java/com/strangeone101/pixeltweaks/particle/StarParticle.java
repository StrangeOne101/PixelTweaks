package com.strangeone101.pixeltweaks.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.strangeone101.pixeltweaks.PixelTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarParticle extends FakeParticle {

    private static IAnimatedSprite texture = loadTexture(new ResourceLocation(PixelTweaks.MODID, "star"));
    private float angleDirection;
    private int startAge;
    private float prevScale;
    private float prevAlpha;
    private float localScale;
    private float drag = 0.9F;

    private static final float[] sizeScale = {0.1F, 0.20F, 0.35F, 0.5F, 0.65F, 0.8F, 0.85F, 1F, 1F, 1F, 1F, 0.85F, 0.45F, 0.2F, 0.1F};

    private static final int[] fadeColors = {0x00D5ED, 0xD400E8, 0xE52A00, 0x95E500, 0xE27C00, 0x5200E0};
    private static final int color = 0xEFEF00;

    public StarParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.motionX = motionX + (Math.random() * 2.0D - 1.0D) * (double)0.1F;
        this.motionY = motionY + (Math.random() * 2.0D - 1.0D) * (double)0.1F;
        this.motionZ = motionZ + (Math.random() * 2.0D - 1.0D) * (double)0.1F;

        this.startAge = this.rand.nextInt(3);
        this.maxAge = this.startAge + 15;
        this.localScale = 0.5F + (this.rand.nextFloat() * 1.5F);
        this.particleAngle = this.rand.nextFloat();
        this.prevParticleAngle = this.particleAngle;
        this.particleRed = 1F - (this.rand.nextFloat() * 0.015F);
        this.particleGreen = 1F - (this.rand.nextFloat() * 0.015F);
        this.particleBlue = 1F - (this.rand.nextFloat() * 0.015F);
        this.particleGravity = 0.4F;
        this.angleDirection = this.rand.nextFloat() > 0.5F ? 1F : -1F;
        this.particleScale = 0F;
        this.setSize(0F, 0F);

        this.selectSpriteRandomly(texture);
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

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (this.angleDirection * 0.5F);
        //this.particleScale = 0.1F * sizeScale[this.age % 20];// * this.getViewScale();
        this.prevScale = this.particleScale;
        float f = 0.10F * sizeScale[realAge % 15] * this.getViewScale() * localScale;
        this.setSize(f, f);
        this.particleScale = f;

        //Make the particle fade out as it is nearing the end of its life
        this.prevAlpha = this.particleAlpha;
        this.particleAlpha = realAge >= 10 ? 1F - ((realAge - 10) / 5F) : 1F;
    }

    private void standardTick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04D * (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)drag;
            this.motionY *= (double)drag;
            this.motionZ *= (double)drag;
            if (this.onGround) {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }

        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        float f = this.particleAlpha;
        float f2 = MathHelper.lerp(partialTicks, this.prevAlpha, this.particleAlpha);
        this.particleAlpha = f2;
        super.renderParticle(buffer, renderInfo, partialTicks);
        this.particleAlpha = f;
    }

    @Override
    public float getScale(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevScale, this.particleScale);
    }

    @Override
    protected void setSize(float particleWidth, float particleHeight) {
        double xOff = this.width - particleWidth;
        double yOff = this.height - particleHeight;

        super.setSize(particleWidth, particleHeight);

        this.move(xOff, yOff, xOff);
    }

    //Get the scale to render the particle at based on how far away the player is
    private float getViewScale() {
        return 0.1F + (float) (Math.sqrt(Minecraft.getInstance().player.getPositionVec().squareDistanceTo(this.posX, this.posY, this.posZ))) / 5.0F;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(PixelTweaks.MODID, "star");
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new StarParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
