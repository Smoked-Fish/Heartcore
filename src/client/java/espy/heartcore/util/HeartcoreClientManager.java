package espy.heartcore.util;

import espy.heartcore.Heartcore;
import net.minecraft.entity.player.PlayerEntity;

import static espy.heartcore.util.HeartcoreManager.serverMinHearts;

public class HeartcoreClientManager {
    public static boolean isOutOfLives(PlayerEntity player) {
        float newMax = player.getMaxHealth() - Heartcore.CONFIG.healingConfig.respawnHeartPenalty;
        return newMax < serverMinHearts;
    }
}
