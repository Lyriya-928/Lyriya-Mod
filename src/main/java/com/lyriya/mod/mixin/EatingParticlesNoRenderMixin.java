package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EatingParticlesNoRenderMixin {
    @Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideEatingParticles(ItemStack stack, int count, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_EATING_PARTICLES.getBooleanValue()
            && stack.getComponents().has(DataComponents.FOOD)) ci.cancel();
    }
}
