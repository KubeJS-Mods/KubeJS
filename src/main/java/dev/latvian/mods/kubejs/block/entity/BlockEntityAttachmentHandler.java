package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public interface BlockEntityAttachmentHandler {
	void energyStorage(String id, Set<Direction> directions, int capacity, int maxReceive, int maxExtract, int autoOutput);

	void fluidTank(String id, Set<Direction> directions, int capacity, @Nullable FluidIngredient inputFilter);

	default void fluidTank(String id, Set<Direction> directions, int capacity) {
		fluidTank(id, directions, capacity, null);
	}

	void inventory(String id, Set<Direction> directions, int width, int height, @Nullable ItemPredicate inputFilter);

	default void inventory(String id, Set<Direction> directions, int width, int height) {
		inventory(id, directions, width, height, null);
	}

	void attachCustomCapability(String id, Set<Direction> directions, BlockCapability<?, ?> capability, Supplier<?> dataFactory);
}
