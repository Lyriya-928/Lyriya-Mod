package com.lyriya.mod.mixin;

import com.lyriya.mod.config.FullbrightMode;
import com.lyriya.mod.config.LyriyaConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Lightmap.class)
public abstract class LightmapGammaFullbrightMixin {
    @Shadow @Final private GpuTexture texture;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void lyriya$renderFullbrightLightmap(LightmapRenderState state, CallbackInfo ci) {
        if (!LyriyaConfig.FULLBRIGHT_ENABLED.getBooleanValue()
            || LyriyaConfig.FULLBRIGHT_MODE.getOptionListValue() != FullbrightMode.GAMMA) return;
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(texture, new Vector4f(1.0f));
        ci.cancel();
    }
}
