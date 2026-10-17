package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.FreecamFeature;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraFreecamMixin {
    @Inject(method = "update", at = @At("TAIL"))
    private void lyriya$moveCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
        FreecamFeature freecam = FreecamFeature.INSTANCE;
        if (!freecam.activePosition()) return;
        CameraFreecamAccessor access = (CameraFreecamAccessor) this;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        access.lyriya$setPosition(freecam.cameraPosition(partialTick));
        access.lyriya$setRotation(freecam.cameraYaw(partialTick), freecam.cameraPitch(partialTick));
    }
}
