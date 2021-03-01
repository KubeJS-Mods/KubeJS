package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.world.BlockContainerJS;

/**
 * @author LatvianModder
 */
public interface BlockPredicate {
	boolean check(BlockContainerJS block);
}