package net.kline72.ksnrpgmod.util;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID)
public class MobLevelUtil {
    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        LevelAccessor world = event.getLevel();
        double distance = 0;
        int mLevel;

        if (!entity.getPersistentData().getBoolean("scaled")) {
            distance = Math.sqrt(Math.pow(entity.getX() - world.getLevelData().getXSpawn(), 2) + Math.pow(entity.getZ() - world.getLevelData().getZSpawn(), 2));
            mLevel = (int) Math.round(distance / 100);
            entity.getPersistentData().putInt("mobLevel", mLevel);
            if (event.getEntity() instanceof Monster) {
                entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getAttributeBaseValue(Attributes.MAX_HEALTH) + entity.getPersistentData().getInt("mobLevel"));
                entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + (entity.getPersistentData().getInt("mobLevel") * 0.2));
            } else if (event.getEntity() instanceof Animal || event.getEntity() instanceof Villager) {
                entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getAttributeBaseValue(Attributes.MAX_HEALTH) + (entity.getPersistentData().getInt("mobLevel") * 2));
            } else if (event.getEntity() instanceof IronGolem) {
                entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getAttributeBaseValue(Attributes.MAX_HEALTH) + (entity.getPersistentData().getInt("mobLevel") * 2));
                entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + (entity.getPersistentData().getInt("mobLevel") * 0.4));
            }
            entity.getPersistentData().putBoolean("scaled", true);
            entity.setHealth(entity.getMaxHealth());
        }
    }
}