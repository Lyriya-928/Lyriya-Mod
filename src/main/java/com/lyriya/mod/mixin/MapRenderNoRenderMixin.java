package com.lyriya.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MapRenderer.class)
public abstract class MapRenderNoRenderMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/MapRenderState;decorations:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<MapDecoration> lyriya$hideMapMarkers(List<MapDecoration> decorations) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_MAP_MARKERS.getBooleanValue()
            ? List.of() : decorations;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void lyriya$hideMapContents(MapRenderState state, com.mojang.blaze3d.vertex.PoseStack pose,
            SubmitNodeCollector collector, boolean showOnlyFrame, int light, CallbackInfo ci) {
        if (LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue() && LyriyaConfig.NO_RENDER_MAP_CONTENTS.getBooleanValue()) ci.cancel();
    }
}
