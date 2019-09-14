package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.documentation.DisplayName;

import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("Block Utilities")
public class BlockWrapper
{
	public Map<String, MaterialJS> getMaterial()
	{
		return MaterialListJS.INSTANCE.map;
	}

	public BlockIDPredicate id(Object id)
	{
		return new BlockIDPredicate(id);
	}

	public BlockIDPredicate id(Object id, Map<String, Object> properties)
	{
		BlockIDPredicate b = id(id);

		for (Map.Entry<String, Object> entry : properties.entrySet())
		{
			b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public BlockEntityPredicate entity(Object id)
	{
		return new BlockEntityPredicate(id);
	}

	public BlockPredicate custom(BlockPredicate predicate)
	{
		return predicate;
	}
}