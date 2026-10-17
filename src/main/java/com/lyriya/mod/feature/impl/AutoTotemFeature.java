package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.AutoTotemMode;
import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ContainerInput;

public final class AutoTotemFeature extends Feature {
    private int ticksUntilSwap;

    public AutoTotemFeature() { super("auto_totem", LyriyaConfig.AUTO_TOTEM_ENABLED); }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.gameMode == null || client.gui.screen() != null) return;
        if (!client.player.getOffhandItem().isEmpty() && client.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;
        if (ticksUntilSwap > 0) { ticksUntilSwap--; return; }

        boolean needsTotem = LyriyaConfig.AUTO_TOTEM_MODE.getOptionListValue() == AutoTotemMode.ALWAYS
            || client.player.getHealth() + client.player.getAbsorptionAmount() <= LyriyaConfig.AUTO_TOTEM_HEALTH.getIntegerValue()
            || (LyriyaConfig.AUTO_TOTEM_ELYTRA.getBooleanValue()
                && client.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && client.player.isFallFlying());
        if (!needsTotem) return;

        var inventory = client.player.getInventory();
        for (int i = 0; i < Math.min(inventory.getContainerSize(), 36); i++) {
            if (!inventory.getItem(i).is(Items.TOTEM_OF_UNDYING)) continue;
            int menuSlot = i < 9 ? i + 36 : i;
            client.gameMode.handleContainerInput(client.player.inventoryMenu.containerId, menuSlot, 40, ContainerInput.SWAP, client.player);
            ticksUntilSwap = LyriyaConfig.AUTO_TOTEM_DELAY.getIntegerValue();
            return;
        }
    }
}
