package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TooltipActionType<T extends TooltipAction>(int type, StreamCodec<? super RegistryFriendlyByteBuf, ? extends T> streamCodec) {
}
