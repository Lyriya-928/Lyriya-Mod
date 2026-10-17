package com.lyriya.mod.mixin;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState.class)
public interface BlockEntityRenderStateAccessor {
    @Accessor("blockState")
    BlockState lyriya$getBlockState();
}
