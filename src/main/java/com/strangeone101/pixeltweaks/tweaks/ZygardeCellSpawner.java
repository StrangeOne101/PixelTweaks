package com.strangeone101.pixeltweaks.tweaks;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.registries.PixelmonBlocks;
import com.pixelmonmod.pixelmon.api.util.Scheduling;
import com.pixelmonmod.pixelmon.api.util.helpers.BlockHelper;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.blocks.ZygardeCellBlock;
import com.pixelmonmod.pixelmon.blocks.tileentity.ZygardeCellTileEntity;
import com.pixelmonmod.pixelmon.listener.ZygardeCellsListener;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.mixin.ZygardeListenerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ZygardeCellSpawner {

    private static ResourceLocation BLOCKS_RESOURCELOCATION = new ResourceLocation(PixelTweaks.MODID, "zygarde_cell_placement/all");
    private static ResourceLocation LOGS_RESOURCELOCATION = new ResourceLocation(PixelTweaks.MODID, "zygarde_cell_placement/logs");
    private static ResourceLocation LEAVES_RESOURCELOCATION = new ResourceLocation(PixelTweaks.MODID, "zygarde_cell_placement/leaves");
    private static ResourceLocation GRASS_RESOURCELOCATION = new ResourceLocation(PixelTweaks.MODID, "zygarde_cell_placement/grass");

    private static ITag<Block> BLOCKS;
    private static ITag<Block> LOGS;
    private static ITag<Block> LEAVES;
    private static ITag<Block> GRASS;

    public ZygardeCellSpawner() {
        Scheduling.schedule(1, () -> {
            try {
                //Get the tasks in the task scheduler
                Class<?> clazz = Scheduling.class;
                Field field = clazz.getDeclaredField("tasks");
                field.setAccessible(true);
                ArrayList<Scheduling.ScheduledTask> tasks = (ArrayList<Scheduling.ScheduledTask>) field.get(null);

                PixelTweaks.LOGGER.debug("Tasks: " + tasks.size());
                //Remove all Zygarde tasks
                for (Scheduling.ScheduledTask task : tasks) {
                    String name = task.task.getClass().getName();
                    if (name.startsWith("com.pixelmonmod.pixelmon.listener.ZygardeCellsListener$$Lambda$")) {
                        task.repeats = false; //Set them to remove next tick. Easiest way to remove them without causing a ConcurrentModificationException
                    }
                    PixelTweaks.LOGGER.debug(task.task.getClass().getName());
                }

                //Get the list of blocks that it can spawn on
                ZygardeListenerMixin.getSpawnableBlocks().clear(); //Clear the existing list
                BLOCKS = BlockTags.getCollection().get(BLOCKS_RESOURCELOCATION);
                ZygardeListenerMixin.getSpawnableBlocks().addAll(BLOCKS.getAllElements()); //Add all the blocks from the tag

                LOGS = BlockTags.getCollection().get(LOGS_RESOURCELOCATION);
                LEAVES = BlockTags.getCollection().get(LEAVES_RESOURCELOCATION);
                GRASS = BlockTags.getCollection().get(GRASS_RESOURCELOCATION);

                Scheduling.schedule(100, (task) -> {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null && server.isServerRunning()) {
                        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                            if (ZygardeCellsListener.checkForCube(player)) {
                                ZygardeListenerMixin.getHasCube().add(player.getUniqueID());
                            }
                        }
                    }
                }, true);

                Scheduling.schedule(200, spawnZygardeTask(), true);
                PixelTweaks.LOGGER.info("Zygarde cell spawner initialized!");

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, false);
    }

    public Consumer<Scheduling.ScheduledTask> spawnZygardeTask() {
        return (task) -> {
            if (PixelmonConfigProxy.getSpawning().isSpawnZygardeCells()) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null && server.isServerRunning()) {
                    if (ZygardeListenerMixin.getHasCube().isEmpty()) {
                        return;
                    }

                    UUID random = (UUID) RandomHelper.getRandomElementFromArray(ZygardeListenerMixin.getHasCube().toArray(new UUID[0]));
                    ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(random);
                    if (player.isSpectator()) {
                        return;
                    }

                    List<Chunk> chunks = Lists.newArrayList();
                    int distance = server.getPlayerList().getViewDistance();
                    int x1 = player.chunkCoordX + distance;
                    int z1 = player.chunkCoordZ + distance;
                    int x2 = player.chunkCoordX - distance;
                    int z2 = player.chunkCoordZ - distance;

                    for(int x = x1; x >= x2; --x) {
                        for(int z = z1; z >= z2; --z) {
                            if (x < player.chunkCoordX - 1 || x > player.chunkCoordX + 1 || z < player.chunkCoordZ - 1 || z > player.chunkCoordZ + 1) {
                                Chunk chunk = player.getServerWorld().getChunkProvider().getChunkNow(x, z);
                                if (chunk != null) {
                                    chunks.add(chunk);
                                }
                            }
                        }
                    }

                    if (!chunks.isEmpty()) {
                        trySpawnInChunk(player, (Chunk)RandomHelper.getRandomElementFromList(chunks));
                    }
                }

            }
        };
    }

    public static void trySpawnInChunk(ServerPlayerEntity player, Chunk chunk) {
        World world = chunk.getWorld();
        int x = RandomHelper.getRandomNumberBetween(1, 14);
        int z = RandomHelper.getRandomNumberBetween(1, 14);
        BlockPos pos = new BlockPos(x, 62, z);
        ZygardeCellTileEntity te = (ZygardeCellTileEntity) BlockHelper.findClosestTileEntity(ZygardeCellTileEntity.class, player, 72.0, (t) -> {
            return true;
        });
        if (te == null) {
            int y = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (y != 120) {
                Multimap<Block, BlockPos> map = MultimapBuilder.hashKeys().hashSetValues().build();
                int x1 = pos.getX() + 1;
                int y1 = pos.getY() + 5;
                int z1 = pos.getZ() + 1;
                int x2 = pos.getX() - 1;
                int y2 = pos.getY() - 4;
                int z2 = pos.getZ() - 1;

                for (int lx = x1; lx >= x2; --lx) {
                    for (int ly = y1; ly >= y2; --ly) {
                        for (int lz = z1; lz >= z2; --lz) {
                            pos = new BlockPos(lx, ly, lz);
                            Block b = chunk.getBlockState(pos).getBlock();
                            if (ZygardeListenerMixin.getSpawnableBlocks().contains(b)) {
                                map.put(b, pos);
                            }
                        }
                    }
                }

                if (!map.isEmpty()) {
                    Direction facing = null;

                    boolean hasLogs = map.keys().stream().anyMatch((b) -> LOGS.contains(b));

                    if (hasLogs) {
                        List<BlockPos> logs = map.keys().stream().filter((b) -> LOGS.contains(b)).flatMap((b) -> map.get(b).stream()).collect(Collectors.toList());
                        Collections.shuffle(logs);

                        for (BlockPos pos1 : logs) {
                            BlockState state = chunk.getBlockState(pos1);
                            if (state.hasProperty(BlockStateProperties.AXIS)) {
                                if (state.get(BlockStateProperties.AXIS) == Direction.Axis.X) {
                                    facing = hasAirPocket(chunk, pos1, Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST);
                                    if (facing != null) {
                                        spawnOn(chunk, pos1.offset(facing), facing.getOpposite(), player);
                                        return;
                                    }
                                } else if (state.get(BlockStateProperties.AXIS) == Direction.Axis.Z) {
                                    facing = hasAirPocket(chunk, pos1, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH);
                                    if (facing != null) {
                                        spawnOn(chunk, pos1.offset(facing), facing.getOpposite(), player);
                                        return;
                                    }
                                }
                            }
                            facing = hasAirPocket(chunk, pos1, Direction.byHorizontalIndex(RandomHelper.getRandom().nextInt(4)));
                            if (facing != null) {
                                spawnOn(chunk, pos1.offset(facing), facing.getOpposite(), player);
                                return;
                            }
                        }
                    }

                    boolean hasLeaves = map.keys().stream().anyMatch((b) -> LEAVES.contains(b));

                    if (hasLeaves) {
                        List<BlockPos> leaves = map.keys().stream().filter((b) -> LEAVES.contains(b)).flatMap((b) -> map.get(b).stream()).collect(Collectors.toList());
                        Collections.shuffle(leaves);
                        Iterator<BlockPos> it = leaves.iterator();
                        BlockPos pos1;
                        BlockState state;

                        exit:
                        while (true) {
                            do {
                                if (!it.hasNext()) {
                                    break exit;
                                }

                                pos1 = it.next();
                                state = chunk.getBlockState(pos1);
                            } while (state.hasProperty(BlockStateProperties.UNSTABLE) && state.get(BlockStateProperties.UNSTABLE));

                            facing = hasAirPocket(chunk, pos1, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
                            if (facing != null) {
                                spawnOn(chunk, pos1.offset(facing), facing.getOpposite(), player);
                                return;
                            }
                        }
                    }

                    boolean hasGrass = map.keys().stream().anyMatch((b) -> GRASS.contains(b));

                    if (hasGrass) {
                        List<BlockPos> grass =  map.keys().stream().filter((b) -> GRASS.contains(b)).flatMap((b) -> map.get(b).stream()).collect(Collectors.toList());
                        Collections.shuffle(grass);

                        for (BlockPos pos1 : grass) {
                            pos1 = pos1.add(0, 1, 0);
                            Direction facing2 = hasAirPocket(chunk, pos1, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
                            if (facing2 != null) {
                                spawnOn(chunk, pos1.offset(facing2), Direction.DOWN, player);
                                return;
                            }
                        }
                    }
                }
            }

        }

    }

    private static void spawnOn(Chunk chunk, BlockPos pos, Direction facing, ServerPlayerEntity player) {
        BlockState currentState = chunk.getBlockState(pos);
        if (currentState.isAir() || (!currentState.isSolid() && (!currentState.hasProperty(BlockStateProperties.WATERLOGGED) || !currentState.get(BlockStateProperties.WATERLOGGED)))) {
            Direction rotation = facing.getAxis() == Direction.Axis.Y ? Direction.byHorizontalIndex(RandomHelper.getRandom().nextInt(4)) : (RandomHelper.getRandomChance() ? Direction.UP : Direction.DOWN);
            Block block = RandomHelper.getRandomChance(5) ? PixelmonBlocks.zygarde_core : PixelmonBlocks.zygarde_cell;
            BlockState state = (BlockState)((BlockState)block.getDefaultState().with(ZygardeCellBlock.ORIENTATION_PROPERTY, facing)).with(ZygardeCellBlock.ROTATION_PROPERTY, rotation);
            chunk.setBlockState(pos, state, false);
            chunk.setBlockState(pos.add(0, 2, 0), Blocks.RED_WOOL.getDefaultState(), false);
            chunk.markDirty();
            PixelTweaks.LOGGER.debug("Spawned Zygarde Cell at " + chunk.getPos().asBlockPos().add(pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    private static Direction hasAirPocket(Chunk chunk, BlockPos pos, Direction... facings) {
        List<Direction> facingList = Lists.newArrayList();
        Direction[] var4 = facings;
        int var5 = facings.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Direction facing = var4[var6];
            BlockPos offset = pos.offset(facing);
            BlockState state = chunk.getBlockState(offset);
            //If its air, or not solid, and also not waterlogged, then we can spawn on it
            if (state.isAir() || (!state.isSolid() && (!state.hasProperty(BlockStateProperties.WATERLOGGED) || !state.get(BlockStateProperties.WATERLOGGED)))) {
                facingList.add(facing);
            }
        }

        return RandomHelper.getRandomElementFromList(facingList);
    }


}
