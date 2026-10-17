package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityVisibilityNoRenderMixin {
    @Inject(method = "isInvisibleTo", at = @At("RETURN"), cancellable = true)
    private void lyriya$showInvisibleEntities(Player viewer, CallbackInfoReturnable<Boolean> cir) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_INVISIBILITY.getBooleanValue()) cir.setReturnValue(false);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideGlowingOutline(CallbackInfoReturnable<Boolean> cir) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_GLOWING.getBooleanValue()) cir.setReturnValue(false);
    }
}
