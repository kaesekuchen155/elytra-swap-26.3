package com.example.elytraswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ElytraSwapClient implements ClientModInitializer {
    private static final int KEY_X = 88; // Tastencode für X
    private static KeyMapping swapKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(ElytraSwap.MOD_ID, "main"));

        // Standardtaste X, in den Steuerungs-Einstellungen änderbar
        swapKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.elytraswap.swap", KEY_X, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (swapKey.consumeClick()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new SwapPayload());
                }
            }
        });
    }
}
