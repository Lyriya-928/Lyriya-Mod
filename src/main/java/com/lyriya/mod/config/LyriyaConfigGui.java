package com.lyriya.mod.config;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiStringListSelection;
import fi.dy.masa.malilib.gui.widgets.WidgetListStringSelection;
import fi.dy.masa.malilib.gui.widgets.WidgetStringListEntry;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

public final class LyriyaConfigGui extends GuiConfigsBase {
    private static Category category = Category.GENERAL;

    public LyriyaConfigGui() {
        super(10, 96, "lyriya_mod", null, "lyriya_mod.gui.title");
    }

    @Override
    public void initGui() {
        List<CategoryButton> categoryButtons = categoryButtons();
        int rows = categoryButtons.stream().mapToInt(CategoryButton::row).max().orElse(0) + 1;
        setListPosition(10, 26 + rows * 22 + 7);
        super.initGui();
        clearOptions();
        for (CategoryButton layout : categoryButtons) {
            Category entry = layout.category();
            ButtonGeneric button = new ButtonGeneric(layout.x(), layout.y(), layout.width(), 20, entry.displayName());
            button.setEnabled(category != entry);
            addButton(button, new CategoryListener(entry, this));
        }
        if (category == Category.AUTO_CRAFTING) {
            ButtonGeneric add = new ButtonGeneric(10, this.height - 32, 140, 20,
                StringUtils.translate("lyriya_mod.config.auto_crafting.add_items"));
            addButton(add, new CraftingListListener(this, true));
            ButtonGeneric remove = new ButtonGeneric(154, this.height - 32, 155, 20,
                StringUtils.translate("lyriya_mod.config.auto_crafting.remove_items"));
            addButton(remove, new CraftingListListener(this, false));
            ButtonGeneric count = new ButtonGeneric(313, this.height - 32, 220, 20,
                StringUtils.translate("lyriya_mod.config.auto_crafting.item_count", LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings().size()));
            addButton(count, new CraftingListListener(this, false));
        }
        if (category == Category.NO_RENDER_ENTITY) {
            ButtonGeneric addTypes = new ButtonGeneric(10, this.height - 32, 170, 20,
                StringUtils.translate("lyriya_mod.config.no_render.add_entities"));
            addButton(addTypes, new EntityTypeListListener(true));
            ButtonGeneric removeTypes = new ButtonGeneric(184, this.height - 32, 180, 20,
                StringUtils.translate("lyriya_mod.config.no_render.remove_entities"));
            addButton(removeTypes, new EntityTypeListListener(false));
        }
        if (category == Category.NO_RENDER_WORLD) {
            ButtonGeneric addBlocks = new ButtonGeneric(10, this.height - 32, 170, 20,
                StringUtils.translate("lyriya_mod.config.no_render.add_block_entities"));
            addButton(addBlocks, new BlockTypeListListener(true));
            ButtonGeneric removeBlocks = new ButtonGeneric(184, this.height - 32, 180, 20,
                StringUtils.translate("lyriya_mod.config.no_render.remove_block_entities"));
            addButton(removeBlocks, new BlockTypeListListener(false));
            ButtonGeneric addParticles = new ButtonGeneric(368, this.height - 32, 82, 20,
                StringUtils.translate("lyriya_mod.config.no_render.add_particles"));
            addButton(addParticles, new ParticleTypeListListener(true));
            ButtonGeneric removeParticles = new ButtonGeneric(454, this.height - 32, 84, 20,
                StringUtils.translate("lyriya_mod.config.no_render.remove_particles"));
            addButton(removeParticles, new ParticleTypeListListener(false));
        }
    }

    private List<CategoryButton> categoryButtons() {
        List<CategoryButton> result = new ArrayList<>();
        int left = 10;
        int right = Math.max(left + 44, this.width - 10);
        int x = left;
        int row = 0;
        for (Category entry : Category.values()) {
            int width = Math.min(getStringWidth(entry.displayName()) + 10, right - left);
            if (x > left && x + width > right) {
                row++;
                x = left;
            }
            result.add(new CategoryButton(entry, x, 26 + row * 22, width, row));
            x += width + 2;
        }
        return result;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(category.options());
    }

