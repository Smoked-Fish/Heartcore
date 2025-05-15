package espy.heartcore.client;

import espy.heartcore.network.ConfigSyncPacket;
import espy.heartcore.network.HandshakePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class HeartcoreClient implements ClientModInitializer {
    public static boolean isServerHeartcorePresent = false;
    public static int serverMinHearts;

    @Override
    public void onInitializeClient() {
        // Register packet
        ClientPlayNetworking.registerGlobalReceiver(HandshakePacket.ID, (payload, context) -> {
            MinecraftClient.getInstance().execute(() -> isServerHeartcorePresent = true);
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, (payload, context) -> {
            MinecraftClient.getInstance().execute(() -> serverMinHearts = payload.modConfig().healingConfig.minHearts);
        });
    }
}
