package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossBarNoRenderMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipBossBars(CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_BOSS_BAR.getBooleanValue()) ci.cancel();
    }
}
