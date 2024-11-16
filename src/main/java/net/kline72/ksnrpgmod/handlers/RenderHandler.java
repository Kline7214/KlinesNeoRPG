package net.kline72.ksnrpgmod.handlers;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderHandler {
    private static final ResourceLocation HUD_BG = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hud_empty.png");
    private static final ResourceLocation HP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/hp_bar_filled.png");
    private static final ResourceLocation MANA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/mana_bar_filled.png");
    private static final ResourceLocation STAMINA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/stamina_bar_filled.png");
    private static final ResourceLocation EXP = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/exp_bar_filled.png");
    private static final ResourceLocation ULA = new ResourceLocation(KlinesNeoRPG.MODID, "textures/hud/coins.png");

    private static final ResourceLocation ENTITY_EMPTY_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar.png");
    private static final ResourceLocation ENTITY_FILLED_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar_filled.png");

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) return;

        if (event.getOverlay().id().equals("minecraft:health") || event.getOverlay().id().equals("minecraft:food")) {
            event.setCanceled(true);
        }

        drawPlayerHud(event.getGuiGraphics(), player);

        KlinesNeoRPG.LOGGER.info("Rendering player HUD...");
    }

    private static void drawPlayerHud(GuiGraphics guiGraphics, Player player) {
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

        int barHpWidth = (int) Math.round(106 * healthPercentage);
        int barManaWidth = (int) Math.round(104 * manaPercentage);
        int barStaminaWidth = (int) Math.round(102 * staminaPercentage);
        int barExpWidth = (int) Math.round(102 * expPercentage);

        float red = (float) Math.min(1.0, 2 * (1 - healthPercentage));
        float green = (float) Math.min(1.0, 2 * healthPercentage);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, HUD_BG);
        guiGraphics.blit(HUD_BG, 8, 8, 0, 0, 123, 23, 123, 23);
        guiGraphics.blit(EXP, 11, 26, 0, 0, barExpWidth, 2, 102, 2);
        guiGraphics.blit(STAMINA, 14, 23, 0, 0, barStaminaWidth, 2, 102, 2);
        guiGraphics.blit(MANA, 17, 18, 0, 0, barManaWidth, 4, 104, 4);
        guiGraphics.blit(ULA, 8, 33, 0, 0, 16, 16, 16, 16);

        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal((int) (healthPercentage * 100) + "%"), 133, 10, -1, false);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal((int) (manaPercentage * 100) + "%"), 129, 17, -1, false);

        RenderSystem.setShaderColor(red, green, 0, 1);
        guiGraphics.blit(HP, 22, 11, 0, 0, barHpWidth, 6, 106, 6);

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = Minecraft.getInstance().player;

        if (player == null || !shouldRender(player, entity)) return;

        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        renderEntityHealthBar(event.getPoseStack(), entity, event.getMultiBufferSource(), health / maxHealth);

        KlinesNeoRPG.LOGGER.info("Rendering entity health bar...");
    }

    private static boolean shouldRender(Player player, LivingEntity entity) {
        double maxDistance = 16.0;

        if (player.level() != entity.level()) return false;
        if (player.distanceTo(entity) > maxDistance) return false;

        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 entityPosition = entity.position().add(0, entity.getBbHeight() / 2, 0);
        BlockHitResult hitResult = entity.level().clip(new ClipContext(
                eyePosition,
                entityPosition,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
        ));
        return hitResult.getType() == HitResult.Type.MISS || !entity.level().getBlockState(hitResult.getBlockPos()).isAir();
    }

    private static void renderEntityHealthBar(PoseStack poseStack, LivingEntity entity, MultiBufferSource bufferSource, float healthRatio) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        font.drawInBatch(entity.getDisplayName(), -font.width(entity.getDisplayName()) / 2F, -16, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        int level = getEntityLevel(entity);
        String levelText = "Lv. " + level;
        font.drawInBatch(levelText, -font.width(levelText) / 2.0F, -8, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        drawHealthBar(poseStack, bufferSource, healthRatio);
        poseStack.popPose();
    }

    private static void drawHealthBar(PoseStack poseStack, MultiBufferSource bufferSource, float healthRatio) {
        int barWidth = 82;
        int barHeight = 8;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ENTITY_EMPTY_BAR);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawTexturedBar(poseStack, -barWidth / 2, 0, barWidth, barHeight);

        float red = (float) Math.min(1.0, 2 * (1 - healthRatio));
        float green = (float) Math.min(1.0, 2 * healthRatio);
        RenderSystem.setShaderTexture(0, ENTITY_FILLED_BAR);
        RenderSystem.setShaderColor(red, green, 0, 1);
        drawTexturedBar(poseStack, -barWidth / 2 + 1, 1, (int) (barWidth * healthRatio) - 2, barHeight - 2);

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

    private static int getEntityLevel(LivingEntity entity) {
        return entity.getPersistentData().getInt("level");
    }
}
