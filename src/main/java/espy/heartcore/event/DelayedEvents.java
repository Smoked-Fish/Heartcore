package espy.heartcore.event;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DelayedEvents {
    private static final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();

    // Call this when you want to delay the game mode change
    public static void scheduleSurvivalMode(ServerPlayerEntity player) {
        playersToUpdate.add(player.getUuid());
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (playersToUpdate.remove(player.getUuid())) {
                    player.changeGameMode(GameMode.SURVIVAL);
                }
            }
        });
    }
}