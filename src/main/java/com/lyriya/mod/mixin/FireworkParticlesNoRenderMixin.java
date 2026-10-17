package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net.minecraft.client.particle.FireworkParticles$SparkParticle", "net.minecraft.client.particle.FireworkParticles$OverlayParticle"})
public abstract class FireworkParticlesNoRenderMixin {
    @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipFireworkParticles(QuadParticleRenderState state, Camera camera, float partialTick, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_FIREWORKS.getBooleanValue()) ci.cancel();
    }
}
