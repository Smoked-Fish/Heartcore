package espy.heartcore.util;

public interface CustomPlayerData {
    boolean getRecentlyDiedFlag();
    boolean getSeenBeforeFlag();
    float getHealthAtDeath();
    void setRecentlyDiedFlag(boolean value);
    void setSeenBeforeFlag(boolean value);
    void setHealthAtDeath(float value);
}