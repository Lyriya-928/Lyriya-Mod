package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;

public final class NoGhostBlocksFeature extends Feature {
    public NoGhostBlocksFeature() { super("no_ghost_blocks", LyriyaConfig.NO_GHOST_ENABLED); }
}
