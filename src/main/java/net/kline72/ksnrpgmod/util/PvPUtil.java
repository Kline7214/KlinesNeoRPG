package net.kline72.ksnrpgmod.util;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID)
public class PvPUtil {
    private static final Map<UUID, Long> yellowTimers = new HashMap<>();
    private static final Map<UUID, Long> redTimers = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player targetPlayer = (Player) event.getTarget();
        Player attackingPlayer = (Player) event.getEntity();

        // Mark the attacker with yellow status for 1 day (20 minutes)
        long gameTime = attackingPlayer.level().getGameTime();
        yellowTimers.put(attackingPlayer.getUUID(), gameTime + 24000L);
    }

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player targetPlayer)) return;
        if (!(event.getSource().getEntity() instanceof Player killingPlayer)) return;

        // Mark the killer with red status for 3 days (60 minutes)
        long gameTime = killingPlayer.level().getGameTime();
        redTimers.put(killingPlayer.getUUID(), gameTime + 72000L);

        // Remove yellow timer if it exists
        yellowTimers.remove(killingPlayer.getUUID());
    }

    public static int getIndicatorColor(Player player) {
        long gameTime = player.level().getGameTime();
        UUID uuid = player.getUUID();

        // Check for red timer first (highest priority)
        if (redTimers.containsKey(uuid) && redTimers.get(uuid) > gameTime) {
            return 0xFF0000; // Red
        }

        // Check for yellow timer
        if (yellowTimers.containsKey(uuid) && yellowTimers.get(uuid) > gameTime) {
            return 0xFFFF00; // Yellow
        }

        // Default to green
        return 0x00FF00; // Green
    }
}