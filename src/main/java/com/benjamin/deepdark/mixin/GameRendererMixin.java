package com.benjamin.deepdark.mixin;

import com.benjamin.deepdark.item.InvertGlassesItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Voorkomt dat Minecraft onze invert shader weggooit
 * terwijl de speler de Invert Glasses draagt.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "disablePostProcessor", at = @At("HEAD"), cancellable = true)
    private void preventDisableWhenWearingGlasses(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Als de speler de bril draagt, blokkeer het uitzetten van de shader
        if (client.player != null &&
                client.player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof InvertGlassesItem) {
            ci.cancel();
        }
    }
}
