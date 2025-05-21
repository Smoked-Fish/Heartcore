package espy.heartcore.event;

import com.google.common.base.Stopwatch;
import espy.heartcore.Heartcore;
import espy.heartcore.util.HeartcoreManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RespawnEvents {
    public static void onPlayerRespawn(ServerPlayerEntity ignoredOldPlayer, ServerPlayerEntity newPlayer, boolean ignoredWasAlive) {
        if (!shouldRespawn(newPlayer)) return;

        applyRespawnEffects(newPlayer);
        tryRandomRespawn(newPlayer);
    }

    private static final Executor randomTeleportExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "RandomRespawn-LocationFinder");
        thread.setUncaughtExceptionHandler((t, e) ->
                Heartcore.LOGGER.error("Exception in Random Respawn thread", e)
        );
        return thread;
    });

    // === Respawn Eligibility & Effects ===

    private static boolean shouldRespawn(ServerPlayerEntity player) {
        return player.getWorld().getLevelProperties().isHardcore()
                && !HeartcoreManager.isOutOfLives(player);
    }

    private static void applyRespawnEffects(ServerPlayerEntity player) {
        HeartcoreManager.removeHeart(player);
        player.setHealth(player.getMaxHealth());
    }


    // Adapted with reference to MIT-licensed code from John-Paul-R/Essential-Commands.
    // See: https://github.com/John-Paul-R/Essential-Commands/ for the original implementation.
    private static void tryRandomRespawn(ServerPlayerEntity player) {
        if (!Heartcore.CONFIG.respawningConfig.randomRespawn) return;

        ServerWorld world = player.getServerWorld();

        randomTeleportExecutor.execute(() -> {
            Heartcore.LOGGER.info("Starting random respawn search for {}", player.getGameProfile().getName());
            Stopwatch timer = Stopwatch.createStarted();

            BlockPos safeRespawn = null;
            for (int attempts = 0; attempts < 10 && safeRespawn == null; attempts++) {
                try {
                    safeRespawn = findRandomLandPosition(world);
                } catch (Exception e) {
                    Heartcore.LOGGER.warn("Respawn attempt {} failed: {}", attempts + 1, e.getMessage());
                }
            }

            if (safeRespawn != null) {
                teleportPlayerToRespawn(player, world, safeRespawn);
            } else {
                Heartcore.LOGGER.error("No valid respawn found after 10 attempts for {}", player.getGameProfile().getName());
            }

            Heartcore.LOGGER.info("Respawn search completed in {}", timer.stop());
        });
    }

    private static void teleportPlayerToRespawn(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        Objects.requireNonNull(player.getServer()).execute(() -> {
            world.getChunk(pos.getX() >> 4, pos.getZ() >> 4); // Ensure chunk is loaded
            player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYaw(), player.getPitch());
            Heartcore.LOGGER.info("Player {} respawned at {}", player.getName().getString(), pos);
        });
    }

    // === Safe Position Search ===

    private static BlockPos findRandomLandPosition(ServerWorld world) {
        Vec3i center = world.getSpawnPos();
        final int MAX_ATTEMPTS = 20;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            BlockPos candidate = getRandomXZ(center);
            Chunk chunk = world.getChunk(candidate);
            OptionalInt safeY = findSafeTopY(candidate.getX(), candidate.getZ(), world);

            if (safeY.isPresent()) {
                BlockPos finalPos = new BlockPos(candidate.getX(), safeY.getAsInt(), candidate.getZ());
                if (isSafePosition(chunk, finalPos)) return finalPos;
            }
        }

        return null;
    }

    private static BlockPos getRandomXZ(Vec3i center) {
        int minRadius = Heartcore.CONFIG.respawningConfig.minRadius;
        int maxRadius = Heartcore.CONFIG.respawningConfig.maxRadius;

        Random rand = new Random();
        double r = Math.sqrt(rand.nextDouble() * (maxRadius * maxRadius - minRadius * minRadius) + minRadius * minRadius);
        double angle = rand.nextDouble() * 2 * Math.PI;

        int dx = (int) Math.round(r * Math.cos(angle));
        int dz = (int) Math.round(r * Math.sin(angle));

        return new BlockPos(center.getX() + dx, 0, center.getZ() + dz);
    }

    private static OptionalInt findSafeTopY(int x, int z, ServerWorld world) {
        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
        BlockPos pos = new BlockPos(x, y, z);

        BlockState below = world.getBlockState(pos.down());
        BlockState feet = world.getBlockState(pos);
        BlockState head = world.getBlockState(pos.up());

        if (below.isSideSolidFullSquare(world, pos.down(), Direction.UP)
                && feet.isAir() && head.isAir()) {
            return OptionalInt.of(y);
        }

        return OptionalInt.empty();
    }

    private static boolean isSafePosition(Chunk chunk, BlockPos pos) {
        if (pos.getY() <= chunk.getBottomY()) return false;
        BlockState state = chunk.getBlockState(pos.down());
        return state.getFluidState().isEmpty() && state.getBlock() != Blocks.FIRE;
    }
}