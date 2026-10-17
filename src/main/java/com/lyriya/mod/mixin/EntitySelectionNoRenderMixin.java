package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.NoRenderFeature;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntitySelectionNoRenderMixin<T extends Entity, S> {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipSelectedEntity(T entity, Frustum frustum, double x, double y, double z, float partialTick,
            CallbackInfoReturnable<Boolean> cir) {
        if (NoRenderFeature.filtersEntity(entity)) cir.setReturnValue(false);
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_FALLING_BLOCKS.getBooleanValue()
            && entity instanceof FallingBlockEntity) cir.setReturnValue(false);
    }

    @Inject(method = "getNameTag", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipNameTags(T entity, CallbackInfoReturnable<Component> cir) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_NAMETAGS.getBooleanValue()) cir.setReturnValue(null);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipDeadEntity(T entity, EntityRenderState state, float partialTick,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_DEAD_ENTITIES.getBooleanValue()
            && entity instanceof LivingEntity living && living.isDeadOrDying()) ci.cancel();
    }

}
