
package net.kline72.ksnrpgmod.events.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UiRenderHandler {

    private static final ResourceLocation HUD_BG = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hud_empty.png");
    private static final ResourceLocation HP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hp_bar_filled.png");
    private static final ResourceLocation MANA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/mana_bar_filled.png");
    private static final ResourceLocation STAMINA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/stamina_bar_filled.png");
    private static final ResourceLocation EXP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/exp_bar_filled.png");
    private static final ResourceLocation ULA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/coins.png");
    private static final ResourceLocation AIR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/water_bar_filled.png");

    private static final ResourceLocation ENTITY_EMPTY_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar.png");
    private static final ResourceLocation ENTITY_FILLED_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar_filled.png");

    private static float displayedHealthPercentage = 1.0f;
    private static float displayedManaPercentage = 1.0f;
    private static float displayedStaminaPercentage = 1.0f;
    private static float displayedExpPercentage = 1.0f;
    private static float displayedAirPercentage = 1.0f;

    private static float healthAlpha = 1.0f;
    private static float manaAlpha = 1.0f;
    private static float staminaAlpha = 1.0f;
    private static float expAlpha = 1.0f;
    private static float airAlpha = 1.0f;

    private static final int GREEN = 0x42F566; // Light green
    private static final int YELLOW_GREEN = 0xC5F542; // Yellowish green
    private static final int RED = 0xF56042; // Light red


    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator())return;
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
                event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
                event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            event.setCanceled(true);
        }
        drawPlayerHud(event.getGuiGraphics(), player);
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        if (entity == player && minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        if (renderInView(player, entity)) {
            float health = entity.getHealth();
            float maxHealth = entity.getMaxHealth();
            renderText(event.getPoseStack(), entity, event.getMultiBufferSource(), health / maxHealth);
        }
    }

    public static void drawPlayerHud(GuiGraphics guiGraphics, Player player) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double healthPercentage = health / maxHealth;
        double mana = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMana();
        double maxMana = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxMana();
        double manaPercentage = maxMana > 0 ? mana / maxMana : 0;
        double stamina = player.getFoodData().getFoodLevel();
        double maxStamina = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxStamina();
        double staminaPercentage = maxStamina > 0 ? stamina / maxStamina : 0;
        double exp = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getExp();
        double maxExp = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getMaxExp();
        double expPercentage = maxExp > 0 ? exp / maxExp : 0;
        double airSupply = player.getAirSupply();
        double maxAirSupply = player.getMaxAirSupply();
        double airPercentage = airSupply / maxAirSupply;

        float animationSpeed = 0.02f;
        displayedHealthPercentage += (float) ((healthPercentage - displayedHealthPercentage) * animationSpeed);
        displayedManaPercentage += (float) ((manaPercentage - displayedManaPercentage) * animationSpeed);
        displayedStaminaPercentage += (float) ((staminaPercentage - displayedStaminaPercentage) * animationSpeed);
        displayedExpPercentage += (float) ((expPercentage - displayedExpPercentage) * animationSpeed);
        displayedAirPercentage += (float) ((airPercentage - displayedAirPercentage) * animationSpeed);

        int healthColor = interpolateHealthColor(displayedHealthPercentage);

        healthAlpha = updateAlpha(healthAlpha, displayedHealthPercentage < 1.0f);
        manaAlpha = updateAlpha(manaAlpha, displayedManaPercentage > 0.0f);
        staminaAlpha = updateAlpha(staminaAlpha, displayedStaminaPercentage > 0.0f);
        expAlpha = updateAlpha(expAlpha, displayedExpPercentage < 1.0f);
        airAlpha = updateAlpha(airAlpha, player.isUnderWater() || airSupply < maxAirSupply);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HUD_BG);
        guiGraphics.blit(HUD_BG, 8, 8, 0, 0, 123, 23, 123, 23);

        renderBar(guiGraphics, HP, 22, 11, (int) (106 * displayedHealthPercentage), 6, 106, healthAlpha, healthColor);
        renderBar(guiGraphics, MANA, 17, 18, (int) (104 * displayedManaPercentage), 4, 104, manaAlpha, 0xFFFFFF);
        renderBar(guiGraphics, STAMINA, 14, 23, (int) (102 * displayedStaminaPercentage), 2, 102, staminaAlpha, 0xFFFFFF);
        renderBar(guiGraphics, EXP, 11, 26, (int) (102 * displayedExpPercentage), 2, 102, expAlpha, 0xFFFFFF);
        guiGraphics.blit(ULA, 8, 33, 0, 0, 16, 16, 16, 16);

        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal((int) (healthPercentage * 100) + "%"), 133, 10, -1, false);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal((int) (manaPercentage * 100) + "%"), 129, 17, -1, false);

        if (player.isUnderWater() || airSupply < maxAirSupply) {
            renderBar(guiGraphics, AIR, 22, 11, (int) (106 * displayedAirPercentage), 6, 106, airAlpha, 0xFFFFFF);
        }
    }

    private static int interpolateHealthColor(float healthPercentage) {
        if (healthPercentage >= 0.5f) {
            return interpolateColor(GREEN, YELLOW_GREEN, (healthPercentage - 0.5f) * 2.0f);
        } else {
            return interpolateColor(YELLOW_GREEN, RED, healthPercentage * 2.0f);
        }
    }

    private static int interpolateColor(int color1, int color2, float factor) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);

        return (r << 16) | (g << 8) | b;
    }

    private static float updateAlpha(float currentAlpha, boolean isActive) {
        float fadeSpeed = 0.02f;
        return isActive ? Math.min(1.0f, currentAlpha + fadeSpeed) : Math.max(0.0f, currentAlpha - fadeSpeed);
    }

    private static void renderBar(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, int textureWidth, float alpha, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.setShaderTexture(0, texture);
        guiGraphics.blit(texture, x, y, 0, 0, width, height, textureWidth, height);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static boolean renderInView(Player player, LivingEntity entity) {
        if (player.level() != entity.level()) return false;

        double maxDistance = 16.0;
        if (player.distanceTo(entity) > maxDistance) return false;
        if (entity.isInvisible()) return false;
        if (entity.isCrouching()) return false;

        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 entityPosition = entity.position().add(0, entity.getBbHeight() / 2, 0);
        BlockHitResult hitResult = entity.level().clip(new ClipContext(
                eyePosition,
                entityPosition,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
        ));
        return hitResult.getType() != HitResult.Type.BLOCK;
    }

    private static void renderText(PoseStack poseStack, LivingEntity entity, MultiBufferSource bufferSource, float healthRatio) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Player player = minecraft.player;

        String eName = entity.getDisplayName().getString();
        int eLv = entity.getPersistentData().getInt("mobLevel");
        String eLvText = "Lv: " + eLv;

        String pName = player.getDisplayName().getString();
        int pLv = (player.getCapability(PlayerStatsProvider.PLAYER_STATS, null).orElse(new PlayerStatsProvider())).getPlayerLevel();
        String pLvText = "Lv: " + pLv;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        if (entity instanceof Mob) {
            font.drawInBatch(eName, -font.width(eName) / 2F, -16, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            font.drawInBatch(eLvText, -font.width(eLvText) / 2F, -8, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        } else if (entity instanceof Player) {
            font.drawInBatch(pName, -font.width(pName) / 2F, -16, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            font.drawInBatch(pLvText, -font.width(pLvText) / 2F, -8, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        renderEntityHealthBar(poseStack, healthRatio);
        poseStack.popPose();
    }

    private static void renderEntityHealthBar(PoseStack poseStack, float healthRatio) {
        int barWidth = 82;
        int barHeight = 8;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ENTITY_EMPTY_BAR);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawTexturedBar(poseStack, -barWidth / 2, 0, barWidth, barHeight);

        float red = (float) Math.min(1.0, 2 * (1 - healthRatio));
        float green = (float) Math.min(1.0, 2 * healthRatio);
        RenderSystem.setShaderTexture(0, ENTITY_FILLED_BAR);
        RenderSystem.setShaderColor(red, green, 0, 1);
        drawTexturedBar(poseStack, -barWidth / 2 + 1, 1, (int) (barWidth * healthRatio) - 2, barHeight - 2);

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private static void drawTexturedBar(PoseStack poseStack, int x, int y, int width, int height) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(poseStack.last().pose(), x, y + height, 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x + width, y + height, 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x + width, y, 0).uv(1, 0).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x, y, 0).uv(0, 0).endVertex();
        tesselator.end();
    }
}
