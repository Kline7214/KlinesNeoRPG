package net.kline72.ksnrpgmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class MobTagsUtil {

    private static final String DATA_KEY = "MobClassification";

    public static void setMobClassification(Entity entity, String classification) {
        CompoundTag tag = entity.getPersistentData();
        tag.putString(DATA_KEY, classification);
    }

    public static String getMobClassification(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.getString(DATA_KEY);
    }

    public static boolean hasClassification(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.contains(DATA_KEY);
    }
}
