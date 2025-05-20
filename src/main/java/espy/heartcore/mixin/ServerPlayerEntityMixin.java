package espy.heartcore.mixin;

import espy.heartcore.util.CustomPlayerData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements CustomPlayerData {
    @Unique private static final String RECENTLY_DIED = "heartcore:recently_died";
    @Unique private boolean recentlyDied = false;

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void onWriteCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(RECENTLY_DIED, recentlyDied);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void onCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof CustomPlayerData) {
            this.recentlyDied = ((CustomPlayerData) oldPlayer).getRecentlyDiedFlag();
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void onReadCustomData(NbtCompound nbt, CallbackInfo ci) {
        this.recentlyDied = nbt.getBoolean(RECENTLY_DIED, recentlyDied);
    }

    @Override
    public boolean getRecentlyDiedFlag() {
        return recentlyDied;
    }

    @Override
    public void setRecentlyDiedFlag(boolean value) {
        this.recentlyDied = value;
    }
}
