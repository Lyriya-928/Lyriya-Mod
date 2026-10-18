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
    // Camera.update() aligns the camera, then builds the view-rotation matrix and
    // cull frustum from its position/rotation. Apply Freecam before that work so
    // the rendered camera and terrain visibility use the same pose.
    @Inject(method = "update", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Camera;getViewRotationMatrix(Lorg/joml/Matrix4f;)Lorg/joml/Matrix4f;"))
    private void lyriya$moveCameraBeforeFrustum(DeltaTracker deltaTracker, CallbackInfo ci) {
        FreecamFeature freecam = FreecamFeature.INSTANCE;
        if (!freecam.activePosition()) return;
        CameraFreecamAccessor access = (CameraFreecamAccessor) this;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        access.lyriya$setPosition(freecam.cameraPosition(partialTick));
        access.lyriya$setRotation(freecam.cameraYaw(partialTick), freecam.cameraPitch(partialTick));
    }
}
