package espy.heartcore.event;

import espy.heartcore.util.CustomPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DeathEvents {
    public static void afterPlayerDeath(LivingEntity entity, DamageSource ignoredDamageSource) {
        if (!(entity instanceof ServerPlayerEntity player)) return;


        updateDeathFlags(player);
    }

    private static void updateDeathFlags(ServerPlayerEntity player) {
        if (player instanceof CustomPlayerData data) {
            data.setRecentlyDiedFlag(true);
            data.setHealthAtDeath(player.getMaxHealth());
        }
    }
}