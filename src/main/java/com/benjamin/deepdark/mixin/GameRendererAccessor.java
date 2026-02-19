package com.benjamin.deepdark.mixin;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Geeft ons toegang tot het postProcessor veld van GameRenderer,
 * zodat we kunnen controleren of onze shader nog actief is.
 */
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("postProcessor")
    PostEffectProcessor getPostProcessor();
}
