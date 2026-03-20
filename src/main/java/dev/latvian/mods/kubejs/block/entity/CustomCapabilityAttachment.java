package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

public record CustomCapabilityAttachment(BlockCapability<?, ?> capability, Object data) {
	public record Config(String id, EnumSet<Direction> directions, BlockCapability<?, ?> capability, Supplier<?> dataFactory) {
	}

	public static Config createConfig(String id, Set<Direction> directions, BlockCapability<?, ?> capability, Supplier<?> dataFactory) {
		return new Config(
			id,
			directions == null || directions.isEmpty() ? DirectionWrapper.EMPTY_SET : EnumSet.copyOf(directions),
			capability,
			dataFactory
		);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> c) {
		if (c == capability) {
			return (CAP) data;
		}
		return null;
	}
}
