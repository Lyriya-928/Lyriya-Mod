package com.lyriya.mod;

import net.fabricmc.api.ClientModInitializer;

import com.lyriya.mod.feature.FeatureManager;

public final class LyriyaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FeatureManager.initialize();
        Lyriya.LOG.info("Lyriya Mod initialized for Minecraft {}", Lyriya.MINECRAFT_VERSION);
    }
}
