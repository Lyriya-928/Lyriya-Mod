/*
 * Adapted from the NoGhostBlocks behavior in Meteor Client.
 * Copyright (c) Meteor Development. Licensed under GPL-3.0.
 * Modified for Lyriya Mod and Minecraft 26.3; this is not a copied module file.
 */
package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeNoGhostMixin {
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void lyriya$reduceBreakPrediction(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!LyriyaConfig.NO_GHOST_ENABLED.getBooleanValue() || !LyriyaConfig.NO_GHOST_BREAKING.getBooleanValue()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.isLocalServer() || client.level == null || client.player == null) return;
        BlockState state = client.level.getBlockState(pos);
        if (!state.isAir()) state.getBlock().playerWillDestroy(client.level, pos, state, client.player);
        cir.setReturnValue(false);
    }
}
