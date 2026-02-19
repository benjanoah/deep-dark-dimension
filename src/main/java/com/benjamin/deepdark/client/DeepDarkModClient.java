package com.benjamin.deepdark.client;

import com.benjamin.deepdark.item.InvertGlassesItem;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class DeepDarkModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // WorldRenderEvents.END vurt als de 3D wereld klaar is in de game framebuffer
        // Op dit moment heeft de framebuffer WEL de wereld-inhoud → inversie werkt!
        WorldRenderEvents.END.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            boolean wearingGlasses = client.player
                    .getEquippedStack(EquipmentSlot.HEAD)
                    .getItem() instanceof InvertGlassesItem;

            if (!wearingGlasses) return;

            // Gebruik framebuffer pixels (niet scaled GUI pixels)
            int w = client.getWindow().getFramebufferWidth();
            int h = client.getWindow().getFramebufferHeight();

            // Sla huidige 3D projectie op en zet 2D ortho
            Matrix4f savedProj = new Matrix4f(context.projectionMatrix());
            RenderSystem.setProjectionMatrix(
                    new Matrix4f().ortho(0, w, h, 0, -1000, 3000),
                    VertexSorter.BY_Z
            );

            // Reset model-view matrix naar identity (geen 3D camera transform)
            MatrixStack modelView = RenderSystem.getModelViewStack();
            modelView.push();
            modelView.peek().getPositionMatrix().identity();
            RenderSystem.applyModelViewMatrix();

            // Schakel depth test uit zodat onze quad over alles heen gaat
            RenderSystem.disableDepthTest();

            // Color inversion blend: result = 1.0 * (1 - framebuffer_kleur) = inversie!
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DstFactor.ZERO,
                    GlStateManager.SrcFactor.ZERO,
                    GlStateManager.DstFactor.ONE
            );
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            // Teken wit vlak over het hele scherm (blend inverteert de kleuren)
            Matrix4f identity = new Matrix4f();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(identity, 0, 0, 0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(identity, 0, h, 0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(identity, w, h, 0).color(1f, 1f, 1f, 1f).next();
            buffer.vertex(identity, w, 0, 0).color(1f, 1f, 1f, 1f).next();
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            // Herstel alles
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            modelView.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setProjectionMatrix(savedProj, VertexSorter.BY_Z);
        });
    }
}
