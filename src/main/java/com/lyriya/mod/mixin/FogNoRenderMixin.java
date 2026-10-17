package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public abstract class FogNoRenderMixin {
    @ModifyExpressionValue(method = "getBuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;fogEnabled:Z", opcode = Opcodes.GETSTATIC))
    private boolean lyriya$disableGeneralFog(boolean enabled) {
        return enabled && !(LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_FOG.getBooleanValue());
    }
}
