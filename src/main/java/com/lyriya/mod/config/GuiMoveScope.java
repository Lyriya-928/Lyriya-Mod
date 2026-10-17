package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum GuiMoveScope implements IConfigOptionListEntry {
    INVENTORY("inventory", "lyriya_mod.config.gui_move.scope.inventory"),
    ALL_SCREENS("all_screens", "lyriya_mod.config.gui_move.scope.all_screens");

    private final String value;
    private final String translationKey;

    GuiMoveScope(String value, String translationKey) {
        this.value = value;
        this.translationKey = translationKey;
    }

    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate(translationKey); }
    @Override public IConfigOptionListEntry cycle(boolean forward) { return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length]; }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (GuiMoveScope scope : values()) if (scope.value.equalsIgnoreCase(input)) return scope;
        return INVENTORY;
    }
}
