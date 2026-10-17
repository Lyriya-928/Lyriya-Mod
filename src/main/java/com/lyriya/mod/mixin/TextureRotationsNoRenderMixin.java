package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
abstract class BlockBehaviourTextureRotationMixin {
    @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true)
    private void lyriya$stableModelSeed(BlockState state, BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (active()) cir.setReturnValue(0L);
    }

    private static boolean active() {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_TEXTURE_ROTATIONS.getBooleanValue();
    }
}

@Mixin(BlockBehaviour.BlockStateBase.class)
abstract class BlockStateTextureRotationMixin {
    @Inject(method = "getOffset", at = @At("HEAD"), cancellable = true)
    private void lyriya$removeModelOffset(BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_TEXTURE_ROTATIONS.getBooleanValue()) cir.setReturnValue(Vec3.ZERO);
    }
}
