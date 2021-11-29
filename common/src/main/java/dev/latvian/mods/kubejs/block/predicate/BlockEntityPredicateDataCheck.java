package dev.latvian.mods.kubejs.block.predicate;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface BlockEntityPredicateDataCheck {
	boolean checkData(@Nullable CompoundTag data);
}