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
    public static void onPlayerRespawn(ServerPlayerEntity ignoredOldPlayer, ServerPlayerEntity newPlayer, boolean ignoredWasAlive) {
        if (!isEligibleForRespawn(newPlayer)) return;

        processRespawnEffects(newPlayer);
        handleRandomRespawn(newPlayer);
    }

    private static boolean isEligibleForRespawn(ServerPlayerEntity player) {
        return player.getWorld().getLevelProperties().isHardcore() && !HeartcoreManager.isOutOfLives(player);
    }

    private static void processRespawnEffects(ServerPlayerEntity player) {
        HeartcoreManager.removeHeart(player);
        player.setHealth(player.getMaxHealth());
    }

    private static void handleRandomRespawn(ServerPlayerEntity player) {
        if (!Heartcore.CONFIG.respawningConfig.randomRespawn) return;

        BlockPos pos = findRandomLandPosition(player.getServerWorld());
        if (pos != null) {
            Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
            player.teleport(player.getServerWorld(), pos.getX(), pos.getY(), pos.getZ(), flags, player.getYaw(), player.getPitch(), true);
        }
    }

    private static BlockPos findRandomLandPosition(ServerWorld world) {
        final int MAX_ATTEMPTS = 20;
        Vec3i center = world.getSpawnPos();

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            BlockPos targetXZ = getRandomXZ(center);
            Chunk chunk = world.getChunk(targetXZ);
            OptionalInt yOpt = findSafeTopY(chunk, targetXZ.getX(), targetXZ.getZ(), world);

            if (yOpt.isPresent()) {
                BlockPos targetPos = new BlockPos(targetXZ.getX(), yOpt.getAsInt(), targetXZ.getZ());
                if (isSafePosition(chunk, targetPos)) return targetPos;
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

    private static OptionalInt findSafeTopY(Chunk chunk, int x, int z, ServerWorld world) {
        final int topY = chunk.getTopYInclusive();
        final int bottomY = chunk.getBottomY();
        final BlockPos.Mutable pos = new BlockPos.Mutable(x, topY - 1, z);

        BlockState head = chunk.getBlockState(pos);
        BlockState body = chunk.getBlockState(pos.move(Direction.DOWN));

        while (pos.getY() > bottomY) {
            BlockState feet = chunk.getBlockState(pos.move(Direction.DOWN));
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
        BlockState state = chunk.getBlockState(pos.down());
        return state.getFluidState().isEmpty() && state.getBlock() != Blocks.FIRE;
    }
}