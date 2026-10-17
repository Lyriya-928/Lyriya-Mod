package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.GuiMoveScope;
import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.config.LyriyaConfigGui;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;

public final class GuiMoveFeature extends Feature {
    public static final GuiMoveFeature INSTANCE = new GuiMoveFeature();

    public GuiMoveFeature() {
        super("gui_move", LyriyaConfig.GUI_MOVE_ENABLED);
    }

    @Override
    protected void onDisable() {
        releaseMovement(Minecraft.getInstance().options);
    }

    @Override
    public void tick() {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen == null) return;
        Options options = Minecraft.getInstance().options;
        if (!allows(screen)) {
            releaseMovement(options);
            return;
        }
        if (!LyriyaConfig.GUI_MOVE_JUMP.getBooleanValue()) options.keyJump.setDown(false);
        if (!LyriyaConfig.GUI_MOVE_SNEAK.getBooleanValue()) options.keyShift.setDown(false);
        if (!LyriyaConfig.GUI_MOVE_SPRINT.getBooleanValue()) options.keySprint.setDown(false);
    }

    private static void releaseMovement(Options options) {
        options.keyUp.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyRight.setDown(false);
        options.keyJump.setDown(false);
        options.keyShift.setDown(false);
        options.keySprint.setDown(false);
    }

    public static boolean allows(Screen screen) {
        if (!LyriyaConfig.GUI_MOVE_ENABLED.getBooleanValue() || screen == null) return false;
        if (screen instanceof LyriyaConfigGui || screen instanceof ChatScreen || screen instanceof SignEditScreen || screen instanceof AnvilScreen
            || screen instanceof AbstractCommandBlockEditScreen || screen instanceof StructureBlockEditScreen
            || screen.getFocused() instanceof EditBox) return false;
        if (screen.getClass().getPackageName().startsWith("fi.dy.masa.malilib.gui")) return false;
        return LyriyaConfig.GUI_MOVE_SCOPE.getOptionListValue() == GuiMoveScope.ALL_SCREENS
            || screen instanceof AbstractContainerScreen<?>;
    }
}
