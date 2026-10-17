package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum AutoTotemMode implements IConfigOptionListEntry {
    SMART("smart", "lyriya_mod.config.auto_totem.mode.smart"),
    ALWAYS("always", "lyriya_mod.config.auto_totem.mode.always");

    private final String value;
    private final String translationKey;

    AutoTotemMode(String value, String translationKey) { this.value = value; this.translationKey = translationKey; }
    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate(translationKey); }
    @Override public IConfigOptionListEntry cycle(boolean forward) { return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length]; }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (AutoTotemMode mode : values()) if (mode.value.equalsIgnoreCase(input)) return mode;
        return SMART;
    }
}
