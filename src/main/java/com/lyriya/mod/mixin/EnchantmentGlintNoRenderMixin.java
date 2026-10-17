package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFeatureRenderer.class)
public abstract class EnchantmentGlintNoRenderMixin {
    @ModifyExpressionValue(method = "prepareMainSubmit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;foilType()Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;"))
    private ItemStackRenderState.FoilType lyriya$hideGlint(ItemStackRenderState.FoilType type) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_ENCHANTMENT_GLINT.getBooleanValue()
            ? ItemStackRenderState.FoilType.NONE : type;
    }
}
