package net.kline72.ksnrpgmod.events.player;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStats;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.kline72.ksnrpgmod.data.arraylist.EntityGroup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEventHandler {
    private static final HashMap<UUID, Integer> killCountMap = new HashMap<>();
    private static final HashMap<UUID, Long> firstKillTimeMap = new HashMap<>();
    private static final int DAY_TICKS = 24000;

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        PlayerStats stats = getPlayerStats(player);
        if (stats == null) {
            return;
        }

        // Calculate if it's a critical hit
        double randomValue = player.getRandom().nextDouble();
        if (randomValue < stats.getCritChance()) {
            // Critical hit
            double newDamage = event.getAmount() * (1.0 + stats.getCritDmg());
            event.setAmount((float) newDamage);

            // Optional: Add visual or audio effects for critical hits
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            // Cancel vanilla critical hits by preventing critical particles
            if (player.isSprinting() && player.fallDistance > 0.0F && !player.onGround()) {
                player.fallDistance = 0.0F; // Reset fall distance to block vanilla crit
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                if (event.getEntity() instanceof IronGolem || event.getEntity() instanceof Villager) {
                    playerStats.setBehaviorState("Red");
                    playerStats.setBehaviourTimer(9 * 24000);
                } else if (event.getEntity() instanceof Mob) {
                    playerStats.setBehaviorState("Yellow");
                    playerStats.setBehaviourTimer(3 * 24000);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Entity killedEntity = event.getEntity();

        if (isTargetEntity(killedEntity)) {
            UUID playerId = serverPlayer.getUUID();

            long currentTime = serverPlayer.level().getGameTime();

            firstKillTimeMap.putIfAbsent(playerId, currentTime);
            long firstKillTime = firstKillTimeMap.get(playerId);

            if (currentTime - firstKillTime >= DAY_TICKS) {
                killCountMap.put(playerId, 0);
                firstKillTimeMap.put(playerId, currentTime);
                serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                    playerStats.setBehaviorState("Green");
                });
            }

            int currentKillCount = killCountMap.getOrDefault(playerId, 0) + 1;
            currentKillCount++;

            killCountMap.put(playerId, currentKillCount);
            int finalCurrentKillCount = currentKillCount;
            serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                if (finalCurrentKillCount >= 3) {
                    playerStats.setBehaviorState("Red");
                } else {
                    playerStats.setBehaviorState("Yellow");
                }
            });
        }
    }

    private static boolean isTargetEntity(Entity entity) {
        EntityType<?> type = entity.getType();
        if (EntityGroup.PASSIVE.contains(type) || EntityGroup.NPC.contains(type)) {
            return true;
        }
        return false;
    }

    private PlayerStats getPlayerStats(Player player) {
        // Replace this with your capability fetching logic
        // For example, if you've implemented Forge capabilities:
        return player.getCapability(PlayerStatsProvider.PLAYER_STATS)
                .orElse(null);
    }
}