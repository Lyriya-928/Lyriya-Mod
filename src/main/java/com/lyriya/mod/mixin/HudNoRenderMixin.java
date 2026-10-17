package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudNoRenderMixin {
    @Inject(method = "extractPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipPortalOverlay(GuiGraphicsExtractor graphics, float alpha, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_PORTAL_OVERLAY.getBooleanValue()) ci.cancel();
    }

    @Inject(method = "extractSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipSpyglassOverlay(GuiGraphicsExtractor graphics, float scale, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_SPYGLASS_OVERLAY)) ci.cancel();
    }

    @ModifyArg(method = "extractCameraOverlays", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/Hud;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V", ordinal = 0), index = 2)
    private float lyriya$hidePumpkinOverlay(float alpha) {
        return active(LyriyaConfig.NO_RENDER_PUMPKIN_OVERLAY) ? 0 : alpha;
    }

    @ModifyArg(method = "extractCameraOverlays", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/Hud;extractTextureOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;F)V", ordinal = 1), index = 2)
    private float lyriya$hidePowderedSnowOverlay(float alpha) {
        return active(LyriyaConfig.NO_RENDER_POWDERED_SNOW_OVERLAY) ? 0 : alpha;
    }

    @Inject(method = "extractConfusionOverlay", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipNausea(GuiGraphicsExtractor graphics, float strength, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_NAUSEA)) ci.cancel();
    }

    @Inject(method = "extractVignette", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipVignette(GuiGraphicsExtractor graphics, net.minecraft.world.entity.Entity camera, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_VIGNETTE)) ci.cancel();
    }

    @Inject(method = "extractScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipScoreboard(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_SCOREBOARD)) ci.cancel();
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipScoreboardContents(GuiGraphicsExtractor graphics, net.minecraft.world.scores.Objective objective, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_SCOREBOARD)) ci.cancel();
    }

    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipCrosshair(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_CROSSHAIR)) ci.cancel();
    }

    @Inject(method = "extractTitle", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipTitle(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_TITLE)) ci.cancel();
    }

    @Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipHeldItemName(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_HELD_ITEM_NAME)) ci.cancel();
    }

    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipPotionIcons(CallbackInfo ci) {
        if (active(LyriyaConfig.NO_RENDER_POTION_ICONS)) ci.cancel();
    }

    private static boolean active(fi.dy.masa.malilib.config.options.ConfigBoolean option) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && option.getBooleanValue();
    }
}
