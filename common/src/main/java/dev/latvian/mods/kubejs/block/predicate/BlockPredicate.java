package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.BlockContainerJS;

public interface BlockPredicate {
	boolean check(BlockContainerJS block);
}