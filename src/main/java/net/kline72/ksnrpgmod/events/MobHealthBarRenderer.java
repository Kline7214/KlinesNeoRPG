package net.kline72.ksnrpgmod.events;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "klinesneorpg", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MobHealthBarRenderer {

    private static final ResourceLocation EMPTY_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar.png");
    private static final ResourceLocation FILLED_BAR = new ResourceLocation(KlinesNeoRPG.MODID, "textures/gui/entity_bar_filled.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();

        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        // Only render if the entity has custom name and is alive
        if (health > 0 && entity.hasCustomName()) {
            renderNameAndHealthBar(entity, event.getPoseStack(), event.getMultiBufferSource(), health, maxHealth);
        }
    }

    private static void renderNameAndHealthBar(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float health, float maxHealth) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        float healthRatio = health / maxHealth;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Component customName = entity.getCustomName();
        if (customName != null) {
            String[] nameParts = customName.getString().split("§NL§");

            for (int i = 0; i < nameParts.length; i++) {
                String line = nameParts[i];
                poseStack.pushPose();
                poseStack.translate(0, entity.getBbHeight() + 1, 0);
                font.drawInBatch(line, -font.width(line) / 2.0F, -10, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                poseStack.popPose();
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EMPTY_BAR);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawTexturedBar(poseStack, -64, 0, 128, 8, 0, 0, 1, 1);

        float red =(float) Math.min(1.0, 2 * (1 - healthRatio));
        float green =(float) Math.min(1.0, 2 * healthRatio);

        RenderSystem.setShaderTexture(0, FILLED_BAR);
        RenderSystem.setShaderColor(red, green, 0, 1);
        drawTexturedBar(poseStack, -63, 1, (int) (124 * healthRatio), 6, 0, 0, healthRatio, 1);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.popPose();
    }

    private static void drawTexturedBar(PoseStack poseStack, int x, int y, int width, int height, float u0, float v0, float u1, float v1) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(poseStack.last().pose(), x, y + height, 0).uv(u0, v1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x + width, y + height, 0).uv(u1, v1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x + width, y, 0).uv(u1, v0).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x, y, 0).uv(u0, v0).endVertex();
        tesselator.end();
    }
}
