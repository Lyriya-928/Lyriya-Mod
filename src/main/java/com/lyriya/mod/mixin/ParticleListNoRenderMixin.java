package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ParticleListNoRenderMixin {
    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void lyriya$filterParticle(ParticleOptions options, double x, double y, double z, double dx, double dy, double dz, CallbackInfo ci) {
        if (isFiltered(options)) ci.cancel();
    }

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V", at = @At("HEAD"), cancellable = true)
    private void lyriya$filterParticle(ParticleOptions options, boolean overrideLimiter, boolean alwaysVisible,
            double x, double y, double z, double dx, double dy, double dz, CallbackInfo ci) {
        if (isFiltered(options)) ci.cancel();
    }

    private static boolean isFiltered(ParticleOptions options) {
        if (!LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()) return false;
        String key = BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()).toString();
        return LyriyaConfig.NO_RENDER_PARTICLE_TYPES.getStrings().contains(key);
    }
}
