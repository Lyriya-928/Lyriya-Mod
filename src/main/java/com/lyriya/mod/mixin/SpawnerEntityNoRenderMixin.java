package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerRenderer.class)
public abstract class SpawnerEntityNoRenderMixin {
    @Inject(method = "submitEntityInSpawner", at = @At("HEAD"), cancellable = true)
    private static void lyriya$skipSpawnerEntity(CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_SPAWNER_ENTITIES.getBooleanValue()) ci.cancel();
    }
}
