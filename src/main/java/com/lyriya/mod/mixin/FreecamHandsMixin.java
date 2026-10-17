package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FreecamHandsMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideHandsInFreecam(float partialTick, PoseStack pose, SubmitNodeCollector collector,
            PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState handsState, CallbackInfo ci) {
        if (LyriyaConfig.FREECAM_ENABLED.getBooleanValue() && !LyriyaConfig.FREECAM_SHOW_HANDS.getBooleanValue()) ci.cancel();
    }
}
