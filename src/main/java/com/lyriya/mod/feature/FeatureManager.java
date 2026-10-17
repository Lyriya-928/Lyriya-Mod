package com.lyriya.mod.feature;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.AutoHelmetFeature;
import com.lyriya.mod.feature.impl.EntityListFeature;
import com.lyriya.mod.feature.impl.PortalsFeature;
import com.lyriya.mod.feature.impl.GuiMoveFeature;
import com.lyriya.mod.feature.impl.AutoTotemFeature;
import com.lyriya.mod.feature.impl.SafeWalkFeature;
import com.lyriya.mod.feature.impl.AutoCraftingFeature;
import com.lyriya.mod.feature.impl.NoRenderFeature;
import com.lyriya.mod.feature.impl.FullbrightFeature;
import com.lyriya.mod.feature.impl.FreecamFeature;
import com.lyriya.mod.feature.impl.NoGhostBlocksFeature;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class FeatureManager {
    public static final String PORTALS = "portals";
    public static final String SAFE_WALK = "safe_walk";
    private static final List<Feature> FEATURES = List.of(
        new AutoHelmetFeature(),
        EntityListFeature.INSTANCE,
        new PortalsFeature(),
        GuiMoveFeature.INSTANCE,
        new AutoTotemFeature(),
        SafeWalkFeature.INSTANCE,
        AutoCraftingFeature.INSTANCE,
        new NoRenderFeature(),
        FullbrightFeature.INSTANCE,
        FreecamFeature.INSTANCE,
        new NoGhostBlocksFeature()
    );
    private static boolean initialized;

    private FeatureManager() {
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        LyriyaConfig.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lyriya_mod", "entity_list"), EntityListFeature.INSTANCE::render);
    }

    public static boolean isEnabled(String id) {
        return switch (id) {
            case PORTALS -> LyriyaConfig.PORTALS_ENABLED.getBooleanValue();
            case SAFE_WALK -> LyriyaConfig.SAFE_WALK_ENABLED.getBooleanValue();
            default -> false;
        };
    }

    private static void tick() {
        for (Feature feature : FEATURES) {
            feature.tickManaged();
        }
    }
}
