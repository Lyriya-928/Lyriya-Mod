package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum SafeWalkMode implements IConfigOptionListEntry {
    FIXED("fixed"), RANDOM_RANGE("random_range");
    private final String value;
    SafeWalkMode(String value) { this.value = value; }
    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate("lyriya_mod.config.safe_walk.mode." + value); }
    @Override public IConfigOptionListEntry cycle(boolean forward) { return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length]; }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (SafeWalkMode mode : values()) if (mode.value.equalsIgnoreCase(input)) return mode;
        return FIXED;
    }
}
