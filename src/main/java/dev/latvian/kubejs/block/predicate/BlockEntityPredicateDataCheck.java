package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.util.MapJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface BlockEntityPredicateDataCheck
{
	boolean checkData(@Nullable MapJS data);
}