package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum AutoCraftingMode implements IConfigOptionListEntry {
    OPEN_SCREEN("open_screen"), HOTKEY("hotkey");
    private final String value;
    AutoCraftingMode(String value) { this.value = value; }
    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate("lyriya_mod.config.auto_crafting.mode." + value); }
    @Override public IConfigOptionListEntry cycle(boolean forward) { return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length]; }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (AutoCraftingMode mode : values()) if (mode.value.equalsIgnoreCase(input)) return mode;
        return OPEN_SCREEN;
    }
}
