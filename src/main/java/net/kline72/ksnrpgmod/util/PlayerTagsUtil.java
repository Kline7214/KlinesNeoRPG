package net.kline72.ksnrpgmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerTagsUtil {

    private static final String DATA_KEY_STATUS = "PlayerStatus";
    private static final String DATA_KEY_EXPIRY = "StatusExpiryTime";

    public static void setPlayerStatus(ServerPlayer player, String status, long expiryTime) {
        CompoundTag tag = player.getPersistentData();
        tag.putString(DATA_KEY_STATUS, status);
        tag.putLong(DATA_KEY_EXPIRY, expiryTime);
    }

    public static String getPlayerStatus(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        return tag.getString(DATA_KEY_STATUS);
    }

    public static boolean hasStatus(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        return tag.contains(DATA_KEY_STATUS);
    }

    public static long getStatusExpiry(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        return tag.getLong(DATA_KEY_EXPIRY);
    }

    public static void clearPlayerStatus(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        tag.remove(DATA_KEY_STATUS);
        tag.remove(DATA_KEY_EXPIRY);
    }
}
