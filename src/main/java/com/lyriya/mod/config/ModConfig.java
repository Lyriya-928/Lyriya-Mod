package com.lyriya.mod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigInteger;

/** Reads values from the pre-MaLiLib JSON shape during one-time config migration. */
public final class ModConfig {
    private ModConfig() {
    }

    static void migrateLegacy(JsonObject root) {
        JsonObject enabled = objectOrEmpty(root, "enabled");
        setBoolean(enabled, "auto_helmet", LyriyaConfig.AUTO_HELMET_ENABLED);
        setBoolean(enabled, "entity_list", LyriyaConfig.ENTITY_LIST_ENABLED);
        setBoolean(enabled, "portals", LyriyaConfig.PORTALS_ENABLED);

        JsonObject options = objectOrEmpty(root, "options");
        JsonObject helmet = objectOrEmpty(options, "auto_helmet");
        setInteger(helmet, "range", LyriyaConfig.AUTO_HELMET_RANGE);
        setInteger(helmet, "delay_ticks", LyriyaConfig.AUTO_HELMET_DELAY);

        JsonObject entity = objectOrEmpty(options, "entity_list");
        setBoolean(entity, "include_drops", LyriyaConfig.ENTITY_INCLUDE_DROPS);
        setBoolean(entity, "include_players", LyriyaConfig.ENTITY_INCLUDE_PLAYERS);
        setBoolean(entity, "include_living", LyriyaConfig.ENTITY_INCLUDE_LIVING);
        setBoolean(entity, "include_other", LyriyaConfig.ENTITY_INCLUDE_OTHER);
        setBoolean(entity, "right_aligned", LyriyaConfig.ENTITY_RIGHT_ALIGNED);
        setInteger(entity, "margin", LyriyaConfig.ENTITY_MARGIN);
        setInteger(entity, "max_rows", LyriyaConfig.ENTITY_MAX_ROWS);
    }

    private static JsonObject objectOrEmpty(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
    }

    private static void setBoolean(JsonObject values, String key, IConfigBoolean option) {
        JsonElement value = values.get(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            option.setBooleanValue(value.getAsBoolean());
        }
    }

    private static void setInteger(JsonObject values, String key, ConfigInteger option) {
        JsonElement value = values.get(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            try {
                option.setIntegerValue(value.getAsInt());
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
