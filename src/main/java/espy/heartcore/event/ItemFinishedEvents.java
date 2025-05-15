package espy.heartcore.event;

import espy.heartcore.Heartcore;
import espy.heartcore.util.HeartcoreManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

    class DefaultHandler {
        public static void register() {
            EVENT.register((player, world, stack) -> {
                if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

                boolean inConfig = Heartcore.CONFIG.healingConfig.healingItems.contains(stack.getItem().toString());
                boolean hasHeartTag = hasAddHeartTag(stack);

                if (inConfig || hasHeartTag) {
                    HeartcoreManager.addHeart(serverPlayer);
                }
            });
        }
    }

    private static boolean hasAddHeartTag(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) return false;

        NbtCompound nbt = component.copyNbt();
        return nbt.contains("heartcore:add_heart") && nbt.getBoolean("heartcore:add_heart");
    }
}
