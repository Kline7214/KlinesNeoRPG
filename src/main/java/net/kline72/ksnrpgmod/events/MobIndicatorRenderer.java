//package net.kline72.ksnrpgmod.events;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.kline72.ksnrpgmod.KlinesNeoRPG;
//import net.kline72.ksnrpgmod.util.PvPUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.Mob;
//import net.minecraft.world.entity.animal.Animal;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.ClipContext;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraft.world.phys.HitResult;
//import net.minecraft.world.phys.Vec3;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RenderLivingEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.joml.Matrix4f;
//
//@Mod.EventBusSubscriber(modid = KlinesNeoRPG.MODID, value = Dist.CLIENT)
//public class MobIndicatorRenderer {
//    @SubscribeEvent
//    public static void onRenderLivingEntity(RenderLivingEvent.Pre event) {
//        LivingEntity entity = event.getEntity();
//        Player player = Minecraft.getInstance().player;
//
//        if (player == null) return;
//
//        if (renderIfNoObstruction(player, entity)) {
//            int color = determineEntityIndicatorColor(player, entity);
//            renderEntityIndicator(event.getPoseStack(), event.getMultiBufferSource(), entity, color, event.getPartialTick());
//        }
//    }
//
//    private static int determineEntityIndicatorColor(Player player, LivingEntity entity) {
//        if (entity instanceof Player targetPlayer) {
//            return PvPUtil.getIndicatorColor(targetPlayer);
//        } else if (entity instanceof Animal) {
//            return 0x00FF00; // Green for passive mobs
//        } else if (entity instanceof Mob mob) {
//            if (mob.getTarget() == player && mob.canAttack(player)) {
//                return 0xFF0000; // Red for mobs targeting the player
//            } else {
//                return 0xFFFF00; // Yellow for hostile mobs not targeting the player
//            }
//        }
//        return 0xFFFFFF; // Default white
//    }
//
//    private static boolean renderIfNoObstruction(Player player, LivingEntity entity) {
//        if (player.level() != entity.level()) return false;
//
//        Vec3 eyePosition = player.getEyePosition(1.0F);
//        Vec3 entityPosition = entity.position().add(0, entity.getBbHeight() / 2, 0);
//        BlockHitResult hitResult = player.level().clip(new ClipContext(
//                eyePosition,
//                entityPosition,
//                ClipContext.Block.VISUAL,
//                ClipContext.Fluid.NONE,
//                player
//        ));
//        return hitResult.getType() == HitResult.Type.MISS || hitResult.getLocation().distanceTo(entityPosition) < 0.1;
//    }
//    private static void renderEntityIndicator(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, int color, float partialTick) {
//        Minecraft minecraft = Minecraft.getInstance();
//
//        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
//        double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) + entity.getBbHeight() + 1.0; // Above the entity
//        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
//
//        Vec3 cameraPos = minecraft.getEntityRenderDispatcher().camera.getPosition();
//
//        poseStack.pushPose();
//        poseStack.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
//        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
//        poseStack.scale(0.5F, 0.5F, 0.5F);
//
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
//        renderColoredPyramid(poseStack.last().pose(), vertexConsumer, color);
//
//        poseStack.popPose();
//    }
//
//    private static void renderColoredPyramid(Matrix4f matrix, VertexConsumer vertexConsumer, int color) {
//        float red = ((color >> 16) & 255) / 255.0F;
//        float green = ((color >> 8) & 255) / 255.0F;
//        float blue = (color & 255) / 255.0F;
//
//        // Define vertices for the base
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        // Top pyramid apex
//        vertexConsumer.vertex(matrix, 0.0F, 1.0F, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        // Connect top apex to base vertices
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.0F, 1.0F, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.0F, 1.0F, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        // Bottom pyramid apex (stretched downwards)
//        float bottomApexY = -1.5F;
//        vertexConsumer.vertex(matrix, 0.0F, bottomApexY, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        // Connect bottom apex to base vertices
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, -0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.0F, bottomApexY, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, 0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//
//        vertexConsumer.vertex(matrix, 0.0F, bottomApexY, 0.0F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//        vertexConsumer.vertex(matrix, -0.5F, 0.0F, 0.5F).color(red, green, blue, 1.0F).uv(0, 0).overlayCoords(0, 0).uv2(0, 0).normal(0, 0, 0).endVertex();
//    }
//}
