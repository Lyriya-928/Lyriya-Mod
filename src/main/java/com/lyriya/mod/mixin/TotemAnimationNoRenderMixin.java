package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class TotemAnimationNoRenderMixin {
    @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
    private void lyriya$skipTotemActivation(ItemStack stack, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_TOTEM_ANIMATION.getBooleanValue()
            && stack.is(Items.TOTEM_OF_UNDYING)) ci.cancel();
    }
}
