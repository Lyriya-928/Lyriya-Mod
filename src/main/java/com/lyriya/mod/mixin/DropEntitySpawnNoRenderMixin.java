package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.NoRenderFeature;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class DropEntitySpawnNoRenderMixin {
    @Inject(method = "handleAddEntity", at = @At("HEAD"), cancellable = true)
    private void lyriya$dropConfiguredSpawnPacket(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        if (NoRenderFeature.dropsEntitySpawn(packet.getType())) ci.cancel();
    }
}
