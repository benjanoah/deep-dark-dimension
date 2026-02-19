package com.benjamin.deepdark.client;

import com.benjamin.deepdark.item.InvertGlassesItem;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.entity.EquipmentSlot;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class DeepDarkModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            boolean wearingGlasses = client.player
                    .getEquippedStack(EquipmentSlot.HEAD)
                    .getItem() instanceof InvertGlassesItem;

            if (!wearingGlasses) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // Eerst alle uitgestelde HUD renders flushen (deferred draw queue)
            ((VertexConsumerProvider.Immediate) drawContext.getVertexConsumers()).draw();

            // Zet color inversion blend mode:
            // result_rgb = src * (1 - dst) = 1.0 * (1 - huidige_kleur) = geïnverteerde kleur
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DstFactor.ZERO,
                    GlStateManager.SrcFactor.ZERO,
                    GlStateManager.DstFactor.ONE
            );
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            // Directe (niet-uitgestelde) rendering via Tessellator
            Matrix4f matrix = drawContext.getMatrices().peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrix, 0,     0,      0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(matrix, 0,     height, 0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(matrix, width, height, 0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(matrix, width, 0,      0).color(1f, 1f, 1f, 1f).next();
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            // Herstel blend state
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        });
    }
}
