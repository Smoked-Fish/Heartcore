package espy.heartcore.event;

import espy.heartcore.Heartcore;
import espy.heartcore.util.HeartcoreManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface ItemFinishedEvents {
    Event<ItemFinishedEvents> ITEM_FINISHED = EventFactory.createArrayBacked(
            ItemFinishedEvents.class,
            (listeners) -> (player, world, stack) -> {
                for (ItemFinishedEvents listener : listeners) {
                    listener.onFinish(player, world, stack);
                }
            });

    void onFinish(PlayerEntity player, World world, ItemStack stack);

    static void onItemFinished(PlayerEntity player, World ignoredWorld, ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        int heartAmount = getHeartsToHeal(stack);
        if (heartAmount > 0) {
            HeartcoreManager.addHeart(serverPlayer, heartAmount);
        }
    }

    static int getHeartsToHeal(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (component != null && component.copyNbt().contains("heartcore:heart_modifier")) {
            return component.copyNbt().getInt("heartcore:heart_modifier");
        }

        return Heartcore.CONFIG.healingConfig.healingItems.contains(stack.getItem().toString())
                ? Heartcore.CONFIG.healingConfig.heartsPerHealingItem
                : 0;
    }
}
