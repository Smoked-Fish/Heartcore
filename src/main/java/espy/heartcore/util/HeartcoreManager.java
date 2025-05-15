package espy.heartcore.util;

import espy.heartcore.Heartcore;
import espy.heartcore.client.HeartcoreClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.Objects;


public class HeartcoreManager {
    public static void removeHeart(PlayerEntity player) {
        if (!player.getWorld().getLevelProperties().isHardcore()) return;

        float newMaxHealth = Math.max((player.getMaxHealth() - Heartcore.CONFIG.healingConfig.respawnCost * 2), Heartcore.CONFIG.healingConfig.minHearts * 2);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(newMaxHealth);
    }

    public static void addHeart(PlayerEntity player) {
        float newMax = Math.min(player.getMaxHealth() + 2, Heartcore.CONFIG.healingConfig.maxHearts * 2);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(newMax);
    }

    public static boolean isOutOfLives(PlayerEntity player) {
        float newMax = player.getMaxHealth() - Heartcore.CONFIG.healingConfig.respawnCost * 2;
        return newMax < HeartcoreClient.serverMinHearts * 2;
    }
}
