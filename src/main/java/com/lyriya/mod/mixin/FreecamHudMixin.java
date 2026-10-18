package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class FreecamHudMixin {
    private static boolean hideHotbar() {
        return LyriyaConfig.FREECAM_ENABLED.getBooleanValue() && !LyriyaConfig.FREECAM_RENDER_HOTBAR.getBooleanValue();
    }

    private static boolean hideStatusBars() {
        return LyriyaConfig.FREECAM_ENABLED.getBooleanValue() && !LyriyaConfig.FREECAM_RENDER_STATUS_BARS.getBooleanValue();
    }

    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideItemHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (hideHotbar()) ci.cancel();
    }

    @Redirect(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;extractHotbar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private void lyriya$hideSpectatorHotbar(SpectatorGui spectatorGui, GuiGraphicsExtractor graphics) {
        if (!hideHotbar()) spectatorGui.extractHotbar(graphics);
    }

    @Inject(method = "extractPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideHealth(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (hideStatusBars()) ci.cancel();
    }

    @Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideVehicleHealth(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (hideStatusBars()) ci.cancel();
    }

    @Inject(method = "extractAirBubbles", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideAirBubbles(GuiGraphicsExtractor graphics, net.minecraft.world.entity.player.Player player,
            int x, int y, int bubbles, CallbackInfo ci) {
        if (hideStatusBars()) ci.cancel();
    }

    @Redirect(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void lyriya$filterContextualBarBackground(ContextualBar bar, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!hideStatusBars()) bar.extractBackground(graphics, deltaTracker);
    }

    @Redirect(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void lyriya$filterContextualBar(ContextualBar bar, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!hideStatusBars()) bar.extractRenderState(graphics, deltaTracker);
    }

    @Redirect(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"))
    private void lyriya$filterExperienceLevel(GuiGraphicsExtractor graphics, Font font, int level) {
        if (!hideStatusBars()) ContextualBar.extractExperienceLevel(graphics, font, level);
    }
}
