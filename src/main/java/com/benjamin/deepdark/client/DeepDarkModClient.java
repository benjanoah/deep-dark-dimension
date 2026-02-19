package com.benjamin.deepdark.client;

import com.benjamin.deepdark.item.InvertGlassesItem;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;

@Environment(EnvType.CLIENT)
public class DeepDarkModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Teken een inversie-overlay via de HUD callback
        // Techniek: wit vlak met ONE_MINUS_DST_COLOR blend = perfecte kleur-inversie!
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            boolean wearingGlasses = client.player
                    .getEquippedStack(EquipmentSlot.HEAD)
                    .getItem() instanceof InvertGlassesItem;

            if (!wearingGlasses) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // Zet blend mode: resultaat = 1.0 - huidige_kleur (= inversie!)
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DstFactor.ZERO,
                    GlStateManager.SrcFactor.ZERO,
                    GlStateManager.DstFactor.ONE
            );

            // Teken wit vlak over het hele scherm → alle kleuren worden geïnverteerd
            drawContext.fill(0, 0, width, height, 0xFFFFFFFF);

            // Herstel normale blend mode
            RenderSystem.defaultBlendFunc();
        });
    }
}
