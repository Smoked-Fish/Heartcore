package espy.heartcore.mixin;

import espy.heartcore.Heartcore;
import espy.heartcore.event.ItemFinishedEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemFinishedMixin {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void heartcore$finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.getLevelProperties().isHardcore()) return;
        if (!(user instanceof ServerPlayerEntity player)) return;
        if (!Heartcore.CONFIG.healingConfig.enableItemHealing) return;

        ItemFinishedEvents.EVENT.invoker().onFinish(player, world, stack.copy());
    }
}
