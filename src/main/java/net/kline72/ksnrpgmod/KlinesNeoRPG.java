package net.kline72.ksnrpgmod;

import com.mojang.logging.LogUtils;
import net.kline72.ksnrpgmod.capability.PlayerStats;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.kline72.ksnrpgmod.client.PlayerHudOverlay;
import net.kline72.ksnrpgmod.events.CombatEventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(KlinesNeoRPG.MODID)
public class KlinesNeoRPG {
    public static final String MODID = "klinesneorpg";
    public static final Logger LOGGER = LogUtils.getLogger();

    public KlinesNeoRPG(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerHudOverlay.class);
        MinecraftForge.EVENT_BUS.register(CombatEventHandler.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Setup for KlinesNeoRPG completed.");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server is starting with KlinesNeoRPG loaded.");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Client setup completed for KlinesNeoRPG.");
        }
    }
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerStatsProvider.PLAYER_STATS).isPresent()) {
                event.addCapability(new ResourceLocation(KlinesNeoRPG.MODID, "player_stats"), new PlayerStatsProvider());
                LOGGER.info("PlayerStats capability attached to player.");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerStats.class);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
            if (!stats.isInitialized()) {
                stats.setMana(25);
                stats.setMaxMana(25);
                stats.setExp(0);
                stats.setMaxExp(50);
                stats.setMaxStamina(20);
                stats.setStrength(1);
                stats.setVitality(1);
                stats.setIntelligence(1);
                stats.setPerception(1);
                stats.setAgility(1);
                stats.setSpirit(1);
                stats.setCritChance(0.001);
                stats.setCritDmg(0.05);
                stats.setMagicResist(0.1);
                stats.setMagicDmg(0.2);
                stats.setPlayerLevel(1);
                stats.setUla(0);
                stats.setAttPoints(0);

                stats.setInitialized(true);

                // Optionally send a welcome message to the player
                player.displayClientMessage(Component.translatable("Welcome! Your stats have been initialized."), true);
            }
        });
    }
}

