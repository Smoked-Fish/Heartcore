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
    Event<ItemFinishedEvents> EVENT = EventFactory.createArrayBacked(ItemFinishedEvents.class,
            (listeners) -> (player, world, stack) -> {
                for (ItemFinishedEvents listener : listeners) {
                    listener.onFinish(player, world, stack);
                }
            });

    void onFinish(PlayerEntity player, World world, ItemStack stack);

    static void onItemFinished(PlayerEntity player, World world, ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        boolean inConfig = Heartcore.CONFIG.healingConfig.healingItems.contains(stack.getItem().toString());
        Integer customData = hasAddHeartTag(stack);

        if (customData == null && inConfig) {
            HeartcoreManager.addHeart(serverPlayer, Heartcore.CONFIG.healingConfig.heartsPerHealingItem);
        } else if (customData != null) {
            HeartcoreManager.addHeart(serverPlayer, customData);
        }
    }

    private static Integer hasAddHeartTag(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) return null;
        if (!component.copyNbt().contains("heartcore:heart_modifier")) return null;

        return component.copyNbt().getInt("heartcore:heart_modifier", 2);
    }
}
