package com.lyriya.mod.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.world.level.LightLayer;

public enum LightLayerOption implements IConfigOptionListEntry {
    BLOCK("block", LightLayer.BLOCK), SKY("sky", LightLayer.SKY);

    private final String value;
    private final LightLayer layer;
    LightLayerOption(String value, LightLayer layer) { this.value = value; this.layer = layer; }
    public LightLayer layer() { return layer; }
    @Override public String getStringValue() { return value; }
    @Override public String getDisplayName() { return StringUtils.translate("lyriya_mod.config.fullbright.light." + value); }
    @Override public IConfigOptionListEntry cycle(boolean forward) { return values()[(ordinal() + (forward ? 1 : values().length - 1)) % values().length]; }
    @Override public IConfigOptionListEntry fromString(String input) {
        for (LightLayerOption option : values()) if (option.value.equalsIgnoreCase(input)) return option;
        return BLOCK;
    }
}
