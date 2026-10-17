package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.FullbrightMode;
import com.lyriya.mod.config.LightLayerOption;
import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;

public final class FullbrightFeature extends Feature {
    public static final FullbrightFeature INSTANCE = new FullbrightFeature();
    private MobEffectInstance ownedNightVision;
    private FullbrightMode lastMode;

    private FullbrightFeature() { super("fullbright", LyriyaConfig.FULLBRIGHT_ENABLED); }

    @Override
    protected void onEnable() {
        lastMode = currentMode();
        applyMode(lastMode);
    }

    @Override
    protected void onDisable() {
        cleanupMode(lastMode);
        lastMode = null;
        invalidateLighting();
    }

    @Override
    public void tick() {
        FullbrightMode mode = currentMode();
        if (mode != lastMode) {
            cleanupMode(lastMode);
            lastMode = mode;
            applyMode(mode);
            invalidateLighting();
        }
        if (mode == FullbrightMode.POTION) maintainNightVision();
    }

    public static int luminance(LightLayer layer, int original) {
        if (!LyriyaConfig.FULLBRIGHT_ENABLED.getBooleanValue()
            || currentMode() != FullbrightMode.LUMINANCE
            || currentLightLayer().layer() != layer) return original;
        return Math.max(original, LyriyaConfig.FULLBRIGHT_MIN_LIGHT.getIntegerValue());
    }

    private static FullbrightMode currentMode() {
        return (FullbrightMode) LyriyaConfig.FULLBRIGHT_MODE.getOptionListValue();
    }

    private static LightLayerOption currentLightLayer() {
        return (LightLayerOption) LyriyaConfig.FULLBRIGHT_LIGHT_LAYER.getOptionListValue();
    }

    private void applyMode(FullbrightMode mode) {
        if (mode == FullbrightMode.LUMINANCE) {
            invalidateLighting();
        }
    }

    private void cleanupMode(FullbrightMode mode) {
        Minecraft client = Minecraft.getInstance();
        if (mode == FullbrightMode.POTION && client.player != null && ownedNightVision != null
            && client.player.getEffect(MobEffects.NIGHT_VISION) == ownedNightVision) {
            client.player.removeEffect(MobEffects.NIGHT_VISION);
            ownedNightVision = null;
        }
        if (mode == FullbrightMode.LUMINANCE) invalidateLighting();
    }

    private void maintainNightVision() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        MobEffectInstance current = client.player.getEffect(MobEffects.NIGHT_VISION);
        if (ownedNightVision != null && current != ownedNightVision) {
            // Another source replaced the effect; leave it untouched and stop claiming ownership.
            ownedNightVision = null;
        }
        int target = LyriyaConfig.FULLBRIGHT_POTION_DURATION.getIntegerValue() * 20;
        if (current == null) {
            MobEffectInstance added = new MobEffectInstance(MobEffects.NIGHT_VISION, target, 0, true, false, false);
            if (client.player.addEffect(added)) ownedNightVision = client.player.getEffect(MobEffects.NIGHT_VISION);
        } else if (current == ownedNightVision && current.getDuration() < target / 2) {
            // addEffect updates this owned effect instance in place; keep its identity for safe cleanup.
            client.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, target, 0, true, false, false));
        }
    }

    private static void invalidateLighting() {
        Minecraft client = Minecraft.getInstance();
        if (client.levelRenderer != null) client.levelExtractor.allChanged();
    }
}
