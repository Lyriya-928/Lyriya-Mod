package com.lyriya.mod.config;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;

/** Small MaLiLib-style screen for editing the persistent Auto Crafting whitelist. */
public final class AutoCraftingWhitelistEditor extends GuiBase {
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_TOP = 91;
    private final Screen parent;
    private GuiTextFieldGeneric input;
    private int selectedIndex = -1;
    private int scrollOffset;

    private AutoCraftingWhitelistEditor(Screen parent) {
        this.parent = parent;
        setParent(parent);
        setTitle("lyriya_mod.config.auto_crafting.editor_title");
    }

    public static void open(Screen parent) {
        GuiBase.openGui(new AutoCraftingWhitelistEditor(parent));
    }

    @Override
    public void initGui() {
        super.initGui();
        int left = Math.max(10, (width - Math.min(500, width - 20)) / 2);
        int contentWidth = Math.min(500, width - 20);
        int addWidth = Math.max(75, getStringWidth(StringUtils.translate("lyriya_mod.config.auto_crafting.add")) + 20);
        input = new GuiTextFieldGeneric(left, 47, contentWidth - addWidth - 6, 20, font);
        input.setMaxLengthWrapper(256);
        input.setHint(Component.translatable("lyriya_mod.config.auto_crafting.input_hint"));
        addTextField(input, new fi.dy.masa.malilib.gui.interfaces.ITextFieldListener<>() {
            @Override public boolean onTextChange(GuiTextFieldGeneric field) { return true; }
        });
        addButton(new ButtonGeneric(left + contentWidth - addWidth, 47, addWidth, 20,
            StringUtils.translate("lyriya_mod.config.auto_crafting.add")), new AddListener(this));

        int buttonY = height - 31;
        int buttonWidth = Math.max(75, (contentWidth - 12) / 3);
        addButton(new ButtonGeneric(left, buttonY, buttonWidth, 20,
            StringUtils.translate("lyriya_mod.config.auto_crafting.remove_selected")), new RemoveListener(this));
        addButton(new ButtonGeneric(left + buttonWidth + 6, buttonY, buttonWidth, 20,
            StringUtils.translate("lyriya_mod.config.auto_crafting.clear_input")), new ClearInputListener(this));
        addButton(new ButtonGeneric(left + (buttonWidth + 6) * 2, buttonY, buttonWidth, 20,
            StringUtils.translate("lyriya_mod.config.auto_crafting.close")), new CloseListener(parent));
        clampScroll();
    }

    @Override
    protected void drawContents(GuiContext context, int mouseX, int mouseY, float partialTick) {
        super.drawContents(context, mouseX, mouseY, partialTick);
        int left = Math.max(10, (width - Math.min(500, width - 20)) / 2);
        int contentWidth = Math.min(500, width - 20);
        int listHeight = Math.max(ROW_HEIGHT, height - LIST_TOP - 48);
        int rows = Math.max(1, listHeight / ROW_HEIGHT);
        context.drawString(font, Component.translatable("lyriya_mod.config.auto_crafting.input_format"),
            left, 72, 0xFFE0E0E0);
        context.drawString(font, Component.translatable("lyriya_mod.config.auto_crafting.current_whitelist",
            LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings().size()), left, LIST_TOP - 15, 0xFFFFFFFF);
        RenderUtils.drawRect(context, left, LIST_TOP, contentWidth, rows * ROW_HEIGHT, 0xA0101010);
        List<String> entries = LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings();
        for (int row = 0; row < rows; row++) {
            int index = scrollOffset + row;
            if (index >= entries.size()) break;
            int y = LIST_TOP + row * ROW_HEIGHT;
            if (index == selectedIndex) RenderUtils.drawRect(context, left + 1, y, contentWidth - 2, ROW_HEIGHT, 0xA04C6F9A);
            String label = displayRule(entries.get(index));
            context.drawString(font, Component.literal(label), left + 6, y + 5, 0xFFFFFFFF);
        }
        if (entries.isEmpty()) {
            context.drawCenteredString(font, Component.translatable("lyriya_mod.config.auto_crafting.empty_whitelist"),
                left + contentWidth / 2, LIST_TOP + 6, 0xFFAAAAAA);
        }
        clampScroll();
    }

