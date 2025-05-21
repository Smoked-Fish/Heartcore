package espy.heartcore.event;

import espy.heartcore.Heartcore;
import espy.heartcore.util.CustomPlayerData;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class JoinEvents {
    static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender ignoredSender, MinecraftServer ignoredServer) {
        CustomPlayerData data = (handler.player instanceof CustomPlayerData) ? (CustomPlayerData) handler.player : null;
        if (data == null) return;

        handleNewPlayer(data, handler.player);
        enforceMaxHealth(handler.player);
    }

    private static void handleNewPlayer(CustomPlayerData data, ServerPlayerEntity player) {
        if (!data.getSeenBeforeFlag()) {
            double maxHearts = Heartcore.CONFIG.healingConfig.maxHearts;

            if (player.getMaxHealth() == maxHearts) return;

            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(maxHearts);

            data.setSeenBeforeFlag(true);
        }
    }

    private static void enforceMaxHealth(PlayerEntity player) {
        if (!Heartcore.CONFIG.healingConfig.enforceMaxHealth) return;

        double maxHearts = Heartcore.CONFIG.healingConfig.maxHearts;
        if (player.getMaxHealth() > maxHearts) {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(maxHearts);
        }
    }
}