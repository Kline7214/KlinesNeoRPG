package net.kline72.ksnrpgmod.util;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStats;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID)
public class LvUpUtil {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        Level world = player.level();

        if (world.isClientSide) return;

        PlayerStats stats = getPlayerStats(player);

        if (stats != null) {
            stats.addExp(1);

            int ulaGained = RANDOM.nextInt(26) + 10;
            stats.setUla(stats.getUla() + ulaGained);

            if (stats.getExp() >= stats.getMaxExp()) {
                stats.setExp(0);
                stats.setPlayerLevel(stats.getPlayerLevel() + 1);
                stats.setAttPoints(stats.getAttPoints() + 5);

                int newMaxExp = (int) (stats.getMaxExp() * 1.75);
                stats.setMaxExp(newMaxExp);

                player.sendSystemMessage(Component.literal("Level Up! You are now level "
                        + stats.getPlayerLevel()));
            }
        }
    }

    private static PlayerStats getPlayerStats(Player player) {
        return player.getCapability(PlayerStatsProvider.PLAYER_STATS).orElse(null);
    }
}
