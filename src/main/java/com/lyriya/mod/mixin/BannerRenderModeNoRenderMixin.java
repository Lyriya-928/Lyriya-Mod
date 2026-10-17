/*
 * This file is part of Lyriya Mod, licensed under GPL-3.0-only.
 * Rendering strategy adapted from Meteor Client's BannerRendererMixin.
 * Copyright (c) Meteor Development. Modified for Lyriya Mod and Minecraft 26.3.
 */
package com.lyriya.mod.mixin;

import com.lyriya.mod.config.BannerRenderMode;
import com.lyriya.mod.config.LyriyaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.BannerBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerRenderer.class)
public abstract class BannerRenderModeNoRenderMixin {
    @Shadow @Final private SpriteGetter sprites;
    @Shadow abstract BannerModel bannerModel(BannerBlock.AttachmentType type);

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BannerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void lyriya$applyBannerRenderMode(BannerRenderState state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()) return;
        BannerRenderMode mode = (BannerRenderMode) LyriyaConfig.NO_RENDER_BANNER_MODE.getOptionListValue();
        if (mode == BannerRenderMode.NONE) ci.cancel();
        else if (mode == BannerRenderMode.PILLAR) {
            lyriya$submitPlainPole(poseStack, collector, state.lightCoords, bannerModel(state.attachmentType),
                sprites, state.breakProgress);
            ci.cancel();
        }
    }

    @Unique
    private static void lyriya$submitPlainPole(PoseStack poseStack, SubmitNodeCollector collector, int light,
            BannerModel model, SpriteGetter sprites, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        SpriteId base = Sheets.BANNER_BASE;
        collector.submitModel(model, Unit.INSTANCE, poseStack, light, OverlayTexture.NO_OVERLAY, -1,
            base, sprites, 0);
    }
}
