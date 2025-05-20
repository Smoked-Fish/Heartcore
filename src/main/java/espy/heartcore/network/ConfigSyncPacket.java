package espy.heartcore.network;

import espy.heartcore.Heartcore;
import espy.heartcore.config.ModConfig;
import espy.heartcore.util.HeartcoreManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

// Define the config values to sync
public record ConfigSyncPacket(ModConfig modConfig) implements CustomPayload {
    public static final Identifier CONFIG_SYNC = Identifier.of(Heartcore.MOD_ID, "config_sync");
    public static final Id<ConfigSyncPacket> ID = new Id<>(CONFIG_SYNC);
    public static MinecraftServer SERVER;

     public static final PacketCodec<RegistryByteBuf, ConfigSyncPacket> CODEC = PacketCodec.tuple(
             PacketCodecs.INTEGER, (ConfigSyncPacket p) -> AutoConfig.getConfigHolder(ModConfig.class).get().healingConfig.minHearts,
             (min) -> new ConfigSyncPacket(new ModConfig(min))
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // Server registration to send the config when a player joins
    public static void register() {
        // Register the packet type
        PayloadTypeRegistry.playS2C().register(ID, CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        // Send config to client on join if hardcore
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!server.isHardcore()) return;
            ServerPlayNetworking.send(handler.player, new ConfigSyncPacket(Heartcore.CONFIG));
        });

        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener(ConfigSyncPacket::onConfigSaved);
    }


    private static ActionResult onConfigSaved(ConfigHolder<ModConfig> holder, ModConfig config) {
        MinecraftServer server = SERVER;

        if (server == null || !server.isHardcore()) return ActionResult.PASS;

        ConfigSyncPacket packet = new ConfigSyncPacket(config);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, packet);
        }

        if (server.isDedicated()){
            HeartcoreManager.serverMinHearts = config.healingConfig.minHearts;
        }

        return ActionResult.SUCCESS;
    }
}
