package com.lyriya.mod.mixin;

import com.lyriya.mod.feature.impl.GuiMoveFeature;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class GuiMoveMixin {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;releaseAll()V"))
    private void lyriya$releaseKeysExceptAllowedMovement(Screen nextScreen) {
        if (!GuiMoveFeature.allows(nextScreen)) {
            KeyMapping.releaseAll();
            return;
        }
        Options options = minecraft.options;
        for (KeyMapping mapping : KeyMappingAccessor.lyriya$getAll().values()) {
            if (isMovementKey(mapping, options)) continue;
            mapping.setDown(false);
        }
    }

    private static boolean isMovementKey(KeyMapping mapping, Options options) {
        return mapping == options.keyUp || mapping == options.keyDown || mapping == options.keyLeft
            || mapping == options.keyRight
            || (com.lyriya.mod.config.LyriyaConfig.GUI_MOVE_JUMP.getBooleanValue() && mapping == options.keyJump)
            || (com.lyriya.mod.config.LyriyaConfig.GUI_MOVE_SNEAK.getBooleanValue() && mapping == options.keyShift)
            || (com.lyriya.mod.config.LyriyaConfig.GUI_MOVE_SPRINT.getBooleanValue() && mapping == options.keySprint);
    }
}
