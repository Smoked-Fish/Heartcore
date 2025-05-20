package espy.heartcore.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class HeartcoreEventRegistry {
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(RespawnEvents::onPlayerRespawn);
        ServerPlayConnectionEvents.JOIN.register(JoinEvents::onPlayerJoin);
        ItemFinishedEvents.EVENT.register(ItemFinishedEvents::onItemFinished);
        ServerLivingEntityEvents.AFTER_DEATH.register(DeathEvents::afterPlayerDeath);
    }
}