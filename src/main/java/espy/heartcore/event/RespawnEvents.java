package espy.heartcore.event;

import espy.heartcore.Heartcore;
import espy.heartcore.util.HeartcoreManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class RespawnEvents {
    public static void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!newPlayer.getWorld().getLevelProperties().isHardcore()) return;
        if (HeartcoreManager.isOutOfLives(newPlayer)) return;

        // Remove a heart and heal
        HeartcoreManager.removeHeart(newPlayer);
        newPlayer.setHealth(newPlayer.getMaxHealth());

        if (Heartcore.CONFIG.respawningConfig.randomRespawn) {
            BlockPos pos = findRandomLandPosition(newPlayer.getServerWorld());
            if (pos != null) {
                Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                newPlayer.teleport(newPlayer.getServerWorld(), pos.getX(), pos.getY(), pos.getZ(), flags, newPlayer.getYaw(), newPlayer.getPitch(), true);
            }
        }
        // Delay game mode change
        DelayedEvents.scheduleSurvivalMode(newPlayer);
    }

    private static BlockPos findRandomLandPosition(ServerWorld world) {
        int MAX_ATTEMPTS = 20;
        Vec3i center = world.getSpawnPos();

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            BlockPos targetXZ = getRandomXZ(center);
            Chunk chunk = world.getChunk(targetXZ);
            OptionalInt yOpt = findSafeTopY(chunk, targetXZ.getX(), targetXZ.getZ(), world);

            if (yOpt.isEmpty()) continue;

            int y = yOpt.getAsInt();
            BlockPos targetPos = new BlockPos(targetXZ.getX(), y, targetXZ.getZ());

            if (isSafePosition(chunk, targetPos)) {
                return targetPos;
            }
        }

        return null;
    }


    private static BlockPos getRandomXZ(Vec3i center) {
        int MIN_RADIUS = Heartcore.CONFIG.respawningConfig.minRadius;
        int MAX_RADIUS = Heartcore.CONFIG.respawningConfig.maxRadius;

        Random rand = new Random();
        double r = Math.sqrt(rand.nextDouble() * (MAX_RADIUS * MAX_RADIUS - MIN_RADIUS * MIN_RADIUS) + MIN_RADIUS * MIN_RADIUS);
        double angle = rand.nextDouble() * 2 * Math.PI;
        int dx = (int) Math.round(r * Math.cos(angle));
        int dz = (int) Math.round(r * Math.sin(angle));
        return new BlockPos(center.getX() + dx, 0, center.getZ() + dz);
    }

    private static OptionalInt findSafeTopY(Chunk chunk, int x, int z, ServerWorld world) {
        final int topY = chunk.getTopYInclusive();
        final int bottomY = chunk.getBottomY();
        final BlockPos.Mutable pos = new BlockPos.Mutable(x, topY - 1, z);

        BlockState head = chunk.getBlockState(pos); // Start at top - 1
        BlockState body = chunk.getBlockState(pos.move(Direction.DOWN));
        BlockState feet;

        while (pos.getY() > bottomY) {
            feet = chunk.getBlockState(pos.move(Direction.DOWN));
            if (feet.isSideSolidFullSquare(world, pos, Direction.UP) && body.isAir() && head.isAir()) {
                return OptionalInt.of(pos.getY() + 1);
            }
            head = body;
            body = feet;
        }

        return OptionalInt.empty();
    }

    private static boolean isSafePosition(Chunk chunk, BlockPos pos) {
        if (pos.getY() <= chunk.getBottomY()) return false;
        var state = chunk.getBlockState(pos.down());
        return state.getFluidState().isEmpty() && state.getBlock() != Blocks.FIRE;
    }
}