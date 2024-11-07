package net.kline72.ksnrpgmod.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, value = Dist.CLIENT)
public class PlayerHudOverlay {

    private static final ResourceLocation HUD_BG = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hud_empty.png");
    private static final ResourceLocation HP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hp_bar_filled.png");
    private static final ResourceLocation MANA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/mana_bar_filled.png");
    private static final ResourceLocation STAMINA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/stamina_bar_filled.png");
    private static final ResourceLocation EXP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/exp_bar_filled.png");
    private static final ResourceLocation ULA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/coins.png");

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiOverlayEvent.@NotNull Post event) {

        Player player = Minecraft.getInstance().player;

        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        // Get player's health stats
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double healthPercentage = health / maxHealth;

        // Retrieve capability-based stats
        double mana = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMana();
        double maxMana = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxMana();
        double manaPercentage = maxMana > 0 ? mana / maxMana : 0;

        double stamina = player.getFoodData().getFoodLevel();
        double maxStamina = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxStamina();
        double staminaPercentage = maxStamina > 0 ? stamina / maxStamina : 0;

        double exp = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getExp();
        double maxExp = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxExp();
        double expPercentage = maxExp > 0 ? exp / maxExp : 0;

        // Calculated bar widths for each stat
        int barHpWidth = (int) Math.round(106 * healthPercentage);
        int barManaWidth = (int) Math.round(104 * manaPercentage);
        int barStaminaWidth = (int) Math.round(102 * staminaPercentage);
        int barExpWidth = (int) Math.round(102 * expPercentage);

        // Colors for health bar
        float red = (float) Math.min(1.0, 2 * (1 - healthPercentage));
        float green = (float) Math.min(1.0, 2 * healthPercentage);

        // Render HUD elements
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        // Set full color for background
        RenderSystem.setShaderColor(1, 1, 1, 1);
        event.getGuiGraphics().blit(HUD_BG, 8, 8, 0, 0, 123, 23, 123, 23);
        event.getGuiGraphics().blit(EXP, 11, 26, 0, 0, barExpWidth, 2, 102, 2);
        event.getGuiGraphics().blit(STAMINA, 14, 23, 0, 0, barStaminaWidth, 2, 102, 2);
        event.getGuiGraphics().blit(MANA, 17, 18, 0, 0, barManaWidth, 4, 104, 4);
        event.getGuiGraphics().blit(ULA, 8, 33, 0, 0, 16, 16, 16, 16);

        // Draw percentage texts
        event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.literal((int) (healthPercentage * 100) + "%"), 133, 10, -1, false);
        event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.literal((int) (manaPercentage * 100) + "%"), 129, 17, -1, false);

        // Render health bar with dynamic color
        RenderSystem.setShaderColor(red, green, 0, 1);
        event.getGuiGraphics().blit(HP, 22, 11, 0, 0, barHpWidth, 6, 106, 6);

        RenderSystem.setShaderColor(1, 1, 1, 1); // Reset color
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
