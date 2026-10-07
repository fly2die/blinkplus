package com.f2d.blinkplus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BlinkPayload(BlockPos from, BlockPos to) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlinkPayload> ID =
            new CustomPacketPayload.Type<>(Blink.id("blink_target"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlinkPayload> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, BlinkPayload::from,
                    BlockPos.STREAM_CODEC, BlinkPayload::to,
                    BlinkPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}