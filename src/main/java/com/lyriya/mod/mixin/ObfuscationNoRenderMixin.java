package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Font.class)
public abstract class ObfuscationNoRenderMixin {
    @ModifyExpressionValue(method = "getGlyph", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Style;isObfuscated()Z"))
    private boolean lyriya$disableObfuscation(boolean original) {
        return original && !(LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_OBFUSCATION.getBooleanValue());
    }
}
