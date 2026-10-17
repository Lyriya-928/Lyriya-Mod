package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.client.renderer.state.level.WorldBorderRenderState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorNoRenderMixin {
    @WrapWithCondition(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V"))
    private boolean lyriya$filterWeather(WeatherEffectRenderer renderer, ClientLevel level, float partialTick, Vec3 camera, WeatherRenderState state) {
        if (!active(LyriyaConfig.NO_RENDER_WEATHER)) return true;
        state.intensity = 0;
        return false;
    }

    @WrapWithCondition(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldBorderRenderer;extract(Lnet/minecraft/world/level/border/WorldBorder;FLnet/minecraft/world/phys/Vec3;DLnet/minecraft/client/renderer/state/level/WorldBorderRenderState;)V"))
    private boolean lyriya$filterWorldBorder(net.minecraft.client.renderer.WorldBorderRenderer renderer, WorldBorder border,
            float partialTick, Vec3 camera, double renderDistance, WorldBorderRenderState state) {
        if (!active(LyriyaConfig.NO_RENDER_WORLD_BORDER)) return true;
        state.alpha = 0;
        return false;
    }

    @Inject(method = "extractBlockDestroyAnimation", at = @At("HEAD"), cancellable = true)
    private void lyriya$filterBlockBreakOverlay(CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_BLOCK_BREAK)) ci.cancel();
    }

    private static boolean active(fi.dy.masa.malilib.config.options.ConfigBoolean option) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && option.getBooleanValue();
    }
}
