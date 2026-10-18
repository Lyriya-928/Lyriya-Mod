package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelExtractor.class)
public abstract class FreecamPlayerBodyMixin {
    // In 26.3 this branch omits the entity used by a first-person camera from
    // the extracted entity states. Freecam keeps the camera entity as the local
    // player, so only this check needs to act detached when body rendering is on.
    @Redirect(method = "extractVisibleEntities",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"))
    private boolean lyriya$includeLocalPlayerInEntityExtraction(Camera camera) {
        return camera.isDetached() || (LyriyaConfig.FREECAM_ENABLED.getBooleanValue()
            && LyriyaConfig.FREECAM_RENDER_PLAYER_BODY.getBooleanValue());
    }
}
