package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.FreecamFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerFreecamMixin {
    /**
     * This call receives the exact deltas after Minecraft sensitivity, smooth-camera,
     * scope and invert-mouse handling. Replacing only the player-turn call leaves the
     * remainder of MouseHandler's cursor, tutorial and drag handling untouched.
     */
    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void lyriya$routeVanillaTurn(LocalPlayer player, double deltaX, double deltaY) {
        Minecraft client = Minecraft.getInstance();
        if (!LyriyaConfig.FREECAM_ENABLED.getBooleanValue() || client.gui.screen() != null) {
            player.turn(deltaX, deltaY);
            return;
        }

        FreecamFeature.INSTANCE.turnProcessed(deltaX, deltaY);
        if (LyriyaConfig.FREECAM_PLAYER_BODY_ROTATION.getBooleanValue()) {
            player.turn(deltaX, deltaY);
        }
    }
}
