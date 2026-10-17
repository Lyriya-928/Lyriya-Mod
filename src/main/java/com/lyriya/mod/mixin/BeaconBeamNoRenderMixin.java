package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconRenderer.class)
public abstract class BeaconBeamNoRenderMixin {
    @Inject(method = "submitBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/resources/Identifier;FFIIIFF)V", at = @At("HEAD"), cancellable = true)
    private static void lyriya$skipBeaconBeam(PoseStack pose, SubmitNodeCollector collector, Identifier texture, float scale,
            float animationTime, int start, int height, int color, float solidRadius, float glowRadius, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_BEACON_BEAMS.getBooleanValue()) ci.cancel();
    }
}