    @Override
    public boolean onMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        int left = Math.max(10, (width - Math.min(500, width - 20)) / 2);
        int contentWidth = Math.min(500, width - 20);
        int listBottom = height - 48;
        if (event.button() == 0 && event.x() >= left && event.x() < left + contentWidth
            && event.y() >= LIST_TOP && event.y() < listBottom) {
            int row = (int) (event.y() - LIST_TOP) / ROW_HEIGHT;
            int index = scrollOffset + row;
            List<String> entries = LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings();
            selectedIndex = index >= 0 && index < entries.size() ? index : -1;
            return true;
        }
        return super.onMouseClicked(event, doubleClick);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int left = Math.max(10, (width - Math.min(500, width - 20)) / 2);
        int contentWidth = Math.min(500, width - 20);
        if (mouseX >= left && mouseX < left + contentWidth && mouseY >= LIST_TOP && mouseY < height - 48) {
            scrollOffset -= (int) Math.signum(deltaY);
            clampScroll();
            return true;
        }
        return super.onMouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void addInput() {
        AutoCraftingWhitelist.Resolution result = AutoCraftingWhitelist.resolveInput(input.getValueWrapper(), mc.level);
        if (result.error() != AutoCraftingWhitelist.Error.NONE) {
            show(result.error());
            return;
        }
        List<String> values = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
        int added = 0;
        for (String rule : result.rules()) {
            if (!values.contains(rule)) {
                values.add(rule);
                added++;
            }
        }
        if (added == 0) {
            show(AutoCraftingWhitelist.Error.DUPLICATE);
            return;
        }
        LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(values);
        LyriyaConfig.INSTANCE.save();
        input.setValueWrapper("");
        selectedIndex = -1;
        scrollOffset = Math.max(0, values.size() - visibleRows());
        addGuiMessage(MessageType.SUCCESS, 100, "lyriya_mod.config.auto_crafting.added", added);
    }

    private void removeSelected() {
        if (selectedIndex < 0) {
            addGuiMessage(MessageType.WARNING, 100, "lyriya_mod.config.auto_crafting.select_entry");
            return;
        }
        List<String> values = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
        if (selectedIndex >= values.size()) return;
        values.remove(selectedIndex);
        LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(values);
        LyriyaConfig.INSTANCE.save();
        selectedIndex = -1;
        clampScroll();
    }

    private void show(AutoCraftingWhitelist.Error error) {
        String key = switch (error) {
            case EMPTY -> "lyriya_mod.config.auto_crafting.error_empty";
            case BAD_FILTER -> "lyriya_mod.config.auto_crafting.error_filter";
            case INVALID_ID -> "lyriya_mod.config.auto_crafting.error_id";
            case UNKNOWN_NAME -> "lyriya_mod.config.auto_crafting.error_name";
            case UNKNOWN_TAG -> "lyriya_mod.config.auto_crafting.error_tag";
            case EMPTY_TAG -> "lyriya_mod.config.auto_crafting.error_empty_tag";
            case DUPLICATE -> "lyriya_mod.config.auto_crafting.error_duplicate";
            case NONE -> "lyriya_mod.config.auto_crafting.added";
        };
        addGuiMessage(MessageType.ERROR, 100, key);
    }

    private int visibleRows() { return Math.max(1, (height - LIST_TOP - 48) / ROW_HEIGHT); }

    private void clampScroll() {
        scrollOffset = Math.max(0, Math.min(scrollOffset,
            LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings().size() - visibleRows()));
    }

    private static String displayRule(String rule) {
        if (rule.startsWith("#")) return rule;
        try {
            Item item = BuiltInRegistries.ITEM.getOptional(Identifier.parse(rule)).orElse(null);
            if (item != null) return new ItemStack(item).getHoverName().getString() + "  [" + rule + "]";
        } catch (RuntimeException ignored) { }
        return rule;
    }

    private record AddListener(AutoCraftingWhitelistEditor editor) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) { editor.addInput(); }
    }
    private record RemoveListener(AutoCraftingWhitelistEditor editor) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) { editor.removeSelected(); }
    }
    private record ClearInputListener(AutoCraftingWhitelistEditor editor) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) { editor.input.setValueWrapper(""); }
    }
    private record CloseListener(Screen parent) implements IButtonActionListener {
        @Override public void actionPerformedWithButton(ButtonBase button, int mouseButton) { GuiBase.openGui(parent); }
    }
}
