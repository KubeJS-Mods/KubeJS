package dev.latvian.mods.kubejs.block.predicate;

import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface BlockEntityPredicateDataCheck {
	boolean checkData(@Nullable CompoundTag data);
}