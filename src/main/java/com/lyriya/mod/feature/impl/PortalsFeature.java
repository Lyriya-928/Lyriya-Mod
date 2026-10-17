package com.lyriya.mod.feature.impl;

import com.lyriya.mod.feature.Feature;
import com.lyriya.mod.config.LyriyaConfig;

public final class PortalsFeature extends Feature {
    public PortalsFeature() {
        super("portals", LyriyaConfig.PORTALS_ENABLED);
    }
}
