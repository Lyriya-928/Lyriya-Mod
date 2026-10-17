package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class NoRenderFeature extends Feature {
    public NoRenderFeature() { super("no_render", LyriyaConfig.NO_RENDER_ENABLED); }

    public static boolean filtersEntity(Entity entity) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_ENTITY_TYPES.getStrings().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    public static boolean dropsEntitySpawn(EntityType<?> type) {
        return LyriyaConfig.NO_RENDER_ENABLED.getBooleanValue()
            && LyriyaConfig.NO_RENDER_DROP_SPAWN_PACKETS.getBooleanValue()
            && LyriyaConfig.NO_RENDER_ENTITY_TYPES.getStrings().contains(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }
}
