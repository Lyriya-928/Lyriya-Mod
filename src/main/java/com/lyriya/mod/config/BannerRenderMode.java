package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum BannerRenderMode implements IConfigOptionListEntry {
    EVERYTHING("everything"), PILLAR("pillar"), NONE("none");

    private final String value;
    BannerRenderMode(String value) { this.value = value; }

    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate("lyriya_mod.config.no_render.banners." + value); }
    @Override public IConfigOptionListEntry cycle(boolean forward) {
        return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length];
    }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (BannerRenderMode mode : values()) if (mode.value.equalsIgnoreCase(input)) return mode;
        return EVERYTHING;
    }
}
