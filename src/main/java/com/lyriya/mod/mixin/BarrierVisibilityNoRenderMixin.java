package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientLevel.class)
public abstract class BarrierVisibilityNoRenderMixin {
    @ModifyArg(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;doAnimateTick(IIIILnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos$MutableBlockPos;)V"), index = 5)
    private net.minecraft.world.level.block.Block lyriya$showBarrier(net.minecraft.world.level.block.Block block) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_BARRIER_INVISIBILITY.getBooleanValue() ? Blocks.BARRIER : block;
    }
}
