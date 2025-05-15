package espy.heartcore.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class HeartcoreEventRegistry {
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(RespawnEvents::onPlayerRespawn);
        ItemFinishedEvents.DefaultHandler.register();
    }
}
