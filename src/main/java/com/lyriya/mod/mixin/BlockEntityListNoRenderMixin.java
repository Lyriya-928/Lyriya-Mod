package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityListNoRenderMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private <S extends BlockEntityRenderState> void lyriya$filterBlockEntities(S state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()) return;
        var blockState = ((BlockEntityRenderStateAccessor) state).lyriya$getBlockState();
        if (blockState != null && LyriyaConfig.NO_RENDER_BLOCK_ENTITY_TYPES.getStrings()
            .contains(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString())) ci.cancel();
    }
}
