package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.world.BlockContainerJS;

/**
 * @author LatvianModder
 */
public interface BlockPredicate {
	boolean check(BlockContainerJS block);
}