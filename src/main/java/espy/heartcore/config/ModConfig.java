package espy.heartcore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "heartcore")
public class ModConfig implements ConfigData {


    @ConfigEntry.Gui.CollapsibleObject
    public Healing healingConfig = new Healing();

    @ConfigEntry.Gui.CollapsibleObject
    public Respawning respawningConfig = new Respawning();


    public static class Healing {
        public int maxHearts = 10;
        public int minHearts = 1;
        public int respawnCost = 1;
        public boolean regainHearts = true;
        public List<String> healingItems = new ArrayList<>(List.of("minecraft:enchanted_golden_apple"));
    }

    public static class Respawning {
        public boolean randomRespawn = false;
        public int maxRadius = 25000;
        public int minRadius = 20000;
    }





    @SuppressWarnings("unused")
    public ModConfig() {}

    public ModConfig(int minHearts) {
        this.healingConfig.minHearts = minHearts;
    }
}
