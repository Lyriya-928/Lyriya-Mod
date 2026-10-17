package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntityListFeature extends Feature {
    public static final EntityListFeature INSTANCE = new EntityListFeature();
    private int loadedEntityCount;
    private Map<RowKey, Integer> groupedCounts = Map.of();

    private EntityListFeature() { super("entity_list", LyriyaConfig.ENTITY_LIST_ENABLED); }

    @Override protected void onDisable() { loadedEntityCount = 0; groupedCounts = Map.of(); }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) { loadedEntityCount = 0; groupedCounts = Map.of(); return; }
        int loaded = 0;
        Map<RowKey, Integer> counts = new HashMap<>();
        for (Entity entity : client.level.entitiesForRendering()) {
            loaded++;
            if (!included(entity)) continue;
            Category category = category(entity);
            String name = entity instanceof ItemEntity item
                ? item.getItem().getHoverName().getString()
                : entity.getType().getDescription().getString();
            counts.merge(new RowKey(name, category), 1, Integer::sum);
        }
        loadedEntityCount = loaded;
        groupedCounts = Map.copyOf(counts);
    }

    public void render(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        if (!isEnabled() || Minecraft.getInstance().level == null) return;
        boolean right = LyriyaConfig.ENTITY_RIGHT_ALIGNED.getBooleanValue();
        boolean bottom = LyriyaConfig.ENTITY_BOTTOM_ALIGNED.getBooleanValue();
        int margin = LyriyaConfig.ENTITY_MARGIN.getIntegerValue();
        int rowsLimit = LyriyaConfig.ENTITY_MAX_ROWS.getIntegerValue();
        float scale = LyriyaConfig.ENTITY_SCALE.getIntegerValue() / 100.0f;
        int spacing = LyriyaConfig.ENTITY_ROW_SPACING.getIntegerValue();
        boolean shadow = LyriyaConfig.ENTITY_TEXT_SHADOW.getBooleanValue();
        int offsetX = LyriyaConfig.ENTITY_OFFSET_X.getIntegerValue();
        int offsetY = LyriyaConfig.ENTITY_OFFSET_Y.getIntegerValue();

        List<Map.Entry<RowKey, Integer>> rows = new ArrayList<>(groupedCounts.entrySet());
        rows.sort(Comparator.<Map.Entry<RowKey, Integer>>comparingInt(Map.Entry::getValue).reversed()
            .thenComparing(entry -> entry.getKey().name()));
        int shown = Math.min(rowsLimit, rows.size());
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        Component title = Component.translatable("hud.lyriya_mod.loaded", loadedEntityCount);
        List<Component> labels = new ArrayList<>(shown + 2);
        labels.add(title);
        for (int i = 0; i < shown; i++) {
            Map.Entry<RowKey, Integer> row = rows.get(i);
            labels.add(Component.literal(row.getKey().name() + " × " + row.getValue()));
        }
        if (rows.size() > shown) labels.add(Component.translatable("hud.lyriya_mod.more_types", rows.size() - shown));

        int maxWidth = labels.stream().mapToInt(font::width).max().orElse(0);
        float scaledWidth = maxWidth * scale;
        float scaledHeight = (labels.size() * font.lineHeight + Math.max(0, labels.size() - 1) * spacing) * scale;
        float x = right ? graphics.guiWidth() - margin - offsetX - scaledWidth : margin + offsetX;
        float y = bottom ? graphics.guiHeight() - margin - offsetY - scaledHeight : margin + offsetY;
        // Keep the complete panel inside the visible GUI even when the user chooses a large offset or scale.
        x = Math.max(0, Math.min(x, graphics.guiWidth() - scaledWidth));
        y = Math.max(0, Math.min(y, graphics.guiHeight() - scaledHeight));

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.text(font, title, right ? maxWidth - font.width(title) : 0, 0, 0xFFFFFFFF, shadow);
        int localY = font.lineHeight + spacing;
        for (int i = 0; i < shown; i++) {
            Map.Entry<RowKey, Integer> row = rows.get(i);
            Component label = labels.get(i + 1);
            int color = color(row.getKey().category());
            graphics.text(font, label, right ? maxWidth - font.width(label) : 0, localY, color, shadow);
            localY += font.lineHeight + spacing;
        }
        if (rows.size() > shown) {
            Component more = labels.get(labels.size() - 1);
            graphics.text(font, more, right ? maxWidth - font.width(more) : 0, localY, 0xFFAAAAAA, shadow);
        }
        graphics.pose().popMatrix();
    }

    private static int color(Category category) {
        return switch (category) {
            case DROPS -> LyriyaConfig.ENTITY_COLOR_DROPS.getIntegerValue();
            case PLAYER -> LyriyaConfig.ENTITY_COLOR_PLAYERS.getIntegerValue();
            case HOSTILE -> LyriyaConfig.ENTITY_COLOR_HOSTILE.getIntegerValue();
            case PASSIVE -> LyriyaConfig.ENTITY_COLOR_PASSIVE.getIntegerValue();
            case OTHER -> LyriyaConfig.ENTITY_COLOR_OTHER.getIntegerValue();
        };
    }

    private boolean included(Entity entity) {
        if (entity instanceof ItemEntity) return LyriyaConfig.ENTITY_INCLUDE_DROPS.getBooleanValue();
        if (entity instanceof Player) return LyriyaConfig.ENTITY_INCLUDE_PLAYERS.getBooleanValue();
        if (entity instanceof net.minecraft.world.entity.LivingEntity) return LyriyaConfig.ENTITY_INCLUDE_LIVING.getBooleanValue();
        return LyriyaConfig.ENTITY_INCLUDE_OTHER.getBooleanValue();
    }

    private static Category category(Entity entity) {
        if (entity instanceof ItemEntity) return Category.DROPS;
        if (entity instanceof Player) return Category.PLAYER;
        if (entity instanceof Enemy) return Category.HOSTILE;
        if (entity instanceof net.minecraft.world.entity.LivingEntity) return Category.PASSIVE;
        return Category.OTHER;
    }

    private enum Category { DROPS, PLAYER, HOSTILE, PASSIVE, OTHER }
    private record RowKey(String name, Category category) { }
}
