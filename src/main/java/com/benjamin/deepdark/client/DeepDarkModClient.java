package com.benjamin.deepdark.client;

import com.benjamin.deepdark.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DeepDarkModClient implements ClientModInitializer {

    private static final Identifier INVERT_SHADER =
            new Identifier("deepdark", "shaders/post/invert.json");

    private static boolean shaderActive = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Check of de speler de Invert Glasses draagt
            ItemStack helmet = client.player.getEquippedStack(EquipmentSlot.HEAD);
            boolean wearingGlasses = helmet.getItem() == ModItems.INVERT_GLASSES;

            if (wearingGlasses && !shaderActive) {
                // Bril op → activeer invert shader
                try {
                    client.gameRenderer.loadPostProcessor(INVERT_SHADER);
                    System.out.println("[InvertGlasses] Shader geladen: " + INVERT_SHADER);
                    shaderActive = true;
                } catch (Exception e) {
                    System.err.println("[InvertGlasses] FOUT bij laden shader: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (!wearingGlasses && shaderActive) {
                // Bril af → deactiveer shader
                client.gameRenderer.disablePostProcessor();
                System.out.println("[InvertGlasses] Shader uitgeschakeld.");
                shaderActive = false;
            }
        });
    }
}
