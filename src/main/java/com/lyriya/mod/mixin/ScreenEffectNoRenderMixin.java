package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectNoRenderMixin {
    @Inject(method = "submitFire", at = @At("HEAD"), cancellable = true)
    private static void lyriya$skipFireOverlay(PoseStack pose, SubmitNodeCollector collector, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_FIRE_OVERLAY.getBooleanValue()) ci.cancel();
    }

    @Inject(method = "submitWater", at = @At("HEAD"), cancellable = true)
    private static void lyriya$skipLiquidOverlay(net.minecraft.client.renderer.state.level.PlayerRenderState.WaterOverlay overlay,
            PoseStack pose, SubmitNodeCollector collector, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_LIQUID_OVERLAY.getBooleanValue()) ci.cancel();
    }

    @Inject(method = "submitBlockSprite", at = @At("HEAD"), cancellable = true)
    private static void lyriya$skipInWallOverlay(net.minecraft.resources.Identifier atlas, float u0, float v0, float u1, float v1,
            PoseStack pose, SubmitNodeCollector collector, int color, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_IN_WALL_OVERLAY.getBooleanValue()) ci.cancel();
    }

}
