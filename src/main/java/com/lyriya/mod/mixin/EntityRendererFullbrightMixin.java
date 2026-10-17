package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.FullbrightFeature;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererFullbrightMixin<T extends Entity, S> {
    @Inject(method = "getSkyLightLevel", at = @At("RETURN"), cancellable = true)
    private void lyriya$minimumEntitySkyLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FullbrightFeature.luminance(LightLayer.SKY, cir.getReturnValue()));
    }

    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
    private void lyriya$minimumEntityBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FullbrightFeature.luminance(LightLayer.BLOCK, cir.getReturnValue()));
    }
}