    private record CategoryListener(Category category, LyriyaConfigGui parent) implements IButtonActionListener {
        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            LyriyaConfigGui.category = category;
            parent.reCreateListWidget();
            if (parent.getListWidget() != null) parent.getListWidget().resetScrollbarPosition();
            parent.initGui();
        }
    }

    private record CategoryButton(Category category, int x, int y, int width, int row) { }

    private record CraftingListListener(LyriyaConfigGui parent, boolean add) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (add) {
                Map<String, String> choices = allItemChoices();
                openSearchableSelection(choices, selected -> {
                    List<String> values = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
                    for (String label : selected) {
                        String id = choices.get(label);
                        if (id != null && !values.contains(id)) values.add(id);
                    }
                    LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(values);
                    LyriyaConfig.INSTANCE.save();
                    return true;
                });
            } else {
                Map<String, String> current = currentItemChoices();
                openSearchableSelection(current, selected -> {
                    List<String> values = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
                    for (String label : selected) values.remove(current.get(label));
                    LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(values);
                    LyriyaConfig.INSTANCE.save();
                    return true;
                });
            }
        }
    }

    private static void openSearchableSelection(Map<String, String> choices, fi.dy.masa.malilib.gui.interfaces.IStringListConsumer consumer) {
        GuiBase.openGui(new SearchableItemSelection(choices, consumer));
    }

    private static final class SearchableItemSelection extends GuiStringListSelection {
        private final Map<String, String> searchTerms;

        private SearchableItemSelection(Map<String, String> choices, fi.dy.masa.malilib.gui.interfaces.IStringListConsumer consumer) {
            super(choices.keySet(), consumer);
            this.searchTerms = Map.copyOf(choices);
        }

        @Override
        public void initGui() {
            super.initGui();
            if (getListWidget() != null && getListWidget().getSearchBarWidget() != null) {
                getListWidget().getSearchBarWidget().setSearchOpen(true);
            }
        }

        @Override
        protected WidgetListStringSelection createListWidget(int x, int y) {
            return new SearchableItemList(x, y, getBrowserWidth(), getBrowserHeight(), this, searchTerms);
        }
    }

    private static final class SearchableItemList extends WidgetListStringSelection {
        private final Map<String, String> searchTerms;

        private SearchableItemList(int x, int y, int width, int height,
                fi.dy.masa.malilib.interfaces.IStringListProvider provider, Map<String, String> searchTerms) {
            super(x, y, width, height, provider);
            this.searchTerms = searchTerms;
        }

        @Override
        protected List<String> getEntryStringsForFilter(String entry) {
            String id = searchTerms.get(entry);
            return id == null ? List.of(entry) : List.of(entry, id);
        }

        @Override
        protected WidgetStringListEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                               String entry) {
            Item item = itemForChoice(searchTerms.get(entry));
            return new ItemIconStringEntry(x, y, browserEntryWidth, getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, item);
        }

        @Override
        protected boolean matchesFilter(String value, String filter) {
            String needle = filter.toLowerCase(java.util.Locale.ROOT);
            return value.toLowerCase(java.util.Locale.ROOT).contains(needle);
        }

        @Override
        public void drawContents(GuiContext context, int mouseX, int mouseY, float partialTick) {
            super.drawContents(context, mouseX, mouseY, partialTick);
            if (hasFilter() && getCurrentEntries().isEmpty()) {
                context.drawCenteredString(context.fontRenderer(),
                    Component.translatable("lyriya_mod.config.auto_crafting.search_no_results"),
                    posX + browserWidth / 2, posY + browserHeight / 2, 0xFFAAAAAA);
            }
        }
    }

    private static Item itemForChoice(String id) {
        try {
            Item item = BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(id));
            if (item != null) return item;
        } catch (RuntimeException ignored) { }
        return net.minecraft.world.item.Items.AIR;
    }

    private static final class ItemIconStringEntry extends WidgetStringListEntry {
        private final Item item;

        private ItemIconStringEntry(int x, int y, int width, int height, boolean isOdd,
                                    String label, int listIndex, Item item) {
            super(x, y, width, height, isOdd, label, listIndex);
            this.item = item;
        }

        @Override
        public void render(GuiContext context, int mouseX, int mouseY, boolean selected) {
            if (selected || isMouseOver(mouseX, mouseY)) {
                RenderUtils.drawRect(context, x, y, width, height, -1603243920);
            } else {
                RenderUtils.drawRect(context, x, y, width, height, -1607454672);
            }
            if (selected) RenderUtils.drawOutline(context, x, y, width, height, -7286544);
            var stack = new net.minecraft.world.item.ItemStack(item);
            context.renderItem(stack, x + 2, y + (height - 16) / 2);
            int textY = y + (height - fontHeight) / 2 + 1;
            drawStringWithShadow(context, x + 22, textY, -1, entry);
            super.postRenderHovered(context, mouseX, mouseY, selected);
        }
    }

    private static Map<String, String> allItemChoices() {
        List<Item> items = new ArrayList<>();
        BuiltInRegistries.ITEM.forEach(items::add);
        items.sort(Comparator.comparing((Item item) -> new net.minecraft.world.item.ItemStack(item).getHoverName().getString(), String.CASE_INSENSITIVE_ORDER));
        Map<String, String> choices = new LinkedHashMap<>();
        Map<String, Integer> nameCounts = new java.util.HashMap<>();
        for (Item item : items) nameCounts.merge(new net.minecraft.world.item.ItemStack(item).getHoverName().getString(), 1, Integer::sum);
        for (Item item : items) {
            String name = new net.minecraft.world.item.ItemStack(item).getHoverName().getString();
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            String label = nameCounts.get(name) > 1 ? name + " [" + id + "]" : name;
            choices.put(label, id);
        }
        return choices;
    }

    private static Map<String, String> currentItemChoices() {
        Map<String, String> choices = new LinkedHashMap<>();
        for (String id : LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings()) {
            String label;
            try {
                Item item = BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(id));
                label = item == null ? id : new net.minecraft.world.item.ItemStack(item).getHoverName().getString();
            } catch (RuntimeException ignored) { label = id; }
            choices.put(label + " [" + id + "]", id);
        }
        return choices;
    }

    private record EntityTypeListListener(boolean add) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            Map<String, String> choices = add ? allEntityChoices() : currentEntityChoices();
            GuiBase.openGui(new GuiStringListSelection(choices.keySet(), selected -> {
                List<String> values = new ArrayList<>(LyriyaConfig.NO_RENDER_ENTITY_TYPES.getStrings());
                for (String label : selected) {
                    String id = choices.get(label);
                    if (id == null) continue;
                    if (add && !values.contains(id)) values.add(id);
                    if (!add) values.remove(id);
                }
                LyriyaConfig.NO_RENDER_ENTITY_TYPES.setStrings(values);
                LyriyaConfig.INSTANCE.save();
                return true;
            }));
        }
    }

    private static Map<String, String> allEntityChoices() {
        List<net.minecraft.world.entity.EntityType<?>> types = new ArrayList<>();
        BuiltInRegistries.ENTITY_TYPE.forEach(types::add);
        types.sort(Comparator.comparing(type -> type.getDescription().getString(), String.CASE_INSENSITIVE_ORDER));
        Map<String, Integer> counts = new java.util.HashMap<>();
        for (var type : types) counts.merge(type.getDescription().getString(), 1, Integer::sum);
        Map<String, String> choices = new LinkedHashMap<>();
        for (var type : types) {
            String name = type.getDescription().getString();
            String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
            choices.put(counts.get(name) > 1 ? name + " [" + id + "]" : name, id);
        }
        return choices;
    }

    private static Map<String, String> currentEntityChoices() {
        Map<String, String> choices = new LinkedHashMap<>();
        for (String id : LyriyaConfig.NO_RENDER_ENTITY_TYPES.getStrings()) {
            String name = id;
            try {
                var type = BuiltInRegistries.ENTITY_TYPE.getValue(net.minecraft.resources.Identifier.parse(id));
                if (type != null) name = type.getDescription().getString();
            } catch (RuntimeException ignored) { }
            choices.put(name + " [" + id + "]", id);
        }
        return choices;
    }

    private record BlockTypeListListener(boolean add) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            Map<String, String> choices = add ? allBlockChoices() : currentBlockChoices();
            GuiBase.openGui(new GuiStringListSelection(choices.keySet(), selected -> {
                List<String> values = new ArrayList<>(LyriyaConfig.NO_RENDER_BLOCK_ENTITY_TYPES.getStrings());
                for (String label : selected) {
                    String id = choices.get(label);
                    if (id == null) continue;
                    if (add && !values.contains(id)) values.add(id);
                    if (!add) values.remove(id);
                }
                LyriyaConfig.NO_RENDER_BLOCK_ENTITY_TYPES.setStrings(values);
                LyriyaConfig.INSTANCE.save();
                return true;
            }));
        }
    }

    private static Map<String, String> allBlockChoices() {
        List<net.minecraft.world.level.block.Block> blocks = new ArrayList<>();
        BuiltInRegistries.BLOCK.forEach(block -> {
            if (block instanceof net.minecraft.world.level.block.EntityBlock
                && !(block instanceof net.minecraft.world.level.block.AbstractBannerBlock)) blocks.add(block);
        });
        blocks.sort(Comparator.comparing(block -> block.getName().getString(), String.CASE_INSENSITIVE_ORDER));
        Map<String, Integer> counts = new java.util.HashMap<>();
        for (var block : blocks) counts.merge(block.getName().getString(), 1, Integer::sum);
        Map<String, String> choices = new LinkedHashMap<>();
        for (var block : blocks) {
            String name = block.getName().getString();
            String id = BuiltInRegistries.BLOCK.getKey(block).toString();
            choices.put(counts.get(name) > 1 ? name + " [" + id + "]" : name, id);
        }
        return choices;
    }

    private static Map<String, String> currentBlockChoices() {
        Map<String, String> choices = new LinkedHashMap<>();
        for (String id : LyriyaConfig.NO_RENDER_BLOCK_ENTITY_TYPES.getStrings()) {
            String name = id;
            try {
                var block = BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(id));
                if (block != null) name = block.getName().getString();
            } catch (RuntimeException ignored) { }
            choices.put(name + " [" + id + "]", id);
        }
        return choices;
    }

    private record ParticleTypeListListener(boolean add) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            Map<String, String> choices = add ? allParticleChoices() : currentParticleChoices();
            GuiBase.openGui(new GuiStringListSelection(choices.keySet(), selected -> {
                List<String> values = new ArrayList<>(LyriyaConfig.NO_RENDER_PARTICLE_TYPES.getStrings());
                for (String label : selected) {
                    String id = choices.get(label);
                    if (id == null) continue;
                    if (add && !values.contains(id)) values.add(id);
                    if (!add) values.remove(id);
                }
                LyriyaConfig.NO_RENDER_PARTICLE_TYPES.setStrings(values);
                LyriyaConfig.INSTANCE.save();
                return true;
            }));
        }
    }

    private static Map<String, String> allParticleChoices() {
        List<net.minecraft.core.particles.ParticleType<?>> particles = new ArrayList<>();
        BuiltInRegistries.PARTICLE_TYPE.forEach(particles::add);
        Map<String, String> choices = new LinkedHashMap<>();
        for (var type : particles) {
            String id = BuiltInRegistries.PARTICLE_TYPE.getKey(type).toString();
            String key = "particle." + BuiltInRegistries.PARTICLE_TYPE.getKey(type).getNamespace() + "."
                + BuiltInRegistries.PARTICLE_TYPE.getKey(type).getPath().replace('/', '.');
            String translated = net.minecraft.network.chat.Component.translatable(key).getString();
            String fallback = BuiltInRegistries.PARTICLE_TYPE.getKey(type).getPath().replace('_', ' ');
            String name = translated.equals(key) ? fallback : translated;
            choices.put(name + " [" + id + "]", id);
        }
        return choices;
    }

    private static Map<String, String> currentParticleChoices() {
        Map<String, String> choices = new LinkedHashMap<>();
        for (String id : LyriyaConfig.NO_RENDER_PARTICLE_TYPES.getStrings()) {
            String label = id;
            try {
                var identifier = net.minecraft.resources.Identifier.parse(id);
                var type = BuiltInRegistries.PARTICLE_TYPE.getValue(identifier);
                if (type != null) {
                    String key = "particle." + identifier.getNamespace() + "." + identifier.getPath().replace('/', '.');
                    String translated = net.minecraft.network.chat.Component.translatable(key).getString();
                    label = translated.equals(key) ? identifier.getPath().replace('_', ' ') : translated;
                }
            } catch (RuntimeException ignored) { }
            choices.put(label + " [" + id + "]", id);
        }
        return choices;
    }

    private enum Category {
        GENERAL("lyriya_mod.config.category.general", LyriyaConfig.GENERAL_OPTIONS),
        AUTO_HELMET("lyriya_mod.config.category.auto_helmet", LyriyaConfig.AUTO_HELMET_OPTIONS),
        ENTITY_LIST("lyriya_mod.config.category.entity_list", LyriyaConfig.ENTITY_LIST_OPTIONS),
        PORTALS("lyriya_mod.config.category.portals", LyriyaConfig.PORTALS_OPTIONS),
        GUI_MOVE("lyriya_mod.config.category.gui_move", LyriyaConfig.GUI_MOVE_OPTIONS),
        AUTO_TOTEM("lyriya_mod.config.category.auto_totem", LyriyaConfig.AUTO_TOTEM_OPTIONS),
        SAFE_WALK("lyriya_mod.config.category.safe_walk", LyriyaConfig.SAFE_WALK_OPTIONS),
        AUTO_CRAFTING("lyriya_mod.config.category.auto_crafting", LyriyaConfig.AUTO_CRAFTING_OPTIONS),
        NO_RENDER_OVERLAY("lyriya_mod.config.category.no_render_overlay", LyriyaConfig.NO_RENDER_OVERLAY_OPTIONS),
        NO_RENDER_HUD("lyriya_mod.config.category.no_render_hud", LyriyaConfig.NO_RENDER_HUD_OPTIONS),
        NO_RENDER_WORLD("lyriya_mod.config.category.no_render_world", LyriyaConfig.NO_RENDER_WORLD_OPTIONS),
        NO_RENDER_ENTITY("lyriya_mod.config.category.no_render_entity", LyriyaConfig.NO_RENDER_ENTITY_OPTIONS),
        FULLBRIGHT("lyriya_mod.config.category.fullbright", LyriyaConfig.FULLBRIGHT_OPTIONS),
        FREECAM("lyriya_mod.config.category.freecam", LyriyaConfig.FREECAM_OPTIONS),
        NO_GHOST_BLOCKS("lyriya_mod.config.category.no_ghost_blocks", LyriyaConfig.NO_GHOST_OPTIONS);

        private final String name;
        private final List<fi.dy.masa.malilib.config.IConfigBase> options;

        Category(String name, List<fi.dy.masa.malilib.config.IConfigBase> options) {
            this.name = name;
            this.options = options;
        }

        private String displayName() {
            return StringUtils.translate(name);
        }

        private List<fi.dy.masa.malilib.config.IConfigBase> options() {
            return options;
        }
    }
}
