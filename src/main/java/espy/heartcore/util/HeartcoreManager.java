package espy.heartcore.util;

import espy.heartcore.Heartcore;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;


public class HeartcoreManager {
    public static int serverMinHearts;

    public static void removeHeart(PlayerEntity player) {
        if (!player.getWorld().getLevelProperties().isHardcore()) return;

        float newMaxHealth = Math.max((player.getMaxHealth() - Heartcore.CONFIG.healingConfig.respawnHeartPenalty), Heartcore.CONFIG.healingConfig.minHearts);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(newMaxHealth);
    }

    public static void addHeart(PlayerEntity player, int healingAmount) {
        float newMax = Math.min(player.getMaxHealth() + healingAmount, Heartcore.CONFIG.healingConfig.maxHearts);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(newMax);
    }

    public static boolean isOutOfLives(PlayerEntity player) {
        float newMax = player.getMaxHealth() - Heartcore.CONFIG.healingConfig.respawnHeartPenalty;
        return newMax < serverMinHearts;
    }

    public static boolean givePlayerHearts(PlayerEntity from, PlayerEntity to, int amount) {
        if (from == to) return false;

        double minHearts = Heartcore.CONFIG.healingConfig.minHearts;
        double maxHearts = Heartcore.CONFIG.healingConfig.maxHearts;

        double fromHearts = from.getAttributeBaseValue(EntityAttributes.MAX_HEALTH);
        double toHearts = to.getAttributeBaseValue(EntityAttributes.MAX_HEALTH);

        if (fromHearts - amount < minHearts) return false;
        if (toHearts + amount > maxHearts) return false;

        Objects.requireNonNull(from.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(fromHearts - amount);
        Objects.requireNonNull(to.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(toHearts + amount);

        return true;
    }
}
