/*
 * Auto Crafting task selection and result handling adapted from Meteor Lotus
 * AutoCrafting.java, Copyright (c) 2025 He, GPL-3.0.
 * Source: https://github.com/xiaohe66/xiaohe66-meteor-lotus/blob/4ca773b8198b5bb8a4ceb7c0d15c18c8516a4f7c/src/main/java/com/xiaohe66/mc/meteor/lotus/modules/AutoCrafting.java
 * Minecraft 26.3 recipe display, inventory, and container APIs are adapted;
 * Meteor-specific settings and inventory utilities are not used here.
 */
package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.AutoCraftingMode;
import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private int pendingTicks;
    private boolean taskRunning;
    private boolean dropOutput;
    private boolean diagnosticShown;
    private boolean awaitingCraftOutput;
    private boolean awaitingResultSync;
    private boolean stoppedForCurrentScreen;
    private boolean hadMatchingRecipe;
    private boolean hadCraftableRecipe;
    private ClientLevel activeLevel;
    private CraftingMenu activeMenu;
    private Item activeTarget;
    private String inputSignatureBeforeOutput;

    private AutoCraftingFeature() { super("auto_crafting", LyriyaConfig.AUTO_CRAFTING_ENABLED); }

    /** C/V select the output handling mode and start or continue the Lotus-style task. */
    public static void toggleTask(boolean drop) { INSTANCE.startTask(drop); }

    private void startTask(boolean drop) {
        Minecraft client = Minecraft.getInstance();
        if (!isEnabled()) {
            showMessage(client, "message.lyriya_mod.crafting_enable_first");
            return;
        }
        if (!(client.gui.screen() instanceof CraftingScreen)
            || client.player == null || !(client.player.containerMenu instanceof CraftingMenu menu)) {
            showMessage(client, "message.lyriya_mod.crafting_open_first");
            return;
        }
        if (!menu.getCarried().isEmpty()) {
            showMessage(client, "message.lyriya_mod.crafting_cursor_busy");
            return;
        }
        if (!drop && client.player.getInventory().getFreeSlot() < 0) {
            showMessage(client, "message.lyriya_mod.crafting_empty_slot_required");
            return;
        }

        if (taskRunning && activeLevel == client.level && activeMenu == menu) {
            // As in Lotus, another C/V press changes the handling mode without
            // issuing a duplicate request while the current result is syncing.
            dropOutput = drop;
            showMessage(client, Component.translatable("message.lyriya_mod.crafting_mode_changed",
                Component.translatable(drop ? "message.lyriya_mod.crafting_drop" : "message.lyriya_mod.crafting_keep")));
            return;
        }

        activeTarget = findInitialTarget(client, menu);
        activeLevel = client.level;
        activeMenu = menu;
        taskRunning = true;
        dropOutput = drop;
        cooldown = 0;
        pendingTicks = 0;
        awaitingCraftOutput = false;
        awaitingResultSync = false;
        diagnosticShown = false;
        showMessage(client, Component.translatable("message.lyriya_mod.crafting_started",
            Component.translatable(drop ? "message.lyriya_mod.crafting_drop" : "message.lyriya_mod.crafting_keep")));
    }

    @Override protected void onDisable() { resetTask(); }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.gameMode == null
            || !(client.gui.screen() instanceof CraftingScreen)
            || !(client.player.containerMenu instanceof CraftingMenu menu)) {
            resetTask();
            return;
        }
        if ((activeLevel != null && activeLevel != client.level) || (activeMenu != null && activeMenu != menu)) {
            resetTask();
            if (currentMode() == AutoCraftingMode.HOTKEY) return;
        }
        if (activeLevel == null) activeLevel = client.level;
        if (activeMenu == null) activeMenu = menu;
        boolean manualTask = taskRunning;
        if (!manualTask && (currentMode() != AutoCraftingMode.OPEN_SCREEN || stoppedForCurrentScreen)) return;
        if (!manualTask && !dropOutput && client.player.getInventory().getFreeSlot() < 0) {
            stopWithMessage(client, "message.lyriya_mod.crafting_empty_slot_required");
            return;
        }
        if (cooldown > 0) { cooldown--; return; }
        if (!menu.getCarried().isEmpty()) {
            stopWithMessage(client, "message.lyriya_mod.crafting_cursor_busy");
            return;
        }

        List<Item> targets = manualTask && activeTarget != null ? List.of(activeTarget) : validTargets();
        if (targets.isEmpty()) {
            stopWithMessage(client, "message.lyriya_mod.crafting_no_targets");
            return;
        }

        if (awaitingResultSync) {
            String currentSignature = inputSignature(menu);
            // An empty result slot can be only the client's optimistic click result.
            // Wait for a confirmed input-grid change before placing another recipe.
            if (!currentSignature.equals(inputSignatureBeforeOutput)) {
                awaitingResultSync = false;
                awaitingCraftOutput = false;
                pendingTicks = 0;
                cooldown = LyriyaConfig.AUTO_CRAFTING_DELAY.getIntegerValue();
                return;
            }
            if (++pendingTicks > 40) stopWithMessage(client, "message.lyriya_mod.crafting_sync_timeout");
            return;
        }

        ItemStack output = menu.getResultSlot().getItem();
        if (awaitingCraftOutput && output.isEmpty()) {
            if (++pendingTicks > 40) stopWithMessage(client, "message.lyriya_mod.crafting_sync_timeout");
            return;
        }
        if (!output.isEmpty()) {
            // Lotus treats an already-visible result as the task target and adds it
            // to its list. Preserve that priority even if it was not preselected.
            if (activeTarget == null) {
                activeTarget = output.getItem();
                addWhitelistItem(activeTarget);
            }
            boolean allowed = output.is(activeTarget);
            if (!allowed) {
                stopWithMessage(client, awaitingCraftOutput
                    ? "message.lyriya_mod.crafting_unexpected_output"
                    : "message.lyriya_mod.crafting_existing_output");
                return;
            }
            if (!dropOutput && client.player.getInventory().getFreeSlot() < 0) {
                stopWithMessage(client, "message.lyriya_mod.crafting_inventory_full");
                return;
            }
            inputSignatureBeforeOutput = inputSignature(menu);
            client.gameMode.handleContainerInput(menu.containerId, CraftingMenu.RESULT_SLOT,
                dropOutput ? 1 : 0, dropOutput ? ContainerInput.THROW : ContainerInput.QUICK_MOVE, client.player);
            awaitingCraftOutput = false;
            awaitingResultSync = true;
            pendingTicks = 0;
            diagnosticShown = false;
            return;
        }

        StackedItemContents contents = collectAvailableItems(client, menu);
        List<RecipeDisplayEntry> craftableEntries = selectCraftableEntries(client, contents);
        hadMatchingRecipe = false;
        hadCraftableRecipe = craftableEntries.stream().anyMatch(entry -> matchingTarget(entry, client, targets) != null);
        for (RecipeCollection collection : client.player.getRecipeBook().getCollections()) {
            for (RecipeDisplayEntry entry : collection.getRecipes()) {
                if (hasTargetOutput(entry, client, targets)) { hadMatchingRecipe = true; break; }
            }
            if (hadMatchingRecipe) break;
        }

        RecipeDisplayEntry selected = null;
        for (RecipeDisplayEntry entry : craftableEntries) {
            Item result = matchingTarget(entry, client, targets);
            if (result == null) continue;
            if (activeTarget == null) activeTarget = result;
            if (activeTarget != result) continue;
            if (!dropOutput && client.player.getInventory().getFreeSlot() < 0) continue;
            selected = entry;
            break;
        }
        if (selected == null) {
            // Opening a table and receiving the recipe-book/slot state are separate
            // client updates. Give OPEN_SCREEN mode a bounded sync window before
            // treating an empty craftable list as a final task failure.
            if (!manualTask && ++pendingTicks <= 40) return;
            String reason = hadCraftableRecipe ? "message.lyriya_mod.crafting_inventory_full"
                : (hadMatchingRecipe ? "message.lyriya_mod.crafting_missing_materials"
                : "message.lyriya_mod.crafting_no_recipe");
            stopWithMessage(client, reason);
            return;
        }

        // Match Lotus: request one craftable recipe, wait for the output packet,
        // take/drop that result, then wait for the input/result slots to update.
        client.gameMode.handlePlaceRecipe(menu.containerId, selected.id(), true);
        awaitingCraftOutput = true;
        pendingTicks = 0;
        cooldown = LyriyaConfig.AUTO_CRAFTING_DELAY.getIntegerValue();
        diagnosticShown = false;
    }

    private static StackedItemContents collectAvailableItems(Minecraft client, CraftingMenu menu) {
        StackedItemContents contents = new StackedItemContents();
        client.player.getInventory().fillStackedContents(contents);
        for (var inputSlot : menu.getInputGridSlots()) contents.accountStack(inputSlot.getItem());
        return contents;
    }

    private static List<RecipeDisplayEntry> selectCraftableEntries(Minecraft client, StackedItemContents contents) {
        List<RecipeDisplayEntry> entries = new ArrayList<>();
        for (RecipeCollection collection : client.player.getRecipeBook().getCollections()) {
            // 26.3 stores craftable status per collection. Refresh it from the
            // player's inventory plus grid before asking for CRAFTABLE entries.
            collection.selectRecipes(contents, display -> true);
            entries.addAll(collection.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE));
        }
        return entries;
    }

    private static Item firstCraftableTarget(Minecraft client, CraftingMenu menu, List<Item> targets) {
        if (targets.isEmpty()) return null;
        StackedItemContents contents = collectAvailableItems(client, menu);
        for (RecipeDisplayEntry entry : selectCraftableEntries(client, contents)) {
            Item result = matchingTarget(entry, client, targets);
            if (result != null) return result;
        }
        return null;
    }

    /** Lotus keydown() first chooses the visible result; otherwise it picks one craftable whitelist item. */
    private Item findInitialTarget(Minecraft client, CraftingMenu menu) {
        ItemStack output = menu.getResultSlot().getItem();
        if (!output.isEmpty()) {
            addWhitelistItem(output.getItem());
            return output.getItem();
        }
        return firstCraftableTarget(client, menu, validTargets());
    }

    private static boolean hasTargetOutput(RecipeDisplayEntry entry, Minecraft client, List<Item> targets) {
        return matchingTarget(entry, client, targets) != null;
    }

    private static Item matchingTarget(RecipeDisplayEntry entry, Minecraft client, List<Item> targets) {
        return entry.resultItems(SlotDisplayContext.fromLevel(client.level)).stream()
            .map(ItemStack::getItem).filter(targets::contains).findFirst().orElse(null);
    }

    private static String inputSignature(CraftingMenu menu) {
        StringBuilder signature = new StringBuilder();
        for (var slot : menu.getInputGridSlots()) {
            ItemStack stack = slot.getItem();
            signature.append(BuiltInRegistries.ITEM.getKey(stack.getItem())).append(':').append(stack.getCount()).append(';');
        }
        return signature.toString();
    }

    private void stopWithMessage(Minecraft client, String key) {
        showMessage(client, key);
        taskRunning = false;
        awaitingCraftOutput = false;
        awaitingResultSync = false;
        cooldown = 0;
        diagnosticShown = true;
        if (currentMode() == AutoCraftingMode.OPEN_SCREEN) stoppedForCurrentScreen = true;
    }

    private void showMessage(Minecraft client, String key) {
        showMessage(client, Component.translatable(key));
    }

    private static void showMessage(Minecraft client, Component message) {
        if (client.gui != null && client.gui.hud != null) client.gui.hud.setOverlayMessage(message, true);
    }

    private static void addWhitelistItem(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        List<String> values = new ArrayList<>(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
        if (!values.contains(id)) {
            values.add(id);
            LyriyaConfig.AUTO_CRAFTING_WHITELIST.setStrings(values);
            LyriyaConfig.INSTANCE.save();
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

    private void resetTask() {
        cooldown = 0;
        pendingTicks = 0;
        taskRunning = false;
        dropOutput = false;
        awaitingCraftOutput = false;
        awaitingResultSync = false;
        diagnosticShown = false;
        stoppedForCurrentScreen = false;
        activeLevel = null;
        activeMenu = null;
        activeTarget = null;
        inputSignatureBeforeOutput = null;
    }
}
