package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.fog.environment.MobEffectFogEnvironment;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectFogEnvironment.class)
public abstract class MobEffectFogNoRenderMixin {
    @Shadow public abstract Holder<MobEffect> getMobEffect();

    @Inject(method = "isApplicable", at = @At("RETURN"), cancellable = true)
    private void lyriya$skipBlindnessOrDarknessFog(FogType fogType, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()) return;
        if (getMobEffect() == MobEffects.BLINDNESS && LyriyaConfig.NO_RENDER_BLINDNESS.getBooleanValue()) cir.setReturnValue(false);
        if (getMobEffect() == MobEffects.DARKNESS && LyriyaConfig.NO_RENDER_DARKNESS.getBooleanValue()) cir.setReturnValue(false);
    }
}
