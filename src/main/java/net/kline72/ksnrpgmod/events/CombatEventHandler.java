package net.kline72.ksnrpgmod.events;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEventHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                double critChance = playerStats.getCritChance();
                double critDmg = playerStats.getCritChance();

                if (Math.random() < critChance) {
                    event.setAmount((float) (event.getAmount() * critDmg));
                    player.displayClientMessage(Component.translatable("Critical Hit!!!"), true);
                } else {
                    player.displayClientMessage(Component.translatable("Not Crit"), true);
                }
            });
        }
    }
}