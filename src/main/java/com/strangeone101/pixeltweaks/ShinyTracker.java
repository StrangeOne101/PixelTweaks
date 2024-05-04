package com.strangeone101.pixeltweaks;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.strangeone101.pixeltweaks.particle.StarParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ShinyTracker {

    public static final ShinyTracker INSTANCE = new ShinyTracker();

    private Set<Pokemon> shinyMap = new HashSet<>();
    private Set<PixelmonEntity> shinyTracking = new HashSet<>();

    public Frustum camera = null;
    private double range = TweaksConfig.shinySparkleRange.get();
    private float volume = Math.min(TweaksConfig.shinySparkleVolume.get().floatValue(), 2F);

    public boolean shouldTrackShiny(PixelmonEntity entity) {
        if (entity.isUncatchable() || !entity.isAlive() || entity.isBossPokemon()
                || entity.getOwner() != null || !entity.getPokemon().isShiny()
                || shinyMap.contains(entity.getPokemon()) || shinyTracking.contains(entity)) {
            return false;
        }
        return true;
    }

    public void track(PixelmonEntity entity) {
        shinyTracking.add(entity);
    }

    public void tick() {
        //Check if the player is in a battle, and if so, don't sparkle shinies
        if (BattleRegistry.getBattle(Minecraft.getInstance().player) != null || camera == null) return;

        //Filter out all dead entities
        shinyTracking.removeIf(entity -> !entity.isLoaded() || !entity.isAlive());

        Vector3f vec = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        camera.prepare(pos.x, pos.y, pos.z);

        //Frustum frustum = new Frustum(Minecraft.getInstance().gameRenderer.getMainCamera()., Minecraft.getInstance().getMainRenderTarget().);


        //Check all pokemon
        Iterator<PixelmonEntity> iterator = shinyTracking.iterator();
        while (iterator.hasNext()) {
            PixelmonEntity entity = iterator.next();
            //Check if the pokemon is in range & entity is being rendered

            boolean rendered = Minecraft.getInstance().getEntityRenderDispatcher().shouldRender(entity, camera, pos.x, pos.y, pos.z);
            boolean visible = rayTrace(entity);

            if (entity.position().distanceToSqr(Minecraft.getInstance().player.position()) <= range * range && rendered && visible) {
                //PixelTweaks.LOGGER.info("Visisble2: " + visible);
                //Remove from tracking
                iterator.remove();
                //PixelTweaks
                ClientScheduler.schedule(10, () -> {
                    Vec3 pos2 = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    camera.prepare(pos2.x, pos2.y, pos2.z);
                    boolean rendered2 = Minecraft.getInstance().getEntityRenderDispatcher().shouldRender(entity, camera, pos2.x, pos2.y, pos2.z);

                    if (rendered2 && rayTrace(entity)) {
                        //Add to shiny map
                        shinyMap.add(entity.getPokemon());
                        spawnSparkle(entity);
                    } else {
                        shinyTracking.add(entity); //Allow it to try again
                    }
                });
            }
        }
    }

    public void untrackAll() {
        shinyTracking.clear();
    }

    public boolean rayTrace(PixelmonEntity entity) {
        LocalPlayer player = Minecraft.getInstance().player;

        Vec3 from = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 to = entity.position();

        if (entity.level() != player.level() || from.distanceToSqr(to) > 128.0D * 128.0D) return false; //Forge Backport MC-209819

        BlockHitResult result = entity.level().clip(new ClipContext(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), entity.position(), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

        if (result.getType() == BlockHitResult.Type.MISS) {
            //PixelTweaks.LOGGER.info(result.getPos() + " " + result.hitInfo);
            return true;
        }
        return false;
    }

    public void spawnSparkle(PixelmonEntity entity) {
        LocalPlayer thiz = Minecraft.getInstance().player;
        if (volume > 0) {
            ClientScheduler.schedule(3, () -> {
                SimpleSoundInstance sound = new SimpleSoundInstance(new ResourceLocation(PixelTweaks.MODID, "sparkle"), SoundSource.PLAYERS,
                        volume, 1F, RandomSource.create(), false, 0, SoundInstance.Attenuation.LINEAR,
                        entity.getX(), entity.getY(), entity.getZ(), false);
                Minecraft.getInstance().getSoundManager().play(sound);
            });
        }


        final double d = entity.getBbWidth() / 2.5D + 0.2D;
        final double h = entity.getBoundingBoxForCulling().getYsize() / 2.5;

        double boxSize = Math.cbrt(entity.getBoundingBoxForCulling().getXsize() * entity.getBoundingBoxForCulling().getYsize() * entity.getBoundingBoxForCulling().getZsize());
        int amount = (int) Math.max(5, Math.min(boxSize * 5, 50));
        int div = 360 / amount;

        for (int i = 0; i < amount; i++) {
            double deg = i * (div) + (entity.level().random.nextInt(div / 6) - div / 3F);
            double xx = Math.cos(Math.toRadians(deg));
            double zz = Math.sin(Math.toRadians(deg));

            double driftX = (entity.level().random.nextDouble() * 0.2D - 0.1D) * d;
            double driftY = (entity.level().random.nextDouble() * 0.4D - 0.15D) * h;
            double driftZ = (entity.level().random.nextDouble() * 0.2D - 0.1D) * d;

            double x = xx * d + entity.getX() + driftX;
            double y = h + entity.getY() + driftY;
            double z = zz * d + entity.getZ() + driftZ;

            double xo = entity.getDeltaMovement().x;
            double yo = entity.getDeltaMovement().y;
            double zo = entity.getDeltaMovement().z;

            StarParticle particle = new StarParticle(Minecraft.getInstance().level, x, y, z, 0.01 * xx + xo, 0.1 * entity.getBbHeight() + yo, 0.01 * zz + zo);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

}
