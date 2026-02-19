package com.benjamin.deepdark.client;

import com.benjamin.deepdark.item.InvertGlassesItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DeepDarkModClient implements ClientModInitializer {

    private static final Identifier INVERT_SHADER =
            new Identifier("deepdark", "shaders/post/invert.json");

    // Bijhouden of WIJ de shader hebben ingeschakeld
    private static boolean wantShader = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean wearingGlasses = client.player
                    .getEquippedStack(EquipmentSlot.HEAD)
                    .getItem() instanceof InvertGlassesItem;

            if (wearingGlasses && !wantShader) {
                // Bril op → shader aan
                wantShader = true;
                try {
                    client.gameRenderer.loadPostProcessor(INVERT_SHADER);
                    System.out.println("[InvertGlasses] Shader AAN!");
                } catch (Exception e) {
                    System.err.println("[InvertGlasses] Fout: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (!wearingGlasses && wantShader) {
                // Bril af → shader uit (mixin blokkeert dit NIET want geen bril)
                wantShader = false;
                client.gameRenderer.disablePostProcessor();
                System.out.println("[InvertGlasses] Shader UIT!");
            }
        });
    }
}
