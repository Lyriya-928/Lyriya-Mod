package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.lyriya.mod.config.LyriyaConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantTableRenderer.class)
public abstract class EnchantTableBookNoRenderMixin {
    @WrapWithCondition(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/EnchantTableRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;I)V"))
    private <S> boolean lyriya$skipBookModel(SubmitNodeCollector collector, Model<? super S> model, Object state,
            PoseStack pose, int light, int overlay, int color, SpriteId sprite, SpriteGetter sprites, int outline) {
        return !LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() || !LyriyaConfig.NO_RENDER_ENCHANT_BOOK.getBooleanValue();
    }
}
