package net.kline72.ksnrpgmod.events;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.util.MobTagsUtil;
import net.kline72.ksnrpgmod.util.PlayerTagsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerActionHandler {

    private static final String DAMAGE_COUNTER_KEY = "DamageCounter";

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        if (source instanceof ServerPlayer player) {
            if (event.getEntity() instanceof Villager || event.getEntity() instanceof IronGolem) {
                CompoundTag tag = player.getPersistentData();
                int damageCount = tag.getInt(DAMAGE_COUNTER_KEY) + 1;

                // Increment damage count
                tag.putInt(DAMAGE_COUNTER_KEY, damageCount);

                // Set status if damage count reaches 3
                if (damageCount >= 3) {
                    long expiryTime = player.level().getGameTime() + 24000; // 1 MC day = 24000 ticks
                    PlayerTagsUtil.setPlayerStatus(player, "Yellow", expiryTime);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity killer = source.getEntity();
        if (killer instanceof ServerPlayer player) {
            if (event.getEntity() instanceof Villager || event.getEntity() instanceof IronGolem) {
                long expiryTime = player.level().getGameTime() + 72000; // 3 MC days = 72000 ticks
                PlayerTagsUtil.setPlayerStatus(player, "Red", expiryTime);
            }
        }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                CompoundTag tag = player.getPersistentData();

                // Clear damage counter if no status set
                if (!PlayerTagsUtil.hasStatus(player)) {
                    tag.putInt(DAMAGE_COUNTER_KEY, 0);
                }

                // Clear status if expired
                long gameTime = serverLevel.getGameTime();
                if (PlayerTagsUtil.hasStatus(player) && gameTime >= PlayerTagsUtil.getStatusExpiry(player)) {
                    PlayerTagsUtil.clearPlayerStatus(player);
                }
            }
        }
    }
}
