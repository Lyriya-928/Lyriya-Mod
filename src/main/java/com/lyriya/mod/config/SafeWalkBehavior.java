package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum SafeWalkBehavior implements IConfigOptionListEntry {
    SOFT_SNEAK("soft_sneak"), EDGE_AUTO_SNEAK("edge_auto_sneak");

    private final String value;

    SafeWalkBehavior(String value) { this.value = value; }

    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate("lyriya_mod.config.safe_walk.behavior." + value); }
    @Override public IConfigOptionListEntry cycle(boolean forward) {
        return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length];
    }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (SafeWalkBehavior mode : values()) if (mode.value.equalsIgnoreCase(input)) return mode;
        return SOFT_SNEAK;
    }
}
