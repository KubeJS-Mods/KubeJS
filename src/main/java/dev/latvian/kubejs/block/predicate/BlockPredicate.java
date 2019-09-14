package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.world.BlockContainerJS;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface BlockPredicate
{
	boolean check(@P("block") BlockContainerJS block);
}