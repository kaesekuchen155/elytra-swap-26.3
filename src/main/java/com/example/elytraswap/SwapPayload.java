package com.example.elytraswap;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SwapPayload() implements CustomPacketPayload {
    public static final Type<SwapPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ElytraSwap.MOD_ID, "swap"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwapPayload> CODEC =
            StreamCodec.unit(new SwapPayload());

    @Override
    public Type<SwapPayload> type() {
        return TYPE;
    }
}
