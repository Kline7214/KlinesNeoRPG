package net.kline72.ksnrpgmod.events.mob;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.capability.PlayerStatsProvider;
import net.kline72.ksnrpgmod.data.arraylist.EntityGroup;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;

@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MobIndicator {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, ?> event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();

        if (!entity.isAlive()) return;

        poseStack.pushPose();

        // Translate to the correct position above the entity
        double yOffset = entity.getBbHeight() + 1.2; // Adjust height above entity
        poseStack.translate(0.0, yOffset, 0.0);

        // Render the indicator
        renderIndicator(poseStack, entity, entity.tickCount + event.getPartialTick());

        poseStack.popPose();
    }

    private static void renderIndicator(PoseStack poseStack, LivingEntity entity, float rotation) {
        // Get the color based on the entity type
        int color = getIndicatorColor(entity);
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = 1.0f;

        // Push pose for transformations
        poseStack.pushPose();

        // Rotate and scale the indicator for animation
        poseStack.mulPose(new Quaternionf().rotationYXZ((float) Math.toRadians(rotation), 0, 0)); // Rotate for animation
        poseStack.scale(0.3f, 0.3f, 0.3f); // Scale down the indicator

        // Set shader and render properties
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        // Enable depth test to respect clouds and other layers
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        // Top pyramid (smaller)
        float topBaseSize = 0.2f; // Smaller base size
        float topHeight = 0.3f;   // Smaller height
        renderPyramid(buffer, poseStack, red, green, blue, alpha, topBaseSize, topHeight);

        // Bottom pyramid (elongated)
        float bottomBaseSize = 0.2f; // Same base size for alignment
        float bottomHeight = 0.5f;   // Larger height for elongation
        poseStack.translate(0.0f, 0.0, 0.0f); // Move down for the bottom pyramid
        renderPyramid(buffer, poseStack, red, green, blue, alpha, bottomBaseSize, -bottomHeight); // Negative height for downward orientation

        tesselator.end();

        // Re-enable face culling after rendering
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        // Pop pose after rendering
        poseStack.popPose();

        // Reset shader color to default
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private static void renderPyramid(BufferBuilder buffer, PoseStack poseStack, float red, float green, float blue, float alpha, float baseSize, float height) {
        // Top point of the pyramid
        buffer.vertex(poseStack.last().pose(), 0.0f, height, 0.0f).color(red, green, blue, alpha).endVertex();

        // Four base corners
        buffer.vertex(poseStack.last().pose(), baseSize, 0.0f, baseSize).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -baseSize, 0.0f, baseSize).color(red, green, blue, alpha).endVertex();

        buffer.vertex(poseStack.last().pose(), 0.0f, height, 0.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -baseSize, 0.0f, baseSize).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -baseSize, 0.0f, -baseSize).color(red, green, blue, alpha).endVertex();

        buffer.vertex(poseStack.last().pose(), 0.0f, height, 0.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), -baseSize, 0.0f, -baseSize).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), baseSize, 0.0f, -baseSize).color(red, green, blue, alpha).endVertex();

        buffer.vertex(poseStack.last().pose(), 0.0f, height, 0.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), baseSize, 0.0f, -baseSize).color(red, green, blue, alpha).endVertex();
        buffer.vertex(poseStack.last().pose(), baseSize, 0.0f, baseSize).color(red, green, blue, alpha).endVertex();
    }

    private static int getIndicatorColor(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        if (EntityGroup.PASSIVE.contains(type) || EntityGroup.NPC.contains(type)) {
            return 0xFF66FF33; // Green for Passive
        } else if (EntityGroup.HOSTILE.contains(type)) {
            return 0xFFFF3333; // Red for Hostile
        } else if (EntityGroup.NEUTRAL.contains(type)) {
            return 0xFFFFC933; // Yellow for Neutral
        } else if (entity instanceof Player player) {
            // Handle Player behavior state
            final int[] color = {0xFFFFFFFF}; // Default White
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(playerStats -> {
                String behavior = playerStats.getBehaviorState();
                switch (behavior) {
                    case "Green" -> color[0] = 0xFF66FF33; // Green
                    case "Yellow" -> color[0] = 0xFFFFC933; // Yellow
                    case "Red" -> color[0] = 0xFFFF3333; // Red
                }
            });
            return color[0];
        }

        return 0xFFFFFFFF; // Default to white
    }
}
