package net.kline72.ksnrpgmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "klinesneorpg", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobScalingUtil {
    private static final int DISTANCE_PER_LEVEL = 1000;
    private static final double HOSTILE_ATTACK_INCREMENT = 0.2;
    private static final double HOSTILE_HEALTH_INCREMENT = 1.0;
    private static final double PASSIVE_HEALTH_INCREMENT = 2.0;
    private static final String SCALED_TAG = "scaled";

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        double distance = mob.position().distanceTo(serverLevel.getSharedSpawnPos().getCenter());
        int level = (int) (distance / DISTANCE_PER_LEVEL);

        applyScaling(mob, level);
    }

    private static void applyScaling(Mob mob, int level) {
        CompoundTag entityData = mob.getPersistentData();
        if (entityData.getBoolean(SCALED_TAG)) {
            return;
        }

        AttributeInstance healthAttribute = mob.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackAttribute = mob.getAttribute(Attributes.ATTACK_DAMAGE);

        if (mob instanceof Monster) {
            if (healthAttribute != null) {
                healthAttribute.setBaseValue(healthAttribute.getBaseValue() + (level * HOSTILE_HEALTH_INCREMENT));
            }
            if (attackAttribute != null) {
                attackAttribute.setBaseValue(attackAttribute.getBaseValue() + (level * HOSTILE_ATTACK_INCREMENT));
            }
        } else if (mob instanceof Animal || mob instanceof Villager) {
            if (healthAttribute != null) {
                healthAttribute.setBaseValue(healthAttribute.getBaseValue() + (level * PASSIVE_HEALTH_INCREMENT));
            }
        }

        mob.setHealth(mob.getMaxHealth());
        entityData.putBoolean(SCALED_TAG, true);
    }
}
