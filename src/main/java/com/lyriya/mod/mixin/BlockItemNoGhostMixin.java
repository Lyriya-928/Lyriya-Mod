/*
 * Adapted from the NoGhostBlocks placement behavior in Meteor Client.
 * Copyright (c) Meteor Development. Licensed under GPL-3.0.
 * Modified for Lyriya Mod and Minecraft 26.3; this is not a copied module file.
 */
package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemNoGhostMixin {
    @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipPlacePrediction(BlockPlaceContext context, BlockState placementState, CallbackInfoReturnable<Boolean> cir) {
        if (context.getLevel().isClientSide() && !Minecraft.getInstance().isLocalServer() && LyriyaConfig.NO_GHOST_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_GHOST_PLACING.getBooleanValue()) {
            cir.setReturnValue(true);
        }
    }
}
