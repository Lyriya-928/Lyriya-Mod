package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.FreecamFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerFreecamMixin {
    @Inject(method = "onMove", at = @At("HEAD"), cancellable = true)
    private void lyriya$turnFreecam(long window, double x, double y, double deltaX, double deltaY, CallbackInfo ci) {
        if (!LyriyaConfig.FREECAM_ENABLED.getBooleanValue() || Minecraft.getInstance().gui.screen() != null) return;
        FreecamFeature freecam = FreecamFeature.INSTANCE;
        freecam.turn(deltaX, deltaY);
        ci.cancel();
    }
}
