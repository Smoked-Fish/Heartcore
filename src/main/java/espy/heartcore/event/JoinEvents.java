package espy.heartcore.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class JoinEvents {
    static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
    }
}