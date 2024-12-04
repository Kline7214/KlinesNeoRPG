package net.kline72.ksnrpgmod.events.player;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStats;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.kline72.ksnrpgmod.data.arraylist.EntityGroup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        PlayerStats stats = getPlayerStats(player);
        if (stats == null) {
            return;
        }

        double randomValue = player.getRandom().nextDouble();
        if (randomValue < stats.getCritChance()) {
            double newDamage = event.getAmount() * (1.0 + stats.getCritDmg());
            event.setAmount((float) newDamage);

            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            // Cancel vanilla Crits
            if (player.isSprinting() && player.fallDistance > 0.0F && !player.onGround()) {
                player.fallDistance = 0.0F;
            }
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
            serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                int killCount = playerStats.getKillCount() + 1;
                playerStats.setKillCount(killCount);
                if (killCount >= 3) {
                    playerStats.setBehaviorState("Red");
                    playerStats.setBehaviourTimer(9 * 24000);
                } else {
                    playerStats.setBehaviorState("Yellow");
                    playerStats.setBehaviourTimer(3 * 24000);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                long timer = playerStats.getBehaviourTimer();

                if (timer > 0) {
                    playerStats.setBehaviourTimer(timer - 1);

                    if (timer - 1 > 0) {
                        playerStats.setBehaviorState("Green");
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("Your state has reset to Green."), true);
                    }
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
        return player.getCapability(PlayerStatsProvider.PLAYER_STATS)
                .orElse(null);
    }
}