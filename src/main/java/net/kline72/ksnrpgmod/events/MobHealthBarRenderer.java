package net.kline72.ksnrpgmod.events;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, value = Dist.CLIENT)
public class MobHealthBarRenderer {

    private static final ResourceLocation EMPTY_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar.png");
    private static final ResourceLocation FILLED_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar_filled.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || !shouldRender(player, entity)) return;

        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        renderNameAndHealthBar(entity, event.getPoseStack(), event.getMultiBufferSource(), health, maxHealth);
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

    private static void renderNameAndHealthBar(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float health, float maxHealth) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        // Use `getDisplayName` to avoid overwriting
        font.drawInBatch(entity.getDisplayName(), -font.width(entity.getDisplayName()) / 2F, -16, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        int level = getEntityLevel(entity);
        String levelText = "Lv. " + level;
        font.drawInBatch(levelText, -font.width(levelText) / 2.0F, -8, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        renderHealthBar(poseStack, bufferSource, health / maxHealth);
        poseStack.popPose();
    }

    private static void renderHealthBar(PoseStack poseStack, MultiBufferSource bufferSource, float healthRatio) {
        int barWidth = 82;
        int barHeight = 8;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EMPTY_BAR);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawTexturedBar(poseStack, -barWidth / 2, 0, barWidth, barHeight);

        float red = (float) Math.min(1.0, 2 * (1 - healthRatio));
        float green = (float) Math.min(1.0, 2 * healthRatio);
        RenderSystem.setShaderTexture(0, FILLED_BAR);
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
