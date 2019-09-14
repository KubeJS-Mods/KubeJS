package dev.latvian.kubejs.bindings;

import com.google.common.base.Optional;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Collections;
import java.util.HashMap;
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

	public IBlockState state(Object id, Map<String, String> properties)
	{
		Block block = Block.REGISTRY.getObject(ID.of(id).mc());

		if (block == null || block == Blocks.AIR)
		{
			return Blocks.AIR.getDefaultState();
		}

		IBlockState state = block.getDefaultState();

		if (!properties.isEmpty())
		{
			Map<String, IProperty> pmap = new HashMap<>();

			for (IProperty property : state.getPropertyKeys())
			{
				pmap.put(property.getName(), property);
			}

			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				IProperty<?> property = pmap.get(entry.getKey());

				if (property != null)
				{
					Optional optional = property.parseValue(entry.getValue());

					if (optional.isPresent())
					{
						state = state.withProperty(property, UtilsJS.cast(optional.get()));
					}
				}
			}
		}

		return state;
	}

	public IBlockState state(Object id)
	{
		return state(id, Collections.emptyMap());
	}
}