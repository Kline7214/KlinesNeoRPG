package net.kline72.ksnrpgmod.events.player;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionHandler {

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
            if (playerStats.getBehaviorState().equals("Red") && event.getTarget() instanceof Villager) {
                event.setCanceled(true);

            }
        });
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
            if (playerStats.getBehaviorState().equals("Red") && isGolemSummoning(event.getItemStack())) {
                event.setCanceled(true);
            }
        });
    }

    private static boolean isGolemSummoning(ItemStack stack) {
        return stack.getItem() == Items.CARVED_PUMPKIN; // Check for golem summoning items
    }
}
