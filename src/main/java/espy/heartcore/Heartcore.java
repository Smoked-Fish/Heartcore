package espy.heartcore;

import espy.heartcore.config.ModConfig;
import espy.heartcore.network.ConfigSyncPacket;
import espy.heartcore.network.HandshakePacket;
import espy.heartcore.event.HeartcoreEventRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Heartcore implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Heartcore");
	public static final String MOD_ID = "heartcore";
	public static ModConfig CONFIG;

	private void loadConfig() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	@Override
	public void onInitialize() {
		HeartcoreEventRegistry.register();
		loadConfig();
		HandshakePacket.register();
		ConfigSyncPacket.register();
	}
}