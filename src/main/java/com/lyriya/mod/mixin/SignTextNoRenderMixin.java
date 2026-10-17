package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignRenderer.class)
public abstract class SignTextNoRenderMixin {
    @Inject(
        method = "submitSignText(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/level/block/entity/SignText;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void lyriya$skipSignText(SignRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
            SignText text, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_SIGN_TEXT.getBooleanValue()) {
            ci.cancel();
        }
    }
}
