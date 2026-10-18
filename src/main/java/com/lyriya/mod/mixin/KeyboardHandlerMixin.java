package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.GuiMoveFeature;
import com.lyriya.mod.feature.impl.FreecamFeature;
import com.lyriya.mod.feature.impl.AutoCraftingFeature;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void lyriya$handleCraftingHotkeys(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (action != 1 || !(minecraft.gui.screen() instanceof CraftingScreen screen)) return;
        if (screen instanceof AbstractContainerEventHandler container
            && container.getFocused() instanceof EditBox editBox && editBox.capturesInput()) return;

        IKeybind keep = LyriyaConfig.AUTO_CRAFTING_TOGGLE.getKeybind();
        IKeybind drop = LyriyaConfig.AUTO_CRAFTING_DROP.getKeybind();
        if (keep.matches(event.key())) {
            AutoCraftingFeature.handleCraftingScreenHotkey(false);
            ci.cancel();
        } else if (drop.matches(event.key())) {
            AutoCraftingFeature.handleCraftingScreenHotkey(true);
            ci.cancel();
        }
    }

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void lyriya$captureFreecamMovement(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (FreecamFeature.INSTANCE.captureKey(event.key(), action)) ci.cancel();
    }

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void lyriya$forwardMovementInput(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (!GuiMoveFeature.allows(minecraft.gui.screen())) return;
        Options options = minecraft.options;
        update(options.keyUp, event, action);
        update(options.keyDown, event, action);
        update(options.keyLeft, event, action);
        update(options.keyRight, event, action);
        if (LyriyaConfig.GUI_MOVE_JUMP.getBooleanValue()) update(options.keyJump, event, action);
        if (LyriyaConfig.GUI_MOVE_SNEAK.getBooleanValue()) update(options.keyShift, event, action);
        if (LyriyaConfig.GUI_MOVE_SPRINT.getBooleanValue()) update(options.keySprint, event, action);
    }

    private static void update(KeyMapping mapping, KeyEvent event, int action) {
        if (((KeyMappingAccessor) (Object) mapping).lyriya$getKey().getValue() == event.key()) {
            mapping.setDown(action != 0);
        }
    }
}

