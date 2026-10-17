package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.FullbrightFeature;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightCoordsUtil.BrightnessGetter.class)
public interface BrightnessGetterFullbrightMixin {
    @ModifyVariable(method = "lambda$static$0", at = @At("STORE"), ordinal = 0)
    private static int lyriya$minimumSkyLight(int original) {
        return FullbrightFeature.luminance(LightLayer.SKY, original);
    }

    @ModifyVariable(method = "lambda$static$0", at = @At("STORE"), ordinal = 1)
    private static int lyriya$minimumBlockLight(int original) {
        return FullbrightFeature.luminance(LightLayer.BLOCK, original);
    }
}
