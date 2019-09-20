package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.util.nbt.NBTCompoundJS;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface BlockEntityPredicateDataCheck
{
	boolean checkData(NBTCompoundJS data);
}