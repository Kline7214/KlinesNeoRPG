package net.kline72.ksnrpgmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class EntityHealthBarRenderer {

    private static final ResourceLocation EMPTY_BAR_TEXTURE = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar.png");
    private static final ResourceLocation FILLED_WHITE_BAR_TEXTURE = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/bar_white_filled.png");

    public static void renderHealthBar(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        if (entity == null || minecraft.level == null || minecraft.player == null) {
            return;
        }

        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPercentage = health / maxHealth;

        poseStack.pushPose();

        poseStack.translate(0.0, entity.getBbHeight() + 0.5, 0.0);
        poseStack.scale(-0.025F, -0.025F, 0.025F); // Scale to a reasonable size

        VertexConsumer emptyBarConsumer = buffer.getBuffer(RenderType.entityTranslucent(EMPTY_BAR_TEXTURE));
        renderCurvedBar(poseStack, emptyBarConsumer, 1.0f, 0xFFFFFF); // White empty bar

        int color = getHealthColor(healthPercentage);
        VertexConsumer filledBarConsumer = buffer.getBuffer(RenderType.entityTranslucent(FILLED_WHITE_BAR_TEXTURE));
        renderCurvedBar(poseStack, filledBarConsumer, healthPercentage, color);

        poseStack.popPose();
    }

    private static int getHealthColor(float healthPercentage) {
        if (healthPercentage > 0.5f) {
            float factor = (healthPercentage - 0.5f) * 2f;
            int red = (int) (255 * (1 - factor));
            int green = 255;
            return (red << 16) | (green << 8);
        } else {
            float factor = healthPercentage * 2f;
            int red = 255;
            int green = (int) (255 * factor);
            return (red << 16) | (green << 8);
        }
    }

    private static void renderCurvedBar(PoseStack poseStack, VertexConsumer vertexConsumer, float healthPercentage, int color) {
        float radius = 0.5f;
        int segments = 20;

        for (int i = 0; i < segments * healthPercentage; i++) {
            float angleStart = (float) (i * Math.PI * 2 / segments);
            float angleEnd = (float) ((i + 1) * Math.PI * 2 / segments);

            float x1 = Mth.cos(angleStart) * radius;
            float y1 = Mth.sin(angleStart) * radius;
            float x2 = Mth.cos(angleEnd) * radius;
            float y2 = Mth.sin(angleEnd) * radius;

            // Extract RGB values from color
            int red = (color >> 16) & 255;
            int green = (color >> 8) & 255;
            int blue = color & 255;

            // Define the vertices for each segment
            vertexConsumer.vertex(poseStack.last().pose(), x1, y1, 0)
                    .color(red, green, blue, 255)
                    .uv(0, 0)
                    .endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x2, y2, 0)
                    .color(red, green, blue, 255)
                    .uv(1, 0)
                    .endVertex();
        }
    }
}
