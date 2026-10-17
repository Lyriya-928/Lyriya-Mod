package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.AutoCraftingMode;
import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.ArrayList;
import java.util.List;

public final class AutoCraftingFeature extends Feature {
    public static final AutoCraftingFeature INSTANCE = new AutoCraftingFeature();
    private int cooldown;
    private boolean taskRunning;
    private boolean diagnosticShown;

    private AutoCraftingFeature() { super("auto_crafting", LyriyaConfig.AUTO_CRAFTING_ENABLED); }

    public static void toggleTask() { INSTANCE.toggleTaskInternal(); }

    private void toggleTaskInternal() {
        Minecraft client = Minecraft.getInstance();
        if (!isEnabled() || currentMode() != AutoCraftingMode.HOTKEY) return;
        if (!(client.gui.screen() instanceof CraftingScreen) || !(client.player != null && client.player.containerMenu instanceof CraftingMenu)) {
            if (client.player != null) client.gui.hud.setOverlayMessage(Component.translatable("message.lyriya_mod.crafting_open_first"), true);
            return;
        }
        taskRunning = !taskRunning;
        cooldown = 0;
        if (client.player != null) client.gui.hud.setOverlayMessage(
            Component.translatable(taskRunning ? "message.lyriya_mod.crafting_started" : "message.lyriya_mod.crafting_stopped"), true);
    }

    @Override protected void onDisable() { cooldown = 0; taskRunning = false; diagnosticShown = false; }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.gameMode == null
            || !(client.gui.screen() instanceof CraftingScreen) || !(client.player.containerMenu instanceof CraftingMenu menu)) {
            cooldown = 0;
            taskRunning = false;
            diagnosticShown = false;
            return;
        }
        if (currentMode() == AutoCraftingMode.HOTKEY && !taskRunning) return;
        if (cooldown > 0) { cooldown--; return; }

        List<Item> targets = validTargets();
        if (targets.isEmpty()) { showCraftDiagnostic(client, "message.lyriya_mod.crafting_no_targets"); return; }
        ItemStack output = menu.getResultSlot().getItem();
        if (!output.isEmpty() && targets.stream().anyMatch(output::is)) {
            if (client.player.getInventory().getSlotWithRemainingSpace(output) < 0) return;
            client.gameMode.handleContainerInput(menu.containerId, CraftingMenu.RESULT_SLOT, 0,
                ContainerInput.QUICK_MOVE, client.player);
            cooldown = LyriyaConfig.AUTO_CRAFTING_DELAY.getIntegerValue();
            diagnosticShown = false;
            return;
        }

        StackedItemContents contents = new StackedItemContents();
        client.player.getInventory().fillStackedContents(contents);
        for (RecipeCollection collection : client.player.getRecipeBook().getCollections()) {
            if (!collection.hasCraftable()) continue;
            for (RecipeDisplayEntry entry : collection.getRecipes()) {
                if (!collection.isCraftable(entry.id()) || !entry.canCraft(contents)) continue;
                boolean allowed = entry.resultItems(SlotDisplayContext.fromLevel(client.level)).stream()
                    .anyMatch(stack -> targets.stream().anyMatch(stack::is));
                if (!allowed) continue;
                Item resultItem = entry.resultItems(SlotDisplayContext.fromLevel(client.level)).stream()
                    .map(ItemStack::getItem).filter(targets::contains).findFirst().orElse(null);
                if (resultItem == null || client.player.getInventory().getSlotWithRemainingSpace(new ItemStack(resultItem)) < 0) continue;
                client.gameMode.handlePlaceRecipe(menu.containerId, entry.id(), true);
                cooldown = LyriyaConfig.AUTO_CRAFTING_DELAY.getIntegerValue();
                diagnosticShown = false;
                return;
            }
        }
        showCraftDiagnostic(client, "message.lyriya_mod.crafting_no_recipe");
    }

    private void showCraftDiagnostic(Minecraft client, String translationKey) {
        if (!diagnosticShown && LyriyaConfig.DEBUG_MESSAGES.getBooleanValue()) {
            client.gui.hud.setOverlayMessage(Component.translatable(translationKey), true);
            diagnosticShown = true;
        }
    }

    private static List<Item> validTargets() {
        List<String> stored = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
        List<String> validIds = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        for (String raw : stored) {
            try {
                Identifier id = Identifier.parse(raw);
                Item item = BuiltInRegistries.ITEM.getValue(id);
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    items.add(item);
                    validIds.add(id.toString());
                }
            } catch (RuntimeException ignored) { }
        }
        if (!validIds.equals(stored)) {
            LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(validIds);
            LyriyaConfig.INSTANCE.save();
        }
        return items;
    }

    private static AutoCraftingMode currentMode() {
        return (AutoCraftingMode) LyriyaConfig.AUTO_CRAFTING_MODE.getOptionListValue();
    }
}
