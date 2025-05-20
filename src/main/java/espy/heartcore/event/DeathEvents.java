package espy.heartcore.event;

import espy.heartcore.util.CustomPlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DeathEvents {
    public static void afterPlayerDeath(LivingEntity entity, DamageSource ignoredDamageSource) {
        if (entity instanceof ServerPlayerEntity player && player instanceof CustomPlayerData data) {
            data.setRecentlyDiedFlag(true);
        }
    }
}
