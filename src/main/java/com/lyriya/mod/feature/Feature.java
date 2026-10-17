package com.lyriya.mod.feature;

import com.lyriya.mod.Lyriya;
import com.lyriya.mod.config.LyriyaConfig;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public abstract class Feature {
    private final ConfigBooleanHotkeyed enabled;
    private final String id;
    private boolean previousEnabled;
    private boolean initialized;

    protected Feature(String id, ConfigBooleanHotkeyed enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public final boolean isEnabled() {
        return enabled.getBooleanValue();
    }

    public final void tickManaged() {
        boolean current = isEnabled();
        if (!initialized) {
            // Persisted enabled states must initialize their behavior without looking like a user toggle.
            if (current) onEnable();
            previousEnabled = current;
            initialized = true;
        } else if (current != previousEnabled) {
            if (current) onEnable();
            else onDisable();
            if (LyriyaConfig.DEBUG_LOGGING.getBooleanValue()) {
                Lyriya.LOG.info("Feature {} changed to {}", id, current ? "enabled" : "disabled");
            }
            if (LyriyaConfig.TOGGLE_NOTIFICATIONS.getBooleanValue()) {
                Minecraft.getInstance().gui.hud.setOverlayMessage(Component.translatable(
                    "message.lyriya_mod.feature_state", Component.translatable("feature.lyriya_mod." + id),
                    Component.translatable(current ? "message.lyriya_mod.enabled" : "message.lyriya_mod.disabled")
                ), true);
            }
            previousEnabled = current;
        }
        if (current) tick();
    }

    protected void onEnable() { }

    protected void onDisable() { }

    public void tick() {
    }

}
