package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.SafeWalkFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class SafeWalkInputMixin extends ClientInput {
    @Inject(method = "tick", at = @At("TAIL"))
    private void lyriya$applyEdgeSneak(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (SafeWalkFeature.INSTANCE.shouldAutoSneak(keyPresses)) {
            Input input = keyPresses;
            keyPresses = new Input(input.forward(), input.backward(), input.left(), input.right(),
                input.jump(), true, input.sprint());
        }
    }
}
