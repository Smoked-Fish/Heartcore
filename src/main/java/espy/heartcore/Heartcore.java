package espy.heartcore;

import espy.heartcore.command.HeartcoreCommandRegistry;
import espy.heartcore.config.ModConfig;
import espy.heartcore.event.HeartcoreEventRegistry;
import espy.heartcore.network.ConfigSyncPacket;
import espy.heartcore.network.HandshakePacket;
import espy.heartcore.util.HeartcoreManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Heartcore implements ModInitializer {
	public static final String MOD_ID = "heartcore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig CONFIG;

	private void loadConfig() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		HeartcoreManager.serverMinHearts = Heartcore.CONFIG.healingConfig.minHearts;
	}

	@Override
	public void onInitialize() {
		HeartcoreEventRegistry.register();
		loadConfig();
		HandshakePacket.register();
		ConfigSyncPacket.register();
		HeartcoreCommandRegistry.register();
	}
}