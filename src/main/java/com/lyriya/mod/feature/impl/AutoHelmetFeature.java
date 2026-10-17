package com.lyriya.mod.feature.impl;

import com.lyriya.mod.feature.Feature;
import com.lyriya.mod.config.LyriyaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class AutoHelmetFeature extends Feature {
    private long threatStartTick = -1;
    private long lastSwapTick = Long.MIN_VALUE;

    public AutoHelmetFeature() {
        super("auto_helmet", LyriyaConfig.AUTO_HELMET_ENABLED);
    }

    @Override
    protected void onEnable() {
        threatStartTick = -1;
        lastSwapTick = Long.MIN_VALUE;
    }

    @Override
    protected void onDisable() {
        threatStartTick = -1;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null || client.gameMode == null) {
            threatStartTick = -1;
            return;
        }

        if (!client.level.dimension().equals(Level.NETHER) || !hasNearbyPiglin(client, player)) {
            threatStartTick = -1;
            return;
        }

        long gameTime = client.level.getGameTime();
        if (threatStartTick < 0) threatStartTick = gameTime;
        int delayTicks = LyriyaConfig.AUTO_HELMET_DELAY.getIntegerValue();
        if (gameTime - threatStartTick < delayTicks || gameTime == lastSwapTick) return;

        ItemStack equipped = player.getItemBySlot(EquipmentSlot.HEAD);
        if (equipped.is(Items.GOLDEN_HELMET) || hasBindingCurse(equipped)) return;
        boolean hadEquippedHelmet = !equipped.isEmpty();
        if (client.gui.screen() != null || player.containerMenu != player.inventoryMenu) return;

        InventoryMenu menu = player.inventoryMenu;
        int helmetSlot = InventoryMenu.ARMOR_SLOT_START + 3;
        int goldenHelmetSlot = findGoldenHelmet(menu);
        if (goldenHelmetSlot < 0) return;

        if (goldenHelmetSlot >= InventoryMenu.USE_ROW_SLOT_START && goldenHelmetSlot < InventoryMenu.USE_ROW_SLOT_END) {
            int hotbarButton = goldenHelmetSlot - InventoryMenu.USE_ROW_SLOT_START;
            client.gameMode.handleContainerInput(menu.containerId, helmetSlot, hotbarButton, ContainerInput.SWAP, player);
        } else {
            click(client, menu.containerId, goldenHelmetSlot, ContainerInput.PICKUP, player);
            click(client, menu.containerId, helmetSlot, ContainerInput.PICKUP, player);
            if (hadEquippedHelmet) click(client, menu.containerId, goldenHelmetSlot, ContainerInput.PICKUP, player);
        }

        lastSwapTick = gameTime;
    }

    private boolean hasNearbyPiglin(Minecraft client, Player player) {
        double range = LyriyaConfig.AUTO_HELMET_RANGE.getIntegerValue();
        double rangeSquared = range * range;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity instanceof Piglin piglin && piglin.isAlive() && piglin.distanceToSqr(player) <= rangeSquared) return true;
        }
        return false;
    }

    private int findGoldenHelmet(InventoryMenu menu) {
        for (int slot = InventoryMenu.INV_SLOT_START; slot < InventoryMenu.INV_SLOT_END; slot++) {
            if (menu.getSlot(slot).getItem().is(Items.GOLDEN_HELMET)) return slot;
        }
        for (int slot = InventoryMenu.USE_ROW_SLOT_START; slot < InventoryMenu.USE_ROW_SLOT_END; slot++) {
            if (menu.getSlot(slot).getItem().is(Items.GOLDEN_HELMET)) return slot;
        }
        return -1;
    }

    private boolean hasBindingCurse(ItemStack stack) {
        return stack.getEnchantments().keySet().stream().anyMatch(enchantment -> enchantment.is(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE));
    }

    private void click(Minecraft client, int menuId, int slot, ContainerInput input, Player player) {
        client.gameMode.handleContainerInput(menuId, slot, 0, input, player);
    }
}
