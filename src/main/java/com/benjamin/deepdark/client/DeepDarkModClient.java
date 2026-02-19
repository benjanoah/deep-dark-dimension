package com.benjamin.deepdark.client;

import com.benjamin.deepdark.item.InvertGlassesItem;
import com.benjamin.deepdark.mixin.GameRendererAccessor;
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

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Check of de speler de Invert Glasses draagt
            ItemStack helmet = client.player.getEquippedStack(EquipmentSlot.HEAD);
            boolean wearingGlasses = helmet.getItem() instanceof InvertGlassesItem;

            // Lees wat er nu IN de GameRenderer zit (via mixin accessor)
            boolean shaderCurrentlyActive = ((GameRendererAccessor) client.gameRenderer)
                    .getPostProcessor() != null;

            if (wearingGlasses && !shaderCurrentlyActive) {
                // Bril op maar shader is weg (of nog niet geladen) → laad hem
                try {
                    client.gameRenderer.loadPostProcessor(INVERT_SHADER);
                    System.out.println("[InvertGlasses] Shader geladen!");
                } catch (Exception e) {
                    System.err.println("[InvertGlasses] FOUT: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (!wearingGlasses && shaderCurrentlyActive) {
                // Bril af maar shader is nog actief → uitzetten
                client.gameRenderer.disablePostProcessor();
                System.out.println("[InvertGlasses] Shader uitgeschakeld.");
            }
        });
    }
}
