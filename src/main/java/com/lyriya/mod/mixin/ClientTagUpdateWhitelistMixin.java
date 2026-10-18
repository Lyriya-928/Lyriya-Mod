package com.lyriya.mod.mixin;

import com.lyriya.mod.config.AutoCraftingWhitelist;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientTagUpdateWhitelistMixin {
    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void lyriya$invalidateWhitelistTagCache(ClientboundUpdateTagsPacket packet, CallbackInfo ci) {
        AutoCraftingWhitelist.invalidateTags();
    }
}
