package com.example.elytraswap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.equipment.Equippable;

public class ElytraSwap implements ModInitializer {
    public static final String MOD_ID = "elytraswap";

    @Override
    public void onInitialize() {
        // Falls dieser Methodenname nicht existiert: serverboundPlay() verwenden
        PayloadTypeRegistry.playC2S().register(SwapPayload.TYPE, SwapPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SwapPayload.TYPE,
                (payload, context) -> swap(context.player()));
    }

    private static boolean isElytra(ItemStack s) {
        return s.is(Items.ELYTRA);
    }

    /** Brustplatte = alles, was in den Brust-Slot passt, aber keine Elytra. */
    private static boolean isChestplate(ItemStack s) {
        if (s.isEmpty() || isElytra(s)) return false;
        Equippable eq = s.get(DataComponents.EQUIPPABLE);
        return eq != null && eq.slot() == EquipmentSlot.CHEST;
    }

    /** Kaputte Elytra (1 Haltbarkeit übrig) zählt nicht. */
    private static boolean isUsableElytra(ItemStack s) {
        return isElytra(s) && s.getDamageValue() < s.getMaxDamage() - 1;
    }

    private static void swap(ServerPlayer player) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);

        // Fluch der Bindung verhindert Ausziehen (außer im Kreativmodus)
        if (!player.isCreative() && !worn.isEmpty()
                && EnchantmentHelper.has(worn, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            return;
        }

        Inventory inv = player.getInventory();
        boolean wearingElytra = isElytra(worn);

        int slot = -1;
        if (wearingElytra) {
            slot = find(inv, true);                  // Elytra an -> Brustplatte suchen
        } else {
            slot = find(inv, false);                 // sonst -> Elytra suchen
            if (slot == -1 && !isChestplate(worn)) { // keine Elytra da: Brustplatte als Fallback
                slot = find(inv, true);
            }
        }
        if (slot == -1) return;

        ItemStack candidate = inv.getItem(slot);
        inv.setItem(slot, worn.copy());
        player.setItemSlot(EquipmentSlot.CHEST, candidate.copy());
    }

    private static int find(Inventory inv, boolean chestplate) {
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack s = inv.getItem(i);
            if (chestplate ? isChestplate(s) : isUsableElytra(s)) return i;
        }
        return -1;
    }
}
