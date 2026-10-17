package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$1", remap = false)
public abstract class MessageSignatureIndicatorNoRenderMixin {
    @ModifyExpressionValue(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;tag()Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;"))
    private GuiMessageTag lyriya$hideSignatureIndicator(GuiMessageTag tag) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_SIGNATURE_INDICATOR.getBooleanValue() ? null : tag;
    }
}
