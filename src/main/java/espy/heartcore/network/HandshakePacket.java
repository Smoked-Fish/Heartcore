package espy.heartcore.network;

import espy.heartcore.Heartcore;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HandshakePacket() implements CustomPayload {
    public static final Identifier HEARTCORE_HANDSHAKE = Identifier.of(Heartcore.MOD_ID, "handshake");
    public static final Id<HandshakePacket> ID = new Id<>(HEARTCORE_HANDSHAKE);

    public static final PacketCodec<net.minecraft.network.PacketByteBuf, HandshakePacket> CODEC = PacketCodec.unit(new HandshakePacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!server.isHardcore()) return;
            ServerPlayNetworking.send(handler.player, new HandshakePacket());
        });
    }
}
