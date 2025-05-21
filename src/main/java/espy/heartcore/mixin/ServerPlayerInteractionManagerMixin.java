package espy.heartcore.mixin;

import espy.heartcore.util.CustomPlayerData;
import espy.heartcore.util.HeartcoreManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method = "changeGameMode", at = @At("HEAD"), cancellable = true)
    private void onChangeGameMode(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        boolean outOfLives = HeartcoreManager.isOutOfLives(player);
        if (player instanceof CustomPlayerData data) {
            if (player.server.isHardcore() && gameMode == GameMode.SPECTATOR && !outOfLives && data.getRecentlyDiedFlag()) {
                data.setRecentlyDiedFlag(false);
                cir.setReturnValue(false);
            }
        }
    }
}