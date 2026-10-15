package com.example.elytraswap;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapClient implements ClientModInitializer {
    private static KeyMapping swapKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(ElytraSwap.MOD_ID, "main"));

        // Standardtaste X – in den Steuerungs-Einstellungen frei änderbar
        swapKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.elytraswap.swap", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (swapKey.consumeClick()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new SwapPayload());
                }
            }
        });
    }
}
