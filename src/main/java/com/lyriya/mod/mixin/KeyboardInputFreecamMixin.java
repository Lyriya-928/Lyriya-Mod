package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputFreecamMixin extends ClientInput {
    @Inject(method = "tick", at = @At("TAIL"))
    private void lyriya$stopPlayerMovementWhileFreecam(CallbackInfo ci) {
        if (LyriyaConfig.FREECAM_ENABLED.getBooleanValue()
            && (LyriyaConfig.FREECAM_CONTROL_KEYS.getBooleanValue()
                || !LyriyaConfig.FREECAM_PLAYER_BODY_MOVEMENT.getBooleanValue())) {
            // Movement and jump belong to the camera, but sneak remains a real
            // player input so vanilla can synchronize it with the server.
            keyPresses = new Input(false, false, false, false, false, keyPresses.shift(), false);
        }
    }
}
