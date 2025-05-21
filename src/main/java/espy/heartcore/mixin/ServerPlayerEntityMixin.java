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
    // NBT keys
    @Unique private static final String RECENTLY_DIED = "heartcore:recently_died";
    @Unique private static final String SEEN_BEFORE = "heartcore:seen_before";
    @Unique private static final String HEALTH_AT_DEATH = "heartcore:health_at_death";

    // Stored state
    @Unique private boolean recentlyDied = false;
    @Unique private boolean seenBefore = false;
    @Unique private float healthAtDeath;


    // === NBT Serialization ===
    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void onWriteCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(RECENTLY_DIED, recentlyDied);
        nbt.putBoolean(SEEN_BEFORE, seenBefore);
        nbt.putFloat(HEALTH_AT_DEATH, healthAtDeath);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void onReadCustomData(NbtCompound nbt, CallbackInfo ci) {
        this.recentlyDied = nbt.getBoolean(RECENTLY_DIED, recentlyDied);
        this.seenBefore = nbt.getBoolean(SEEN_BEFORE, seenBefore);
        this.healthAtDeath = nbt.getFloat(HEALTH_AT_DEATH, healthAtDeath);
    }


    // === Player Data Transfer ===
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void onCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof CustomPlayerData) {
            this.recentlyDied = ((CustomPlayerData) oldPlayer).getRecentlyDiedFlag();
            this.seenBefore = ((CustomPlayerData) oldPlayer).getSeenBeforeFlag();
            this.healthAtDeath = ((CustomPlayerData) oldPlayer).getHealthAtDeath();
        }
    }


    // === Getters ===
    @Override
    public boolean getRecentlyDiedFlag() {
        return recentlyDied;
    }

    @Override
    public boolean getSeenBeforeFlag() {
        return seenBefore;
    }

    @Override
    public float getHealthAtDeath() {
        return healthAtDeath;
    }


    // === Setters ===
    @Override
    public void setRecentlyDiedFlag(boolean value) {
        this.recentlyDied = value;
    }

    @Override
    public void setSeenBeforeFlag(boolean value) {
        this.seenBefore = value;
    }

    @Override
    public void setHealthAtDeath(float value) {
        this.healthAtDeath = value;
    }
}
