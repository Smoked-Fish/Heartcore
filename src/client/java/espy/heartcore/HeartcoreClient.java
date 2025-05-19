package espy.heartcore;

import espy.heartcore.network.ConfigSyncPacket;
import espy.heartcore.network.HandshakePacket;
import espy.heartcore.util.HeartcoreManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class HeartcoreClient implements ClientModInitializer {
	public static boolean isServerHeartcorePresent = false;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(HandshakePacket.ID, (payload, context) -> {
			MinecraftClient.getInstance().execute(() -> isServerHeartcorePresent = true);
		});

		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, (payload, context) -> {
			MinecraftClient.getInstance().execute(() -> HeartcoreManager.serverMinHearts = payload.modConfig().healingConfig.minHearts);
		});
	}
}