package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.FeatureManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
    @Redirect(
        method = "handlePortalTransitionEffect(Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;screen()Lnet/minecraft/client/gui/screens/Screen;",
            ordinal = 0
        )
    )
    private Screen lyriya$allowPortalGui(Gui gui) {
        return FeatureManager.isEnabled(FeatureManager.PORTALS) ? null : gui.screen();
    }
}
