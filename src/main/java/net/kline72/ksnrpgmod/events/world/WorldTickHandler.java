package net.kline72.ksnrpgmod.events.world;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldTickHandler {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {

        if (!(event.level instanceof ServerLevel level)) return;

        if (event.phase == TickEvent.Phase.END) {
            for (Player player : level.players()) {
                player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
                    if (stats.getBehaviourTimer() > 0) {
                        stats.setBehaviourTimer(stats.getBehaviourTimer() - 1);

                        if (stats.getBehaviourTimer() == 0) {
                            stats.setBehaviorState("GREEN");
                        }
                    }
                });
            }
        }
    }
}
