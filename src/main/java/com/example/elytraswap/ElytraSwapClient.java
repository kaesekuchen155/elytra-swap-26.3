package com.example.elytraswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;

public class ElytraSwapClient implements ClientModInitializer {
    private static final int KEY_X = 88;        // Tastencode für X
    private static final int CHEST_MENU_SLOT = 6; // Brust-Slot im Inventar-Menü
    private static KeyMapping swapKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(ElytraSwap.MOD_ID, "main"));

        swapKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.elytraswap.swap", KEY_X, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (swapKey.consumeClick()) {
                swap(client);
            }
        });
    }

    private static boolean isElytra(ItemStack s) {
        return s.is(Items.ELYTRA);
    }

    private static boolean isChestplate(ItemStack s) {
        if (s.isEmpty() || isElytra(s)) return false;
        Equippable eq = s.get(DataComponents.EQUIPPABLE);
        return eq != null && eq.slot() == EquipmentSlot.CHEST;
    }

    private static boolean isUsableElytra(ItemStack s) {
        return isElytra(s) && s.getDamageValue() < s.getMaxDamage() - 1;
    }

    private static void click(Minecraft mc, LocalPlayer player, int menuSlot) {
        mc.gameMode.handleContainerInput(0, menuSlot, 0, ContainerInput.PICKUP, player);
    }

    private static void swap(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null) return;
        if (!player.inventoryMenu.getCarried().isEmpty()) return;

        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);

        // Fluch der Bindung verhindert Ausziehen (außer im Kreativmodus)
        if (!player.isCreative() && !worn.isEmpty()
                && EnchantmentHelper.has(worn, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            return;
        }

        Inventory inv = player.getInventory();
        boolean wearingElytra = isElytra(worn);
        int index = find(inv, wearingElytra); // Elytra an -> Brustplatte suchen, sonst Elytra
        if (index == -1) return;

        // Inventarplatz -> Slot-Nummer im Inventar-Menü (Hotbar 36-44, Rest 9-35)
        int menuSlot = index < 9 ? 36 + index : index;

        click(mc, player, menuSlot);        // Ersatz aufnehmen
        click(mc, player, CHEST_MENU_SLOT); // in Brust-Slot legen, getragenes Teil kommt an den Cursor
        click(mc, player, menuSlot);        // getragenes Teil zurück auf den freien Platz
    }

    private static int find(Inventory inv, boolean chestplate) {
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack s = inv.getItem(i);
            if (chestplate ? isChestplate(s) : isUsableElytra(s)) return i;
        }
        return -1;
    }
}
